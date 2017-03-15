package com.yihu.ehr.portal.common;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.portal.service.common.PortalAuthClient;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * 切点类
 * @author hzp
 * @since 20170314
 */
@Aspect
@Component
public  class PortalAspect {
    //日志服务

    @Autowired
    private PortalAuthClient portalAuthClient;

    @Autowired
    private ObjectMapper objectMapper;

    //Controller层切点
    @Pointcut("@annotation(com.yihu.ehr.portal.common.RequestAccess)")
    public  void controllerAspect() {
    }

    /**
     * token校验
     */
    @Around("controllerAspect()")
    public Object checkToken(ProceedingJoinPoint point) throws Throwable {
        Object o = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        String error = "";
        try {
            /******************** 可记录日志 ******************************/
            System.out.print(getControllerMethodDescription(point));



            //校验权限
            boolean check = isNeedCheckToken(point);
            if(check)
            {
                String token = request.getHeader("token");
                String clientId = request.getHeader("clientId");
                if(!StringUtils.isEmpty(token)&&!StringUtils.isEmpty(clientId)) {
                    String re = portalAuthClient.validToken(clientId, token);
                    Result result = objectMapper.readValue(re,Result.class);
                    if(!result.isSuccessFlg())
                    {
                        error = result.getMessage();
                    }
                }
                else{
                    error = "非法调用，请根据制定规则调用接口！";
                }
            }

        }
        catch (Exception ex) {
            error = ex.getMessage();
        }

        if(!StringUtils.isEmpty(error))
        {
            response.setStatus(901);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = null;

            try {
                out = response.getWriter();
                out.append(error);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            //response.sendRedirect(ApiVersion.Version1_0 + "/portal/error?message="+ java.net.URLEncoder.encode(error));
        }
        else{
            o = point.proceed();
        }
        return o;
    }




    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public  String getControllerMethodDescription(JoinPoint joinPoint)  throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(RequestAccess.class).value();
                    break;
                }
            }
        }
        return description;
    }

    /**
     * 获取是否需要校验Token
     * @param joinPoint
     * @return
     * @throws Exception
     */
    public boolean isNeedCheckToken(JoinPoint joinPoint)  throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        boolean check = true;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    check = method.getAnnotation(RequestAccess.class).check();
                    break;
                }
            }
        }
        return check;
    }


    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     * @param joinPoint 切点
     */
    /*@Before("controllerAspect()")
    public  void doBefore(JoinPoint joinPoint) throws Exception  {

        try {
            String description = getControllerMethodDescription(joinPoint);
            if(!StringUtils.isEmpty(description)) {
                System.out.print("Description:" + description +"\r\n");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //读取session中的用户
        User user = (User) session.getAttribute(WebConstants.CURRENT_USER);
        //请求的IP
        String ip = request.getRemoteAddr();
        try {
            *//*//**//*========控制台输出=========*//**//*//*
            System.out.println("=====前置通知开始=====");
            System.out.println("请求方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            System.out.println("方法描述:" + getControllerMethodDescription(joinPoint));
            System.out.println("请求人:" + user.getName());
            System.out.println("请求IP:" + ip);
            *//*//**//*========数据库日志=========*//**//*//*
            Log log = SpringContextHolder.getBean("logxx");
            log.setDescription(getControllerMethodDescription(joinPoint));
            log.setMethod((joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            log.setType("0");
            log.setRequestIp(ip);
            log.setExceptionCode( null);
            log.setExceptionDetail( null);
            log.setParams( null);
            log.setCreateBy(user);
            log.setCreateDate(DateUtil.getCurrentDate());
            //保存数据库
            logService.add(log);
            System.out.println("=====前置通知结束=====");
        }  catch (Exception e) {
            //记录本地异常日志
            logger.error("==前置通知异常==");
            logger.error("异常信息:{}", e.getMessage());
        }
    }*/
}