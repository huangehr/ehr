package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDictDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDictEntryDetailModel;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/orgdict")
@Controller
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class OrgDictController {

    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("initial")
    public String orgDictInit() {
        return "/adapter/orgdict/index";
    }

    /**
     * 字典新增、修改窗口
     * @param model
     * @param id
     * @param mode
     * @return
     */
    @RequestMapping("template/orgDictInfo")
    public Object orgDictInfoTemplate(Model model, String id, String mode) {
        String url = "/adapter/org/dict";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                model.addAttribute("rs", "success");
            }
            model.addAttribute("sort","");
            model.addAttribute("info", resultStr);
            model.addAttribute("mode",mode);

            model.addAttribute("contentPage","/adapter/orgCollection/dialog");
            return "simpleView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 字典项新增、修改窗口
     * @param model
     * @param id
     * @param mode
     * @return
     */
    @RequestMapping("template/orgDictItemsInfo")
    public Object orgDictItemsInfoTemplate(Model model, String id, String mode) {
        String url = "/adapter/org/item/"+id;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                model.addAttribute("rs", "success");
            }
            model.addAttribute("sort","");
            model.addAttribute("info", resultStr);
            model.addAttribute("mode",mode);

            model.addAttribute("contentPage","/adapter/orgCollection/dialog");
            return "simpleView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping("getOrgDict")
    @ResponseBody
    public Object getOrgDict(String id) {
        String url = "/orgDict/orgDict";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            OrgDictModel orgDictModel = mapper.readValue(resultStr, OrgDictModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            OrgDict orgDict  = (OrgDict) orgDictManager.getOrgDict(Long.parseLong(id));
//            OrgDictModel model = new OrgDictModel();
//            model.setCode(orgDict.getCode());
//            model.setName(orgDict.getName());
//            model.setDescription(orgDict.getDescription());
//            result.setObj(model);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//
//        return result.toJson();
    }

    /**
     * 创建机构字典
     * @return
     */
    @RequestMapping(value = "createOrgDict",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object createOrgDict(String jsonDataModel, HttpServletRequest request){
        String url="/adapter/org/dict";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            OrgDictDetailModel orgDictDetailModel = toOrgDictDetailModel(jsonDataModel);
            orgDictDetailModel.setCreateUser(getCurUser(request).getId());
            params.put("json_data", toJson(orgDictDetailModel));
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    /**
     * 删除机构字典
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDict")
    @ResponseBody
    public Object deleteOrgDict(long id) {

        String url = "/adapter/org/dict/"+id;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            //todo:内部做级联删除(删除关联的字典项)
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 修改机构字典
     * @param jsonDataModel
     * @return
     */
    @RequestMapping(value="updateOrgDict",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updateOrgDict(String jsonDataModel, HttpServletRequest request) {

        String url="/adapter/org/dict";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            OrgDictDetailModel orgDictDetailModel = toOrgDictDetailModel(jsonDataModel);
            orgDictDetailModel.setUpdateUser(getCurUser(request).getId());
            params.put("json_data",toJson(orgDictDetailModel));
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }


    /**
     * 条件查询
     * @param codeOrName
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDicts")
    @ResponseBody
    public Object searchOrgDicts(String organizationCode,String codeOrName,int page, int rows) {
        String url = "/adapter/org/dicts";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();

        String filters = "organization="+organizationCode;
        if(!StringUtils.isEmpty(codeOrName)){
            filters += " g1;code?"+codeOrName+" g2;name?"+codeOrName+" g2";
        }

        params.put("fields", "");
        params.put("filters", filters);
        params.put("sorts", "");
        params.put("page", page);
        params.put("size", rows);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    //---------------------------以上是机构字典部分，以下是机构字典详情部分---------------------------

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping("getOrgDictItem")
    @ResponseBody
    public Object getOrgDictItem(String id) {
        String url = "/orgDictItem/orgDictItem";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try{
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            OrgDictItemModel orgDictItemModel = mapper.readValue(resultStr, OrgDictItemModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            OrgDictItem orgDictItem  = (OrgDictItem) orgDictItemManager.getOrgDictItem(Long.parseLong(id));
//            OrgDictItemModel model = new OrgDictItemModel();
//            model.setCode(orgDictItem.getCode());
//            model.setName(orgDictItem.getName());
//            model.setSort(Integer.toString(orgDictItem.getSort()));
//            model.setDescription(orgDictItem.getDescription());
//            result.setObj(model);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }


    /**
     * 创建机构字典项数据
     * @param jsonDataModel
     * @return
     */
    @RequestMapping(value="createOrgDictItem",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object createOrgDictItem(String jsonDataModel, HttpServletRequest request){

        String url="/adapter/org/item";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();


        try{
            OrgDictEntryDetailModel orgDictEntryDetailModel = toOrgDictEntryDetailModel(jsonDataModel);
            orgDictEntryDetailModel.setCreateUser(getCurUser(request).getId());
            params.put("json_data", toJson(orgDictEntryDetailModel));
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 删除机构字典数据
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDictItem")
    @ResponseBody
    public Object deleteOrgDictItem(long id) {
        //todo 可与批量删除整合一起
        String url = "/orgDict/deleteOrgDictItem";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("ids",id);
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try {
//            XOrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
//            if(orgDictItem == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典项不存在！");
//                return  result.toJson();
//            }
//            orgDictItemManager.deleteOrgDictItem(id);
//            result.setSuccessFlg(true);
//            return  result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除字典项失败！");
//            return  result.toJson();
//        }
    }

    /**
     * 批量删除机构字典数据
     * @param ids
     * @return
     */
    @RequestMapping("deleteOrgDictItemList")
    @ResponseBody
    public Object deleteOrgDictItemList(String ids) {
        String url = "/adapter/org/item";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("ids",ids);
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//
//        if(StringUtil.isEmpty(ids)){
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }else {
//            try {
//                orgDictItemManager.deleteOrgDictItemList(ids);
//            } catch (Exception e) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("删除字典项失败！");
//                return result.toJson();
//            }
//            result.setSuccessFlg(true);
//
//            return result.toJson();
//        }
    }

    /**
     * 修改机构字典项数据
     * @param jsonDataModel
     * @return
     */
    @RequestMapping(value="updateDictItem",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updateDictItem(String jsonDataModel, HttpServletRequest request) {
        String url="/adapter/org/item";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            OrgDictEntryDetailModel orgDictEntryDetailModel = toOrgDictEntryDetailModel(jsonDataModel);
            orgDictEntryDetailModel.setUpdateUser(getCurUser(request).getId());
            params.put("json_data", toJson(orgDictEntryDetailModel));
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);

            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }


    /**
     * 条件查询
     * @param codeOrName
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDictItems")
    @ResponseBody
    public Object searchOrgDictItems(Integer orgDictSeq,String organizationCode,String codeOrName,int page, int rows) {
        String url = "/adapter/org/items";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();

        String filters = "orgDict="+orgDictSeq+" g1;organization="+organizationCode+" g2";
        if (!StringUtils.isEmpty(codeOrName)){
            filters += ";code?"+codeOrName+" g4;name?"+codeOrName+" g4";
        }

        params.put("fields", "");
        params.put("filters", filters);
        params.put("sorts", "");
        params.put("page", page);
        params.put("size", rows);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return  resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

//    //todo 没发现哪里用到以下这个方法
//    @RequestMapping(value = "getOrganizationList",produces = "text/html;charset=UTF-8")
//    @ResponseBody
//    //获取机构列表
//    public String getOrganizationList(){
//        Result result = new Result();
//        try {
//            List<XOrganization> organizations = orgDictItemManager.getOrganizationList();
//            List<String> orgCodeName =new ArrayList<>();
//            for (XOrganization organization : organizations) {
//                orgCodeName.add(organization.getOrgCode()+','+organization.getFullName());
//            }
//            result.setObj(orgCodeName);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
//    }


    private OrgDictDetailModel toOrgDictDetailModel(String json) throws IOException {
        return objectMapper.readValue(json, OrgDictDetailModel.class);
    }

    private OrgDictEntryDetailModel toOrgDictEntryDetailModel(String json) throws IOException {
        return objectMapper.readValue(json, OrgDictEntryDetailModel.class);
    }

    private String toJson(Object obj) throws JsonProcessingException {

        return objectMapper.writeValueAsString(obj);
    }

    private UserDetailModel getCurUser(HttpServletRequest request){

        return (UserDetailModel)request.getSession().getAttribute(SessionAttributeKeys.CurrentUser);
    }
}
