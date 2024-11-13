package com.cowave.commons.client.http.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import static com.cowave.commons.client.http.constants.HttpCode.SUCCESS;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
@Setter
public class HttpResponse<T> extends ResponseEntity<T> {

    @JsonIgnore
    private String message;

    @JsonIgnore
    private Throwable cause;

    @JsonIgnore
    private final int status;

    public HttpResponse(){
        super(HttpStatus.OK);
        this.status = HttpStatus.OK.value();
    }

    public HttpResponse(ResponseCode responseCode){
        super(null, null, responseCode.getStatus());
        this.status = responseCode.getStatus();
    }

    public HttpResponse(ResponseCode responseCode, MultiValueMap<String, String> headers, T body) {
        super(body, headers, responseCode.getStatus());
        this.status = responseCode.getStatus();
    }

    public HttpResponse(ResponseCode responseCode, MultiValueMap<String, String> headers, T body, String message) {
        super(body, headers, responseCode.getStatus());
        this.message = message;
        this.status = responseCode.getStatus();
    }

    public HttpResponse(int httpStatus, MultiValueMap<String, String> headers, T body) {
        super(body, headers, httpStatus);
        this.status = httpStatus;
    }

    public HttpResponse(int httpStatus, MultiValueMap<String, String> headers, T body, String message) {
        super(body, headers, httpStatus);
        this.message = message;
        this.status = httpStatus;
    }

    /**
     * 获取Http Header
     */
    public String getHeader(String headerName){
        HttpHeaders headers = this.getHeaders();
        List<String> list = headers.get(headerName);
        if(list == null || list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取Http Header
     */
    public List<String> getHeaders(String headerName){
        HttpHeaders headers = this.getHeaders();
        return headers.get(headerName);
    }

    /**
     * status == 200
     */
    public boolean isSuccess(){
        return getStatusCodeValue() == 200;
    }

    /**
     * status != 200
     */
    public boolean isFailed(){
        return getStatusCodeValue() != 200;
    }

    /**
     * status=#{responseCode.status}, body=null
     */
    public static HttpResponse<Void> header(int status, String key, String... values){
        HttpHeaders headers = new HttpHeaders();
        headers.put(key, Arrays.asList(values));
        return new HttpResponse<>(status, headers, null);
    }

    /**
     * status=#{responseCode.status}, body=#{responseCode.msg}
     */
    public static HttpResponse<Object> code(ResponseCode responseCode) {
        return new HttpResponse<>(responseCode, null, responseCode.getMsg());
    }

    /**
     * status=#{responseCode.status}, body=#{data}
     */
    public static <V> HttpResponse<V> body(ResponseCode responseCode, V data) {
        return new HttpResponse<>(responseCode, null, data);
    }

    /**
     * status=200, body=null
     */
    public static <V> HttpResponse<V> success(){
        return new HttpResponse<>(SUCCESS, null, null);
    }

    /**
     * status=200, body=null
     */
    public static <V> HttpResponse<V> success(Action action) throws Exception {
        if (action != null) {
            action.exec();
        }
        return success();
    }

    /**
     * status=200, body=#{data}
     */
    public static <V> HttpResponse<V> success(V data) {
        return new HttpResponse<>(SUCCESS, null, data);
    }

    @Override
    public HttpStatus getStatusCode(){
        for (HttpStatus statusEnum : HttpStatus.values()) {
            if (statusEnum.value() == this.status) {
                return statusEnum;
            }
        }

        if(this.status >= HttpStatus.INTERNAL_SERVER_ERROR.value()){
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }else if(this.status >= HttpStatus.BAD_REQUEST.value()){
            return HttpStatus.BAD_REQUEST;
        }else if(this.status >= HttpStatus.MULTIPLE_CHOICES.value()){
            return HttpStatus.MULTIPLE_CHOICES;
        }else if(this.status >= HttpStatus.OK.value()){
            return HttpStatus.OK;
        }else{
            return HttpStatus.CONTINUE;
        }
    }
}
