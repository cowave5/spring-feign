package com.cowave.commons.client.http.put;

import com.cowave.commons.client.http.annotation.HttpClient;
import com.cowave.commons.client.http.annotation.HttpLine;
import com.cowave.commons.client.http.response.HttpResponse;

import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient(url = "http://127.0.0.1:8080")
public interface PutMockService {

    @HttpLine("PUT /api/v1/put")
    HttpResponse<Object> put1(Map<String, Object> bodyMap);
}
