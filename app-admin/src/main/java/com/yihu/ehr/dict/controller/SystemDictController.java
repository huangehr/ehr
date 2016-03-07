package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/dict")
@Controller(RestAPI.SystemDictManagerController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class SystemDictController extends BaseRestController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;
    @RequestMapping("initial")
    public String systemDictDialog(Model model) {
        model.addAttribute("contentPage","/dict/systemDict");
        return "pageView";
    }

    @RequestMapping("createDict")
    @ResponseBody
    public Object createDict(String name, String reference, String userId) {
        Envelop result = new Envelop();
        if(StringUtils.isEmpty(name)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典名字不能为空");
            return result;
        }
        try{
            Map<String,Object> params = new HashMap();
            params.put("name",name);
            //TODO 缺少验证字典名唯一性api（原有）
            String urlCheck = "/sys_dict/isExistDict*********";
            String _msg = HttpClientUtil.doGet(comUrl+urlCheck,params,username,password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.RepeatSysDictName.toString());
                return result;
            }
            String url = "/sys_dict/dictionaries";
            params.put("reference",reference);
            params.put("userId",userId);
            String _rus = HttpClientUtil.doPost(comUrl+url,params,username,password);
            if (StringUtils.isEmpty(_rus)) {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidCreateSysDict.toString());
            } else {
                result.setSuccessFlg(true);
                //TODO 若不转化则需修改前端页面显示
                result.setObj(_rus);
            }
        }catch (Exception ex){
            result.setSuccessFlg(false);
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*XSystemDict systemDict = systemDictManager.createDict(name, reference, user);
        if (systemDict == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidCreateSysDict.toString());
            return result.toJson();
        }
        result.setSuccessFlg(true);
        result.setObj(systemDict);
        return result.toJson();*/
    }

    @RequestMapping("deleteDict")
    @ResponseBody
    public Object deleteDict(long dictId) {
        Envelop result = new Envelop();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            String urlCheckDict = "/sys_dict/dictionaries/{id}";
            String _rusDict = HttpClientUtil.doGet(comUrl+urlCheckDict,params,username,password);
            if(StringUtils.isEmpty(_rusDict)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
                return  result;
            }
            String url ="/sys_dict/dictionaries/{id}";
            String _msg = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }
        }catch (Exception ex){
            result.setSuccessFlg(false);
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setErrorMsg(ErrorCode.SystemError.toString());        }
        return result;

        /*Result result = new Result();
        XSystemDict systemDict = systemDictManager.getDict(dictId);
        if (systemDict == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
            return result.toJson();
        }
        try {
            systemDictManager.deleteDict(dictId);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidDelSysDict.toString());
            return result.toJson();
        }*/
    }

    @RequestMapping("updateDict")
    @ResponseBody
    public Object updateDict(long dictId, String name) {
        Envelop result = new Envelop();
        if(StringUtils.isEmpty(name)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典名字不能为空！");
            return result;
        }
        try {
            Map<String,Object> args = new HashMap<>();
            args.put("name",name);
            //TODO 缺少验证字典名唯一性api（原有）
            String urlCheckName = "/sys_dict/isExistDict******";
            String _msg = HttpClientUtil.doGet(comUrl + urlCheckName, args, username, password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.RepeatSysDictName.toString());
            }
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            String urlCheckDict = "/sys_dict/dictionaries/{id}";
            String _rusDict = HttpClientUtil.doGet(comUrl + urlCheckDict, params, username, password);
            if(StringUtils.isEmpty(_rusDict)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
                return  result;
            }
            params.put("name",name);
            String url = "/sys_dict/dictionaries/{id}";
            String _rus = HttpClientUtil.doPut(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdateSysDict.toString());
            }else{
                result.setSuccessFlg(true);
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        XSystemDict systemDict = systemDictManager.getDict(dictId);
        if (systemDict == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
            return result.toJson();
        }
        try {
            systemDict.setName(name);
            systemDictManager.updateDict(systemDict);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidUpdateSysDict.toString());
            return result.toJson();
        }*/
    }

    @RequestMapping("searchSysDicts")
    @ResponseBody
    public Object searchSysDicts(String searchNm, Integer page, Integer rows) {
        Envelop result = new Envelop();
        String url ="/sys_dict/dictionaries";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name",searchNm);
            params.put("phoneticCode",searchNm);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
            }else{
                return _rus;
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;



       /* Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("name",searchNm);
        conditionMap.put("phoneticCode", searchNm);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);
        List<XSystemDict> systemDicts = systemDictManager.searchSysDicts(conditionMap);
        Integer totalCount = systemDictManager.searchAppsInt(conditionMap);
        Result result = getResult(systemDicts, totalCount, page, rows);
        return result.toJson();*/
    }

    @RequestMapping("createDictEntry")
    @ResponseBody
    public Object createDictEntry(Long dictId, String code, String value, Integer sort, String catalog) {
        Envelop result = new Envelop();
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
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            String urlCheckDict = "/sys_dict/dictionaries/{id}";
            String _rusDict = HttpClientUtil.doGet(comUrl + urlCheckDict, params, username,password);
            if(StringUtils.isEmpty(_rusDict)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
                return  result;
            }
            params.put("code",code);
            String urlCheckCode = "/sys_dict/dictionaries/{id}/entries/{code}";
            String _rusDictEntry = HttpClientUtil.doGet(comUrl+urlCheckCode,params,username,password);
            if(StringUtils.isEmpty(_rusDictEntry)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.RepeatSysDictEntryName.toString());
                return result;
            }
            params.put("value",value);
            params.put("sort",sort);
            params.put("catalog",catalog);
            String url = "/sys_dict/dictionaries/{id}/entries";
            String _rus = HttpClientUtil.doPost(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidCreateSysDictEntry.toString());
            }else{
                result.setSuccessFlg(true);
                //TODO 若不转化则需修改前端页面显示
                result.setObj(_rus);
            }
        }catch (Exception ex){
            result.setSuccessFlg(false);
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        XSystemDict systemDict = systemDictManager.getDict(dictId);
        if (systemDict == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
            return result.toJson();
        }
        if (systemDict.containEntry(code)) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.RepeatSysDictEntryName.toString());
            return result.toJson();
        }
        int nextSort;
        if (sort != null) {
            nextSort = sort;
        } else {
            nextSort = systemDictManager.getNextSort(dictId);
        }
        XSystemDictEntry systemDictEntry = systemDict.insertEntry(code, value, nextSort, catalog);
        if (systemDictEntry == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidCreateSysDictEntry.toString());
            return result.toJson();
        }
        result.setSuccessFlg(true);
        result.setObj(systemDictEntry);
        return result.toJson();*/
    }

    @RequestMapping("deleteDictEntry")
    @ResponseBody
    public Object deleteDictEntry(long dictId, String code) {
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(code)){
            result.setSuccessFlg(false);
            result.setErrorMsg("字典项编码不能为空！");
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            params.put("code",code);
            String urlCheckEntry = "/sys_dict/dictionaries/{id}/entries/{code}";
            String _rusDictEntry = HttpClientUtil.doGet(comUrl+urlCheckEntry,params,username,password);
            if(StringUtils.isEmpty(_rusDictEntry)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidSysDictEntry.toString());
                return result;
            }
            String url = "/sys_dict/dictionaries/{id}/entries/{code}";
            String _msg = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteDictEntryFailed.toString());
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

       /* Result result = new Result();
        XSystemDict systemDict = systemDictManager.getDict(dictId);
        if (systemDict == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
            return result.toJson();
        }
        if (!systemDict.containEntry(code)) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidSysDictEntry.toString());
            return result.toJson();
        }
        try {
            systemDict.deleteEntry(code);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidDelSysDictEntry.toString());
            return result.toJson();
        }*/

    }

    @RequestMapping("updateDictEntry")
    @ResponseBody
    public Object updateDictEntry(Long dictId, String code, String value, Integer sort, String catalog) {
        Envelop result = new Envelop();
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
        String url = "/sys_dict/dictionaries/{id}/entries/{code}";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            params.put("code",code);
            params.put("value",value);
            params.put("sort",sort);
            params.put("catalog",catalog);
            String _rus = HttpClientUtil.doPut(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdateSysDictEntry.toString());
            }else{
                result.setSuccessFlg(true);
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        try {
            XSystemDict systemDict = systemDictManager.getDict(dictId);
            systemDict.setEntryValue(code, value, sort, catalog);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidUpdateSysDictEntry.toString());
            return result.toJson();
        }*/
    }

    @RequestMapping("searchDictEntryListForManage")
    @ResponseBody
    public Object searchDictEntryList(Long dictId, Integer page, Integer rows) {
        Envelop result = new Envelop();
        String url = "/sys_dict/dictionaries/{id}/entries";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            params.put("value","");
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidSysDictEntry.toString());
            }else{
                result.setSuccessFlg(true);
                return _rus;
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


        /*Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("dictId", dictId);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);

//        XSystemDict systemDict = systemDictManager.getDict(dictId);
//        if(systemDict.getEntryList().length == 0){
//            result.setSuccessFlg(true);
//            return  result.toJson();
//        }
//        XSystemDictEntry[] systemDictEntrys = systemDict.getEntryList();
        //List<XSystemDictEntry> systemDictEntryList = Arrays.asList(systemDictEntrys);
        Map<String, Object> infoMap = null;
        infoMap = systemDictManager.searchEntryList(conditionMap);
        List<XSystemDictEntry> systemDictEntryList = (List<XSystemDictEntry>) infoMap.get("SystemDictEntry");
        Integer totalCount = (Integer) infoMap.get("totalCount");
        Result result = new Result();
        result.setSuccessFlg(true);
        result = getResult(systemDictEntryList, totalCount, page, rows);

        return result.toJson();*/
    }

    @RequestMapping("selecttags")
    @ResponseBody
    public Object selectTags() {
        Envelop result = new Envelop();
        //TODO  无对应
        String url = "/sys_dict/***********";
        try {
            String _tags = HttpClientUtil.doGet(comUrl + url,username, password);
            if(StringUtils.isEmpty(_tags)){
                result.setSuccessFlg(false);
            }else{
                result.setSuccessFlg(true);
                //TODO 若不转化则需修改前端页面显示
                result.setObj(_tags);
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

       /* List<Tags> tags = abstractDictEntryManage.getTagsList();
        Result result = new Result();
        result.setObj(tags);
        return result.toJson();*/

    }

    @RequestMapping("searchDictEntryList")
    @ResponseBody
    public Object searchDictEntryListForDDL(Long dictId, Integer page, Integer rows) {
        Envelop result = new Envelop();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            String urlCheckDict = "/sys_dict/dictionaries/{id}";
            String _rusDict = HttpClientUtil.doGet(comUrl+urlCheckDict,params,username,password);
            if(StringUtils.isEmpty(_rusDict)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
                return  result;
            }
            params.put("value","");
            params.put("page",page);
            params.put("rows",rows);
            String url = "/sys_dict/dictionaries/{id}";
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidSysDictEntry.toString());
            }else{
                //TODO 原方法只返回result.setDetailModelList(systemDictEntryList);
                return _rus;
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

       /* Result result = new Result();
        XSystemDict systemDict = systemDictManager.getDict(dictId);
        if (systemDict==null || systemDict.getEntryList().length == 0) {
            result.setSuccessFlg(true);
            return result.toJson();
        }
        XSystemDictEntry[] systemDictEntrys = systemDict.getEntryList();
        List<XSystemDictEntry> systemDictEntryList = Arrays.asList(systemDictEntrys);
        result.setSuccessFlg(true);
        result.setDetailModelList(systemDictEntryList);
        return result.toJson();*/

    }
    @RequestMapping("validator")
    @ResponseBody
    public Object validator(String systemName){
        Envelop result = new Envelop();
        String url ="/sys_dict/isExistDictName**********";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("code",systemName);
            String _msg = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(_msg)){
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
        // key参数为value值
        Envelop result = new Envelop();
        //TODO 提供api，返回字典项列表
        String url = "/sys_dict/searchByValue*****";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dictId",dictId);
            params.put("dictName",key);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.NotExistSysDict.toString());
            }else{
                result.setSuccessFlg(true);
                // TODO 若不转化要修改前端页面
                result.setObj(_rus);
            }
        }catch (Exception ex){
            LogService.getLogger(SystemDictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        XSystemDictEntry[] xSystemDictEntrys = null;
        try {
            xSystemDictEntrys = systemDictManager.getDict(dictId,key);
            List<XSystemDictEntry> systemDictEntryList = Arrays.asList(xSystemDictEntrys);
            result.setSuccessFlg(true);
            result.setDetailModelList(systemDictEntryList);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();*/
    }

}
