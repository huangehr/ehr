package com.yihu.ehr.geography.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@Controller
@RequestMapping("/address")
public class AddressController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("getParent")
    @ResponseBody
    public Object getParent(Integer level) {
        String url = "/geography_entries/level/";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("level",level);
        try{
            resultStr = HttpClientUtil.doGet(comUrl + url + level, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            Envelop envelop = mapper.readValue(resultStr, Envelop.class);
            if (envelop.isSuccessFlg()) {
                result.setObj(envelop.getDetailModelList());
                result.setSuccessFlg(true);
                return result;
            }else{
                result.setSuccessFlg(false);
                return result;
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        XAddressDict[] addrArray = addressManager.getLevelToAddr(level);
//        Map<Integer, String> parentMap = new HashMap<>();
//        for (XAddressDict addr : addrArray) {
//            parentMap.put(addr.getId(), addr.getName());
//        }
//        Result result = new Result();
//        result.setObj(parentMap);
//
//        return result.toJson();
    }

    @RequestMapping("getChildByParent")
    @ResponseBody
    public Object getChildByParent(Integer pid) {
        String url = "/geography_entries/pid/";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("pid",pid);
        try{
            resultStr = HttpClientUtil.doGet(comUrl + url + pid, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            Envelop envelop = mapper.readValue(resultStr, Envelop.class);
            if (envelop.isSuccessFlg()) {
                result.setObj(envelop.getDetailModelList());
                result.setSuccessFlg(true);
                return result;
            }else{
                result.setSuccessFlg(false);
                return result;
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        XAddressDict[] addrArray = addressManager.getPidToAddr(pid);
//        Map<Integer, String> childMap = new HashMap<>();
//        for (XAddressDict addr : addrArray) {
//            childMap.put(addr.getId(), addr.getName());
//        }
//        Result result = new Result();
//        result.setObj(childMap);
//
//        return result.toJson();
    }

    @RequestMapping("getOrgs")
    @ResponseBody
    public Object getOrgs(String province, String city) {
//        String url = "/address/search";
        String url = "/organizations/geography";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("province",province);
        params.put("city",city);
        params.put("district","");
        try{
            //todo 后台转换成Map后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);

            ObjectMapper mapper = new ObjectMapper();
            envelop = mapper.readValue(resultStr,Envelop.class);

            if (envelop.isSuccessFlg()){
                envelop.setObj(envelop.getDetailModelList());
                envelop.setSuccessFlg(true);
            }

            return envelop;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
//        Map<String, Object> conditionMap = new HashMap<>();
//        conditionMap.put("province", province);
//        conditionMap.put("city", city);
//        List<XOrganization> orgList = orgManager.searchByAddress(conditionMap);
//        Map<String, String> orgMap = new HashMap<>();
//        for (XOrganization org : orgList) {
//            orgMap.put(org.getOrgCode(), org.getFullName());
//        }
//
//        Result result = new Result();
//        result.setObj(orgMap);
//
//        return result.toJson();
    }
}
