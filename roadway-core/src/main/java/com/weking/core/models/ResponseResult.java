package com.weking.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Jim Cen
 * @date 2020/7/13 15:23
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ResponseResult {
    public final static int OK = 200;
    public final static int FAILED = 600;
    public final static int UNAUTHORIZED = 601;
    public ResponseResult() {
        this.code = OK;
    }
    /**
     * HttpStatus对应的Code
     */
    private int code;
    /**
     * 对应成功，失败的提示
     */
    private String message;
    /**
     * 成功时返回的信息，失败时为null
     */
    private Object data;
}

