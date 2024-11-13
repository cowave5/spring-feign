package com.cowave.commons.client.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 服务地址，优先级高于@HttpClient，格式: http(s)://ip:port
 *
 * @author shanhuiming
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface HttpHost {

}
