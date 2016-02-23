package com.yihu.ehr.geography.controller;

import com.yihu.ehr.constrant.ErrorCode;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import org.codehaus.jackson.map.ObjectMapper;
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
public class AddressController extends BaseController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");  //目前定义为rest
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

    @RequestMapping("getParent")
    @ResponseBody
    public String getParent(Integer level) {
        String url = "/address/address/level";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("level",level);
        try{
            //todo 后台转换成Map后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
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
    public String getChildByParent(Integer pid) {
        String url = "/address/address/pid";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("pid",pid);
        try{
            //todo 后台转换成Map后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            Map<Integer, String> map = mapper.readValue(resultStr, Map.class);
            result.setObj(map);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
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
    public String getOrgs(String province, String city) {
        String url = "/address/search";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("province",province);
        params.put("city",city);
        try{
            //todo 后台转换成Map后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(resultStr, Map.class);
            result.setObj(map);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
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
