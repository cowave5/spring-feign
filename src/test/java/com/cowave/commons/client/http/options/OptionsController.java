package com.cowave.commons.client.http.options;

import com.cowave.commons.client.http.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
public class OptionsController {

    @RequestMapping(value = "/options", method = RequestMethod.OPTIONS)
    public HttpResponse<Void> head(@RequestHeader(X_Request_ID) String requestId){
        return HttpResponse.header(SUCCESS.getStatus(), "X-id", requestId);
    }
}
