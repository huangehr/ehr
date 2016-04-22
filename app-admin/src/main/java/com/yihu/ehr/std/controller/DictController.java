package com.yihu.ehr.std.controller;

import com.yihu.ehr.agModel.standard.dict.DictEntryModel;
import com.yihu.ehr.agModel.standard.dict.DictModel;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/cdadict")
@Controller
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class DictController  extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.stdurl}")
    private String comUrl;
    @Value("${service-gateway.stdsourceurl}")
    private String stdsourceurl;

    @RequestMapping("initial")
    public String dictInitial(Model model) {
        model.addAttribute("contentPage","/std/dict/stdDict");
        return "pageView";
    }

    @RequestMapping("template/stdDictInfo")
    public String stdDictInfoTemplate(Model model, String dictId, String strVersionCode, String mode,String staged) {
        Map<String, Object> params = new HashMap<>();
        DictModel dictModel = new DictModel();
        Envelop result = new Envelop();
        String resultStr = "";

        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            model.addAttribute("rs", "suc");
            if (strVersionCode == null) {
                model.addAttribute("rs", ("找不到version_code"));
            }else{
                String url = "/dict";
                try {
                    params.put("dictId",dictId);
                    params.put("version_code",strVersionCode);
                    resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                    result = getEnvelop(resultStr);
                    dictModel = getEnvelopModel(result.getObj(), DictModel.class);
                    if(dictModel.getBaseDict()!=null){
                        Long baseDictId = dictModel.getBaseDict();
                        params.replace("dictId", baseDictId);
                        params.put("version_code",strVersionCode);

                        String baseResultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                        result = getEnvelop(baseResultStr);
                        if(result.isSuccessFlg()){
                            DictModel baseDictModel = getEnvelopModel(result.getObj(),DictModel.class);
                            String baseDictName = baseDictModel.getName();
                            model.addAttribute("baseDictId",baseDictId);
                            model.addAttribute("baseDictName",baseDictName);
                        }
                    }
                    else{
                        model.addAttribute("baseDictId","");
                        model.addAttribute("baseDictName","");
                    }
                } catch (Exception ex) {
                    LogService.getLogger(DictController.class).error(ex.getMessage());
                    model.addAttribute("rs", "error");
                }
            }
        }
        model.addAttribute("info",toJson(dictModel));
        model.addAttribute("mode",mode);
        model.addAttribute("staged",staged);
        model.addAttribute("contentPage","/std/dict/stdDictInfoDialog");
        return "simpleView";
    }

    @RequestMapping("template/dictEntryInfo")
    public String dictEntryInfoTemplate(Model model, String id, String dictId, String strVersionCode,String staged, String mode) {
        String resultStr = "";
        DictEntryModel dictEntryModel = new DictEntryModel();
        dictEntryModel.setDictId(Long.parseLong(dictId));

        Envelop result = new Envelop();
        model.addAttribute("rs", "suc");
        //mode定义：new modify view三种模式，新增，修改，查看
        String url = "/dict_entry";
        try {
            if(!mode.equals("new")){
                Map<String,Object> params = new HashMap<>();
                Long dictEntryId = id.equals("") ? 0 : Long.parseLong(id);
                //Long dictIdForEntry = dictId.equals("") ? 0 : Long.parseLong(dictId);

                params.put("versionCode",strVersionCode);
                params.put("entryId",dictEntryId);
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                result = getEnvelop(resultStr);
                dictEntryModel = getEnvelopModel(result.getObj(),DictEntryModel.class);
            }
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            model.addAttribute("rs", "error");
        }
        model.addAttribute("info", toJson(dictEntryModel));
        model.addAttribute("mode",mode);
        model.addAttribute("staged",staged);
        model.addAttribute("contentPage","/std/dict/dictEntryInfoDialog");
        return "simpleView";
    }

    /**
     * 新增或更新字典数据。
     *
     * @return
     */
    @RequestMapping("saveDict")
    @ResponseBody
    public Object saveDict(String cdaVersion, String dictId, String code, String name, String baseDict, String stdSource, String stdVersion, String description,String userId) {
        Envelop result = new Envelop();
        String resultStr = "";

        if (StringUtils.isEmpty(cdaVersion)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        if (StringUtils.isEmpty(code)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("代码不能为空！");
            return result;
        }
        if (StringUtils.isEmpty(name)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("名称不能为空！");
            return result;
        }

        Map<String, Object> params = new HashMap<>();
        DictModel dictModel = new DictModel();
        dictModel.setId(Long.parseLong(dictId));
        dictModel.setCode(code);
        dictModel.setName(name);
        dictModel.setBaseDict(StringUtils.isEmpty(baseDict) ? null : Long.parseLong(baseDict));
        dictModel.setSourceId(stdSource);
        dictModel.setDescription(description);
        dictModel.setAuthor(userId);

        params.put("version_code",cdaVersion);
        params.put("json_data",toJson(dictModel));

        try{
            String url = "/save_dict";
            resultStr = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }
       /* Result result = new Result();
        Long id = dictId.equals("") ? 0 : Long.parseLong(dictId);
        Long baseDictL = baseDict.equals("") ? 0 : Long.parseLong(baseDict);
        //新增字典
        try {
            if (cdaVersion == null || cdaVersion.equals("")) {
                return missParameter("VersionCode");
            }
            List<String> ids = new ArrayList<>();
            ids.add(stdSource);
            XStandardSource[] xStandardSource = xStandardSourceManager.getSourceById(ids);
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);
            if (code == null || code.equals("")) {
                return missParameter("code");
            }
            if (name == null || name.equals("")) {
                return missParameter("name");
            }
            Date createTime = new Date();
            XDict xDict = new Dict();
            if(id!=0){
                xDict = dictManager.getDict(id, xcdaVersion);
                if(!code.equals(xDict.getCode())){
                    if(dictManager.isDictCodeExist(xcdaVersion,code)){
                        result.setErrorMsg("codeNotUnique");
                        return result;
                    }
                }
            }
            else if(dictManager.isDictCodeExist(xcdaVersion,code)){
                result.setErrorMsg("codeNotUnique");
                return result;
            }
            xDict.setId(id);
            xDict.setCode(code);
            xDict.setName(name);
            xDict.setAuthor(user.getId());
            xDict.setSource(xStandardSource[0]);
            xDict.setBaseDictId(baseDictL);
            xDict.setCreateDate(createTime);
            xDict.setDescription(description);
            xDict.setStdVersion(stdVersion);
            xDict.setInnerVersion(xcdaVersion);
            int resultItem = dictManager.saveDict(xDict);
            if (resultItem >= 1) {
                result.setSuccessFlg(true);
                return result;
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SaveDictFailed.toString());
                return result;
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SaveDictFailed.toString());
            return result;
        }*/
    }

    @RequestMapping("deleteDict")
    @ResponseBody
    public Object deleteDict(String cdaVersion, String dictId) {
        Envelop result = new Envelop();
        String resultStr = "";

        String url = "/dict";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",cdaVersion);
            params.put("dictId",dictId);

            resultStr = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
/*        Result result = new Result();
        Long id = Long.parseLong(dictId);
        XCDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
        XDict dict = dictManager.createDict(xcdaVersion);
        dict.setId(id);
        try {
            int resultItem = dictManager.removeDict(dict);
            if (resultItem >= 1) {
                result.setSuccessFlg(true);
                return result;
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteDictFailed.toString());
                return result;
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.DeleteDictFailed.toString());
            return result;
        }*/
    }

    /**
     * 根据CdaVersion查询相应版本的字典数据。
     *
     * @return
     */
    @RequestMapping("getCdaDictList")
    @ResponseBody
    public Object getCdaDictList(String searchNm, String strVersionCode, Integer page, Integer rows) {
        Envelop result = new Envelop();
        String resultStr = "";
        Map<String, Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(searchNm)) {
            stringBuffer.append("name?" + searchNm + " g1;code?" + searchNm + " g1");
        }
        String filters = stringBuffer.toString();
        params.put("filters", "");
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        params.put("page", page);
        params.put("size", rows);
        params.put("fields", "");
        params.put("sorts", "");
        if (strVersionCode == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        params.put("version",strVersionCode);

        try{
            String url = "/dicts";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 查询StdSource用于下拉框赋值。
     *
     * @return
     */
    @RequestMapping("getStdSourceList")
    @ResponseBody
    public Object getStdSourceList() {
        Envelop result = new Envelop();
        String resultStr = "";
        DictModel dictModel = new DictModel();
        Map<String,Object> params = new HashMap<>();

        params.put("filters", "");
        params.put("page", 1);
        params.put("size", 500);
        params.put("fields", "");
        params.put("sorts", "");

        try {
            String url = "/sources";
            resultStr = HttpClientUtil.doGet(stdsourceurl+url,params,username,password);

            return resultStr;

        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetCDAVersionListFailed.toString());
            return result;
        }
    }

    /**
     * 根据CdaVersion及字典ID查询相应版本的字典详细信息。
     *
     * @return
     */
    @RequestMapping(value = "/getCdaDictInfo", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object getCdaDictInfo(String dictId, String strVersionCode) {
        Envelop result = new Envelop();
        String resultStr = "";
        Map<String,Object> params = new HashMap<>();
        if (strVersionCode == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        params.put("versionCode",strVersionCode);
        params.put("dictId",dictId);

        try{
            String url = "/dict";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //result = getEnvelop(resultStr);
            //DictModel dictModel = getEnvelopModel(result.getObj(),DictModel.class);

            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

        /*Result result = new Result();
        Long id = dictId == null ? 0 : Long.parseLong(dictId);
        if (strVersionCode == null) {
            return missParameter("version_code");
        }
        try {
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            XDict xDict = dictManager.getDict(id, xcdaVersion);
            if (xDict != null) {
                XDictForInterface info = new DictForInterface();
                info.setId(String.valueOf(xDict.getId()));
                info.setCode(xDict.getCode());
                info.setName(xDict.getName());
                info.setAuthor(xDict.getAuthor());
                info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                info.setDescription(xDict.getDescription());
                info.setStdVersion(xDict.getStdVersion());
                if (xDict.getSource() == null) {
                    info.setStdSource(null);
                } else {
                    info.setStdSource(xDict.getSource().getId());
                }
                info.setHashCode(String.valueOf(xDict.getHashCode()));
                info.setInnerVersionId(xDict.getInnerVersionId());
                result.setSuccessFlg(true);
                result.setObj(info);
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetDictFaild.toString());
        }
        return result.toJson();*/
    }

    /**
     * 查询CdaVersion用于下拉框赋值。
     *
     * @return
     */
    @RequestMapping("getCdaVersionList")
    @ResponseBody
    public Object getCdaVersionList() {
        Envelop result = new Envelop();
        String resultStr = "";
        Map<String,Object> params = new HashMap<>();

        params.put("filters", "");
        params.put("page", "");
        params.put("size", "");

        try {
            String url = "/versions";
            resultStr = HttpClientUtil.doGet(comUrl+url,username,password);

            return resultStr;
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetCDAVersionListFailed.toString());
            return result;
        }
    }

    /**
     * 根据输入条件，查询字典List(过滤掉当前字典)
     *
     * @return
     */
    @RequestMapping("getCdaBaseDictList")
    @ResponseBody
    public Object getCdaBaseDictList(String strVersionCode, String dictId) {
        Envelop result = new Envelop();
        String resultStr = "";
        Map<String,Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(dictId)) {
            stringBuffer.append("dictId<>" + dictId);
        }
        String filters = stringBuffer.toString();
        params.put("filters", "");
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        params.put("page", "");
        params.put("size", "");

        if (strVersionCode == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        params.put("version",strVersionCode);

        try{
            String url = "/dict";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);

            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 根据输入条件，查询字典List(过滤掉当前字典)
     *
     * @return
     */
    @RequestMapping("searchCdaBaseDictList")
    @ResponseBody
    public Object searchCdaBaseDictList(String strVersionCode, String param ,Integer page, Integer rows) {
        Envelop result = new Envelop();
        String resultStr = "";
        Map<String,Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(param)) {
            stringBuffer.append("code?" + param + " g1;name?" + param + " g1;");
        }
        String filters = stringBuffer.toString();
        params.put("filters", "");
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        params.put("page", page);
        params.put("size", rows);

        if (strVersionCode == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        params.put("version",strVersionCode);

        try{
            String url = "/dicts";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);

            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }
    /**
     * 查询最新版本的CdaVersion，用于初始化查询字典数据。
     *
     * @return
     */
    @RequestMapping("getLastCdaVersion")
    @ResponseBody
    public Object getLastCdaVersion() {

        //TODO 获取最新的版本信息API方法缺失
        /*String url = "/version/latestVersion";
        String strJson = "";
        try {
            strJson = HttpClientUtil.doGet(comUrl+url,username,password);
            return strJson;
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            return (ErrorCode.GetCDAVersionListFailed);
        }*/

       /* try {
            String strJson = "";
            XCDAVersion cdaVersion = cdaVersionManager.getLatestVersion();
            if (cdaVersion != null) {
                CDAVersionForInterface info = new CDAVersionForInterface();
                info.setVersion(cdaVersion.getVersion());
                info.setAuthor(cdaVersion.getAuthor());
                info.setBaseVersion(cdaVersion.getBaseVersion());
                info.setCommitTime(cdaVersion.getCommitTime());
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                strJson = objectMapper.writeValueAsString(info);
            }
            RestEcho echo = new RestEcho().success();
            echo.putResultToList(strJson);
            return echo;
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            return failed(ErrorCode.GetCDAVersionListFailed);
        }*/
        return  null;
    }

    @RequestMapping("saveDictEntry")
    @ResponseBody
    public Object saveDictEntry(String cdaVersion, String dictId, String id, String code, String value, String desc) {
        String resultStr = "";

        Envelop result = new Envelop();
        if (StringUtils.isEmpty(cdaVersion)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        if (StringUtils.isEmpty(dictId)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("字典ID不能为空！");
            return result;
        }
        if (StringUtils.isEmpty(id)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("ID不能为空！");
            return result;
        }
        if (StringUtils.isEmpty(code)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("代码不能为空！");
            return result;
        }
        if (StringUtils.isEmpty(value)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("值不能为空！");
            return result;
        }

        Map<String, Object> params = new HashMap<>();
        DictEntryModel dictEntryModel = new DictEntryModel();
        dictEntryModel.setId(Long.parseLong(id));
        dictEntryModel.setDictId(Long.parseLong(dictId));
        dictEntryModel.setCode(code);
        dictEntryModel.setValue(value);
        dictEntryModel.setDesc(desc);

        params.put("version_code",cdaVersion);
        params.put("json_data",toJson(dictEntryModel));

        try{
            String url = "/dict_entry";
            resultStr = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

       /* Result result = new Result();
        try {
            Long entryId = id.equals("") ? 0 : Long.parseLong(id);
            if (cdaVersion == null || cdaVersion.equals("")) {
                return missParameter("stdVersion");
            }
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);
            Long dictIdForEntry = dictId.equals("") ? 0 : Long.parseLong(dictId);
            if (dictIdForEntry == 0) {
                return missParameter("DictId");
            }
            XDict xDict = dictManager.getDict(dictIdForEntry, xcdaVersion);
            if (code == null || code.equals("")) {
                return missParameter("Code");
            }
            if (value == null || value.equals("")) {
                return missParameter("Value");
            }
            XDictEntry xDictEntry = new DictEntry();
            if(entryId!=0){
                xDictEntry = dictEntryManager.getEntries(entryId, xDict);
                if(!code.equals(xDictEntry.getCode())){
                    if(dictEntryManager.isDictEntryCodeExist(xDict, code)){
                        result.setErrorMsg("codeNotUnique");
                        return result;
                    }
                }
            }
            else{
                if(dictEntryManager.isDictEntryCodeExist(xDict, code)){
                    result.setErrorMsg("codeNotUnique");
                    return result;
                }
            }
            xDictEntry.setId(entryId);
            xDictEntry.setDict(xDict);
            xDictEntry.setCode(code);
            xDictEntry.setValue(value);
            xDictEntry.setDesc(desc);
            int resultFlag = dictEntryManager.saveEntry(xDictEntry);
            if (resultFlag >= 1) {
                result.setSuccessFlg(true);
                return result.toJson();
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.saveDictEntryFailed.toString());
                return result.toJson();
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.saveDictEntryFailed.toString());
            return result.toJson();
        }*/
    }

    @RequestMapping("deleteDictEntry")
    @ResponseBody
    public Object deleteDictEntry(String cdaVersion, String dictId, String entryId) {
        Envelop result = new Envelop();
        String resultStr = "";

        Map<String,Object> params = new HashMap<>();
        params.put("versionCode",cdaVersion);
        //params.put("dictId",dictId); 字典ID项不需要

        String[] entryIds = new String[1];
        entryIds[0] = entryId;
        params.put("entryIds",entryIds);

        try{
            String url = "/dict_entry";
            resultStr = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("deleteDictEntryList")
    @ResponseBody
    public Object deleteDictEntryList(String cdaVersion,String dictId, String id) {
        Envelop result = new Envelop();
        String resultStr = "";
        if (StringUtils.isEmpty(cdaVersion)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        try{
            String url = "/dict_entry";
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",cdaVersion);
            //params.put("dictId",dictId);
            params.put("entryIds",id);
            resultStr = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            result = getEnvelop(resultStr);

            if(result.isSuccessFlg()){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteDictEntryFailed.toString());
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;
    }

    @RequestMapping("searchDictEntryList")
    @ResponseBody
    public Object searchDictEntryList(Long dictId, String searchNmEntry, String strVersionCode, Integer page, Integer rows) {
        Envelop result = new Envelop();
        String resultStr = "";
        Map<String, Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("dictId=" + dictId);
        if (!StringUtils.isEmpty(searchNmEntry)) {
            stringBuffer.append(";value?" + searchNmEntry + " g1;code?" + searchNmEntry + " g1");
        }

        String filters = stringBuffer.toString();
        params.put("filters", "");
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }
        params.put("page", page);
        params.put("size", rows);
        params.put("fields", "");
        params.put("sorts", "");

        if (strVersionCode == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        params.put("version",strVersionCode);

        try{
            String url = "/dict_entrys";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

 /*       Result result = new Result();
        if (strVersionCode == null) {
            return missParameter("version_code");
        }
        try {
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            XDict xDict = dictManager.getDict(dictId, xcdaVersion);
            XDictEntry[] xDictEntries = dictManager.getDictEntries(xDict, searchNmEntry, page, rows);
            int totalCount = dictManager.getDictEntriesForInt(xDict, searchNmEntry);
            if (xDictEntries != null) {
                XDictEntryForInterface[] infos = new DictEntryForInterface[xDictEntries.length];
                int i = 0;
                for (XDictEntry xDictEntry : xDictEntries) {
                    XDictEntryForInterface info = new DictEntryForInterface();
                    info.setId(String.valueOf(xDictEntry.getId()));
                    info.setCode(xDictEntry.getCode());
                    info.setValue(xDictEntry.getValue());
                    info.setDictId(String.valueOf(xDictEntry.getDictId()));
                    info.setDesc(xDictEntry.getDesc());
                    infos[i] = info;
                    i++;
                }
                result = getResult(Arrays.asList(infos), totalCount, page, rows);
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetDictEntryListFailed.toString());
        }

        return result.toJson();*/
    }

    @RequestMapping("getDictEntry")
    @ResponseBody
    public Object getDictEntry(String id, String dictId, String strVersionCode) {
        Envelop result = new Envelop();
        String resultStr = "";
        String url = "/dict_entry";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",dictId);
            params.put("entryId",id);
            resultStr = HttpClientUtil.doGet(comUrl+url,params,username,password);

            return resultStr;

        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());

            return result;
        }


        /*Result result = new Result();
        Long dictEntryId = id.equals("") ? 0 : Long.parseLong(id);
        Long dictIdForEntry = dictId.equals("") ? 0 : Long.parseLong(dictId);
        XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
        XDict xDict = dictManager.getDict(dictIdForEntry, xcdaVersion);
        try {
            XDictEntry xDictEntry = dictEntryManager.getEntries(dictEntryId, xDict);
            if (xDictEntry != null) {
                XDictEntryForInterface info = new DictEntryForInterface();
                info.setId(String.valueOf(xDictEntry.getId()));
                info.setCode(xDictEntry.getCode());
                info.setValue(xDictEntry.getValue());
                info.setDictId(String.valueOf(xDictEntry.getDictId()));
                info.setDesc(xDictEntry.getDesc());
                result.setSuccessFlg(true);
                result.setObj(info);
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetDictEntryListFailed.toString());
        }
        return result.toJson();*/
    }

//    public void exportToExcel(){
//        //todo：test 导出测试
//        XDict[] dicts = dictManager.getDictList(0, 0,cdaVersionManager.getLatestVersion());
//        dictManager.exportToExcel("E:/workspaces/excel/testExport.xls", dicts);
//    }
//
//    public void importFromExcel(){
//        //todo：test导入测试
//        dictManager.importFromExcel("E:/workspaces/excel/测试excel导入.xls", cdaVersionManager.getLatestVersion());
//
//    }
}
