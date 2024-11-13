package com.cowave.commons.client.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 超时参数，优先级高于@HttpClient
 *
 * @author shanhuiming
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface HttpOptions {

    int connectTimeout() default -1;

    int readTimeout() default -1;

    int retryTimes() default -1;

    int retryInterval() default -1;
}
