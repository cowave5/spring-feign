package com.cowave.commons.client.http.post;

import com.cowave.commons.client.http.annotation.HttpClient;
import com.cowave.commons.client.http.annotation.HttpHeaders;
import com.cowave.commons.client.http.annotation.HttpLine;
import com.cowave.commons.client.http.invoke.codec.decoder.ResponseDecoder;

import java.util.Map;

import static com.cowave.commons.client.http.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient(url = "http://127.0.0.1:8080", decoder = ResponseDecoder.class)
public interface PostMockDecodeService {

    @HttpHeaders({X_Request_ID + ": 12345"})
    @HttpLine("POST /api/v1/create")
    PostBody create(Map<String, Object> bodyMap);
}
