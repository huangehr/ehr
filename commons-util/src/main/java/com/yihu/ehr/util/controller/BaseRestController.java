package com.yihu.ehr.util.controller;

import com.yihu.ehr.util.Envelop;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private final static String ResourceCount = "X-Total-Count";
    private final static String ResourceLink = "Link";

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }

    /**
     * 将实体转换为模型。
     *
     * @param source
     * @param targetCls
     * @param ignoreProperties
     * @param <T>
     * @return
     */
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
        Class targetCls = null;

        Iterator iterator = sources.iterator();
        while (iterator.hasNext()) {
            Object source = iterator.next();
            if (targetCls == null) targetCls = source.getClass();

            T target = (T) BeanUtils.instantiate(targetCls);
            BeanUtils.copyProperties(source, target, ignoreProperties);
            targets.add(target);
        }

        return targets;
    }

    /**
     * 客户端调用REST接口时，若返回的是分页结果，则需要在响应头中添加资源的总数量及其他资源的分页导航。
     * EHR平台使用响应头中的 X-Total-Count 字段记录资源的总数量，link header作为其他资源的分页导航。
     *
     * @return
     */
    public void echoCollection(HttpServletResponse response, String baseUri, long resourceCount, long currentPage, long pageSize) {
        response.setHeader(ResourceCount, Long.toString(resourceCount));

        baseUri = "<" + baseUri + "page=:page&size=" + Long.toString(pageSize) + ">";

        long firstPage = currentPage == 1 ? -1 : new Long(1);
        long lastPage = currentPage == (resourceCount / pageSize + 1) ? -1 : resourceCount / pageSize + 1;
        long nextPage = currentPage == (resourceCount / pageSize + 1) ? -1 : currentPage + 1;
        long previousPage = currentPage == firstPage ? -1 : currentPage - 1;

        Map<String, String> map = new HashMap<>();
        if(firstPage != -1) map.put(baseUri.replace(":page", Long.toString(firstPage)), "rel=\"first\",");
        if(previousPage != -1) map.put(baseUri.replace(":page", Long.toString(firstPage)), "rel=\"prev\",");
        if(nextPage != -1) map.put(baseUri.replace(":page", Long.toString(firstPage)), "rel=\"next\",");
        if(lastPage != -1) map.put(baseUri.replace(":page", Long.toString(firstPage)), "rel=\"last\",");

        response.setHeader(ResourceLink, linkMap(map));
    }

    private String linkMap(Map<String, String> map){
        StringBuffer links = new StringBuffer("");
        for (String key : map.keySet()){
            links.append(key).append("; ").append(map.get(key));
        }

        return links.toString();
    }

    /**
     * 返回一个信封对象。信封对象的返回场景参见 Envelop.
     *
     * @param modelList
     * @param totalCount
     * @param currPage
     * @param rows
     * @return
     */
    protected Envelop getResult(List modelList, int totalCount, int currPage, int rows) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(modelList);
        envelop.setTotalCount(totalCount);

        return envelop;
    }
}
