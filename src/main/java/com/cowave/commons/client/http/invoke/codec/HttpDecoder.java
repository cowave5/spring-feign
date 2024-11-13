package com.cowave.commons.client.http.invoke.codec;

import com.cowave.commons.client.http.response.HttpResponseTemplate;
import org.slf4j.event.Level;

import java.lang.reflect.Type;

/**
 *
 * @author shanhuiming
 *
 */
public interface HttpDecoder {

    Object decode(HttpResponseTemplate response, Type type, String url, long cost, int httpCode, Level level) throws Exception;
}
