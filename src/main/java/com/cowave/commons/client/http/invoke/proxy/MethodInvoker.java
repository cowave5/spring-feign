package com.cowave.commons.client.http.invoke.proxy;

/**
 *
 * @author shanhuiming
 *
 */
public interface MethodInvoker {

    Object invoke(Object[] argv) throws Throwable;
}
