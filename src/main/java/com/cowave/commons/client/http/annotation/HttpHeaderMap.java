package com.cowave.commons.client.http.annotation;

import com.cowave.commons.client.http.HttpClientInterceptor;
import com.cowave.commons.client.http.request.HttpRequest;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A template parameter that can be applied to a Map that contains header
 * entries, where the keys are Strings that are the header field names and the
 * values are the header field values. The headers specified by the map will be
 * applied to the request after all other processing, and will take precedence
 * over any previously specified header parameters.
 * <br>
 * This parameter is useful in cases where different header fields and values
 * need to be set on an API method on a per-request basis in a thread-safe manner
 * and independently of client construction. A concrete example of a case
 * like this are custom metadata header fields (e.g. as "x-amz-meta-*" or
 * "x-goog-meta-*") where the header field names are dynamic and the range of keys
 * cannot be determined a priori. The {@link HttpHeaders} annotation does not allow this
 * because the header fields that it defines are static (it is not possible to add or
 * remove fields on a per-request basis), and doing this using a custom {@link Target}
 * or {@link HttpClientInterceptor} can be cumbersome (it requires more code for per-method
 * customization, it is difficult to implement in a thread-safe manner and it requires
 * customization when the client for the API is built).
 * <br>
 * <pre>
 * ...
 * &#64;RequestLine("GET /servers/{serverId}")
 * void get(&#64;Param("serverId") String serverId, &#64;HttpHeaderMap Map<String, Object>);
 * ...
 * </pre>
 * The annotated parameter must be an instance of {@link Map}, and the keys must
 * be Strings. The header field value of a key will be the value of its toString
 * method, except in the following cases:
 * <br>
 * <br>
 * <ul>
 * <li>if the value is null, the value will remain null (rather than converting
 * to the String "null")
 * <li>if the value is an {@link Iterable}, it is converted to a {@link List} of
 * String objects where each value in the list is either null if the original
 * value was null or the value's toString representation otherwise.
 * </ul>
 * <br>
 * Once this conversion is applied, the query keys and resulting String values
 * follow the same contract as if they were set using
 * {@link HttpRequest#header(String, String...)}.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
@Documented
public @interface HttpHeaderMap {

}
