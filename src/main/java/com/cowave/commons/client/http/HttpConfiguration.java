package com.cowave.commons.client.http;

import com.cowave.commons.client.http.annotation.EnableHttpClient;
import com.cowave.commons.client.http.asserts.I18Messages;
import org.springframework.context.MessageSource;

import javax.annotation.Resource;

/**
 *
 * @author shanhuiming
 *
 */
@EnableHttpClient
public class HttpConfiguration {

    @Resource
    public void setMessageSource(MessageSource messageSource) {
        I18Messages.setMessageSource(messageSource);
    }
}
