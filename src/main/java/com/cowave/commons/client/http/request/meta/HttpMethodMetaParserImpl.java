package com.cowave.commons.client.http.request.meta;

import com.cowave.commons.client.http.annotation.*;
import com.cowave.commons.client.http.asserts.Asserts;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;

/**
 *
 * @author shanhuiming
 *
 */
public class HttpMethodMetaParserImpl implements HttpMethodMetaParser {

    @Override
    public List<HttpMethodMeta> parse(Class<?> clazz) throws UnsupportedEncodingException {
        Asserts.isTrue(clazz.getTypeParameters().length == 0,
                "Parameterized types unsupported: " + clazz.getSimpleName());
        Asserts.isTrue(clazz.getInterfaces().length <= 1,
                "Only single inheritance supported: " + clazz.getSimpleName());
        if (clazz.getInterfaces().length == 1) {
            Asserts.isTrue(clazz.getInterfaces()[0].getInterfaces().length == 0,
                    "Only single-level inheritance supported: " + clazz.getSimpleName());
        }

        Map<String, HttpMethodMeta> result = new LinkedHashMap<>();
        for (Method method : clazz.getMethods()) {
            if (method.getDeclaringClass() == Object.class
                    || (method.getModifiers() & Modifier.STATIC) != 0
                    || HttpMethodMetaParser.isDefault(method)) {
                continue;
            }

            // 解析方法
            HttpMethodMeta metadata = parseAndValidateMetadata(clazz, method);
            Asserts.isTrue(!result.containsKey(metadata.getMethodKey()),
                    "Overrides unsupported: " + metadata.getMethodKey());
            result.put(metadata.getMethodKey(), metadata);
        }
        return new ArrayList<>(result.values());
    }

    protected HttpMethodMeta parseAndValidateMetadata(Class<?> targetType, Method method) throws UnsupportedEncodingException {
        HttpMethodMeta metadata = new HttpMethodMeta();
        metadata.setReturnType(Types.resolve(targetType, targetType, method.getGenericReturnType()));
        metadata.setMethodKey(HttpMethodMetaParser.methodKey(targetType, method));

        // class注解
        if(targetType.getInterfaces().length == 1) {
            processAnnotationOnClass(metadata, targetType.getInterfaces()[0]);
        }
        processAnnotationOnClass(metadata, targetType);

        // method注解
        for (Annotation methodAnnotation : method.getAnnotations()) {
            processAnnotationOnMethod(metadata, methodAnnotation, method);
        }
        Asserts.isTrue(metadata.getHttpRequest().method() != null,
                "Method " + method.getName() + " not annotated with HTTP method type (ex. GET, POST)");

        // parameter注解
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            boolean isHttpAnnotation = false;
            if (parameterAnnotations[i] != null) {
                isHttpAnnotation = processAnnotationsOnParameter(metadata, parameterAnnotations[i], i);
            }

            if (parameterTypes[i] == URI.class) {
                metadata.setUrlIndex(i);
            } else if (!isHttpAnnotation) {
                Asserts.isTrue(metadata.getBodyIndex() == null, "Method has too many Body parameters: " + method);
                metadata.setBodyIndex(i);
                metadata.setBodyType(Types.resolve(targetType, targetType, method.getGenericParameterTypes()[i]));
            }
        }

        // 校验
        if (metadata.getHeaderMapIndex() != null) {
            Asserts.isTrue(Map.class.isAssignableFrom(parameterTypes[metadata.getHeaderMapIndex()]),
                    "HttpHeaderMap parameter must be a Map: " + parameterTypes[metadata.getHeaderMapIndex()]);
        }
        if (metadata.getParamMapIndex() != null) {
            Asserts.isTrue(Map.class.isAssignableFrom(parameterTypes[metadata.getParamMapIndex()]),
                    "HttpParamMap parameter must be a Map: " + parameterTypes[metadata.getParamMapIndex()]);
        }
        if (metadata.getMultipartFormIndex() != null) {
            Asserts.isTrue(Map.class.isAssignableFrom(parameterTypes[metadata.getMultipartFormIndex()]),
                    "HttpMultiForm parameter must be a Map: " + parameterTypes[metadata.getMultipartFormIndex()]);
        }
        return metadata;
    }

    protected void processAnnotationOnClass(HttpMethodMeta data, Class<?> targetType) {
        if (targetType.isAnnotationPresent(HttpHeaders.class)) {
            String[] headersOnType = targetType.getAnnotation(HttpHeaders.class).value();
            Asserts.isTrue(headersOnType.length > 0,
                    "Headers annotation was empty on type " + targetType.getName());

            Map<String, Collection<String>> headers = toMap(headersOnType);
            headers.putAll(data.getHttpRequest().headers());
            data.getHttpRequest().headers(null); // to clear
            data.getHttpRequest().headers(headers);
        }
    }

    protected void processAnnotationOnMethod(HttpMethodMeta metadata, Annotation methodAnnotation, Method method) throws UnsupportedEncodingException {
        Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
        if (annotationType == HttpLine.class) {
            String httpLine = ((HttpLine) methodAnnotation).value();
            Asserts.notBlank(httpLine, "RequestLine annotation was empty on method " + method.getName());

            if (httpLine.indexOf(' ') == -1) {
                Asserts.isTrue(httpLine.indexOf('/') == -1,
                        "RequestLine annotation didn't start with an HTTP verb on method " + method.getName());
                metadata.getHttpRequest().method(httpLine);
                return;
            }

            metadata.getHttpRequest().method(httpLine.substring(0, httpLine.indexOf(' ')));
            if (httpLine.indexOf(' ') == httpLine.lastIndexOf(' ')) {
                // no HTTP version is ok
                metadata.getHttpRequest().append(httpLine.substring(httpLine.indexOf(' ') + 1));
            } else {
                // skip HTTP version
                metadata.getHttpRequest().append(httpLine.substring(httpLine.indexOf(' ') + 1, httpLine.lastIndexOf(' ')));
            }
            metadata.getHttpRequest().decodeSlash(((HttpLine) methodAnnotation).decodeSlash());

        } else if (annotationType == HttpBody.class) {
            String body = ((HttpBody) methodAnnotation).value();
            Asserts.notBlank(body, "Body annotation was empty on method " + method.getName());

            if (body.indexOf('{') == -1) {
                metadata.getHttpRequest().body(body);
            } else {
                metadata.getHttpRequest().bodyTemplate(body);
            }
        } else if (annotationType == HttpHeaders.class) {
            String[] headersOnMethod = ((HttpHeaders) methodAnnotation).value();
            Asserts.isTrue(headersOnMethod.length > 0,
                    "Headers annotation was empty on method " + method.getName());
            metadata.getHttpRequest().headers(toMap(headersOnMethod));
        } else if (annotationType == HttpOptions.class) {
            HttpOptions httpOptions = (HttpOptions) methodAnnotation;
            metadata.setReadTimeout(httpOptions.readTimeout());
            metadata.setConnectTimeout(httpOptions.connectTimeout());
            metadata.setRetryTimes(httpOptions.retryTimes());
            metadata.setRetryInterval(httpOptions.retryInterval());
        }
    }

    protected boolean processAnnotationsOnParameter(HttpMethodMeta metadata, Annotation[] annotations, int paramIndex) {
        boolean isHttpAnnotation = false;
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == HttpParam.class) {
                String paramName = ((HttpParam) annotation).value();
                Asserts.notBlank(paramName, "HttpParam annotation was empty on param " + paramIndex);
                Collection<String> paramNames =
                        metadata.getParamIndexName().containsKey(paramIndex) ? metadata.getParamIndexName().get(paramIndex) : new ArrayList<>();
                paramNames.add(paramName);
                metadata.getParamIndexName().put(paramIndex, paramNames);
                isHttpAnnotation = true;
            } else if (annotationType == HttpParamMap.class) {
                Asserts.isNull(metadata.getParamMapIndex(),
                        "ParamMap annotation was present on multiple parameters.");
                metadata.setParamMapIndex(paramIndex);
                metadata.setParamMapEncoded(((HttpParamMap) annotation).encoded());
                isHttpAnnotation = true;
            } else if (annotationType == HttpHeaderMap.class) {
                Asserts.isNull(metadata.getHeaderMapIndex(),
                        "HeaderMap annotation was present on multiple parameters.");
                metadata.setHeaderMapIndex(paramIndex);
                isHttpAnnotation = true;
            } else if (annotationType == HttpHost.class) {
                metadata.setHostIndex(paramIndex);
                isHttpAnnotation = true;
            } else if (annotationType == HttpMultiForm.class) {
                metadata.setMultipartFormIndex(paramIndex);
                isHttpAnnotation = true;
            } else if (annotationType == HttpMultiFile.class) {
                HttpMultiFile httpMultiFile = (HttpMultiFile) annotation;
                metadata.setMultipartFileIndex(paramIndex);
                metadata.setMultipartFileName(httpMultiFile.fileName());
                metadata.setMultipartFileBoundary(httpMultiFile.boundary());
                isHttpAnnotation = true;
            } else if (annotationType == HttpMultiParam.class) {
                String paramName = ((HttpMultiParam) annotation).value();
                Asserts.notBlank(paramName, "HttpMultiParam annotation was empty on param " + paramIndex);
                metadata.getMultiParamIndexName().put(paramIndex, paramName);
                metadata.getMultipartParams().add(paramName); // 判断标记
                isHttpAnnotation = true;
            }
        }
        return isHttpAnnotation;
    }

    private static Map<String, Collection<String>> toMap(String[] input) {
        Map<String, Collection<String>> result = new LinkedHashMap<>(input.length);
        for (String header : input) {
            int colon = header.indexOf(':');
            String name = header.substring(0, colon);
            if (!result.containsKey(name)) {
                result.put(name, new ArrayList<>(1));
            }
            result.get(name).add(header.substring(colon + 2));
        }
        return result;
    }
}
