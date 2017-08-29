package com.yihu.ehr.analysis.service;

import com.google.gson.Gson;
import com.yihu.ehr.analysis.util.HttpClientUtil;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/7/18.
 */
@Service
public class AppFeatureService {

    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    /**
     * 根据url查询对应的菜单信息
     *
     * @param url
     * @return
     */
    public Object appFeatureFindUrl(String url) {
        String rqUrl = "/AppFeatureFindUrl";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(url)) {
            stringBuffer.append("url=" + url);
        }
        params.put("filters", "");
        String filters = stringBuffer.toString();
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        try {
            resultStr = HttpClientUtil.doGet(comUrl + rqUrl, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }

    }

    /**
     * 根据parentId查询对应菜单信息
     *
     * @param parentId
     * @return
     */
    public Object appFeatureFindParentId(String parentId) {
        String url = "/appFeature/" + parentId;
        String resultStr = "";
        Envelop envelop = new Envelop();
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    public Map<String,String> getOperatPageName(Object obj){
        String function = ""; // 操作页面名称
        String operation = "";// 操作内容（增、删、改、查、导入）
        Map<String,String> map = new HashMap<>();
        Gson gson = new Gson();
        Envelop envelop = gson.fromJson(obj.toString(), Envelop.class);
        if(envelop.isSuccessFlg()){
            List<Object> appFeatureList = envelop.getDetailModelList();
            if (CollectionUtils.isNotEmpty(appFeatureList)) {
                Map<Object, Object> objectMap = (Map<Object, Object>) appFeatureList.get(0);
                String type = (String) objectMap.get("type");
                if ("3".equals(type)) {
                    operation = (String) objectMap.get("name");
                    String parentId = Double.valueOf((Double) objectMap.get("parentId")).intValue() + "";
                    //调用接口查询parentId对应的菜单
                    Object obj2 = appFeatureFindParentId(parentId);
                    Envelop envelop2 = gson.fromJson(obj2.toString(), Envelop.class);
                    if(envelop2.isSuccessFlg()){
                        Map<Object, Object> objectMap2 = (Map<Object, Object>) envelop2.getObj();
                        function = (String) objectMap2.get("name");
                    }
                } else {
                    function = (String) objectMap.get("name");
                }
            }
        }
        map.put("operation",operation);
        map.put("function",function);
        return map;
    }



}
