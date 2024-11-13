package com.cowave.commons.client.http.request.meta;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 *
 * @author shanhuiming
 *
 */
public interface HttpMethodMetaParser {

    List<HttpMethodMeta> parse(Class<?> clazz) throws UnsupportedEncodingException;

    // Default methods are public non-abstract, non-synthetic, and non-static instance methods
    // declared in an interface.
    // method.isDefault() is not sufficient for our usage as it does not check
    // for synthetic methods.  As a result, it picks up overridden methods as well as actual default methods.
    static boolean isDefault(Method method) {
        final int SYNTHETIC = 0x00001000;
        return method.getDeclaringClass().isInterface() &&
                ((method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC | SYNTHETIC)) == Modifier.PUBLIC);
    }

    static String methodKey(Class<?> clazz, Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append(clazz.getSimpleName());
        builder.append('#').append(method.getName()).append('(');
        for (Type param : method.getGenericParameterTypes()) {
            param = Types.resolve(clazz, clazz, param);
            builder.append(Types.getRawType(param).getSimpleName()).append(',');
        }
        if (method.getParameterTypes().length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.append(')').toString();
    }
}
