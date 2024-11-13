package com.cowave.commons.client.http.annotation;

import com.cowave.commons.client.http.request.ssl.NoopHostnameVerifier;
import com.cowave.commons.client.http.request.ssl.NoopTlsSocketFactory;
import org.slf4j.event.Level;
import org.springframework.core.annotation.AliasFor;
import com.cowave.commons.client.http.invoke.codec.decoder.JacksonDecoder;
import com.cowave.commons.client.http.invoke.codec.encoder.JacksonEncoder;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
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
public @interface HttpClient {

	@AliasFor(attribute = "url")
	String value() default "";

	@AliasFor(attribute = "value")
	String url() default "";

	String name() default "";

	int poolConnections() default 10;

	int connectTimeout() default 10000;

	String connectTimeoutStr() default "";

	int readTimeout() default 60000;

	String readTimeoutStr() default "";

	int retryTimes() default 0;

	String retryTimesStr() default "";

	int retryInterval() default 1000;

	String retryIntervalStr() default "";

	Class<?> encoder() default JacksonEncoder.class;

	Class<?> decoder() default JacksonDecoder.class;

	Class<? extends SSLSocketFactory> sslSocketFactory() default NoopTlsSocketFactory.class;

	Class<? extends HostnameVerifier> hostnameVerifier() default NoopHostnameVerifier.class;

	Level level() default Level.INFO;

	boolean ignoreError() default false;
}
