package com.cowave.commons.client.http.constants;

/**
 * @see <a href="https://http.dev/methods">GET, DELETE, POST, PATCH, PUT, HEAD, OPTIONS, TRACE, CONNECT</a>
 *
 * @author shanhuiming
 */
public interface HttpMethod {

    String GET = "GET";

    String DELETE = "DELETE";

    String POST = "POST";

    String PATCH = "PATCH";

    String PUT = "PUT";

    String HEAD = "HEAD";

    String OPTIONS = "OPTIONS";

    String TRACE = "TRACE";

    @Deprecated
    String CONNECT = "CONNECT";
}
