package com.yihu.ehr.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler
    @ResponseBody
    public Envelop handle(HttpServletResponse response, Exception e) throws IOException {
        Envelop envelop = new Envelop();
        if(e instanceof NoHandlerFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            envelop.setErrorCode(HttpStatus.NOT_FOUND.value());
            envelop.setErrorMsg(e.getMessage());
        }else if(e instanceof HttpRequestMethodNotSupportedException){
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
            envelop.setErrorCode(HttpStatus.METHOD_NOT_ALLOWED.value());
            envelop.setErrorMsg(e.getMessage());
        }else if(e instanceof MissingServletRequestParameterException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            envelop.setErrorCode(HttpStatus.BAD_REQUEST.value());
            envelop.setErrorMsg(e.getMessage());
        }else if(e instanceof HystrixRuntimeException) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            envelop.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            String message = e.getCause().getMessage();
            if(message.indexOf("{") != -1) {
                String content = message.substring(message.indexOf("{"));
                envelop = objectMapper.readValue(content, Envelop.class);
                envelop.setErrorMsg(message.substring(0, message.indexOf(";") + 1) + " Caused by: " + envelop.getErrorMsg());
            }else {
                envelop.setErrorMsg(message);
            }
        }else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            envelop.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            envelop.setErrorMsg(e.getMessage());
        }
        e.printStackTrace();
        return envelop;
    }

}
