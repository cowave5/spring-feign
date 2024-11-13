package com.cowave.commons.client.http.asserts;

import com.cowave.commons.client.http.response.ResponseCode;
import lombok.Getter;

/**
 * 不打印异常日志
 *
 * @author shanhuiming
 */
@Getter
public class HttpHintException extends HttpException {

    public HttpHintException(String message, Object... args) {
        super(message, args);
    }

    public HttpHintException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public HttpHintException(ResponseCode responseCode) {
        super(responseCode);
    }

    public HttpHintException(ResponseCode responseCode, String message, Object... args) {
       super(responseCode, message, args);
    }

    public HttpHintException(ResponseCode responseCode, Throwable cause, String message, Object... args) {
        super(responseCode, cause, message, args);
    }

    public HttpHintException(int status, String code) {
        super(status, code);
    }

    public HttpHintException(int status, String code, String message, Object... args) {
        super(status, code, message, args);
    }

    public HttpHintException(int status, String code, Throwable cause, String message, Object... args) {
        super(status, code, cause, message, args);
    }
}
