package com.cowave.commons.client.http.put;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class PutController {

    @PutMapping("/put")
    public PutBody put1(@RequestBody PutBody putBody){
        return putBody;
    }
}
