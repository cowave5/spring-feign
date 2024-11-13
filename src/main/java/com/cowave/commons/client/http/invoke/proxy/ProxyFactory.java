package com.cowave.commons.client.http.invoke.proxy;

import com.cowave.commons.client.http.request.meta.HttpMethodMetaParser;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
public class ProxyFactory {

    private final HttpMethodInvokerFactory httpMethodInvokerFactory;

    public ProxyFactory(HttpMethodInvokerFactory httpMethodInvokerFactory) {
        this.httpMethodInvokerFactory = httpMethodInvokerFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T newProxy(ProxyTarget<T> proxyTarget) throws UnsupportedEncodingException {
        // 默认方法
        List<DefaultMethodInvoker> defaultProxyInvokerList = new LinkedList<>();

        // Http调用方法
        Map<String, MethodInvoker> httpProxyInvokerMap = httpMethodInvokerFactory.create(proxyTarget);

        Map<Method, MethodInvoker> methodInvokeHandlerMap = new LinkedHashMap<>();
        for (Method method : proxyTarget.type().getMethods()) {
            if (method.getDeclaringClass() == Object.class) {
                // object方法不进行代理
            } else if (HttpMethodMetaParser.isDefault(method)) {
                DefaultMethodInvoker handler = new DefaultMethodInvoker(method);
                defaultProxyInvokerList.add(handler);
                methodInvokeHandlerMap.put(method, handler);
            } else {
                methodInvokeHandlerMap.put(
                        method, httpProxyInvokerMap.get(HttpMethodMetaParser.methodKey(proxyTarget.type(), method)));
            }
        }

        // 创建代理
        T proxy = (T) Proxy.newProxyInstance(
                proxyTarget.type().getClassLoader(),
                new Class<?>[]{proxyTarget.type()},
                new ProxyInvoker(proxyTarget, methodInvokeHandlerMap));
        for (DefaultMethodInvoker methodHandler : defaultProxyInvokerList) {
            methodHandler.bindTo(proxy);
        }
        return proxy;
    }
}
