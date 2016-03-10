package com.yihu.ehr.std.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqb on 2015/9/18.
 */
@Controller
@RequestMapping("/standardsource")
public class StdSourceManagerController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;
    public StdSourceManagerController() {
    }

    @RequestMapping("initial")
    public String cdaInitial(Model model) {
        model.addAttribute("contentPage","std/standardsource/standardsource");
        return "pageView";
    }

    @RequestMapping("template/stdInfo")
    public String stdInfoTemplate(Model model, String id, String mode) {
        String url = "/stdSource/standardSource";
        String _rus = "";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
        }
        model.addAttribute("std", _rus); // 已修改jsp页面 var std = $.parseJSON('${std}');
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/standardsource/stdInfoDialog");
        return "simpleView";

        /*XStandardSource standardSource = null;
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view") || mode.equals("modify")){
            List ls = new ArrayList<>();
            ls.add(id);
            XStandardSource[] rs = xStandardSourceManager.getSourceById(ls);
            standardSource = rs.length>0 ? rs[0] : new StandardSource();
            standardSource.setCode(StringUtil.latinString(standardSource.getCode()));
            standardSource.setName(StringUtil.latinString(standardSource.getName()));
            standardSource.setDescription(StringUtil.latinString(standardSource.getDescription()));
        }
        else{
            standardSource = new StandardSource();
        }
        model.addAttribute("std", standardSource);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/standardsource/stdInfoDialog");
        return "simpleView";*/
    }

    @RequestMapping("searchStdSource")
    @ResponseBody
    public Object searchStdSource(String searchNm, String searchType, Integer page, Integer rows) {
        Envelop result = new Envelop();
        String url = "/stdSource/getStdSource";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("code",searchNm);
            params.put("name",searchNm);
            params.put("type",searchType);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetStandardSourceFailed.toString());
            }else{
                return _rus;
            }
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*List<XStandardSource> standardSources = xStandardSourceManager.getSourceByKey(searchNm,searchType, page, rows);
        List<StandardSourceModel> standardSourceModels = new ArrayList<>();
        for(XStandardSource standardSource:standardSources){
            StandardSourceModel standardSourceModel = xStandardSourceManager.getSourceByKey(standardSource);
            standardSourceModels.add(standardSourceModel);
        }
        Integer totalCount = xStandardSourceManager.getSourceByKeyInt(searchNm,searchType);
        Result result = getResult(standardSourceModels, totalCount, page, rows);
        return result.toJson();*/
    }

    @RequestMapping("getStdSource")
    @ResponseBody
    //获取标准来源信息
    public Object getStdSource(String id) {
        Envelop result = new Envelop();
        if(StringUtils.isEmpty(id)){
            result.setSuccessFlg(false);
            result.setErrorMsg("标准来源id为空！");
            return result;
        }
        String url = "/stdSource/standardSource";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //TODO 或修改页面显示？
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            StandardSourceModel[] stdSourcesModel = objectMapper.readValue(_rus, StandardSourceModel[].class);
//            Map<String,StandardSourceModel> data = new HashMap<>();
//            data.put("stdSourceModel",stdSourcesModel[0]);
            result.setObj(_rus);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.GetStandardSourceFailed.toString());
            }else{
                result.setSuccessFlg(true);
            }
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*List<String> ids = new ArrayList<>();
        ids.add(id);
        XStandardSource[] xStandardSources = xStandardSourceManager.getSourceById(ids);
        Map<String, XStandardSource> data = new HashMap<>();
        data.put("stdSourceModel", xStandardSources[0]);
        Result result = new Result();
        result.setObj(data);
        return result.toJson();*/
    }

    @RequestMapping("updateStdSource")
    @ResponseBody
    //更新标准来源
    public Object updateStdSource(String id,String code, String name, String type, String description) {
        //TODO 该方法兼有新增/修改操作
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(code)){
            result.setSuccessFlg(false);
            result.setErrorMsg("标准来源编码不能为空！");
            return result;
        }
        try{
            String urlCheckCode = "/stdSource/isCodeExist***";
            Map<String,Object> args = new HashMap<>();
            args.put("code",code);
            String _msg = HttpClientUtil.doGet(comUrl+urlCheckCode,args,username,password);
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            String urlCheckSource = "/stdSource/isStandardSourceExist";
            String _rusSource = HttpClientUtil.doGet(comUrl+urlCheckSource,params,username,password);
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            StandardSourceModel stdSources = objectMapper.readValue(_rusSource,StandardSourceModel.class);
            if (Boolean.parseBoolean(_rusSource)) {
                result.setSuccessFlg(false);
                result.setErrorMsg("编码已存在!");
                return result;
            }
            params.put("code",code);
            String url = "/stdSource/standardSource";
            params.put("name",name);
            params.put("type",type);
            params.put("description",description);
            String _rus = HttpClientUtil.doPost(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg("标准来源更新失败");
            }else{
                result.setSuccessFlg(true);
            }
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*try {
            Result result = new Result();
            List<String> ids = new ArrayList<>();
            ids.add(id);
            XStandardSource[] xStandardSources = xStandardSourceManager.getSourceById(ids);
            XStandardSource standardSource = null;
            boolean checkCode = true;
            if (xStandardSources == null || xStandardSources.length == 0) {
                standardSource = new StandardSource();
            } else {
                standardSource = xStandardSources[0];
                if(code!=null && code.equals(standardSource.getCode()))
                    checkCode = false;
            }
            if(checkCode){
                if(xStandardSourceManager.isSourceCodeExist(code)){
                    result.setErrorMsg("codeNotUnique");
                    return result.toJson();
                }
            }
            standardSource.setCode(code);
            standardSource.setName(name);
            standardSource.setSourceType(conventionalDictEntry.getStdSourceType(type));
            standardSource.setDescription(description);
            standardSource.setUpdateDate(new Date());
            xStandardSourceManager.saveSourceInfo(standardSource);
            result = getSuccessResult(true);
            return result.toJson();
        } catch (Exception e) {
            result = getSuccessResult(false);
            return result.toJson();
        }*/
    }

    @RequestMapping("delStdSource")
    @ResponseBody
    //删除标准来源-可批量删除
    public Object delStdSource(String id) {
        Envelop result = new Envelop();
        String url = "/stdSource/standardSource";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            String _msg = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg("标准来源删除失败");
            }
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

       /* String idTemp[] = id.split(",");
        List<String> ids = Arrays.asList(idTemp);
        int rtn = xStandardSourceManager.deleteSource(ids);
        Result result = rtn == -1 ? getSuccessResult(false) : getSuccessResult(true);
        return result.toJson();*/
    }

    @RequestMapping("getVersionList")
    @ResponseBody
    //获取版本号用于下拉框
    public Object getVersionList() {
        Envelop result = new Envelop();
        String url = "/version/allVersions";
        try{
            String  _rus = HttpClientUtil.doGet(comUrl+url,username,password);
            //todo:后台转MAP
//            List<Map> versions = new ArrayList<>();
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            CDAVersionModel[] cdaVersionModels = objectMapper.readValue(_rus, CDAVersionModel[].class);
//            for (CDAVersionModel cdaVersion : cdaVersionModels) {
//                Map<String,String> map = new HashMap<>();
//                map.put("code",cdaVersion.getVersion());
//                map.put("value",cdaVersion.getVersionName());
//                versions.add(map);
//            }
//            result.setDetailModelList(versions);
            result.setSuccessFlg(true);
            result.setObj(_rus);
        }catch (Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
        }
        return result;

       /* XCDAVersion[] cdaVersions = cdaVersionManager.getVersionList();
        List<Map> versions = new ArrayList<>();
        for (XCDAVersion cdaVersion : cdaVersions) {
            Map<String,String> map = new HashMap<>();
            map.put("code",cdaVersion.getVersion());
            map.put("value",cdaVersion.getVersionName());
            versions.add(map);
        }
        Result result = new Result();
        result.setDetailModelList(versions);
        return result.toJson();*/
    }
}
