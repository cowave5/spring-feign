package com.cowave.commons.client.http.invoke.exec;

import com.cowave.commons.client.http.request.HttpRequestTemplate;
import com.cowave.commons.client.http.response.HttpResponseTemplate;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import static com.cowave.commons.client.http.constants.HttpHeader.*;
import static com.cowave.commons.client.http.constants.HttpMethod.*;

/**
 *
 * @author shanhuiming
 *
 */
public class HttpClientExecutor implements HttpExecutor {

    private final HttpClient httpClient;

    public HttpClientExecutor(SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslSocketFactory, hostnameVerifier);
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslConnectionSocketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(100);

        HttpRequestRetryHandler retryHandler = (exception, execCount, context) -> {
            Integer retryTimes = (Integer) context.getAttribute("retryTimes");
            Integer retryInterval = (Integer) context.getAttribute("retryInterval");
            if (retryTimes == null) {
                retryTimes = 0;
            }
            if (execCount > retryTimes) {
                return false;
            }

            if (retryInterval == null) {
                retryInterval = 1000;
            }
            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            return exception instanceof ConnectTimeoutException
                    || exception instanceof NoHttpResponseException
                    || exception instanceof SocketException;
        };

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(connectionManager);
        httpClientBuilder.setRetryHandler(retryHandler);
        this.httpClient = httpClientBuilder.build();
    }

    @Override
    public HttpResponseTemplate execute(HttpRequestTemplate request) throws IOException {
        HttpRequestBase httpRequest = buildRequest(request);
        // 超时参数
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(request.getConnectTimeout())
                .setSocketTimeout(request.getReadTimeout()).build();
        // 重试设置
        HttpClientContext context = HttpClientContext.create();
        context.setAttribute("retryTimes", request.getRetryTimes());
        context.setAttribute("retryInterval", request.getRetryInterval());

        httpRequest.setConfig(requestConfig);
        HttpResponse httpResponse = httpClient.execute(httpRequest, context);

        int status = httpResponse.getStatusLine().getStatusCode();
        String reason = httpResponse.getStatusLine().getReasonPhrase();

        // 响应 Header
        Map<String, Collection<String>> headers = new HashMap<>();
        for (Header header : httpResponse.getAllHeaders()) {
            headers.computeIfAbsent(header.getName(), k -> new ArrayList<>()).add(header.getValue());
        }

        InputStream stream = httpResponse.getEntity() != null ? httpResponse.getEntity().getContent() : null;
        long length = httpResponse.getEntity() != null ? httpResponse.getEntity().getContentLength() : 0;
        return new HttpResponseTemplate(status, headers, reason, stream, (int) length);
    }

    private HttpRequestBase buildRequest(HttpRequestTemplate request) throws IOException {
        // 请求压缩
        Collection<String> encodings = request.getHeaders().get(Content_Encoding);
        // gzip
        boolean useGzipEncode = encodings != null && encodings.contains("gzip");
        // deflate
        boolean useDeflateEncode = encodings != null && encodings.contains("deflate");

        String url = request.getUrl();
        String method = request.getMethod().toUpperCase();
        switch (method.toUpperCase()) {
            case GET:
                HttpGet httpGet = new HttpGet(url);
                setHeader(httpGet, request);
                return httpGet;
            case DELETE:
                HttpDelete httpDelete = new HttpDelete(url);
                setHeader(httpDelete, request);
                return httpDelete;
            case POST:
                HttpPost httpPost = new HttpPost(url);
                setHeader(httpPost, request);
                setEntity(httpPost, request, useGzipEncode, useDeflateEncode);
                return httpPost;
            case PATCH:
                HttpPatch httpPatch = new HttpPatch(url);
                setHeader(httpPatch, request);
                setEntity(httpPatch, request, useGzipEncode, useDeflateEncode);
                return httpPatch;
            case PUT:
                HttpPut httpPut = new HttpPut(url);
                setHeader(httpPut, request);
                setEntity(httpPut, request, useGzipEncode, useDeflateEncode);
                return httpPut;
            case HEAD:
                HttpHead httpHead = new HttpHead(url);
                setHeader(httpHead, request);
                return httpHead;
            case OPTIONS:
                HttpOptions httpOptions = new HttpOptions(url);
                setHeader(httpOptions, request);
                return httpOptions;
            case TRACE:
                HttpTrace httpTrace = new HttpTrace(url);
                setHeader(httpTrace, request);
                return httpTrace;
            default:
                throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        }
    }

    private void setEntity(HttpEntityEnclosingRequestBase httpRequest, HttpRequestTemplate request,
                           boolean useGzipEncode, boolean useDeflateEncode) throws IOException {
        if (request.getBody() != null) {
            if (useGzipEncode) {
                try (ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
                     GZIPOutputStream gzipOutStream = new GZIPOutputStream(byteArrayOutStream)) {
                    gzipOutStream.write(request.getBody());
                    HttpEntity httpEntity = new ByteArrayEntity(byteArrayOutStream.toByteArray());
                    httpRequest.setEntity(httpEntity);
                }
            } else if (useDeflateEncode) {
                try (ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
                     DeflaterOutputStream defeaterOutStream = new DeflaterOutputStream(byteArrayOutStream)) {
                    defeaterOutStream.write(request.getBody());
                    HttpEntity httpEntity = new ByteArrayEntity(byteArrayOutStream.toByteArray());
                    httpRequest.setEntity(httpEntity);
                }
            } else {
                HttpEntity httpEntity = new ByteArrayEntity(request.getBody());
                httpRequest.setEntity(httpEntity);
            }
        } else if (request.getMultiFile() != null || request.getMultiForm() != null) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (request.getMultiFile() != null) {
                builder.addPart("file", new InputStreamBody(
                        request.getMultiFile(), ContentType.APPLICATION_OCTET_STREAM, request.getMultiFileName()));
            }
            if (request.getMultiForm() != null) {
                for (Map.Entry<String, Object> entry : request.getMultiForm().entrySet()) {
                    builder.addTextBody(entry.getKey(), entry.getValue().toString());
                }
            }
            HttpEntity httpEntity = builder.build();
            httpRequest.setEntity(httpEntity);
        }
    }

    private void setHeader(HttpRequestBase httpRequest, HttpRequestTemplate request) {
        boolean hasAcceptHeader = false;
        for (String field : request.getHeaders().keySet()) {
            if (field.equalsIgnoreCase(Accept)) {
                hasAcceptHeader = true;
            }
            for (String value : request.getHeaders().get(field)) {
                if (!field.equals(Content_Length)) {
                    httpRequest.addHeader(field, value);
                }
            }
        }

        // 接受任意类型的响应
        if (!hasAcceptHeader) {
            httpRequest.setHeader(Accept, "*/*");
        }
    }
}
