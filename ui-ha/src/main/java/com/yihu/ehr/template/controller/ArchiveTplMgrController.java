package com.yihu.ehr.template.controller;

import com.yihu.ha.constrant.ErrorCode;
import com.yihu.ha.constrant.Result;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.organization.model.OrgDetailModel;
import com.yihu.ha.organization.model.XOrgManager;
import com.yihu.ha.organization.model.XOrganization;
import com.yihu.ha.std.model.*;
import com.yihu.ha.template.model.ArchiveTpl;
import com.yihu.ha.template.model.TemplateDetailModel;
import com.yihu.ha.template.model.TemplateModel;
import com.yihu.ha.template.model.XArchiveTpl;
import com.yihu.ha.template.model.XArchiveTplManager;
import com.yihu.ha.util.HttpClientUtil;
import com.yihu.ha.util.ResourceProperties;
import com.yihu.ha.util.controller.BaseController;
import com.yihu.ha.util.operator.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 健康档案浏览器模板管理控制器�?
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.18 18:23
 */
@Controller
@RequestMapping("/template")
public class ArchiveTplMgrController extends BaseController {
    @Resource(name = Services.ArchiveTemplateManager)
    private XArchiveTplManager tplManager;

    @Resource(name = Services.CDADocumentManager)
    private XCDADocumentManager cdaDocumentManager;

    @Resource(name = Services.CDAVersionManager)
    private XCDAVersionManager cdaVersionManager;

    @Resource(name = Services.OrgManager)
    private XOrgManager orgManager;

    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");  //目前定义为rest
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

    @RequestMapping("initial")
    public String initial(Model model) {

        model.addAttribute("contentPage","/template/archiveTplManager");
        return "pageView";
    }
    @RequestMapping("template/tplInfo")
    public String tplInfo(Model model,String mode,String idNo ) {
        String url = "/template/tplInfo";
        String resultStr = "";
        XArchiveTpl archiveTpl = null;
        XOrganization org = null;
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("id",idNo);
        if(mode.equals("view")){

            try {
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
                org = archiveTpl!=null?archiveTpl.getOrg():null;
            } catch (Exception e) {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SystemError.toString());
                return result.toJson();
            }
        }else if(mode.equals("modify") || mode.equals("copy")){
            try {
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
                org = archiveTpl!=null?archiveTpl.getOrg():null;
            } catch (Exception e) {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SystemError.toString());
                return result.toJson();
            }
        }else{
            archiveTpl = new ArchiveTpl();
        }
        if(org!=null && org.getLocation()!=null){
            model.addAttribute("province", org.getLocation().getProvince());
            model.addAttribute("city", org.getLocation().getCity());
            model.addAttribute("orgCode", org.getOrgCode());
        }
        else {
            model.addAttribute("province", "");
            model.addAttribute("city", "");
            model.addAttribute("orgCode", "");
        }

        model.addAttribute("tpl", archiveTpl);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/template/archiveTplDialog");
        return "generalView";


//        XOrganization org = null;
//        XArchiveTpl tpl = null;
//        Integer id=null;
//        if(!StringUtil.isEmpty(idNo)){
//             id = Integer.valueOf(idNo);
//        }
//        //mode定义：new modify view copy模式，新增，修改，查�? 复制
//        if(mode.equals("view")){
//            tpl = tplManager.getArchiveTemplate(id);
//            org = tpl!=null?tpl.getOrg():null;
//        }
//        else if(mode.equals("modify") || mode.equals("copy")){
//            tpl = tplManager.getArchiveTemplate(id);
//            org = tpl!=null?tpl.getOrg():null;
//        }
//        else{
//            tpl = new ArchiveTpl();
//        }
//        if(org!=null && org.getLocation()!=null){
//            model.addAttribute("province", org.getLocation().getProvince());
//            model.addAttribute("city", org.getLocation().getCity());
//            model.addAttribute("orgCode", org.getOrgCode());
//        }
//        else {
//            model.addAttribute("province", "");
//            model.addAttribute("city", "");
//            model.addAttribute("orgCode", "");
//        }
//
//        model.addAttribute("tpl", tpl);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage","/template/archiveTplDialog");
//        return "generalView";
    }

    @RequestMapping("getTemplateHtml")
    @ResponseBody
    public String getTemplateHtml(Integer templateId) {
        String url = "/template/getTemplateHtml";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("templateId",templateId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
        return result.toJson();

//        try {
//            XArchiveTpl template = tplManager.getArchiveTemplate(templateId);
//            String htmlCode = template.getPcTplContent();
//            Result result = getSuccessResult(true);
//            result.setObj(htmlCode);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("updateTemplate")
    @ResponseBody
    public String updateTemplate(int id, String title, String version, String cdaType, String orgCode) {
        String url = "/template/updateTemplate";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("id",id);
        params.put("title",title);
        params.put("version",version);
        params.put("cdaType",cdaType);
        params.put("orgCode",orgCode);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
        return result.toJson();

//
//        try {
//            XArchiveTpl tpl = new ArchiveTpl();
//            if(id>0){
//                tpl = tplManager.getArchiveTemplate(id);
//            }
//            tpl.setTitle(title);
//            tpl.setCdaVersion(version);
//            tpl.setOrg(orgCode);
//            tpl.setCdaDocumentId(cdaType);
//            tplManager.saveArchiveTpl(tpl);
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("addTemplate")
    @ResponseBody
    public String addTemplate(TemplateModel templateModel) {
        String url = "/template/addTemplate";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("templateModel",templateModel);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
        return result.toJson();
//
//        try {
//            tplManager.addArchiveTpl(templateModel);
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("copyTemplate")
    @ResponseBody
    public String copyTemplate(int id, String title, String version, String cdaType, String orgCode){
        String url = "/template/copyTemplate";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("id",id);
        params.put("title",title);
        params.put("version",version);
        params.put("cdaType",cdaType);
        params.put("orgCode",orgCode);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
        return result.toJson();

//        try {
//            XArchiveTpl tplNew = new ArchiveTpl();
//            tplNew.setTitle(title);
//            tplNew.setCdaVersion(version);
//            tplNew.setOrg(orgCode);
//            tplNew.setCdaDocumentId(cdaType);
//            tplManager.copyTemplate(id, tplNew);
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("getTemplateModel")
    @ResponseBody
    public String getTemplateModel(String templateId) {
        String url = "/template/getTemplateModel";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("templateId",templateId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            TemplateModel templateModel = mapper.readValue(resultStr,TemplateModel.class);
            result.setObj(templateModel);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
        return result.toJson();

//        try {
//            TemplateModel templateModel = tplManager.getTemplateById(templateId);
//            Result result = getSuccessResult(true);
//            result.setObj(templateModel);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

    @RequestMapping("archiveTplManager")
    public String archiveTplManager(Model model) {
        String url = "/template/archiveTplManager";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            List<String> templateList = Arrays.asList(mapper.readValue(resultStr,String[].class));
            model.addAttribute("versionList", templateList);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
        return "/template/archiveTplManager";

//        1
//        XCDAVersion[] versions = cdaVersionManager.getVersionList();
//        List<String> versionList = new ArrayList<>();
//        for (XCDAVersion version : versions) {
//            versionList.add(version.getVersion());
//        }
//        model.addAttribute("versionList", versionList);
//
//        return "/template/archiveTplManager";
    }

    @RequestMapping("searchTemplate")
    @ResponseBody
    public String searchTemplate(String version, String orgName, int page, int rows) {
        String url = "/template/searchTemplate";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("version",version);
        params.put("orgName",orgName);
        params.put("page",page);
        params.put("rows",rows);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            TemplateDetailModel templateList = mapper.readValue(resultStr,TemplateDetailModel.class);
            result.setObj(templateList);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
        return result.toJson();


//        Map<String, Object> conditionMap = new HashMap<>();
//        conditionMap.put("version", encodeStr(version));
//        conditionMap.put("orgName", orgName);
//        conditionMap.put("page", page);
//        conditionMap.put("pageSize", rows);
//        List<XArchiveTpl> templateList = tplManager.searchTemplate(conditionMap);
//        if(templateList == null){
//            Result result = new Result();
//            result.setSuccessFlg(true);
//
//            return result.toJson();
//        }
//        else{
//            List<TemplateDetailModel> detailModelList = tplManager.searchTemplateDetailModel(templateList);
//            Integer totalCount = tplManager.searchTemplateEchoId(conditionMap);
//
//            Result result = getResult(detailModelList, totalCount, page, rows);
//
//            return result.toJson();
//        }
    }
    @RequestMapping("validateTitle")
    @ResponseBody
    public String validateTitle(String version, String title){
        String url = "/template/validateTitle";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("version",version);
        params.put("title",title);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }

//
//        Integer num =tplManager.validateTitle(version,title) ;
//        if(num >0){
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        }
//        Result result = getSuccessResult(false);
//        return  result.toJson();
    }

    @RequestMapping(value = "update_tpl_content")
    @ResponseBody
    public String updateTplContent(HttpServletRequest request, HttpServletResponse response){
        String url = "/template/updateTplContent";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("request",request);

        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }


//        try {
//            Integer templateId = Integer.parseInt(request.getParameter("templateId"));
//
//            XArchiveTpl archiveTpl = tplManager.getArchiveTemplate(templateId);
//            archiveTpl.setPcTplContent(request.getInputStream());
//
//            tplManager.updateArchiveTpl(archiveTpl);
//        } catch (Exception e) {
//            Result result = new Result();
//            result.setSuccessFlg(false);
//            result.setErrorMsg(e.getMessage());
//        }
    }

    @RequestMapping("/searchOrg")
    @ResponseBody
    public String searchOrg(String key){
        String url = "/template/searchOrg";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("orgCode",key);
        params.put("fullName",key);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(resultStr == null){
                result.setSuccessFlg(true);
                return result.toJson();
            }
            List<OrgDetailModel> datailModelList = Arrays.asList(mapper.readValue(resultStr,OrgDetailModel.class));
            result.setDetailModelList(datailModelList);
            result.setSuccessFlg(true);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }



//        Map<String,Object> conditionMap = new HashMap<>();
//        if(key == null){
//            key="";
//        }
//        conditionMap.put("orgCode",key);
//        conditionMap.put("fullName",key);
//        List<OrgDetailModel> datailModelList = orgManager.searchOrgDetailModel(conditionMap);
//        if(datailModelList.size()<0){
//            return getSuccessResult(false).toJson();
//        }
//        Result result = new Result();
//        result.setDetailModelList(datailModelList);
//        result.setSuccessFlg(true);
//        return result.toJson();
    }
    @RequestMapping("/getCDAListByVersionAndKey")
    @ResponseBody
    public String getCDAListByVersionAndKey(String value){
        String url = "/template/getCDAListByVersionAndKey";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("value",value);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(resultStr == null){
                result.setSuccessFlg(true);
                return result.toJson();
            }
            List<String> datailModelList = Arrays.asList(mapper.readValue(resultStr,String[].class));
            result.setDetailModelList(datailModelList);
            result.setSuccessFlg(true);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }

//        if(value.equals("")||value == null){
//            return null;
//        }
//        XCDADocument[] xcdaDocuments = cdaDocumentManager.getDocumentList(value,"");
//        List<Map> cdaList = new ArrayList<>();
//                for(XCDADocument xcdaDocument:xcdaDocuments){
//                    Map<String,String> cdaMap = new HashMap<>();
//                    cdaMap.put("id",xcdaDocument.getId());
//                    cdaMap.put("value",xcdaDocument.getName());
//                    cdaList.add(cdaMap);
//                }
//        Result result = new Result();
//        result.setDetailModelList(cdaList);
//        return result.toJson();
    }
}
