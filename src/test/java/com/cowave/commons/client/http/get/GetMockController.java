package com.cowave.commons.client.http.get;

import com.cowave.commons.client.http.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.cowave.commons.client.http.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mock")
public class GetMockController {

    private final GetMockUrlService getMockUrlService;

    private final GetMockNameService getMockNameService;

    private final GetMockHostService getMockHostService;

    @GetMapping("/get1")
    public HttpResponse<Object> urlGet1() {
        return getMockUrlService.getWithHttpParam(6);
    }

    @GetMapping("/get2")
    public HttpResponse<GetParam> urlGet2() {
        Map<String, Object> headMap = new HashMap<>();
        headMap.put(X_Request_ID, "23456");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 1);
        paramMap.put("name", "xxx");
        return getMockUrlService.getWithHttpParamMap(headMap, paramMap);
    }

    @GetMapping("/get3")
    public HttpResponse<Object> nameGet1() {
        return getMockNameService.getWithHttpParam(6);
    }

    @GetMapping("/get4")
    public HttpResponse<GetParam> nameGet2() {
        Map<String, Object> headMap = new HashMap<>();
        headMap.put(X_Request_ID, "23456");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", 1);
        paramMap.put("name", "xxx");
        return getMockNameService.getWithHttpParamMap(headMap, paramMap);
    }

    @GetMapping("/get5")
    public HttpResponse<Object> hostGet1() {
        return getMockHostService.getWithHttpParam("http://127.0.0.1:8080", 5);
    }

    @GetMapping("/get6")
    public HttpResponse<GetParam> hostGet2() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "xxx");
        return getMockHostService.getWithHttpParamMap("http://127.0.0.1:8080", "23456", map);
    }
}
