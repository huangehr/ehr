package com.yihu.ehr.std.controller;

import com.yihu.ehr.constants.ErrorCode;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqb on 2015/9/18.
 */
@Controller
@RequestMapping("/standardsource")
public class StdSourceManagerController extends BaseRestController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

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
        model.addAttribute("std", _rus); // ���޸�jspҳ�� var std = $.parseJSON('${std}');
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/standardsource/stdInfoDialog");
        return "simpleView";

        /*XStandardSource standardSource = null;
        //mode���壺new modify view����ģʽ���������޸ģ��鿴
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
    //��ȡ��׼��Դ��Ϣ
    public Object getStdSource(String id) {
        Envelop result = new Envelop();
        if(StringUtils.isEmpty(id)){
            result.setSuccessFlg(false);
            result.setErrorMsg("��׼��ԴidΪ�գ�");
            return result;
        }
        String url = "/stdSource/standardSource";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            String _rus = HttpClientUtil.doGet(comUrl + url, params, username, password);
            //TODO ���޸�ҳ����ʾ��
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
    //���±�׼��Դ
    public Object updateStdSource(String id,String code, String name, String type, String description) {
        //TODO �÷�����������/�޸Ĳ���
        Envelop result = new Envelop();
        if (StringUtils.isEmpty(code)){
            result.setSuccessFlg(false);
            result.setErrorMsg("��׼��Դ���벻��Ϊ�գ�");
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
                result.setErrorMsg("�����Ѵ���!");
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
                result.setErrorMsg("��׼��Դ����ʧ��");
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
    //ɾ����׼��Դ-������ɾ��
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
                result.setErrorMsg("��׼��Դɾ��ʧ��");
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
    //��ȡ�汾������������
    public Object getVersionList() {
        Envelop result = new Envelop();
        String url = "/version/allVersions";
        try{
            String  _rus = HttpClientUtil.doGet(comUrl+url,username,password);
            //todo:��̨תMAP
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
