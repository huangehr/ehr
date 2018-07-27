package com.yihu.ehr.exception;

import com.yihu.ehr.util.rest.Envelop;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler - EHR全局错误
 * Created by progr1mmer on 2018/1/25.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.application.name}")
    private String appName;

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    @ResponseBody
    public Envelop handle(HttpServletResponse response, Exception e) throws IOException {
        Envelop envelop = new Envelop();
        if (e instanceof NoHandlerFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            envelop.setErrorCode(HttpStatus.NOT_FOUND.value());
            envelop.setErrorMsg(e.getMessage());
        } else if (e instanceof HttpRequestMethodNotSupportedException){
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            envelop.setErrorCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            envelop.setErrorMsg(e.getMessage());
        } else if (e instanceof MissingServletRequestParameterException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            envelop.setErrorCode(HttpStatus.BAD_REQUEST.value());
            envelop.setErrorMsg(e.getMessage());
        } else if (e instanceof FeignException) { //执行Feign失败的时候默认当前服务做为网关执行请求的时候，从上游服务器接收到无效的响应
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
            envelop.setErrorCode(HttpStatus.BAD_GATEWAY.value());
            envelop.setErrorMsg("Execute Feign: " + e.getMessage());
        } else if (e instanceof ApiException) { //业务逻辑的异常默认为请求成功，但是前端需要判断成功标识
            ApiException apiException = (ApiException) e;
            response.setStatus(apiException.httpStatus().value());
            envelop.setErrorCode(apiException.errorCode().value());
            envelop.setErrorMsg(e.getMessage());
            return envelop; //此异常不进行日志记录
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            envelop.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            envelop.setErrorMsg(e.getMessage());
        }
        logger.error(e.getMessage(), e);
        return envelop;
    }

}
