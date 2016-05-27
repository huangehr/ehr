package com.yihu.ehr.util.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.PageArg;
import com.yihu.ehr.exception.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 控制器基类。提供模型转换，分页规范实现。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 17:01
 */
public class BaseRestEndPoint extends AbstractController {
    protected final static String ResourceCount = "X-Total-Count";
    protected final static String ResourceLink = "Link";

    @Autowired
    protected ObjectMapper objectMapper;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }

    /**
     * 将实体转换为模型。
     *
     * @param source
     * @param targetCls
     * @param properties
     * @param <T>
     * @return
     */
    public <T> T convertToModel(Object source, Class<T> targetCls, String... properties) {
        if (source == null) {
            return null;
        }
        T target = BeanUtils.instantiate(targetCls);
        BeanUtils.copyProperties(source, target, propertyDiffer(properties, targetCls));
        return target;
    }

    public <T> T toEntity(String json, Class<T> entityCls) {
        try {
            T entity = objectMapper.readValue(json, entityCls);
            return entity;
        } catch (IOException ex) {
            throw new ApiException(ErrorCode.SystemError, "Unable to parse json, " + ex.getMessage());
        }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 将实体集合转换为模型集合。
     *
     * @param sources
     * @param targets
     * @param properties
     * @param <T>
     * @return
     */
    public <T> Collection<T> convertToModels(Collection sources, Collection<T> targets, Class<T> targetCls, String properties) {
        if (sources == null) {
            return null;
        }
        Iterator iterator = sources.iterator();
        while (iterator.hasNext()) {
            Object source = iterator.next();

            T target = (T) BeanUtils.instantiate(targetCls);
            BeanUtils.copyProperties(source, target, propertyDiffer(StringUtils.isEmpty(properties) ? null : properties.split(","), targetCls));
            targets.add(target);
        }

        return targets;
    }

    /**
     * 计算不在类中的属性。
     *
     * @return
     */
    protected String[] propertyDiffer(String[] properties, Class targetCls) {
        if (properties == null || properties.length == 0) return null;

        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(targetCls);
        List<String> propertiesList = Arrays.asList(properties);
        List<String> arrayList = new ArrayList<>();

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && !propertiesList.contains(targetPd.getName())) {
                arrayList.add(targetPd.getName());
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);
    }

    /**
     * 客户端调用REST接口时，若返回的是分页结果，则需要在响应头中添加资源的总数量及其他资源的分页导航。
     * EHR平台使用响应头中的 X-Total-Count 字段记录资源的总数量，link header作为其他资源的分页导航。
     *
     * @return
     */
    public void pagedResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            Long resourceCount, Integer currentPage, Integer pageSize) {
        if (request == null || response == null) return;

        response.setHeader(ResourceCount, Long.toString(resourceCount));
        if (resourceCount == 0) return;

        if (currentPage == null) currentPage = new Integer(PageArg.DefaultPage);
        if (pageSize == null) pageSize = new Integer(PageArg.DefaultSize);


        String baseUri = "<" + request.getRequestURL().append("?").toString() + request.getQueryString() + ">";
        long firstPage = currentPage == 1 ? -1 : 1;
        long prevPage = currentPage == 1 ? -1 : currentPage - 1;

        long lastPage = resourceCount % pageSize == 0 ? resourceCount / pageSize : resourceCount / pageSize + 1;
        long nextPage = currentPage == lastPage ? -1 : currentPage + 1;

        lastPage = currentPage == lastPage ? -1 : lastPage;

        Map<String, String> map = new HashMap<>();
        if (firstPage != -1)
            map.put("rel='first',", baseUri.replaceAll("page=\\d+", "page=" + Long.toString(firstPage)));
        if (prevPage != -1) map.put("rel='prev',", baseUri.replaceAll("page=\\d+", "page=" + Long.toString(prevPage)));
        if (nextPage != -1) map.put("rel='next',", baseUri.replaceAll("page=\\d+", "page=" + Long.toString(nextPage)));
        if (lastPage != -1) map.put("rel='last',", baseUri.replaceAll("page=\\d+", "page=" + Long.toString(lastPage)));

        response.setHeader(ResourceLink, linkMap(map));
    }

    private String linkMap(Map<String, String> map) {
        StringBuffer links = new StringBuffer("");
        for (String key : map.keySet()) {
            links.append(map.get(key)).append("; ").append(key);
        }

        return links.toString();
    }

    protected Integer reducePage(Integer page) {
        if (page != null || page > 0) {
            page = page - 1;
            return page;
        }

        return 1;
    }

    protected String getClientId(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");

        return StringUtils.isEmpty(userAgent) ? "" : userAgent.split(" ")[1];
    }
}
