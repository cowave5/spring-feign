package com.cowave.commons.client.http;

import com.cowave.commons.client.http.asserts.HttpException;

/**
 *
 * @author shanhuiming
 *
 */
public interface HttpExceptionHandler {

    void handle(HttpException e);
}
