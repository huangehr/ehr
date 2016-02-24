package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/cdadict")
@Controller(RestAPI.DictManagerController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class DictController extends BaseRestController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");  //目前定义为rest
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

    @RequestMapping("initial")
    public String dictInitial(Model model) {
        model.addAttribute("contentPage","/std/dict/stdDict");
        return "pageView";
    }

    @RequestMapping("template/stdDictInfo")
    public String stdDictInfoTemplate(Model model, String dictId, String strVersionCode, String mode) {
        String _rus = "";
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            model.addAttribute("rs", "suc");
            if (strVersionCode == null) {
                model.addAttribute("rs", ("找不到version_code"));
            }else{
                String url = "/dict/dict";
                try {
                    Map<String,Object> params = new HashMap<>();
                    params.put("dictId",dictId);
                    params.put("versionCode",strVersionCode);
                    _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
                    //todo: 还要包含baseDictId、baseDictName
//                    ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                    DictForInterface dict = objectMapper.readValue(_rus, DictForInterface.class);
//                    if (dict != null) {
//                        params.replace("dictId",dict.getBaseDictId());
//                        DictForInterface tmp = StringUtil.isEmpty(dict.getId()) ? null : objectMapper.readValue(HttpClientUtil.doGet(host+url,params,username,password), DictForInterface.class);
//                        if(tmp!=null){
//                            model.addAttribute("baseDictId", tmp.getId());
//                            model.addAttribute("baseDictName", tmp.getName());
//                        }
//                    }
//                    else{
//                        model.addAttribute("baseDictId", "");
//                        model.addAttribute("baseDictName", "");
//                    }
                } catch (Exception ex) {
                    LogService.getLogger(DictController.class).error(ex.getMessage());
                    model.addAttribute("rs", "error");
                }
            }
        }
        model.addAttribute("info", _rus);   // jsp页面已修改
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/dict/stdDictInfoDialog");
        return "simpleView";

       /* XDictForInterface info = new DictForInterface();
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            Result result = new Result();
            model.addAttribute("rs", "suc");
            Long id = dictId == null ? 0 : Long.parseLong(dictId);
            if (strVersionCode == null) {
                model.addAttribute("rs", missParameter("version_code"));
            }else{
                try {
                    XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
                    XDict xDict = dictManager.getDict(id, xcdaVersion);
                    if (xDict != null) {
                        XDict tmp = xDict.getBaseDictId()==0 ? null : dictManager.getDict(xDict.getBaseDictId() , xcdaVersion);
                        if(tmp!=null){
                            model.addAttribute("baseDictId", tmp.getId());
                            model.addAttribute("baseDictName", tmp.getName());
                        }
                        info.setId(String.valueOf(xDict.getId()));
                        info.setCode( StringUtil.latinString(xDict.getCode()) );
                        info.setName( StringUtil.latinString(xDict.getName()) );
                        info.setAuthor(xDict.getAuthor());
//                      info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                        info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                        info.setDescription(StringUtil.latinString(xDict.getDescription()));
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
                    else{
                        model.addAttribute("baseDictId", "");
                        model.addAttribute("baseDictName", "");
                    }
                } catch (Exception ex) {
                    LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
                    model.addAttribute("rs", "error");
                }
            }
        }
        model.addAttribute("info", info);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/dict/stdDictInfoDialog");
        return "simpleView";*/
    }

    @RequestMapping("template/dictEntryInfo")
    public String dictEntryInfoTemplate(Model model, String id, String dictId, String strVersionCode, String mode) {
        String _rus = "";
        model.addAttribute("rs", "suc");
        //mode定义：new modify view三种模式，新增，修改，查看
        String url = "/dict/dictEntry*****";
        try {
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",dictId);
            params.put("entryId",id);
            _rus = HttpClientUtil.doPost(comUrl+url,params,username,password);
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            model.addAttribute("rs", "error");
        }
        model.addAttribute("info", _rus); // 已修改jsp页面
        //model.addAttribute("dictId",dictId);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/dict/dictEntryInfoDialog");
        return "simpleView";

       /* XDictEntryForInterface info = new DictEntryForInterface();
        model.addAttribute("rs", "suc");
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            Result result = new Result();
            Long dictEntryId = id.equals("") ? 0 : Long.parseLong(id);
            Long dictIdForEntry = dictId.equals("") ? 0 : Long.parseLong(dictId);
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            XDict xDict = dictManager.getDict(dictIdForEntry, xcdaVersion);
            try {
                XDictEntry xDictEntry = dictEntryManager.getEntries(dictEntryId, xDict);
                if (xDictEntry != null) {
                    info.setId(String.valueOf(xDictEntry.getId()));
                    info.setCode(xDictEntry.getCode());
                    info.setValue(xDictEntry.getValue());
                    info.setDictId(String.valueOf(xDictEntry.getDict().getId()));
                    info.setDesc(xDictEntry.getDesc());
                }
            } catch (Exception ex) {
                LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
                model.addAttribute("rs", "error");
            }
        }else{
            info.setDictId(dictId);
        }
        model.addAttribute("info", info);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/dict/dictEntryInfoDialog");
        return "simpleView";*/
    }

    /**
     * 新增或更新字典数据。
     *
     * @return
     */
    @RequestMapping("saveDict")
    @ResponseBody
    public Object saveDict(String cdaVersion, String dictId, String code, String name, String baseDict, String stdSource, String stdVersion, String description, @ModelAttribute(SessionAttributeKeys.CurrentUser) XUser user) {
        Envelop result = new Envelop();
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
        try{
            String url = "/dict/updateDict";
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",cdaVersion);
            params.put("dictId",dictId);
            params.put("code",code);
            // 检查字典编码是否重复
            String urlCheckCode = "/dict/isExistCode******";
            String _msg = HttpClientUtil.doGet(comUrl+urlCheckCode,params,username,password);
            if (Boolean.parseBoolean(_msg)){
                result.setErrorMsg("codeNotUnique");
                return result;
            }
            params.put("name",name);
            params.put("baseDict",baseDict);
            params.put("stdSource",stdSource);
            params.put("stdVersion",stdVersion);
            params.put("description",description);
            params.put("userId",user.getId());
            String _rus = HttpClientUtil.doPost(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SaveDictFailed.toString());
            }else{
                result.setSuccessFlg(true);
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

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
        String url = "/dict/deleteDict";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",cdaVersion);
            params.put("dictId",dictId);
            String _msg = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteDictFailed.toString());
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


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
        if (strVersionCode == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        String url = "/dict/getCdaDictList";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",searchNm);
            params.put("code",searchNm);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDictListFaild.toString());
            }else{
                return _rus;
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        if (strVersionCode == null) {
            return missParameter("version_code");
        }
        try {
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            XDict[] xDicts = dictManager.getDictListForInter(page, rows, xcdaVersion, searchNm);
            if (xDicts != null) {
                XDictForInterface[] infos = new DictForInterface[xDicts.length];
                int i = 0;
                for (XDict xDict : xDicts) {
                    XDictForInterface info = new DictForInterface();
                    info.setId(String.valueOf(xDict.getId()));
                    info.setCode(xDict.getCode());
                    info.setName(xDict.getName());
                    info.setAuthor(xDict.getAuthor());
                    info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                    info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                    info.setDescription(xDict.getDescription());
                    info.setStdVersion(xDict.getStdVersion());
                    info.setHashCode(String.valueOf(xDict.getHashCode()));
                    info.setInnerVersionId(xDict.getInnerVersionId());
                    infos[i] = info;
                    i++;
                }
                Integer totalCount = dictManager.getDictListInt(xcdaVersion, searchNm);
                result = getResult(Arrays.asList(infos), totalCount, page, rows);
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetDictListFaild.toString());
        }
        return result.toJson();*/
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
        if (strVersionCode == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        try{
            String url = "/dict/dict";
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",dictId);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDictFaild.toString());
            }else{
                result.setSuccessFlg(true);
                //TODO 或修改前端页面
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                XDictForInterface info = objectMapper.readValue(_rus,XDictForInterface.class);
                result.setObj(_rus);
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


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
        try {
            String url = "/version**/**********";
            String _rus = HttpClientUtil.doGet(comUrl+url,username,password);
            /*ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            CDAVersionModel[] cdaVersions = objectMapper.readValue(_rus,CDAVersionModel[].class);
            if(StringUtil.isEmpty(cdaVersions)){
                CDAVersionForInterface[] infos = new CDAVersionForInterface[cdaVersions.length];
                int i = 0;
                for (CDAVersionModel cdaVersion : cdaVersions) {
                    CDAVersionForInterface info = new CDAVersionForInterface();
                    info.setVersion(cdaVersion.getVersion() + ',' + cdaVersion.getVersionName());
                    info.setAuthor(cdaVersion.getAuthor());
                    info.setBaseVersion(cdaVersion.getBaseVersion());
                    //info.setCommitTime(cdaVersion.getCommitTime());
                    SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                    info.setCommitTime(sdf.parse(cdaVersion.getCommitTime()));
                    infos[i] = info;
                    i++;
                }
                strJson = objectMapper.writeValueAsString(infos);
            }*/
            result.setSuccessFlg(true);
            result.setObj(_rus);
            return result;
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetCDAVersionListFailed.toString());
            return result;
        }

        /*try {
            String strJson = "";
            XCDAVersion[] cdaVersions = cdaVersionManager.getVersionList();
            if (cdaVersions != null) {
                CDAVersionForInterface[] infos = new CDAVersionForInterface[cdaVersions.length];
                int i = 0;
                for (XCDAVersion xcdaVersion : cdaVersions) {
                    CDAVersionForInterface info = new CDAVersionForInterface();
                    info.setVersion(xcdaVersion.getVersion() + ',' + xcdaVersion.getVersionName());
                    info.setAuthor(xcdaVersion.getAuthor());
                    info.setBaseVersion(xcdaVersion.getBaseVersion());
                    info.setCommitTime(xcdaVersion.getCommitTime());
                    infos[i] = info;
                    i++;
                }
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                strJson = objectMapper.writeValueAsString(infos);
            }
            RestEcho echo = new RestEcho().success();
            echo.putResultToList(strJson);
            return echo;
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            return failed(ErrorCode.GetCDAVersionListFailed);
        }*/
    }

    /**
     * 查询StdSource用于下拉框赋值。
     *
     * @return
     */
    @RequestMapping("getStdSourceList")
    @ResponseBody
    public Object getStdSourceList(String strVersionCode) {
        Envelop result = new Envelop();
        String strJson = "";
        try {
            if (StringUtils.isEmpty(strVersionCode)) {
                result.setSuccessFlg(false);
                result.setErrorMsg("版本号不能为空！");
                return result;
            }
            String url = "/stdSource/standardSources";
            strJson = HttpClientUtil.doGet(comUrl+url,username,password);
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            StandardSourceForInterface[] stdSource = objectMapper.readValue(strJson,StandardSourceForInterface[].class);
//            if (stdSource == null) {
//                return failed(ErrorCode.GetStandardSourceFailed);
//            }
            result.setSuccessFlg(true);
            result.setObj(strJson);
            return result;
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetStandardSourceFailed.toString());
            return result;
        }

        /*String strJson = "";
        try {
            if (strVersionCode == null || strVersionCode.equals("")) {
                return missParameter("version_code");
            }
            XStandardSource[] xStandardSources = xStandardSourceManager.getSourceList();
            if (xStandardSources == null) {
                return failed(ErrorCode.GetStandardSourceFailed);
            }
            List<StandardSourceForInterface> resultInfos = GetStandardSourceForInterface(xStandardSources);
            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
            strJson = objectMapper.writeValueAsString(resultInfos);
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            return failed(ErrorCode.GetStandardSourceFailed);
        }
        RestEcho echo = new RestEcho().success();
        echo.putResultToList(strJson);
        return echo;*/
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
        String url = "/dict/*************";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",dictId);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDictListFaild.toString());
            }else{
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                List<DictForInterface> infos = Arrays.asList(objectMapper.readValue(_rus, DictForInterface[].class));
//                result.setDetailModelList(infos);
                result.setSuccessFlg(true);
                result.setObj(_rus);
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


        /*Result result = new Result();
        if (strVersionCode == null) {
            return missParameter("version_code");
        }
        try {
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            XDict[] xDicts = dictManager.getBaseDictList(xcdaVersion, dictId);
            if (xDicts != null) {
                XDictForInterface[] infos = new DictForInterface[xDicts.length];
                int i = 0;
                for (XDict xDict : xDicts) {
                    XDictForInterface info = new DictForInterface();
                    info.setId(String.valueOf(xDict.getId()));
                    info.setCode(xDict.getCode());
                    info.setName(xDict.getName());
                    info.setAuthor(xDict.getAuthor());
                    //info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                    info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                    info.setDescription(xDict.getDescription());
                    info.setStdVersion(xDict.getStdVersion());
                    info.setHashCode(String.valueOf(xDict.getHashCode()));
                    info.setInnerVersionId(xDict.getInnerVersionId());
                    infos[i] = info;
                    i++;
                }
                List<XDictForInterface> xDictForInterfaces = Arrays.asList(infos);
                result.setSuccessFlg(true);
                result.setDetailModelList(xDictForInterfaces);
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.GetDictListFaild.toString());
        }
        return result.toJson();*/
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
        String url = "/dict/getCdaBaseDictList";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",param);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDictListFaild.toString());
            }else{
                return _rus;
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


        /*Result result = new Result();
        try {
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            XDict[] xDicts = dictManager.getDictListForInter(page, rows,xcdaVersion, param);
            if (xDicts != null) {
                XDictForInterface[] infos = new DictForInterface[xDicts.length];
                int i = 0;
                for (XDict xDict : xDicts) {
                    XDictForInterface info = new DictForInterface();
                    info.setId(String.valueOf(xDict.getId()));
                    info.setCode(xDict.getCode());
                    info.setName(xDict.getName());
                    info.setAuthor(xDict.getAuthor());
                    //info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                    info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                    info.setDescription(xDict.getDescription());
                    info.setStdVersion(xDict.getStdVersion());
                    info.setHashCode(String.valueOf(xDict.getHashCode()));
                    info.setInnerVersionId(xDict.getInnerVersionId());
                    infos[i] = info;
                    i++;
                }
                Integer totalCount = dictManager.getDictListInt(xcdaVersion, param);
                result = getResult(Arrays.asList(infos), totalCount, page, rows);
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
        }
        return result.toJson();*/
    }
    /**
     * 查询最新版本的CdaVersion，用于初始化查询字典数据。
     *
     * @return
     */
    @RequestMapping("getLastCdaVersion")
    @ResponseBody
    public Object getLastCdaVersion() {
        String url = "/version/latestVersion";
        String strJson = "";
        try {
            strJson = HttpClientUtil.doGet(comUrl+url,username,password);
            return strJson;
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            return (ErrorCode.GetCDAVersionListFailed);
        }

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
    }

    @RequestMapping("saveDictEntry")
    @ResponseBody
    public Object saveDictEntry(String cdaVersion, String dictId, String id, String code, String value, String desc) {
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

        String url = "/dict/updateDictEntry";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("cdaVersion",cdaVersion);
            params.put("dictId",dictId);
            params.put("entryId",id);
            params.put("entryCode",code);
            params.put("entryValue",value);
            params.put("description",desc);
            String _rus = HttpClientUtil.doPost(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.saveDictEntryFailed.toString());
            }else{
                result.setSuccessFlg(true);
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


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
        if (StringUtils.isEmpty(cdaVersion)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        try{
            String url = "/dict/updateDict";
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",cdaVersion);
            params.put("dictId",dictId);
            params.put("entryIds",entryId);
            String _msg = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            if(Boolean.parseBoolean(_msg)){
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


        /*Result result = new Result();
        try {
            if (cdaVersion == null || cdaVersion.equals("")) {
                return missParameter("VersionCode");
            }
            if (dictId == null || dictId.equals("")) {
                return missParameter("DictId");
            }
            if (entryId == null || entryId.equals("")) {
                return missParameter("DictEntryId");
            }
            XCDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(ErrorCode.GetCDAVersionFailed);
            XDict xDict = dictManager.getDict(Long.parseLong(dictId), xcdaVersion);
            XDictEntry xDictEntry = dictEntryManager.getEntries(Long.parseLong(entryId), xDict);
            int resultFlag = dictEntryManager.deleteEntry(xDictEntry);
            if (resultFlag >= 1) {
                result.setSuccessFlg(true);
                return result.toJson();
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteDictEntryFailed.toString());
                return result.toJson();
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.DeleteDictEntryFailed.toString());
            return result.toJson();
        }*/
    }

    @RequestMapping("deleteDictEntryList")
    @ResponseBody
    public Object deleteDictEntryList(String cdaVersion, String id) {
        //TODO API要求 dictId 参数
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(cdaVersion)) {
            result.setSuccessFlg(false);
            result.setErrorMsg("版本号不能为空！");
            return result;
        }
        try{
            String url = "/dict/dictEntry";
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",cdaVersion);
            //params.put("dictId",dictId);
            params.put("entryIds",id);
            String _msg = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            if(Boolean.parseBoolean(_msg)){
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


/*        Result result = new Result();
        List<String> ids = new ArrayList<>();
        ids.add(id);
        XCDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
        if (xcdaVersion == null)
            return failed(ErrorCode.GetCDAVersionFailed);
        try {
            if (cdaVersion == null || cdaVersion.equals("")) {
                return missParameter("VersionCode");
            }
            int resultFlag = dictManager.delDictEntryList(xcdaVersion, ids);
            if (resultFlag >= 1) {
                result.setSuccessFlg(true);
                return result.toJson();
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.DeleteDictEntryFailed.toString());
                return result.toJson();
            }
        } catch (Exception ex) {
            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.DeleteDictEntryFailed.toString());
            return result.toJson();
        }*/
    }

    @RequestMapping("searchDictEntryList")
    @ResponseBody
    public Object searchDictEntryList(Long dictId, String searchNmEntry, String strVersionCode, Integer page, Integer rows) {
        Envelop result = new Envelop();
        String url = "/dict/dictEntrys";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",dictId);
            params.put("entryCode",searchNmEntry);
            params.put("entryValue",searchNmEntry);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDictEntryListFailed.toString());
            }else{
                result.setSuccessFlg(true);
                return _rus;
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


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
        String url = "/dict/dictEntry";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("versionCode",strVersionCode);
            params.put("dictId",dictId);
            params.put("entryId",id);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetDictEntryFailed.toString());
            }else{
                result.setSuccessFlg(true);
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                XDictEntryForInterface info = objectMapper.readValue(_rus, XDictEntryForInterface.class);
                result.setObj(_rus);
            }
        }catch(Exception ex){
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

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

    /*public List<StandardSourceForInterface> GetStandardSourceForInterface(XStandardSource[] xStandardSources) {
        List<StandardSourceForInterface> results = new ArrayList<>();
        for (XStandardSource xStandardSource : xStandardSources) {
            StandardSourceForInterface info = new StandardSourceForInterface();
            info.setId(xStandardSource.getId());
            info.setCode(xStandardSource.getCode());
            info.setName(xStandardSource.getName());
            info.setSourceType(xStandardSource.getSourceType().getCode());
            info.setDescription(xStandardSource.getDescription());
            results.add(info);
        }
        return results;
    }*/

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
