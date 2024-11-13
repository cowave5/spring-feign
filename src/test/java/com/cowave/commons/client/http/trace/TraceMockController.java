package com.cowave.commons.client.http.trace;

import com.cowave.commons.client.http.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mock")
public class TraceMockController {

    private final TraceMockService traceMockService;

    @GetMapping("/trace1")
    public String trace1() {
        HttpResponse<String> httpResponse = traceMockService.trace();
        return httpResponse.getBody();
    }
}
