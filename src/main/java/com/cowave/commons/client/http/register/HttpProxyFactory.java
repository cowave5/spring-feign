package com.cowave.commons.client.http.register;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import com.cowave.commons.client.http.HttpClientInterceptor;
import com.cowave.commons.client.http.annotation.HttpClient;
import com.cowave.commons.client.http.invoke.exec.HttpClientExecutor;
import com.cowave.commons.client.http.invoke.codec.HttpEncoder;
import com.cowave.commons.client.http.invoke.proxy.ProxyFactory;
import com.cowave.commons.client.http.invoke.proxy.ProxyFactoryBuilder;
import com.cowave.commons.client.http.invoke.proxy.ProxyTarget;
import com.cowave.commons.client.http.request.Options;
import org.springframework.context.ApplicationContext;
import com.cowave.commons.client.http.HttpExceptionHandler;
import com.cowave.commons.client.http.invoke.codec.HttpDecoder;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author shanhuiming
 *
 */
class HttpProxyFactory {

    private static ApplicationContext applicationContext;

    private static StringValueResolver valueResolver;

    static void setApplicationContext(ApplicationContext applicationContext) {
        HttpProxyFactory.applicationContext = applicationContext;
    }

    static void setStringValueResolver(StringValueResolver valueResolver) {
        HttpProxyFactory.valueResolver = valueResolver;
    }

    static <T> T newProxy(Class<T> clazz, HttpClient httpClient) throws UnsupportedEncodingException {
        int retryTimes = getInt(httpClient.retryTimes(), httpClient.retryTimesStr());
        int retryInterval = getInt(httpClient.retryInterval(), httpClient.retryIntervalStr());
        int connectTimeout = getInt(httpClient.connectTimeout(), httpClient.connectTimeoutStr());
        int readTimeout = getInt(httpClient.readTimeout(), httpClient.readTimeoutStr());
        ProxyFactoryBuilder proxyFactoryBuilder = new ProxyFactoryBuilder(new Options(connectTimeout, readTimeout, retryTimes, retryInterval));

        String[] interceptors = applicationContext.getBeanNamesForType(HttpClientInterceptor.class);
        for (String interceptorName : interceptors) {
            HttpClientInterceptor httpClientInterceptor = applicationContext.getBean(interceptorName, HttpClientInterceptor.class);
            proxyFactoryBuilder.addHttpInterceptor(httpClientInterceptor);
        }

        String[] handlers = applicationContext.getBeanNamesForType(HttpExceptionHandler.class);
        if (handlers.length > 0) {
            String handler = handlers[0];
            HttpExceptionHandler exceptionHandler = applicationContext.getBean(handler, HttpExceptionHandler.class);
            proxyFactoryBuilder.setExceptionHandler(exceptionHandler);
        }

        try {
            // encode / decoder
            proxyFactoryBuilder.setEncoder((HttpEncoder) httpClient.encoder().newInstance());
            proxyFactoryBuilder.setDecoder((HttpDecoder) httpClient.decoder().newInstance());
            // Https设置
            Class<? extends SSLSocketFactory> sslSocketFactoryClass = httpClient.sslSocketFactory();
            Class<? extends HostnameVerifier> hostnameVerifierClass = httpClient.hostnameVerifier();
            SSLSocketFactory SSLSocketFactory = sslSocketFactoryClass.newInstance();
            HostnameVerifier hostnameVerifier = hostnameVerifierClass.newInstance();
            proxyFactoryBuilder.setHttpExecutor(new HttpClientExecutor(SSLSocketFactory, hostnameVerifier));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ProxyFactory proxyFactory = proxyFactoryBuilder.newProxyFactory(httpClient.level(), httpClient.ignoreError());
        ProxyTarget<T> proxyTarget = new ProxyTarget<>(
                applicationContext, valueResolver, clazz, httpClient.name(), httpClient.url());
        return proxyFactory.newProxy(proxyTarget);
    }

    private static int getInt(int defaultValue, String regex) {
        if (!StringUtils.hasText(regex)) {
            return defaultValue;
        }
        return Integer.parseInt(Objects.requireNonNull(valueResolver.resolveStringValue(regex)));
    }
}
