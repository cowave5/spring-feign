package org.springframework.feign.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 *
 * @author shanhm1991@163.com
 *
 */
public class HttpsHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(final String s, final SSLSession sslSession) {
		return true;
	}
}
