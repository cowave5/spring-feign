package com.cowave.commons.client.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Expands the request-line supplied in the {@code value}, permitting path and query variables, or
 * just the http method. <br>
 * <pre>
 * ...
 * &#64;HttpLine("POST /servers")
 * ...
 *
 * &#64;HttpLine("GET /servers/{serverId}?count={count}")
 * void get(&#64;Param("serverId") String serverId, &#64;Param("count") int count);
 * ...
 *
 * &#64;HttpLine("GET")
 * Response getNext(URI nextLink);
 * ...
 * </pre>
 * HTTP version suffix is optional, but permitted.  There are no guarantees this version will impact
 * that sent by the client. <br>
 * <pre>
 * &#64;HttpLine("POST /servers HTTP/1.1")
 * ...
 * </pre>
 * <br> <strong>Note:</strong> Query params do not overwrite each other. All queries with the same
 * name will be included in the request. <br><br><b>Relationship to JAXRS</b><br> <br> The following
 * two forms are identical. <br>
 * <pre>
 * &#64;HttpLine("GET /servers/{serverId}?count={count}")
 * void get(&#64;Param("serverId") String serverId, &#64;Param("count") int count);
 * ...
 * </pre>
 * <br>
 * <pre>
 * &#64;GET &#64;Path("/servers/{serverId}")
 * void get(&#64;PathParam("serverId") String serverId, &#64;QueryParam("count") int count);
 * ...
 * </pre>
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface HttpLine {

    String value();

    boolean decodeSlash() default true;
}
