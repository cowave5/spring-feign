package com.cowave.commons.client.http.delete;

import com.cowave.commons.client.http.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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
public class DeleteMockController {

    private final DeleteMockService deleteMockService;

    @GetMapping("/delete1")
    public Response.Page<Integer> delete1() {
        return deleteMockService.batchDelete(Arrays.asList(2, 3, 4, 5, 6));
    }

    @GetMapping("/delete2")
    public Response.Page<Integer> delete2() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ids", Arrays.asList(2, 3, 4, 5, 6));
        return deleteMockService.batchDelete2(paramMap);
    }
}
