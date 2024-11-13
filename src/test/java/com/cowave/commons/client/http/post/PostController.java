package com.cowave.commons.client.http.post;

import com.cowave.commons.client.http.response.Response;
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
public class PostController {

    @PostMapping("/create")
    public Response<PostBody> create(@RequestBody PostBody postBody){
        return Response.success(postBody);
    }
}
