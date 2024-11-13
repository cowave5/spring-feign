package com.cowave.commons.client.http.get;

import com.cowave.commons.client.http.asserts.Asserts;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.cowave.commons.client.http.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class GetController {

    @GetMapping("/get/{id}")
    public Integer getById(@PathVariable Integer id, @RequestHeader(X_Request_ID) String xId){
        Asserts.equals(xId, "12345", "");
        return id;
    }

    @GetMapping("/getByMap")
    public GetParam getByMap(GetParam getParam, @RequestHeader(X_Request_ID) String xId){
        Asserts.equals(xId, "23456", "");
        return getParam;
    }
}
