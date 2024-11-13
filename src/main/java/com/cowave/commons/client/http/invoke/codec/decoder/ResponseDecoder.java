package com.cowave.commons.client.http.invoke.codec.decoder;

import com.cowave.commons.client.http.response.HttpResponseTemplate;
import com.cowave.commons.client.http.response.Response;
import com.cowave.commons.client.http.asserts.HttpHintException;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import com.cowave.commons.client.http.invoke.codec.HttpDecoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.cowave.commons.client.http.constants.HttpCode.SUCCESS;
import static org.slf4j.event.Level.WARN;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
public class ResponseDecoder implements HttpDecoder {

    private final ObjectMapper mapper;

    public ResponseDecoder() {
        this.mapper = JacksonDecoder.MAPPER;
    }

    public ResponseDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object decode(HttpResponseTemplate response, Type type, String url, long cost, int status, Level level) throws Exception {
        if (response.getInputStream() == null) {
            if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
                log.info(">< {} {}ms {}", status, cost, url);
            }
            return null;
        }

        Reader reader = new InputStreamReader(response.getInputStream(), StandardCharsets.UTF_8);
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader, 1);
        }

        // Read the first byte to see if we have any data
        reader.mark(1);
        // Eagerly returning null avoids "No content to map due to end-of-input"
        if (reader.read() == -1) {
            if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
                log.info(">< {} {}ms {}", status, cost, url);
            }
            return null;
        }
        reader.reset();

        Response<?> resp = mapper.readValue(reader, Response.class);
        if(!Objects.equals(SUCCESS.getCode(), resp.getCode())){
            log.error(">< {} {}ms {} {code={}, msg={}}", status, cost, url, resp.getCode(), resp.getMsg());
            throw new HttpHintException(status, resp.getCode(), resp.getMsg());
        }

        if (void.class == type) {
            if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
                log.info(">< {} {}ms {} {code={}, msg={}}", status, cost, url, resp.getCode(), resp.getMsg());
            }
            return null;
        }

        if(log.isDebugEnabled() || level.toInt() < WARN.toInt()){
            log.info(">< {} {}ms {} {code={}, msg={}}", status, cost, url, resp.getCode(), resp.getMsg());
        }
        String data = mapper.writeValueAsString(resp.getData());
        return mapper.readValue(data, mapper.constructType(type));
    }
}
