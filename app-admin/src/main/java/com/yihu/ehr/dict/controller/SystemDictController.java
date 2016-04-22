package com.yihu.ehr.dict.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.dict.SystemDictEntryModel;
import com.yihu.ehr.agModel.dict.SystemDictModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/dict")
@Controller
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class SystemDictController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("initial")
    public String systemDictDialog(Model model) {
        model.addAttribute("contentPage","/dict/systemDict");
        return "pageView";
    }

    @RequestMapping("/createDict")
    @ResponseBody
    public Object createDict(String name, String reference, String userId) {

        Map<String, Object> params = new HashMap<>();
        Envelop result = new Envelop();
        String resultStr = "";

        if(StringUtils.isEmpty(name)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典名字不能为空");
            return result;
        }
        SystemDictModel systemDictModel= new SystemDictModel();
        systemDictModel.setName(name);
        systemDictModel.setAuthorId("System");
        params.put("dictionary",toJson(systemDictModel));

        try{
            String urlCheck = "/dictionaries/existence";
            Map<String, Object> paramsCheck = new HashMap<>();
            paramsCheck.put("dict_name",name);
            String resultCheckStr = HttpClientUtil.doGet(comUrl + urlCheck, paramsCheck, username, password);

            if(Boolean.parseBoolean(resultCheckStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("字典名字在系统中已存在。");
                return result;
            }

            String url = "/dictionaries";
            resultStr = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return resultStr;

        }catch (Exception ex){
            result.setSuccessFlg(false);
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("deleteDict")
    @ResponseBody
    public Object deleteDict(long dictId) {

        Envelop result = new Envelop();
        String resultStr = "";

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);

            String url ="/dictionaries/" + dictId;
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            return resultStr;

        }catch (Exception ex){
            result.setSuccessFlg(false);
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("updateDict")
    @ResponseBody
    public Object updateDict(long dictId, String name) {

        Map<String, Object> params = new HashMap<>();
        Envelop result = new Envelop();
        String resultStr = "";

        if(StringUtils.isEmpty(name)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("字典名字不能为空！");
            return result;
        }

        try {
            String urlGetDict = "/dictionaries/" + dictId;
            Map<String, Object> dictParams = new HashMap<>();
            dictParams.put("id",dictId);
            String dictResultStr = HttpClientUtil.doGet(comUrl + urlGetDict, dictParams, username, password);
            result = getEnvelop(dictResultStr);

            if(result.isSuccessFlg()){
                SystemDictModel systemDictModel = getEnvelopModel(result.getObj(),SystemDictModel.class);
                systemDictModel.setName(name);
                params.put("dictionary",toJson(systemDictModel));

                String urlCheckDict = "/dictionaries";
                resultStr = HttpClientUtil.doPut(comUrl + urlCheckDict, params, username, password);
                return resultStr;
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;
    }

    @RequestMapping("searchSysDicts")
    @ResponseBody
    public Object searchSysDicts(String searchNm, Integer page, Integer rows) {

        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(searchNm)) {
            stringBuffer.append("name?" + searchNm + " g1;phoneticCode?" + searchNm + " g1");
        }
        String filters = stringBuffer.toString();
        params.put("filters", "");
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        params.put("page", page);
        params.put("size", rows);

        try {
            String url ="/dictionaries";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("createDictEntry")
    @ResponseBody
    public Object createDictEntry(Long dictId, String code, String value, Integer sort, String catalog) {

        Map<String, Object> params = new HashMap<>();
        Envelop result = new Envelop();
        String resultStr = "";

        if(StringUtils.isEmpty(dictId)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典Id不能为空！");
            return result;
        }
        if(StringUtils.isEmpty(code)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典项编码不能为空！");
            return result;
        }
        if(StringUtils.isEmpty(value)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典项值不能为空！");
            return result;
        }

        SystemDictEntryModel dictEntryModel = new SystemDictEntryModel();
        dictEntryModel.setDictId(dictId);
        dictEntryModel.setCode(code);
        dictEntryModel.setValue(value);
        dictEntryModel.setSort(sort);
        dictEntryModel.setCatalog(catalog);
        params.put("entry",toJson(dictEntryModel));

        try {
            String urlCheck = "/dictionaries/existence/" + dictId + "/" + code;
            Map<String, Object> paramsCheck = new HashMap<>();
            paramsCheck.put("dict_id",dictId);
            paramsCheck.put("code",code);
            String resultCheckStr = HttpClientUtil.doGet(comUrl + urlCheck, paramsCheck, username, password);

            if(Boolean.parseBoolean(resultCheckStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("代码在该字典中已存在，请确认。");
                return result;
            }

            String url = "/dictionaries/entries";
            resultStr = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return resultStr;

        }catch (Exception ex){
            result.setSuccessFlg(false);
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("deleteDictEntry")
    @ResponseBody
    public Object deleteDictEntry(long dictId, String code) {

        Envelop result = new Envelop();
        String resultStr = "";
        Map<String, Object> params = new HashMap<>();
        params.put("id",dictId);
        params.put("code",code);

        String url = "/dictionaries/"+ dictId + "/entries/"+ code;

        try {
            resultStr = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            return resultStr;
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("updateDictEntry")
    @ResponseBody
    public Object updateDictEntry(Long dictId, String code, String value, Integer sort, String catalog) {

        Map<String, Object> params = new HashMap<>();
        Envelop result = new Envelop();
        String resultStr = "";

        if(StringUtils.isEmpty(dictId)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典id不能为空！");
            return result;
        }
        if(StringUtils.isEmpty(code)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典项编码code不能为空！");
            return result;
        }

        SystemDictEntryModel dictEntryModel = new SystemDictEntryModel();
        dictEntryModel.setDictId(dictId);
        dictEntryModel.setCode(code);
        dictEntryModel.setValue(value);
        dictEntryModel.setSort(sort);
        dictEntryModel.setCatalog(catalog);
        params.put("dictionary",toJson(dictEntryModel));

        try {
            String dictEntryUrl = "/dictionaries/" + dictId + "/entries/" + code;
            Map<String, Object> dictEntryParams = new HashMap<>();
            dictEntryParams.put("dict_id",dictId);
            dictEntryParams.put("code",code);
            String dictEntryResultStr = HttpClientUtil.doGet(comUrl + dictEntryUrl, dictEntryParams, username, password);
            result = getEnvelop(dictEntryResultStr);

            if(result.isSuccessFlg()){
                SystemDictEntryModel systemDictEntryModel = getEnvelopModel(result.getObj(),SystemDictEntryModel.class);
                systemDictEntryModel.setValue(value);
                systemDictEntryModel.setSort(sort);
                systemDictEntryModel.setCatalog(catalog);
                params.put("entry",toJson(systemDictEntryModel));

                String urlCheckDict = "/dictionaries/entries";
                resultStr = HttpClientUtil.doPut(comUrl + urlCheckDict, params, username, password);
                return resultStr;
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;
    }

    @RequestMapping("searchDictEntryListForManage")
    @ResponseBody
    public Object searchDictEntryList(Long dictId, Integer page, Integer rows) {

        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(dictId)) {
            stringBuffer.append("dictId=" + dictId);
        }
        String filters = stringBuffer.toString();
        params.put("filters", "");
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        params.put("page", page);
        params.put("size", rows);

        try {
            String url ="/dictionaries/entries";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        }catch (Exception ex){
            LogService.getLogger(SystemDictEntryModel.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("selecttags")
    @ResponseBody
    public Object selectTags() {

        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        try {
            String url ="/dictionaries/tags";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);

            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.readValue(resultStr, Envelop.class);
            result.setObj(result.getDetailModelList());

            return result;
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("searchDictEntryList")
    @ResponseBody
    public Object searchDictEntryListForDDL(Long dictId, Integer page, Integer rows) {

        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(dictId)) {
            stringBuffer.append("dictId=" + dictId);
        }
        String filters = stringBuffer.toString();
        params.put("filters", "");
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        if(StringUtils.isEmpty(page) || page == 0){
            page = 1;
        }
        if(StringUtils.isEmpty(rows) || rows == 0){
            rows = 50;
        }
        params.put("page", page);
        params.put("size", rows);

        try {
            String url ="/dictionaries/entries";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        }catch (Exception ex){
            LogService.getLogger(SystemDictEntryModel.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
    }

    @RequestMapping("validator")
    @ResponseBody
    public Object validator(String systemName){
        Envelop result = new Envelop();
        String resultStr = "";

        Map<String, Object> params = new HashMap<>();
        params.put("dict_name",systemName);

        try {
            String url ="/dictionaries/existence";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);

            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.RepeatSysDictName.toString());
            }else{
                result.setSuccessFlg(true);
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;
    }

    @RequestMapping("autoSearchDictEntryList")
    @ResponseBody
    public Object autoSearchDictEntryList(Long dictId,String key){

        return null;
    }

}
