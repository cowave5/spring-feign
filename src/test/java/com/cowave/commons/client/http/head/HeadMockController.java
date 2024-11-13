package com.cowave.commons.client.http.head;

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
public class HeadMockController {

    private final HeadMockService headMockService;

    @GetMapping("/head1")
    public String head1() {
        HttpResponse<Void> httpResponse = headMockService.head();
        return httpResponse.getHeader("X-id");
    }
}
