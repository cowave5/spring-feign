package org.springframework.feign.invoke;

import com.cowave.commons.response.exception.HttpHintException;
import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.feign.FeignServiceChooser;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
public class FeignTarget<T> implements Target<T> {
    private final Class<T> type;
    private final String name;
    private final String url;
    private final ApplicationContext applicationContext;
    private final StringValueResolver valueResolver;

    public FeignTarget(Class<T> type, String name, String url,
                       ApplicationContext applicationContext, StringValueResolver valueResolver) {
        this.type = type;
        this.name = name;
        this.url = url;
        this.applicationContext = applicationContext;
        this.valueResolver = valueResolver;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String url() {
        return url;
    }

    public Request apply(RequestTemplate request, String protocolUrl) {
        if(StringUtils.hasText(protocolUrl)){
            request.insert(0, protocolUrl);
            return request.request();
        }else{
            return apply(request);
        }
    }

    @Override
    public Request apply(RequestTemplate request) {
        String prasedName = "";
        if(StringUtils.hasText(name)){
            FeignServiceChooser serviceChooser = applicationContext.getBean(FeignServiceChooser.class);
            prasedName = serviceChooser.choose(name);
        }

        String prasedUrl = url;
        if(StringUtils.hasText(url) && url.contains("${")){
            prasedUrl = valueResolver.resolveStringValue(url);
        }

        String parsed = prasedName + prasedUrl;
        if (request.url().indexOf("http") != 0) {
            if(parsed.indexOf("http") != 0){
                log.error(">< Remote failed due to illegal url, {}", parsed);
                throw new HttpHintException("{frame.remote.failed}");
            }
            request.insert(0, parsed);
        }
        return request.request();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FeignTarget<?> other) {
            return type.equals(other.type) && name.equals(other.name) && url.equals(other.url);
        }
        return false;
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
