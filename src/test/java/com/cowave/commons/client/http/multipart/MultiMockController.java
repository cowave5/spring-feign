package com.cowave.commons.client.http.multipart;

import com.cowave.commons.client.http.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mock")
public class MultiMockController {

    private final MultiMockService multiMockService;

    @GetMapping("/multi1")
    public HttpResponse<Object> multi1() {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("id", 1);
        bodyMap.put("name", "xxx");
        return multiMockService.submit(bodyMap);
    }

    @GetMapping("/multi5")
    public HttpResponse<Object> multi5() {
        return multiMockService.submitParam(1, "xxx");
    }

    @GetMapping("/multi2")
    public HttpResponse<Object> multi2() throws IOException {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("id", 1);
        bodyMap.put("name", "xxx");
        return multiMockService.submit2(bodyMap, new ClassPathResource("file.txt").getFile());
    }

    @GetMapping("/multi3")
    public HttpResponse<Object> multi3() throws IOException {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("id", 1);
        bodyMap.put("name", "xxx");
        return multiMockService.submit3(bodyMap, new ClassPathResource("file.txt").getInputStream());
    }

    @GetMapping("/multi4")
    public HttpResponse<Object> multi4() throws IOException {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("id", 1);
        bodyMap.put("name", "xxx");
        return multiMockService.submit4(bodyMap, IOUtils.toByteArray(new ClassPathResource("file.txt").getInputStream()));
    }
}
