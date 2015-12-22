package com.yihu.ehr.pack.exception;

import com.yihu.ehr.constrant.ErrorCode;
import com.yihu.ehr.util.ApiException;
import com.yihu.ehr.util.RestEcho;
import com.yihu.ehr.util.log.LogService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 16:56
 */
@ControllerAdvice
public class ApiHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    public static RestEcho failed(ErrorCode errorCode, String...args){
        return new RestEcho().failed(errorCode, args);
    }

    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                              Exception ex) {
        LogService.getLogger().error(ex.getMessage(), ex);

        try {
            writeJsonResponse(ex, response);
            return new ModelAndView();
        } catch (Exception e) {
            LogService.getLogger().error("Error rendering json response!", e);
        }

        return null; // pass handle to default ExceptionResolver
    }

    private void writeJsonResponse(Exception ex, HttpServletResponse response)
            throws HttpMessageNotWritableException, IOException {
        response.setContentType("application/json;charset=utf-8");

        if (ex instanceof ApiException) {
            ApiException apiException = (ApiException)ex;

            String restEcho = failed(apiException.getErrorCode(), apiException.getMessage()).toString();
            response.getWriter().print(restEcho);
        } else if (ex instanceof IllegalArgumentException || ex instanceof MissingServletRequestParameterException) {
            String restEcho = failed(ErrorCode.InvalidParameter, ex.getMessage()).toString();
            response.getWriter().print(restEcho);
        } else {
            String restEcho = failed(ErrorCode.InvalidParameter, ex.getMessage()).toString();
            response.getWriter().print(restEcho);
        }
    }
}
