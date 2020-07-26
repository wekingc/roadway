package com.weking.navigator.exceptions;


/**
 * 自定义错误类型
 * @author Jim Cen
 */
public class HttpException extends RuntimeException {
    private final int statusCode;
    public HttpException(String message,int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public HttpException(String message) {
        super(message);
        this.statusCode = 500;
    }
    public int getStatusCode() {
        return statusCode;
    }
}
