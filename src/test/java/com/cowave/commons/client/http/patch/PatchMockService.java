package com.cowave.commons.client.http.patch;

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
public interface PatchMockService {

    @HttpHeaders({X_Request_ID + ": 12345"})
    @HttpLine("PATCH /api/v1/modify/{id}")
    HttpResponse<Object> modifyWithHttpParam(@HttpParam("id") Integer id, Map<String, Object> bodyMap);
}
