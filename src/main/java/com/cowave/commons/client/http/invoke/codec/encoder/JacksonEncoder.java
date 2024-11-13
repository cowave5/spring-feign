package com.cowave.commons.client.http.invoke.codec.encoder;

import com.cowave.commons.client.http.invoke.codec.HttpEncoder;
import com.cowave.commons.client.http.request.HttpRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.TimeZone;

/**
 * @author shanhuiming
 */
public class JacksonEncoder implements HttpEncoder {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setTimeZone(TimeZone.getDefault());
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private final ObjectMapper mapper;

    public JacksonEncoder() {
        this(MAPPER);
    }

    public JacksonEncoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void encode(HttpRequest request, Object object) throws JsonProcessingException {
        request.body(mapper.writeValueAsString(object));
    }
}
