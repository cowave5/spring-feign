package com.cowave.commons.client.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 表单参数，仅支持参数类型：Map<String, Object>
 *
 * @author shanhuiming
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface HttpMultiForm {

}
