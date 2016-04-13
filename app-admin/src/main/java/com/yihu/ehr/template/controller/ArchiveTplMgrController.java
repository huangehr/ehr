package com.yihu.ehr.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康档案浏览器模板管理控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.18 18:23
 */
@Controller
@RequestMapping("/template")
public class ArchiveTplMgrController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("initial")
    public String initial(Model model) {

        model.addAttribute("contentPage","/template/archiveTplManager");
        return "pageView";
    }
    @RequestMapping("template/tplInfo")
    public Object tplInfo(Model model,String mode,String idNo ) {
        String url = "/template/tplInfo";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("mode",mode);
        params.put("id",idNo);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            model.addAttribute("tpl", resultStr);
            //todo: province + city + orgCode
            url = "/template/local";
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            model.addAttribute("local", resultStr);
            model.addAttribute("mode",mode);
            model.addAttribute("contentPage","/template/archiveTplDialog");
            return "generalView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

//        XOrganization org = null;
//        XArchiveTpl tpl = null;
//        Integer id=null;
//        if(!StringUtil.isEmpty(idNo)){
//             id = Integer.valueOf(idNo);
//        }
//        //mode定义：new modify view copy模式，新增，修改，查看 复制
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
    public Object getTemplateHtml(Integer templateId) {
        String url = "/template/getTemplateHtml";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("templateId",templateId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //todo: result--model
//            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
//            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
        return result;

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
    public Object updateTemplate(int id, String title, String version, String cdaType, String orgCode) {
        String url = "/template/updateTemplate";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("id",id);
        params.put("title",title);
        params.put("version",version);
        params.put("cdaType",cdaType);
        params.put("orgCode",orgCode);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            //todo: result--model
//            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
//            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
        return result;

//
//        try {
//            XArchiveTpl tpl = new ArchiveTpl();
//            if(id>0){
//                tpl = tplManager.getArchiveTemplate(id);
//            }
//            tpl.setTitle(title);
//            tpl.setCdaVersion(version);
//            tpl.setOrg(orgCode);
//            tpl.setId(cdaType);
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
    public Object addTemplate(String templateModel) {
        String url = "/template/addTemplate";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("templateModel",templateModel);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            //todo: result--model
//            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
//            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
        return result;
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
    public Object copyTemplate(int id, String title, String version, String cdaType, String orgCode){
        String url = "/template/copyTemplate";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        params.put("title",title);
        params.put("version",version);
        params.put("cdaType",cdaType);
        params.put("orgCode",orgCode);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            //todo: result--model
//            ObjectMapper mapper = new ObjectMapper();
//            XArchiveTpl archiveTpl = mapper.readValue(resultStr,XArchiveTpl.class);
//            result.setObj(archiveTpl);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
        return result;

//        try {
//            XArchiveTpl tplNew = new ArchiveTpl();
//            tplNew.setTitle(title);
//            tplNew.setCdaVersion(version);
//            tplNew.setOrg(orgCode);
//            tplNew.setId(cdaType);
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
    public Object getTemplateModel(String templateId) {
        String url = "/template/getTemplateModel";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        params.put("templateId",templateId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //todo: result--model
//            TemplateModel templateModel = mapper.readValue(resultStr,TemplateModel.class);
//            result.setObj(templateModel);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
        return result;

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
    public Object archiveTplManager(Model model) {
        String url = "/template/archiveTplManager";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //todo: result--model
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> templateList = Arrays.asList(mapper.readValue(resultStr,String[].class));
//            model.addAttribute("versionList", templateList);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
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
    public Object searchTemplate(String version, String orgName, int page, int rows) {
        String url = "/template/searchTemplate";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        params.put("version",version);
        params.put("orgName",orgName);
        params.put("page",page);
        params.put("rows",rows);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //todo: result--model
//            ObjectMapper mapper = new ObjectMapper();
//            TemplateDetailModel templateList = mapper.readValue(resultStr,TemplateDetailModel.class);
//            result.setObj(templateList);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
        return result;


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
    public Object validateTitle(String version, String title){
        String url = "/template/validateTitle";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("version",version);
        params.put("title",title);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
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
    public Object updateTplContent(HttpServletRequest request, HttpServletResponse response){
        String url = "/template/updateTplContent";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("request",request);

        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
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
    public Object searchOrg(String key){
        String url = "/template/searchOrg";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        params.put("orgCode",key);
        params.put("fullName",key);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //todo: result--model
//            if(resultStr == null){
//                result.setSuccessFlg(true);
//                return result;
//            }
//            ObjectMapper mapper = new ObjectMapper();
//            List<OrgDetailModel> datailModelList = Arrays.asList(mapper.readValue(resultStr,OrgDetailModel.class));
//            result.setDetailModelList(datailModelList);
            result.setSuccessFlg(true);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
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
    public Object getCDAListByVersionAndKey(String value){
        String url = "/template/getCDAListByVersionAndKey";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("value",value);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //todo: result--model
//            if(resultStr == null){
//                result.setSuccessFlg(true);
//                return result;
//            }
//            ObjectMapper mapper = new ObjectMapper();
//            List<String> datailModelList = Arrays.asList(mapper.readValue(resultStr,String[].class));
//            result.setDetailModelList(datailModelList);
            result.setSuccessFlg(true);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
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
