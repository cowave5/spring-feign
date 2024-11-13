package com.cowave.commons.client.http.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
@RequiredArgsConstructor
public class HttpRequestTemplate {
    private final String method;
    private final String url;
    private final byte[] body;
    private final Charset charset;
    private final Map<String, Collection<String>> headers;
    private final int connectTimeout;
    private final int readTimeout;
    private final int retryTimes;
    private final int retryInterval;
    private final InputStream multiFile;
    private final String multiFileName;
    private final Map<String, Object> multiForm;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(method).append(' ').append(url).append(" HTTP/1.1\n");
        for (String field : headers.keySet()) {
            for (String value : valuesOrEmpty(headers, field)) {
                builder.append(field).append(": ").append(value).append('\n');
            }
        }
        if (body != null) {
            builder.append('\n').append(charset != null ? new String(body, charset) : "Binary data");
        }
        return builder.toString();
    }

    public static <T> Collection<T> valuesOrEmpty(Map<String, Collection<T>> map, String key) {
        return map.containsKey(key) && map.get(key) != null ? map.get(key) : Collections.emptyList();
    }
}
