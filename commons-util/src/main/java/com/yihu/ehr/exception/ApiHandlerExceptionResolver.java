package com.yihu.ehr.exception;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.log.LogService;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * API 错误返回方式：提取异常中的错误代码与格式化后的错误消息，组成 ApiErrorEcho 返回。若是 API 未发生错误，则直接返回结果的JSON对象。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 16:56
 */
@ControllerAdvice
public class ApiHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
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
        response.setStatus(403);

        if (ex instanceof ApiException) {
            ApiException apiException = (ApiException)ex;

            ApiErrorEcho errorEcho = new ApiErrorEcho(apiException.getErrorCode(), apiException.getErrMsg());
            response.getWriter().print(errorEcho.toString());
        } else if (ex instanceof IllegalArgumentException || ex instanceof MissingServletRequestParameterException) {
            ApiErrorEcho errorEcho = new ApiErrorEcho(ErrorCode.InvalidParameter, ex.getMessage());
            response.getWriter().print(errorEcho.toString());
        } else {
            ApiErrorEcho errorEcho = new ApiErrorEcho(ErrorCode.SystemError, ex.getMessage());
            response.getWriter().print(errorEcho.toString());
        }
    }
}
