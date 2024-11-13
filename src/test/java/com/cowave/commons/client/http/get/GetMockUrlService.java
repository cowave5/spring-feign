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
@HttpClient(url = "http://127.0.0.1:8080")
public interface GetMockUrlService {

    @HttpHeaders({X_Request_ID + ": 12345"})
    @HttpLine("GET /api/v1/get/{id}")
    HttpResponse<Object> getWithHttpParam(@HttpParam("id") Integer id);

    @HttpLine("GET /api/v1/getByMap")
    HttpResponse<GetParam> getWithHttpParamMap(@HttpHeaderMap Map<String, Object> headMap, @HttpParamMap Map<String, Object> paramMap);
}
