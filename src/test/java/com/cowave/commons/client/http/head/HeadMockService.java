package com.cowave.commons.client.http.head;

import com.cowave.commons.client.http.annotation.*;
import com.cowave.commons.client.http.response.HttpResponse;

import static com.cowave.commons.client.http.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient(url = "http://127.0.0.1:8080")
public interface HeadMockService {

    @HttpHeaders({X_Request_ID + ": 12345"})
    @HttpLine("HEAD /api/v1/head")
    HttpResponse<Void> head();
}
