package com.cowave.commons.client.http.multipart;

import com.cowave.commons.client.http.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class MultiController {

    @PostMapping("/submit")
    public Response<MultiBody> submit(MultiBody multiBody){
        return Response.success(multiBody);
    }

    @PostMapping("/submit2")
    public Response<MultiBody> submit2(MultiBody multiBody, MultipartFile file) throws IOException {
        multiBody.setFileContent(new String(file.getBytes()));
        return Response.success(multiBody);
    }

    @PostMapping("/submit3")
    public Response<MultiBody> submit3(MultiBody multiBody, MultipartFile file) throws IOException {
        multiBody.setFileName(file.getOriginalFilename());
        multiBody.setFileContent(new String(file.getBytes()));
        return Response.success(multiBody);
    }
}
