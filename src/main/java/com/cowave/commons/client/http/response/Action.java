package com.cowave.commons.client.http.response;

/**
 *
 * @author shanhuiming
 *
 */
@FunctionalInterface
public interface Action {

    void exec() throws Exception;
}
