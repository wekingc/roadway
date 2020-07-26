package com.weking.navigator.exceptions.handlers;

import com.weking.core.services.interfaces.LogService;
import com.weking.navigator.exceptions.HttpException;
import com.weking.core.models.ResponseResult;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jim Cen
 * @date 2020/7/14 14:21
 */
public class GlobalExceptionHandler  extends DefaultErrorWebExceptionHandler {

    private final LogService logService;

    /**
     * Create a new {@code DefaultErrorWebExceptionHandler} instance.
     *
     * @param errorAttributes    the error attributes
     * @param resourceProperties the resources configuration properties
     * @param errorProperties    the error configuration properties
     * @param applicationContext the current application context
     * @param logService the message logger
     */
    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                  ErrorProperties errorProperties, ApplicationContext applicationContext,
                                  LogService logService) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
        this.logService = logService;
    }


    /**
     * 异常显示的信息
     * HttpClient连接异常的记录
     * @param request 请求
     * @param options 选项
     * @return 结果
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String,Object> errorAttribute = super.getErrorAttributes(request,options);
        int code = (int)errorAttribute.get("status");
        Throwable error = super.getError(request);

        logService.log(LogService.Level.ERROR,error.getMessage());

        String errorMessage;
        if(error instanceof HttpException) {
            errorMessage = error.getMessage();
            code =((HttpException) error).getStatusCode();
        }
        else {
            if(code == 0 ){
                code = ResponseResult.FAILED;
            }
            errorMessage = error.getClass().getName() + ":" + error.getMessage();
        }

        Map<String, Object> map = new HashMap<>(4);
        map.put("code", code);
        map.put("message", errorMessage);
        map.put("data", null);
        return map;
    }

    /***
     * 异常处理的情况
     * @param errorAttributes 属性
     * @return 新路由
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 永远是正常返回
     * @param errorAttributes 属性
     * @return 返回http代号
     */
    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return HttpStatus.OK.value();
    }
}
