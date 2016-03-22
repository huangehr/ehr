package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.standardversion.StdVersionModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by yww on 2016/3/17.
 */
@RequestMapping("/cdaVersion")
@Controller(RestAPI.CdaVersionController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class StdVersionController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;

    @Autowired
    ObjectMapper objectMapper;
//    @Value("${service-gateway.url}")
//    private String comUrl;

    //TODO 访问路径，一般有aimin而标准部分网关没有admin
    String comUrl = "http://localhost:10000/api/v1.0/version";

    @RequestMapping("initial")
    public String cdaVersionInitial(Model model){
        model.addAttribute("contentPage", "std/cdaVersion/cdaVersion");
        return "pageView";
    }

    @RequestMapping("searchVersions")
    @ResponseBody
    public Object searchVersions(String searchNm,int page, int rows){
        Envelop envelop = new Envelop();
        String url = "/versions";
        String filters = "";
        if(!StringUtils.isEmpty(searchNm)){
            filters = "version?"+searchNm+" g1;versionName?"+searchNm+" g1;";
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("fields", "");
            params.put("filters", filters);
            params.put("sorts", "");
            params.put("page", 1);
            params.put("size", 30);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return envelopStr;
        } catch (Exception ex) {
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("getVersionList")
    @ResponseBody
    //获取版本列表用于下拉框
    public Object getVersionList() {
        //使用 标准数据集页面、++
        //TODO 暂时使用的方法，原来流程是通过标准字典controller得到
        //TODO 原方法将编码、名字组成（"version,name")形式，到前端页面在拆分，，现在返回对象、不再去封装，页面直接取对应的值（标准数据集页面）
        Envelop envelop = new Envelop();
        String url = "/versions";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("fields", "");
            params.put("filters", "");
            params.put("sorts", "");
            params.put("page", 1);
            params.put("size", 100);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            Envelop en = objectMapper.readValue(envelopStr,Envelop.class);
            if (en.isSuccessFlg()){
                List<StdVersionModel> list = (List<StdVersionModel>)getEnvelopList(en.getDetailModelList(),new ArrayList<StdVersionModel>(),StdVersionModel.class);
            }
            return envelopStr;
        } catch (Exception ex) {
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("getVersionById")
    //@ResponseBody
    public String getVersionById(Model model,String strVersion,String mode){
        //TODO 未使用
        return "simpleView";
    }

    // 检查是否存在处于编辑状态的版本
    @RequestMapping("existInStage")
    @ResponseBody
    public Object existInStage(){
        Envelop envelop = new Envelop();
        String url = "/version/exist_instage";
        try{
            String _msg = HttpClientUtil.doGet(comUrl+url,username,password);
            if("true".equals(_msg)){
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("不存在处于编辑状态的标准版本！");
            }
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    //获取最新已发布版本



    @RequestMapping("addVersion")
    @ResponseBody
    public Object addVersion(){
        //@ModelAttribute(SessionAttributeKeys.CurrentUser) Xuser user
        Envelop envelop = new Envelop();
        String url = "/version";
        Map<String,Object> params = new HashMap<>();
        //TODO 临时测试数据--需要用户账号
        String userLoginCode = "wwcs";
        params.put("userLoginCode",userLoginCode);
        try{
            String envelopStr = HttpClientUtil.doPost(comUrl+url,params,username,password);
            //TODO 新增成功单无法返回数据（会报超时异常，微服务操作过程中的重启）
            return envelopStr;
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }


    @RequestMapping("updateVersion")
    @ResponseBody
    public Object updateVersion(String versionName){
        //TODO 未使用
        //TODO 标准版本信息能否修改，可修改那些内容？
        return false;
    }

    @RequestMapping("dropCDAVersion")
    //该方法可删除已发布版本，然后将子版本的baseversion设置为该版本的父级版本
    public Object dropCDAVersion(String strVersion){
        //TODO 未使用
        Envelop envelop = new Envelop();
        String url = "/version/version/"+strVersion+"/drop";
        Map<String,Object> params = new HashMap<>();
        params.put("version",strVersion);
        try {
            String _msg = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            if("true".equals(_msg)){
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("标准版本删除失败！");
            }
        }
        catch (Exception ex) {
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    //删除编辑状态的版本
    @RequestMapping("deleteStageVersion")
    @ResponseBody
    public Object deleteStageVersion(String strVersion){
        Envelop envelop = new Envelop();
        String url = "/version/"+strVersion+"/revert";
        Map<String,Object> params = new HashMap<>();
        params.put("version",strVersion);
        try {
            String _msg = HttpClientUtil.doDelete(comUrl+url,params,username,password);
            if("true".equals(_msg)){
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("标准版本删除失败！");
            }
        }
        catch (Exception ex) {
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @RequestMapping("isLatestVersion")
    @ResponseBody
    public Object isLatestVersion(String strVersion){
        //TODO 未使用
        Envelop envelop = new Envelop();
        String url = "/version/"+strVersion+"/isLatest";
        try{
            String _msg = HttpClientUtil.doGet(comUrl+url,username,password);
            if("true".equals(_msg)){
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("该版本不是最新已发布版本！");
            }

        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @RequestMapping("addVersionDialog")
    public Object createStageVersion(Model model) {
        //TODO 未使用
        try{
//            String latestVersion = cdaVersionManager.getLatestVersion().getVersion();
//            model.addAttribute("latesVersion",latestVersion);
        }catch (Exception ex){
           // LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        model.addAttribute("contentPage","std/cdaVersion/cdaVersionEdit");
        return "generalView";
    }

    //发布新版本
    @RequestMapping("commitVersion")
    @ResponseBody
    public Object commitVersion(String strVersion){
        Envelop envelop = new Envelop();
        String url = "/version/"+strVersion+"/commit";
        Map<String,Object> params = new HashMap<>();
        params.put("version",strVersion);
        try{
            String _msg = HttpClientUtil.doPut(comUrl+url,params,username,password);
            if("true".equals(_msg)){
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("标准版本发布失败！");
            }
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    //将最新的已发布版本回滚为编辑状态
    @RequestMapping("rollbackToStage")
    @ResponseBody
    public Object rollbackToStage(String strVersion){
        //TODO 未使用
        Envelop envelop = new Envelop();
        String url = "/version/"+strVersion+"/rollback_stage";
        Map<String,Object> params = new HashMap<>();
        params.put("version",strVersion);
        try{
            String _msg = HttpClientUtil.doPut(comUrl+url,params,username,password);
            if("true".equals(_msg)){
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("标准版本返回编辑状态失败！");
            }
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    @RequestMapping("checkVersionName")
    @ResponseBody
    public Object checkVersionName(String versionName){
        //TODO 未使用
        Envelop envelop = new Envelop();
        String url = "/version/check_name";
        Map<String,Object> params = new HashMap<>();
        params.put("versionName",versionName);
        try{
            String _msg = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if("true".equals(_msg)){
                //名字已存在
                envelop.setSuccessFlg(true);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("该版本名称不存在！");
            }
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }
}
