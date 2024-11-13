package com.cowave.commons.client.http.annotation;

import com.cowave.commons.client.http.request.HttpRequest;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A template parameter that can be applied to a Map that contains query
 * parameters, where the keys are Strings that are the parameter names and the
 * values are the parameter values. The queries specified by the map will be
 * applied to the request after all other processing, and will take precedence
 * over any previously specified query parameters. It is not necessary to
 * reference the parameter map as a variable. <br>
 * <br>
 * <pre>
 * ...
 * &#64;RequestLine("POST /servers")
 * void servers(&#64;HttpParamMap Map<String, String>);
 * ...
 *
 * &#64;RequestLine("GET /servers/{serverId}?count={count}")
 * void get(&#64;Param("serverId") String serverId, &#64;Param("count") int count, &#64;HttpParamMap Map<String, Object>);
 * ...
 * </pre>
 * The annotated parameter must be an instance of {@link Map}, and the keys must
 * be Strings. The query value of a key will be the value of its toString
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
 * {@link HttpRequest#query(String, String...)}.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
@Documented
public @interface HttpParamMap {

    boolean encoded() default false;
}
