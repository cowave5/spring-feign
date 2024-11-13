package com.cowave.commons.client.http.head;

import com.cowave.commons.client.http.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cowave.commons.client.http.constants.HttpCode.SUCCESS;
import static com.cowave.commons.client.http.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class HeadController {

    @RequestMapping(value = "/head", method = RequestMethod.HEAD)
    public HttpResponse<Void> head(@RequestHeader(X_Request_ID) String requestId){
        return HttpResponse.header(SUCCESS.getStatus(), "X-id", requestId);
    }
}
