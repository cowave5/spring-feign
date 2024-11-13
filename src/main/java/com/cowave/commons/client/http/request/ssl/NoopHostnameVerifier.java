package com.cowave.commons.client.http.request.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 *
 * @author shanhuiming
 *
 */
public class NoopHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(final String s, final SSLSession sslSession) {
		return true;
	}

	@Override
    public final String toString() {
        return "NO_OP";
    }
}
