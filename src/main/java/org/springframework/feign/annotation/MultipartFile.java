package org.springframework.feign.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * @author shanhuiming
 *
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface MultipartFile {

    String fileName();

    String boundary() default "----boundary";
}
