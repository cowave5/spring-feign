package com.cowave.commons.client.http.invoke.proxy;

import com.cowave.commons.client.http.asserts.HttpHintException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import com.cowave.commons.client.http.HttpServiceChooser;
import com.cowave.commons.client.http.request.HttpRequestTemplate;
import com.cowave.commons.client.http.request.HttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@RequiredArgsConstructor
public class ProxyTarget<T> {
    private final ApplicationContext applicationContext;
    private final StringValueResolver valueResolver;
    private final Class<T> type;
    private final String name;
    private final String url;

    public HttpRequestTemplate apply(HttpRequest httpRequest, String hostUrl,
                                     int retryTimes, int retryInterval, int connectTimeout, int readTimeout) throws UnsupportedEncodingException {
        if(StringUtils.hasText(hostUrl)){
            httpRequest.insert(0, hostUrl);
            return httpRequest.requestTemplate(retryTimes, retryInterval, connectTimeout, readTimeout);
        }else{
            return apply(httpRequest, retryTimes, retryInterval, connectTimeout, readTimeout);
        }
    }

    public HttpRequestTemplate apply(HttpRequest httpRequest,
                                     int retryTimes, int retryInterval, int connectTimeout, int readTimeout) throws UnsupportedEncodingException {
        String prasedName = "";
        if(StringUtils.hasText(name)){
            HttpServiceChooser serviceChooser = applicationContext.getBean(HttpServiceChooser.class);
            prasedName = serviceChooser.choose(name);
        }

        String prasedUrl = url;
        if(StringUtils.hasText(url) && url.contains("${")){
            prasedUrl = valueResolver.resolveStringValue(url);
        }

        String parsed = prasedName + prasedUrl;
        if (httpRequest.url().indexOf("http") != 0) {
            if(parsed.indexOf("http") != 0){
                log.error(">< Remote failed due to illegal url, {}", parsed);
                throw new HttpHintException("Remote failed");
            }
            httpRequest.insert(0, parsed);
        }
        return httpRequest.requestTemplate(retryTimes, retryInterval, connectTimeout, readTimeout);
    }

    public Class<T> type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String url() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ProxyTarget<?> other = (ProxyTarget<?>) obj;
        return type.equals(other.type) && name.equals(other.name) && url.equals(other.url);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 3 * result + type.hashCode();
        result = 5 * result + name.hashCode();
        result = 7 * result + url.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "{type=" + type.getSimpleName() + ", name=" + name + ", url=" + url + "}";
    }
}
