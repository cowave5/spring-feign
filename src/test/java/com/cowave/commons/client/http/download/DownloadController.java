package com.cowave.commons.client.http.download;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class DownloadController {

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        String filename = URLEncoder.encode("file.txt", "UTF-8").replace("\\+", "%20");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + filename + "\"");
        try (InputStream input = new ClassPathResource("file.txt").getInputStream();
             OutputStream outPut = response.getOutputStream();) {
            IOUtils.copy(input, outPut);
        }
    }
}
