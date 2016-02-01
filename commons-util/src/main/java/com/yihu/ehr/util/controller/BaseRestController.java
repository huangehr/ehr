package com.yihu.ehr.util.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.StringBuilderUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * REST风格控控制器基类。此控制器用于对API进行校验，并处理平台根层级的业务，如API参数校验，错误及返回码设定等。
 * <p>
 * 根层级的校验，如果是正确的，直接返回HTTP代码200，若出错，则会将HTTP返回代码设置为1X或2X，并在HTTP响应体中包含响应的信息。
 * HTTP响应体格式为JSON。
 * + 成功：会根据各业务逻辑自行决定要返回的数据，各业务模块的返回结构不同。
 * + 失败：{"code":"错误代码", "message":"错误原因"}
 *
 * @author zhiyong
 * @author Sand
 */
@Controller
@RequestMapping("/rest")
public class BaseRestController extends AbstractController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }

    public <T> T convertToModel(Object source, Class<T> targetCls, String... ignoreProperties) {
        T target = BeanUtils.instantiate(targetCls);
        BeanUtils.copyProperties(source, target, ignoreProperties);

        return target;
    }

    /**
     * 将实体集合转换为模型集合。
     *
     * @param sources
     * @param targets
     * @param ignoreProperties
     * @param <T>
     * @return
     */
    public <T> Collection<T> convertToModels(Collection sources, Collection<T> targets, String... ignoreProperties) {
        Class<T> targetCls = (Class<T>)((ParameterizedType)targets.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        Iterator iterator = sources.iterator();
        while(iterator.hasNext()){
            Object source = iterator.next();

            T target = BeanUtils.instantiate(targetCls);
            BeanUtils.copyProperties(source, target, ignoreProperties);
            targets.add(target);
        }

        return targets;
    }
}
