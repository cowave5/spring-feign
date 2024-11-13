package com.cowave.commons.client.http.invoke.proxy;

import com.cowave.commons.client.http.HttpClientInterceptor;
import com.cowave.commons.client.http.invoke.codec.decoder.JacksonDecoder;
import com.cowave.commons.client.http.invoke.exec.HttpExecutor;
import com.cowave.commons.client.http.response.HttpResponse;
import com.cowave.commons.client.http.response.HttpResponseTemplate;
import com.cowave.commons.client.http.request.Options;
import com.cowave.commons.client.http.request.HttpRequestTemplate;
import com.cowave.commons.client.http.request.HttpRequest;
import com.cowave.commons.client.http.request.HttpRequestFactory;
import com.cowave.commons.client.http.request.meta.HttpMethodMeta;
import com.cowave.commons.client.http.asserts.HttpException;
import com.cowave.commons.client.http.asserts.HttpHintException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import com.cowave.commons.client.http.HttpExceptionHandler;
import com.cowave.commons.client.http.invoke.codec.HttpDecoder;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.cowave.commons.client.http.constants.HttpCode.SERVICE_ERROR;
import static com.cowave.commons.client.http.constants.HttpHeader.Content_Length;
import static org.slf4j.event.Level.WARN;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
public class HttpMethodInvoker implements MethodInvoker {
    private final Level level;
    private final boolean ignoreError;
    private final HttpMethodMeta metadata;
    private final ProxyTarget<?> proxyTarget;
    private final HttpExecutor httpExecutor;
    private final List<HttpClientInterceptor> httpClientInterceptors;
    private final HttpRequestFactory httpRequestFactory;
    private final Options options;
    private final HttpDecoder decoder;
    private final HttpExceptionHandler exceptionHandler;

    public HttpMethodInvoker(ProxyTarget<?> proxyTarget,
                             HttpMethodMeta metadata,
                             HttpRequestFactory httpRequestFactory,
                             HttpExecutor httpExecutor,
                             List<HttpClientInterceptor> httpClientInterceptors,
                             Options options,
                             HttpDecoder decoder,
                             Level level,
                             boolean ignoreError,
                             HttpExceptionHandler exceptionHandler) {
        this.proxyTarget = proxyTarget;
        this.httpExecutor = httpExecutor;
        this.httpClientInterceptors = httpClientInterceptors;
        this.metadata = metadata;
        this.httpRequestFactory = httpRequestFactory;
        this.options = options;
        this.decoder = decoder;
        this.level = level;
        this.ignoreError = ignoreError;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Object invoke(Object[] args) throws Throwable {
        HttpRequest httpRequest = httpRequestFactory.create(args);
        return executeAndDecode(httpRequest);
    }

    Object executeAndDecode(HttpRequest httpRequest) throws Throwable {
        Type returnType = metadata.getReturnType();
        Type httpType = getParamTypeOf(returnType, HttpResponse.class);

        // Http请求
        HttpRequestTemplate httpRequestTemplate = applyTemplate(httpRequest);
        String url = httpRequestTemplate.getUrl();
        String method = httpRequestTemplate.getMethod();

        long start = System.nanoTime();
        HttpResponseTemplate httpResponseTemplate;
        try {
            httpResponseTemplate = httpExecutor.execute(httpRequestTemplate);
        } catch (IOException e) {
            log.error(">< {} {} {}", e.getMessage(), method, url, e);
            return throwOrReturn(httpType, new HttpHintException("Remote failed"));
        }

        long cost = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        int status = httpResponseTemplate.getStatus();
        try {
            // 响应类型: HttpResponse
            if (httpType != null) {
                return parseHttpResponse(httpType, httpResponseTemplate, status, method, url, cost);
            }

            // decoder解码
            if (status == 200) {
                return decoder.decode(httpResponseTemplate, metadata.getReturnType(), url, cost, status, level);
            }

            String reason = httpResponseTemplate.getReason();
            if (httpResponseTemplate.getInputStream() != null) {
                reason = StreamUtils.copyToString(httpResponseTemplate.getInputStream(), StandardCharsets.UTF_8);
            }

            if(StringUtils.hasText(reason)){
                log.error(">< {} {}ms {} {} {}", status, cost, method, url, reason);
            }else {
                log.error(">< {} {}ms {} {}", status, cost, method, url);
            }

            throw new HttpHintException(status, SERVICE_ERROR.getCode(), "Remote failed");
        } catch (HttpException e) {
            if (exceptionHandler != null) {
                exceptionHandler.handle(e);
            }
            return throwOrReturn(httpType, e);
        } catch (Exception e) {
            log.error(">< {}ms {} {} {}", cost, e.getMessage(), method, url, e);
            return throwOrReturn(httpType, new HttpHintException("Remote failed", e));
        }
    }

    private Object throwOrReturn(Type httpType, Exception exception) throws Throwable {
        if (ignoreError && httpType != null) {
            HttpResponse<?> httpResponse = new HttpResponse<>(SERVICE_ERROR);
            httpResponse.setCause(exception);
            httpResponse.setMessage(exception.getMessage());
            return httpResponse;
        }
        throw exception;
    }

    private HttpResponse<?> parseHttpResponse(Type paramType, HttpResponseTemplate response,
                                              int status, String method, String url, long cost) throws IOException {
        // Header信息
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, Collection<String>> entry : response.getHeaders().entrySet()) {
            if(Content_Length.equals(entry.getKey())){
                // 重新序列化后Content-Length会发生变化，这里直接去掉
                continue;
            }
            headers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        // InputStream交给调用者处理
        if (paramType.equals(InputStream.class)) {
            if (status == 200) {
                if (log.isDebugEnabled() || level.toInt() < WARN.toInt()) {
                    log.info(">< {} {}ms {} {}", status, cost, method, url);
                }
            } else {
                log.warn(">< {} {}ms {}, {}", status, cost, method, url);
            }
            return new HttpResponse<>(response.getStatus(), headers, response.getInputStream());
        }

        // 获取响应主体
        String body = null;
        if (response.getInputStream() != null) {
            body = StreamUtils.copyToString(response.getInputStream(), StandardCharsets.UTF_8);
        }

        if (status == 200) {
            if (log.isDebugEnabled() || level.toInt() < WARN.toInt()) {
                log.info(">< {} {}ms {} {}", status, cost, method, url);
            }
            if (!StringUtils.hasText(body) || paramType.equals(String.class) || paramType.equals(Void.class)) {
                return new HttpResponse<>(response.getStatus(), headers, body);
            } else {
                return new HttpResponse<>(response.getStatus(), headers, JacksonDecoder.readValue(body, paramType));
            }
        } else {
            String reason = response.getReason();
            if(body != null){
                reason = body;
            }

            if(StringUtils.hasText(reason)){
                log.error(">< {} {}ms {} {} {}", status, cost, method, url, reason);
            }else {
                log.error(">< {} {}ms {} {}", status, cost, method, url);
            }

            HttpResponse<?> httpResponse;
            if (!StringUtils.hasText(body) || paramType.equals(String.class) || paramType.equals(Void.class)) {
                httpResponse = new HttpResponse<>(response.getStatus(), headers, body);
            } else {
                httpResponse = new HttpResponse<>(response.getStatus(), headers, tryReadObject(body, paramType));
            }
            httpResponse.setMessage(reason);
            return httpResponse;
        }
    }

    private Object tryReadObject(String body, Type paramType){
        try{
            return JacksonDecoder.readValue(body, paramType);
        }catch (Exception e){
            return null;
        }
    }

    private Type getParamTypeOf(Type type, Class<?> clazz) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class<?> && clazz.equals(rawType)) {
                Type[] paramTypes = parameterizedType.getActualTypeArguments();
                if (paramTypes == null || paramTypes.length == 0) {
                    return Object.class;
                } else {
                    return paramTypes[0];
                }
            }
        }
        return null;
    }

    HttpRequestTemplate applyTemplate(HttpRequest httpRequest) throws UnsupportedEncodingException {
        for (HttpClientInterceptor interceptor : httpClientInterceptors) {
            interceptor.apply(httpRequest);
        }

        // 方法Options注解指定的超时优先级更高
        int connectTimeout = options.getConnectTimeout();
        int readTimeout = options.getReadTimeout();
        int retryTimes = options.getRetryTimes();
        int retryInterval = options.getRetryInterval();
        if (metadata.getConnectTimeout() != -1) {
            connectTimeout = metadata.getConnectTimeout();
        }
        if (metadata.getReadTimeout() != -1) {
            readTimeout = metadata.getReadTimeout();
        }
        if (metadata.getRetryTimes() != -1) {
            retryTimes = metadata.getRetryTimes();
        }
        if (metadata.getRetryInterval() != -1) {
            retryInterval = metadata.getRetryInterval();
        }
        // 设置url及一些参数
        return proxyTarget.apply(httpRequest,
                httpRequest.getHostUrl(), retryTimes, retryInterval, connectTimeout, readTimeout);
    }
}
