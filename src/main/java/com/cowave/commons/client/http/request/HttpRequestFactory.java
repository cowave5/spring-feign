package com.cowave.commons.client.http.request;

import com.cowave.commons.client.http.request.meta.HttpMethodMeta;
import com.cowave.commons.client.http.asserts.Asserts;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *
 * @author shanhuiming
 *
 */
public class HttpRequestFactory {
    protected final HttpMethodMeta metadata;

    public HttpRequestFactory(HttpMethodMeta metadata) {
        this.metadata = metadata;
    }

    public HttpRequest create(Object[] args) throws Exception {
        HttpRequest httpRequest = new HttpRequest(metadata.getHttpRequest());
        // 处理hostUrl
        if (metadata.getUrlIndex() != null) {
            int urlIndex = metadata.getUrlIndex();
            Asserts.isTrue(args[urlIndex] != null, "URI parameter " + urlIndex + " was null");
            httpRequest.insert(0, String.valueOf(args[urlIndex]));
        }

        Map<String, Object> paramMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Collection<String>> entry : metadata.getParamIndexName().entrySet()) {
            Object paramValue = args[entry.getKey()];
            if (paramValue != null) {
                paramValue = expandElements(paramValue);
                for (String paramName : entry.getValue()) {
                    paramMap.put(paramName, paramValue);
                }
            }
        }

        Map<String, Object> multiParamMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : metadata.getMultiParamIndexName().entrySet()) {
            Object paramValue = args[entry.getKey()];
            multiParamMap.put(entry.getValue(), paramValue);
        }

        // 请求模板
        HttpRequest resolvedRequest = resolve(args, httpRequest, paramMap, multiParamMap);

        if (metadata.getParamMapIndex() != null) {
            addParamMapParameters(args, resolvedRequest);
        }

        if (metadata.getHeaderMapIndex() != null) {
            addHeaderMapHeaders(args, resolvedRequest);
        }
        return resolvedRequest;
    }

    @SuppressWarnings("rawtypes")
    private Object expandElements(Object value) {
        if (value instanceof Iterable) {
            return expandIterable((Iterable) value);
        }
        return value.toString();
    }

    @SuppressWarnings("rawtypes")
    private String expandIterable(Iterable value) {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = value.iterator();
        while (iterator.hasNext()) {
            Object element = iterator.next();
            if (element != null) {
                builder.append(element);
            }
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private HttpRequest addHeaderMapHeaders(Object[] argv, HttpRequest template) {
        Map<Object, Object> headerMap = (Map<Object, Object>) argv[metadata.getHeaderMapIndex()];
        for (Map.Entry<Object, Object> currEntry : headerMap.entrySet()) {
            Asserts.isTrue(currEntry.getKey().getClass() == String.class,
                    "HeaderMap key must be a String: " + currEntry.getKey());
            Collection<String> values = new ArrayList<String>();
            Object currValue = currEntry.getValue();
            if (currValue instanceof Iterable<?>) {
                Iterator<?> iter = ((Iterable<?>) currValue).iterator();
                while (iter.hasNext()) {
                    Object nextObject = iter.next();
                    values.add(nextObject == null ? null : nextObject.toString());
                }
            } else {
                values.add(currValue == null ? null : currValue.toString());
            }

            template.header((String) currEntry.getKey(), values);
        }
        return template;
    }

    @SuppressWarnings("unchecked")
    private HttpRequest addParamMapParameters(Object[] argv, HttpRequest httpRequest) throws UnsupportedEncodingException {
        Map<Object, Object> paramMap = (Map<Object, Object>) argv[metadata.getParamMapIndex()];
        for (Map.Entry<Object, Object> currEntry : paramMap.entrySet()) {
            Asserts.isTrue(currEntry.getKey().getClass() == String.class,
                    "ParamMap key must be a String: " + currEntry.getKey());
            Collection<String> values = new ArrayList<>();
            Object currValue = currEntry.getValue();
            if (currValue instanceof Iterable<?>) {
                Iterator<?> iter = ((Iterable<?>) currValue).iterator();
                while (iter.hasNext()) {
                    Object nextObject = iter.next();
                    values.add(nextObject == null ? null : nextObject.toString());
                }
            } else {
                values.add(currValue == null ? null : currValue.toString());
            }
            httpRequest.query(metadata.isParamMapEncoded(), (String) currEntry.getKey(), values);
        }
        return httpRequest;
    }

    protected HttpRequest resolve(Object[] argv, HttpRequest httpRequest,
                                  Map<String, Object> variables, Map<String, Object> multiParams) throws Exception {
        // 参数拼接
        httpRequest.resolve(variables);

        // 解析一下hostUrl
        if (metadata.getHostIndex() != null) {
            Object url = argv[metadata.getHostIndex()];
            httpRequest.setHostUrl(url.toString());
        }
        return httpRequest;
    }
}
