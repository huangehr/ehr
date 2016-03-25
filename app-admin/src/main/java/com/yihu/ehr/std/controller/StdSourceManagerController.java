package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.standardsource.StdSourceDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StdSourceManagerController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.stdsourceurl}")
    private String comUrl;

    @Autowired
    ObjectMapper objectMapper;

    public StdSourceManagerController() {
    }

    @RequestMapping("initial")
    public String cdaInitial(Model model) {
        model.addAttribute("contentPage","std/standardsource/standardsource");
        return "pageView";
    }

    @RequestMapping("template/stdInfo")
    public String stdInfoTemplate(Model model, String id, String mode) {
        String url = "/source/"+id;
        String envelopStr = "";
        try{
            envelopStr = HttpClientUtil.doGet(comUrl + url, username, password);
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
        }
        model.addAttribute("envelop", envelopStr);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/std/standardsource/stdInfoDialog");
        return "simpleView";
    }

    @RequestMapping("searchStdSource")
    @ResponseBody
    public Object searchStdSource(String searchNm, String searchType, Integer page, Integer rows) {
        Envelop envelop = new Envelop();
        String url = "/sources";
        StringBuffer filters = new StringBuffer();
        if(!StringUtils.isEmpty(searchNm)){
            filters.append("code?"+searchNm+" g1;name?"+searchNm+" g1;");
        }
        if(!StringUtils.isEmpty(searchType)){
            filters.append("sourceType="+searchType+";");
        }
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("fields","");
            params.put("filters",filters);
            params.put("sorts","");
            params.put("size",rows);
            params.put("page",page);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(envelopStr)){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(ErrorCode.GetStandardSourceFailed.toString());
            }else{
                return envelopStr;
            }
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @RequestMapping("getStdSource")
    @ResponseBody
    //获取标准来源信息
    public Object getStdSource(String id) {
        Envelop envelop = new Envelop();
        String url = "/stdSource/"+id;
        String envelopStr = "";
        try{
            envelopStr = HttpClientUtil.doGet(comUrl + url, username, password);
            return envelopStr;
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;

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
        //TODO 非空、唯一性（前端/网关）
        Envelop envelop = new Envelop();
        String envelopStr = "";
        String urlGet = "/source/"+id;

        try{
            Map<String,Object> params = new HashMap<>();
            if (StringUtils.isEmpty(id)){
                String urlNew = "/source";
                StdSourceDetailModel detailModel = new StdSourceDetailModel();
                detailModel.setId(id);
                detailModel.setCode(code);
                detailModel.setName(name);
                detailModel.setSourceType(type);
                detailModel.setDescription(description);
                String modelJsonNew = objectMapper.writeValueAsString(detailModel);
                params.put("model",modelJsonNew);
                envelopStr = HttpClientUtil.doPost(comUrl+urlNew,params,username,password);
                return envelopStr;
            }

            envelopStr = HttpClientUtil.doGet(comUrl+urlGet,username,password);
            envelop = objectMapper.readValue(envelopStr,Envelop.class);
            if (!envelop.isSuccessFlg()){
                return envelopStr;
            }
            StdSourceDetailModel modelForUpdate = getEnvelopModel(envelop.getObj(),StdSourceDetailModel.class);
            modelForUpdate.setCode(code);
            modelForUpdate.setName(name);
            modelForUpdate.setSourceType(type);
            modelForUpdate.setDescription(description);
            String urlUpdate = "/source";

            String modelJsonUpdate = objectMapper.writeValueAsString(modelForUpdate);
            params.put("model",modelJsonUpdate);
            envelopStr = HttpClientUtil.doPut(comUrl+urlUpdate,params,username,password);
            return envelopStr;
        }catch (Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("delStdSource")
    @ResponseBody
    //删除标准来源-可批量删除
    public Object delStdSource(String id) {
        Envelop result = new Envelop();
        String url = "/sources";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("ids",id);
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
    }

    @RequestMapping("isSourceCodeExit")
    @ResponseBody
    //判断标准来源编码是否已存在
    public boolean isSourceCodeExit(String code) {
        //TODO 网关要提供接口
        String url = "";

        return true;
    }

    @RequestMapping("isSourceNameExit")
    @ResponseBody
    //判断标准来源名称是否已存在
    public boolean isSourceNameExit(String name) {
        //TODO 网关要提供接口
        String url = "";

        return true;
    }


    @RequestMapping("getVersionList")
    @ResponseBody
    //获取版本号用于下拉框
    public Object getVersionList() {
        Envelop result = new Envelop();
        //TODO 前端页面未使用
        String url = "/version/allVersions";
        try{
            String  _rus = HttpClientUtil.doGet(comUrl+url,username,password);
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

    /**
     * 通用的用于下拉列表框(获取所有标准来源)
     * @return
     */
    @RequestMapping("getStdSourceList")
    @ResponseBody
    public Object getStdSourceList() {
        Envelop envelop = new Envelop();
        String url = "/sources";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("fields","");
            params.put("filters","");
            params.put("sorts","");
            params.put("page",1);
            params.put("size",999);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(StringUtils.isEmpty(envelopStr)){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(ErrorCode.GetStandardSourceFailed.toString());
            }else{
                return envelopStr;
            }
        }catch(Exception ex){
            LogService.getLogger(StdSourceManagerController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }
}
