package com.yihu.ehr.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.PageArg;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
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
public class BaseRestController extends AbstractController {
    private final static String ResourceCount = "X-Total-Count";
    private final static String ResourceLink = "Link";

    @Value("${admin.region}")
    Short adminRegion = 3502;
    @Autowired
    protected EntityManager entityManager;
    @Autowired
    ObjectMapper objectMapper;

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
        T target = BeanUtils.instantiate(targetCls);
        BeanUtils.copyProperties(source, target, propertyDiffer(properties, targetCls));

        return target;
    }

    public <T> T toEntity(String json, Class<T> entityCls) {
        try {
            T entity = objectMapper.readValue(json, entityCls);
            return entity;
        } catch (IOException ex) {
            throw new ApiException(ErrorCode.SystemError,  "无法转换json, " + ex.getMessage());
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
        Iterator iterator = sources.iterator();
        while (iterator.hasNext()) {
            Object source = iterator.next();

            T target = (T) BeanUtils.instantiate(targetCls);
            BeanUtils.copyProperties(source, target, propertyDiffer(properties == null ? null : properties.split(","), targetCls));
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
        if (page != null) {
            page = page - 1;
            return page;
        }

        return 1;
    }

    /**
     * 返回一个信封对象。信封对象的返回场景参见 Envelop.
     *
     * @param modelList
     * @param totalCount
     * @return
     */
    protected Envelop getResult(List modelList, int totalCount) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(modelList);
        envelop.setTotalCount(totalCount);

        return envelop;
    }

    protected String getObjectId(BizObject bizObject){
        return new ObjectId(adminRegion, bizObject).toString();
    }
}
