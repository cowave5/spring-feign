package com.cowave.commons.client.http.invoke.exec;

import com.cowave.commons.client.http.request.HttpRequestTemplate;
import com.cowave.commons.client.http.response.HttpResponseTemplate;

import java.io.IOException;

/**
 *
 * @author shanhuiming
 *
 */
public interface HttpExecutor {

    HttpResponseTemplate execute(HttpRequestTemplate httpRequestTemplate) throws IOException;
}
