package com.cowave.commons.client.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 请求行参数
 *
 * @author shanhuiming
 */
@Retention(RUNTIME)
@Target(PARAMETER)
@Documented
public @interface HttpParam {

    String value();
}
