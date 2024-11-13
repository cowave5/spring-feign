package com.cowave.commons.client.http.invoke.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
public class ProxyInvoker implements InvocationHandler {

    @SuppressWarnings("rawtypes")
    private final ProxyTarget proxyTarget;

    private final Map<Method, MethodInvoker> methodInvokeHandlerMap;

    @SuppressWarnings("rawtypes")
    ProxyInvoker(ProxyTarget proxyTarget, Map<Method, MethodInvoker> methodInvokeHandlerMap) {
        this.proxyTarget = proxyTarget;
        this.methodInvokeHandlerMap = methodInvokeHandlerMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }
        return methodInvokeHandlerMap.get(method).invoke(args);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProxyInvoker) {
            ProxyInvoker other = (ProxyInvoker) obj;
            return proxyTarget.equals(other.proxyTarget);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return proxyTarget.hashCode();
    }

    @Override
    public String toString() {
        return proxyTarget.toString();
    }
}
