package com.cowave.commons.client.http.delete;

import com.cowave.commons.client.http.annotation.*;
import com.cowave.commons.client.http.invoke.codec.decoder.ResponseDecoder;
import com.cowave.commons.client.http.response.Response;

import java.util.List;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient(url = "http://127.0.0.1:8080", decoder = ResponseDecoder.class)
public interface DeleteMockService {

    @HttpLine("DELETE /api/v1/del/{list}")
    Response.Page<Integer> batchDelete(@HttpParam("list") List<Integer> list);

    @HttpLine("DELETE /api/v1/del")
    Response.Page<Integer> batchDelete2(@HttpParamMap Map<String, Object> paramMap);
}
