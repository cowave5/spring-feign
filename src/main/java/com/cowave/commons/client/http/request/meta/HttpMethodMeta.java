package com.cowave.commons.client.http.request.meta;

import com.cowave.commons.client.http.request.HttpRequest;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.*;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
@Setter
public class HttpMethodMeta {
    private final HttpRequest httpRequest = new HttpRequest();
    private final Map<Integer, Collection<String>> paramIndexName = new LinkedHashMap<>();
    private final Map<Integer, String> multiParamIndexName = new LinkedHashMap<>();
    private final List<String> multipartParams = new ArrayList<>();
    private Integer urlIndex;
    private Integer bodyIndex;
    private Integer headerMapIndex;
    private Integer paramMapIndex;
    private Integer hostIndex;
    private Integer multipartFileIndex;
    private Integer multipartFormIndex;
    private String multipartFileName;
    private String multipartFileBoundary;
    private String methodKey;
    private boolean paramMapEncoded;
    private transient Type bodyType;
    private transient Type returnType;
    private int connectTimeout = -1;
    private int readTimeout = -1;
    private int retryTimes;
    private int retryInterval;
}
