package org.springframework.feign.annotation;

import org.slf4j.event.Level;
import org.springframework.core.annotation.AliasFor;
import org.springframework.feign.NULL;
import org.springframework.feign.codec.EJacksonDecoder;
import org.springframework.feign.codec.EJacksonEncoder;
import org.springframework.stereotype.Component;

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
@Component
public @interface FeignClient {

	@AliasFor(attribute = "url")
	String value() default "";

	@AliasFor(attribute = "value")
	String url() default "";

	Level level() default Level.WARN;

	boolean suppressError() default false;

	String name() default "";

	Class<?> encoder() default EJacksonEncoder.class;

	Class<?> decoder() default EJacksonDecoder.class;

	Class<?> sslSocketFactory() default NULL.class;

	Class<?> hostnameVerifier() default NULL.class;

	String sslPasswd() default "";

	String sslCertPath() default "";

	int connectTimeoutMillis() default 10000;

	String connectTimeoutMillisStr() default "";

	int readTimeoutMillis() default 60000;

	String readTimeoutMillisStr() default "";

	@Deprecated
	int maxAttempts() default 1;

	@Deprecated
	String maxAttemptsStr() default "";

	@Deprecated
	long period() default 1000;

	@Deprecated
	String periodStr() default "";

	@Deprecated
	long maxPeriod() default 1000;

	@Deprecated
	String maxPeriodStr() default "";
}
