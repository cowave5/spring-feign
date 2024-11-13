package com.cowave.commons.client.http.asserts;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
public class I18Messages {

    private static final ThreadLocal<Locale> LANGUAGE = new TransmittableThreadLocal<>();

    private static MessageSource messageSource;

    public static void setMessageSource(MessageSource messageSource) {
        I18Messages.messageSource = messageSource;
    }

    public static void setLanguage(String language){
        if(StringUtils.hasText(language)){
            if(language.toLowerCase().contains("en")) {
                LANGUAGE.set(new Locale("en", "US"));
            }else if(language.toLowerCase().contains("zh")) {
                LANGUAGE.set(new Locale("zh", "CN"));
            }
        }
    }

    public static Locale getLanguage() {
        Locale local = LANGUAGE.get();
        if(local != null){
            return local;
        }
        return Locale.CHINA;
    }

    public static void clearLanguage() {
        LANGUAGE.remove();
    }

    public static String msg(String message, Object... args) {
        return messageSource.getMessage(message, args, message, getLanguage());
    }

    public static String msgWithDefault(String message, String defaultMessage, Object... args) {
        return messageSource.getMessage(message, args, defaultMessage, getLanguage());
    }

    public static String translateIfNeed(String message, Object... args){
        if(!StringUtils.hasText(message)){
            return "";
        }
        if (message.startsWith("{") && message.endsWith("}")) {
            message = message.substring(1, message.length() - 1);
            return messageSource.getMessage(message, args, message, getLanguage());
        }
        return message;
    }

    public static String translateAndReplace(String message, Map<String, Object> args, String prefix, String suffix) {
        if(!StringUtils.hasText(message)){
            return "";
        }
        String msg = messageSource.getMessage(message, null, message, getLanguage());
        if(MapUtils.isNotEmpty(args)){
            StringSubstitutor substitutor = new StringSubstitutor(args, prefix, suffix);
            return substitutor.replace(msg);
        }
        return msg;
    }
}
