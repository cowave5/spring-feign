package com.cowave.commons.client.http.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
@RequiredArgsConstructor
public class HttpResponseTemplate {
    private final int status;
    private final Map<String, Collection<String>> headers;
    private final String reason;
    private final InputStream inputStream;
    private final Integer length;
}
