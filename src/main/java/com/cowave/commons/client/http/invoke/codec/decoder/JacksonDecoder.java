package com.cowave.commons.client.http.invoke.codec.decoder;

import com.cowave.commons.client.http.response.HttpResponseTemplate;
import com.cowave.commons.client.http.invoke.codec.HttpDecoder;
import com.cowave.commons.client.http.response.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.TimeZone;

import static com.cowave.commons.client.http.constants.HttpCode.SUCCESS;
import static org.slf4j.event.Level.WARN;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
public class JacksonDecoder implements HttpDecoder {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setTimeZone(TimeZone.getDefault());
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final ObjectMapper mapper;

    public JacksonDecoder() {
        this(MAPPER);
    }

    public JacksonDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object decode(HttpResponseTemplate response, Type type, String url, long cost, int status, Level level) throws Exception {
        Reader reader = new InputStreamReader(response.getInputStream(), StandardCharsets.UTF_8);
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader, 1);
        }

        reader.mark(1);
        if (reader.read() == -1) {
            return null;
        }
        reader.reset();

        if (void.class == type) {
            if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
                log.info(">< {} {}ms {}", status, cost, url);
            }
            return null;
        }

        Object obj = mapper.readValue(reader, mapper.constructType(type));
        if(obj != null){
            if(Response.class.isAssignableFrom(obj.getClass())){
                Response<?> resp = (Response<?>)obj;
                if(!Objects.equals(SUCCESS.getCode(), resp.getCode())){
                    log.error(">< {} {}ms {} {code={}, msg={}}", status, cost, url, resp.getCode(), resp.getMsg());
                }else if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
                    log.info(">< {} {}ms {} {code={}, msg={}}", status, cost, url, resp.getCode(), resp.getMsg());
                }
            }else if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
                log.info(">< {} {}ms {}", status, cost, url);
            }
        }else if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
            log.info(">< {} {}ms {}", status, cost, url);
        }
        return obj;
    }

    public static Object readValue(String json, Type type) throws IOException {
        return MAPPER.readValue(json, MAPPER.constructType(type));
    }
}
