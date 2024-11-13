package org.springframework.feign.invoke;

import feign.*;
import org.slf4j.event.Level;
import org.springframework.feign.FeignExceptionHandler;
import org.springframework.feign.codec.FeignDecoder;
import org.springframework.feign.invoke.method.FeignMethodMetadata;
import org.springframework.feign.invoke.template.FeignRequestFactory;

import java.util.List;

import static feign.Util.checkNotNull;

/**
 *
 * @author shanhuiming
 *
 */
public class FeignMethodHandlerFactory {
    private final Client client;
    private final org.springframework.feign.retryer.Retryer retryer;
    private final List<RequestInterceptor> requestInterceptors;

    private final FeignExceptionHandler exceptionHandler;

    FeignMethodHandlerFactory(Client client, org.springframework.feign.retryer.Retryer retryer,
                              List<RequestInterceptor> requestInterceptors, FeignExceptionHandler exceptionHandler) {
        this.client = checkNotNull(client, "client");
        this.retryer = checkNotNull(retryer, "retryer");
        this.requestInterceptors = checkNotNull(requestInterceptors, "requestInterceptors");
        this.exceptionHandler = exceptionHandler;
    }

    public InvocationHandlerFactory.MethodHandler create(Target<?> target, FeignMethodMetadata md,
                                                         FeignRequestFactory buildTemplateFromArgs, Request.Options options, FeignDecoder decoder,
                                                         Level level, boolean errorSuppress) {
        return new FeignSyncInvoker(target,
                client, retryer, requestInterceptors, md, buildTemplateFromArgs, options, decoder, level, errorSuppress, exceptionHandler);
    }
}
