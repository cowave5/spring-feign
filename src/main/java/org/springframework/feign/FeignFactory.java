package org.springframework.feign;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.feign.annotation.FeignClient;
import org.springframework.feign.invoke.FeignBuilder;
import org.springframework.lang.NonNull;
import org.springframework.util.StringValueResolver;

/**
 *
 * @author shanhuiming
 *
 */
public class FeignFactory<T> implements FactoryBean<T>, EmbeddedValueResolverAware, ApplicationContextAware {

    private Class<T> feignClass;

    private StringValueResolver valueResolver;

    private ApplicationContext applicationContext;

    public FeignFactory() {
    }

    public Class<T> getFeignClass() {
        return feignClass;
    }

    public void setFeignClass(Class<T> feignClass) {
        this.feignClass = feignClass;
    }

    @Override
    public void setEmbeddedValueResolver(@NonNull StringValueResolver valueResolver) {
        this.valueResolver = valueResolver;
        FeignManager.setStringValueResolver(valueResolver);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        FeignManager.setApplicationContext(applicationContext);
    }

    @Override
    public T getObject() {
        FeignClient feign = AnnotationUtils.getAnnotation(feignClass, FeignClient.class);
        assert feign != null;
        FeignBuilder builder = FeignManager.builder(feign);
        return builder.target(feignClass, feign.url(), feign.name(), applicationContext, valueResolver, feign.level(), feign.suppressError());
    }

    @Override
    public Class<?> getObjectType() {
        return feignClass;
    }
}
