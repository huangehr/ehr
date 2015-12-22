package com.yihu.ehr.pack.exception;

import com.yihu.ehr.constrant.ErrorCode;
import com.yihu.ehr.util.ApiException;
import com.yihu.ehr.util.RestEcho;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.20 16:04
 */

public class GenericExceptionHandler {
    public static Object failed(ErrorCode errorCode, String...args){
        return new RestEcho().failed(errorCode, args);
    }

    @ExceptionHandler(ApiException.class)
    public ModelAndView handleCustomException(ApiException ex) {
        ModelAndView model = new ModelAndView();
        model.addObject("error_code", ex.getErrorCode());
        model.addObject("error_msg", ex.getErrMsg());

        return model;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex) {
        ModelAndView model = new ModelAndView();
        model.addObject("error_msg", "this is Exception.class");

        return model;
    }
}


