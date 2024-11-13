package com.cowave.commons.client.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 文件参数，仅支持参数类型：InputStream、File、MultipartFile、byte[]
 *
 * @author shanhuiming
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface HttpMultiFile {

    String fileName();

    String boundary() default "----boundary";
}
