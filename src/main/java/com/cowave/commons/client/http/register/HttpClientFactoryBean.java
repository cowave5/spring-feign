package com.cowave.commons.client.http.register;

import com.cowave.commons.client.http.annotation.HttpClient;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringValueResolver;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author shanhuiming
 *
 */
@Setter
public class HttpClientFactoryBean<T> implements FactoryBean<T>, EmbeddedValueResolverAware, ApplicationContextAware {

    private Class<T> targetClass;

    public HttpClientFactoryBean() {

    }

    @Override
    public T getObject() throws UnsupportedEncodingException {
        HttpClient httpClient = AnnotationUtils.getAnnotation(targetClass, HttpClient.class);
        return HttpProxyFactory.newProxy(targetClass, httpClient);
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }

    @Override
    public void setEmbeddedValueResolver(@NonNull StringValueResolver valueResolver) {
        HttpProxyFactory.setStringValueResolver(valueResolver);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        HttpProxyFactory.setApplicationContext(applicationContext);
    }
}
