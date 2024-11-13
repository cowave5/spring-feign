package com.cowave.commons.client.http.error;

import com.cowave.commons.client.http.annotation.*;
import com.cowave.commons.client.http.response.HttpResponse;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient(ignoreError = true)
public interface ErrorMockService {

    @HttpOptions(connectTimeout = 500)
    @HttpLine("GET /api/v1/get/{id}")
    HttpResponse<Void> error1(@HttpHost String httpUrl, @HttpParam("id") Integer id);
}
