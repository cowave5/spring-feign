package com.cowave.commons.client.http.asserts;

import com.cowave.commons.client.http.response.ResponseCode;
import lombok.Getter;

import static com.cowave.commons.client.http.constants.HttpCode.SERVICE_ERROR;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
public class HttpException extends RuntimeException {

    private final int status;

    private final String code;

    public HttpException(String message, Object... args) {
        super(I18Messages.translateIfNeed(message, args));
        this.code = SERVICE_ERROR.getCode();
        this.status = SERVICE_ERROR.getStatus();
    }

    public HttpException(Throwable cause, String message, Object... args) {
        super(I18Messages.translateIfNeed(message, args), cause);
        this.code = SERVICE_ERROR.getCode();
        this.status = SERVICE_ERROR.getStatus();
    }

    public HttpException(ResponseCode responseCode) {
        super(I18Messages.translateIfNeed(responseCode.getMsg()));
        this.code = responseCode.getCode();
        this.status = responseCode.getStatus();
    }

    public HttpException(ResponseCode responseCode, String message, Object... args) {
        super(I18Messages.translateIfNeed(message, args));
        this.code = responseCode.getCode();
        this.status = responseCode.getStatus();
    }

    public HttpException(ResponseCode responseCode, Throwable cause, String message, Object... args) {
        super(I18Messages.translateIfNeed(message, args), cause);
        this.code = responseCode.getCode();
        this.status = responseCode.getStatus();
    }

    public HttpException(int status, String code) {
        this.code = code;
        this.status = status;
    }

    public HttpException(int status, String code, String message, Object... args) {
        super(I18Messages.translateIfNeed(message, args));
        this.code = code;
        this.status = status;
    }

    public HttpException(int status, String code, Throwable cause, String message, Object... args) {
        super(I18Messages.translateIfNeed(message, args), cause);
        this.code = code;
        this.status = status;
    }
}
