package com.cowave.commons.client.http.download;

import com.cowave.commons.client.http.annotation.HttpClient;
import com.cowave.commons.client.http.annotation.HttpLine;
import com.cowave.commons.client.http.response.HttpResponse;

import java.io.InputStream;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient(url = "http://127.0.0.1:8080")
public interface DownloadMockService {

    @HttpLine("GET /api/v1/download")
    HttpResponse<InputStream> download();
}
