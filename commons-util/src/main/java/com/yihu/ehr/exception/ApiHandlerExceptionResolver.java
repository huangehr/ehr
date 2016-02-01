package com.yihu.ehr.exception;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.log.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * API 异常机制：如果是业务流程正常执行，则返回业务Model, 即以 M 开头的Java对象。
 * 若出现异常则执行以下流程：
 *  - 设置返回状态码为 403
 *  - 提取异常中的信息，若是业务主动抛出的异常，则异常类为 ApiException。
 *  - 若是Java运行时招聘的异常，异做其他处理，并将异常代码标识为 SystemError。
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
        response.setStatus(HttpStatus.FORBIDDEN.value());

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

            ApiErrorEcho errorEcho = new ApiErrorEcho(apiException.getErrorCode(), apiException.toString());
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
