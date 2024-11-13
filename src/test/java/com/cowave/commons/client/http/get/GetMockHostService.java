package com.cowave.commons.client.http.get;

import com.cowave.commons.client.http.annotation.*;
import com.cowave.commons.client.http.response.HttpResponse;

import java.util.Map;

import static com.cowave.commons.client.http.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient
public interface GetMockHostService {

    @HttpHeaders({X_Request_ID + ": 12345"})
    @HttpLine("GET /api/v1/get/{id}")
    HttpResponse<Object> getWithHttpParam(@HttpHost String httpUrl, @HttpParam("id") Integer id);

    @HttpHeaders({X_Request_ID + ": {requestId}"})
    @HttpLine("GET /api/v1/getByMap")
    HttpResponse<GetParam> getWithHttpParamMap(@HttpHost String httpUrl, @HttpParam("requestId") String requestId, @HttpParamMap Map<String, Object> map);
}
