package com.cowave.commons.client.http.asserts;

import com.cowave.commons.client.http.response.ResponseCode;
import lombok.Getter;

/**
 * 异常日志只打印e.message
 *
 * @author shanhuiming
 */
@Getter
public class HttpWarnException extends HttpException {

    public HttpWarnException(String message, Object... args) {
        super(message, args);
    }

    public HttpWarnException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public HttpWarnException(ResponseCode responseCode) {
        super(responseCode);
    }

    public HttpWarnException(ResponseCode responseCode, String message, Object... args) {
       super(responseCode, message, args);
    }

    public HttpWarnException(ResponseCode responseCode, Throwable cause, String message, Object... args) {
        super(responseCode, cause, message, args);
    }

    public HttpWarnException(int status, String code) {
        super(status, code);
    }

    public HttpWarnException(int status, String code, String message, Object... args) {
        super(status, code, message, args);
    }

    public HttpWarnException(int status, String code, Throwable cause, String message, Object... args) {
        super(status, code, cause, message, args);
    }
}
