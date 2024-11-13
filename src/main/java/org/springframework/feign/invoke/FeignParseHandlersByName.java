package org.springframework.feign.invoke;

import static feign.Util.checkNotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.event.Level;
import org.springframework.feign.codec.FeignDecoder;
import org.springframework.feign.invoke.method.FeignContract;
import org.springframework.feign.invoke.method.FeignMethodMetadata;
import org.springframework.feign.invoke.template.FeignBodyRequestFactory;
import org.springframework.feign.invoke.template.FeignFormRequestFactory;
import org.springframework.feign.invoke.template.FeignMultipartRequestFactory;
import org.springframework.feign.invoke.template.FeignRequestFactory;

import feign.InvocationHandlerFactory;
import feign.Request;
import feign.Target;
import feign.codec.Encoder;

/**
 *
 * @author shanhuiming
 *
 */
public class FeignParseHandlersByName {
    private final FeignContract contract;
    private final Request.Options options;
    private final Encoder encoder;
    private final FeignDecoder decoder;
    private final FeignMethodHandlerFactory factory;
    private final boolean errorSuppress;
    private Level level;

    FeignParseHandlersByName(FeignContract contract, Request.Options options, Encoder encoder, FeignDecoder decoder,
                        FeignMethodHandlerFactory factory, Level level, boolean errorSuppress) {
        this.contract = contract;
        this.options = options;
        this.factory = factory;
        this.encoder = checkNotNull(encoder, "encoder");
        this.decoder = checkNotNull(decoder, "decoder");
        this.errorSuppress = errorSuppress;
        this.level = level;
    }

    public Map<String, InvocationHandlerFactory.MethodHandler> apply(Target<?> key) {
        List<FeignMethodMetadata> metaList = contract.parseAndValidateMetadata(key.type());
        Map<String, InvocationHandlerFactory.MethodHandler> result = new LinkedHashMap<>();
        for (FeignMethodMetadata meta : metaList) {
            FeignRequestFactory feignRequestFactory;
            if(meta.multipartFileIndex() != null || meta.multipartFormIndex() != null){
                // multipart/form-data
                feignRequestFactory = new FeignMultipartRequestFactory(meta);
            } else if (!meta.formParams().isEmpty() && meta.template().bodyTemplate() == null) {
                // 存在表单参数，且body为null
                feignRequestFactory = new FeignFormRequestFactory(meta, encoder);
            } else if (meta.bodyIndex() != null) {
                // 存在body
                feignRequestFactory = new FeignBodyRequestFactory(meta, encoder);
            } else {
                feignRequestFactory = new FeignRequestFactory(meta);
            }
            result.put(meta.configKey(), factory.create(key, meta, feignRequestFactory, options, decoder, level, errorSuppress));
        }
        return result;
    }
}
