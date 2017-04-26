package com.yihu.ehr.redis.common;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by hzp on 20170317.
 */
@Component
public class GlobalHandlerExceptionResolver implements HandlerExceptionResolver {

    /**
     * 在这里处理所有得异常信息
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object o, Exception ex) {
        ex.printStackTrace();
        String error = ex.getMessage();
        if (ex instanceof NullPointerException) {
            error = "业务判空异常";
        }
        printWrite(501,error, resp);
        return new ModelAndView();
    }

    /**
     * 将错误信息添加到response中
     */
    public static void printWrite(int status,String msg, HttpServletResponse response) {
        try {
            response.setStatus(status);
            PrintWriter pw = response.getWriter();
            pw.write(msg);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}