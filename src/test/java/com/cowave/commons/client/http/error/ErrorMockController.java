package com.cowave.commons.client.http.error;

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
public class ErrorMockController {

    private final ErrorMockService errorMockService;

    @GetMapping("/error1")
    public String error1() {
        return errorMockService.error1("http://127.0.1.1:8080", 5).getMessage();
    }
}
