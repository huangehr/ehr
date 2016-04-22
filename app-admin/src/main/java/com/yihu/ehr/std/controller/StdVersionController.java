package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.standardversion.StdVersionModel;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.constants.ErrorCode;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yww on 2016/3/17.
 */
@RequestMapping("/cdaVersion")
@Controller
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class StdVersionController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.versionurl}")
    private String comUrl;

    @Autowired
    ObjectMapper objectMapper;

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
            params.put("size", 999);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return envelopStr;
        } catch (Exception ex) {
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    /**
     * 通用的用于下拉列表框（所有的版本，发布/未发布）
     * @return
     */
    @RequestMapping("getVersionList")
    @ResponseBody
    public Object getVersionList() {
        //原来流程是通过标准字典controller得到
        Envelop envelop = new Envelop();
        String url = "/versions";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("fields", "");
            params.put("filters", "");
            params.put("sorts", "");
            params.put("page", 1);
            params.put("size", 999);
            String envelopStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return envelopStr;
        } catch (Exception ex) {
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("getVersion")
    @ResponseBody
    public Object getVersion(String strVersion){
        Envelop envelop = new Envelop();
        if(StringUtils.isEmpty(strVersion)){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("标准版本号不能为空！");
            return envelop;
        }
        try {
            String url = "/version/"+strVersion;
            String envelopStr = HttpClientUtil.doGet(comUrl+url,username,password);
            return envelopStr;
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    // 检查是否存在处于编辑状态的版本
    @RequestMapping("existInStage")
    @ResponseBody
    public Object existInStage(){
        Envelop result = new Envelop();
        String url = "/version/exist_instage";
        String resultStr="";
        try{
            resultStr = HttpClientUtil.doGet(comUrl+url,username,password);
            if("true".equals(resultStr)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                //获取最新版本
                url = "/version/latest";
                resultStr = HttpClientUtil.doGet(comUrl+url,username,password);
                Envelop envelop = getEnvelop(resultStr);
                if (envelop.isSuccessFlg()){
                    StdVersionModel stdVersionModel = getEnvelopModel(envelop.getObj(), StdVersionModel.class);
                    if (stdVersionModel!=null){
                        result.setObj(stdVersionModel.getVersionName());
                    }else{
                        result.setErrorMsg("获取最新版本错误!");
                    }
                }else{
                    if (envelop.getErrorMsg().equals("没有版本信息")){
                        result.setObj("000000000000");
                    }else{
                        result.setErrorMsg("获取最新版本错误!");
                    }
                }
            }
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;
    }

    @RequestMapping("addVersion")
    @ResponseBody
    public Object addVersion(@ModelAttribute(SessionAttributeKeys.CurrentUser) UserDetailModel user){
        Envelop envelop = new Envelop();
        String url = "/version";
        Map<String,Object> params = new HashMap<>();
        params.put("userLoginCode",user.getLoginCode());
        try{
            String envelopStr = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return envelopStr;
        }catch (Exception ex){
            LogService.getLogger(StdVersionController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }


    @RequestMapping("dropCDAVersion")
    //该方法可删除已发布版本，然后将子版本的baseversion设置为该版本的父级版本
    public Object dropCDAVersion(String strVersion){
        //未使用
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
        //未使用
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

    //发布新版本
    @RequestMapping("commitVersion")
    @ResponseBody
    public Object commitVersion(String strVersion){
        Envelop envelop = new Envelop();
        String url = "/version/"+strVersion+"/commit";
        Map<String,Object> params = new HashMap<>();
        params.put("version",strVersion);
        try{
            String envelopStr = HttpClientUtil.doPut(comUrl+url,params,username,password);
            return envelopStr;
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
        //未使用
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
        //现未使用,原为修改版本时使用
        Envelop envelop = new Envelop();
        String url = "/version/check_name";
        Map<String,Object> params = new HashMap<>();
        params.put("versionName",versionName);
        try{
            String _msg = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if("true".equals(_msg)){
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
