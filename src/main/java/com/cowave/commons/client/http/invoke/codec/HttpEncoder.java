package com.cowave.commons.client.http.invoke.codec;

import com.cowave.commons.client.http.request.HttpRequest;

/**
 *
 * @author shanhuiming
 *
 */
public interface HttpEncoder {

    void encode(HttpRequest request, Object object) throws Exception;
}
