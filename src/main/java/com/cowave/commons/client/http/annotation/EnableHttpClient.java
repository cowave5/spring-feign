package com.cowave.commons.client.http.annotation;

import org.springframework.context.annotation.Import;
import com.cowave.commons.client.http.register.HttpClientBeanDefinitionRegistrar;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * @author shanhuiming
 *
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
@Import(HttpClientBeanDefinitionRegistrar.class)
public @interface EnableHttpClient {

}
