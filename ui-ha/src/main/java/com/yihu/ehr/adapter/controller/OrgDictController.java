package com.yihu.ehr.adapter.controller;

import com.yihu.ha.adapter.model.*;
import com.yihu.ha.constrant.*;
import com.yihu.ha.organization.model.XOrgManager;
import com.yihu.ha.organization.model.XOrganization;
import com.yihu.ha.user.model.XUser;
import com.yihu.ha.util.HttpClientUtil;
import com.yihu.ha.util.ResourceProperties;
import com.yihu.ha.util.controller.BaseController;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/orgdict")
@Controller(RestAPI.OrgDictController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class OrgDictController extends BaseController {

    @Resource(name = Services.OrgDictManager)
    private XOrgDictManager orgDictManager;

    @Resource(name = Services.OrgDictItemManager)
    private XOrgDictItemManager orgDictItemManager;

    @Resource(name = Services.OrgManager)
    private XOrgManager orgManager;

    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");  //目前定义为rest
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

    @RequestMapping("initial")
    public String orgDictInit() {
        return "/adapter/orgdict/index";
    }

    @RequestMapping("template/orgDictInfo")
    public String orgDictInfoTemplate(Model model, String id, String mode) {
        String url = "/orgDict/orgDict";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                //todo 后台转换成model后传前台
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
            return result.toJson();
        }
//        OrgDictModel orgDictModel = new OrgDictModel();
//        //mode定义：new modify view三种模式，新增，修改，查�?
//        if(mode.equals("view") || mode.equals("modify")){
//            try {
//                OrgDict orgDict  = (OrgDict) orgDictManager.getOrgDict(Long.parseLong(id));
//                orgDictModel.setId(String.valueOf(orgDict.getId()));
//                orgDictModel.setCode(StringUtil.latinString(orgDict.getCode()));
//                orgDictModel.setName(StringUtil.latinString(orgDict.getName()));
//                orgDictModel.setDescription(StringUtil.latinString(orgDict.getDescription()));
//                model.addAttribute("rs","success");
//            }catch (Exception ex){
//                model.addAttribute("rs", "error");
//            }
//        }
//        model.addAttribute("sort","");
//        model.addAttribute("info", orgDictModel);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
//        return "simpleView";
    }

    @RequestMapping("template/orgDictItemsInfo")
    public String orgDictItemsInfoTemplate(Model model, String id, String mode) {
        String url = "/orgDict/orgDict";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                //todo 后台转换成model后传前台
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
            return result.toJson();
        }
//        OrgDictItemModel orgDictItemModel = new OrgDictItemModel();
//        //mode定义：new modify view三种模式，新增，修改，查�?
//        if(mode.equals("view") || mode.equals("modify")){
//            try {
//                OrgDictItem orgDictItem  = (OrgDictItem) orgDictItemManager.getOrgDictItem(Long.parseLong(id));
//                orgDictItemModel.setId(String.valueOf(orgDictItem.getId()));
//                orgDictItemModel.setCode(StringUtil.latinString(orgDictItem.getCode()));
//                orgDictItemModel.setName(StringUtil.latinString(orgDictItem.getName()));
//                orgDictItemModel.setDescription(StringUtil.latinString(orgDictItem.getDescription()));
//                model.addAttribute("sort",Integer.toString(orgDictItem.getSort()));
//                model.addAttribute("rs","success");
//            }catch (Exception ex){
//                model.addAttribute("rs", "error");
//            }
//        }
//        model.addAttribute("info", orgDictItemModel);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
//        return "simpleView";
    }

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping("getOrgDict")
    @ResponseBody
    public String getOrgDict(String id) {
        String url = "/orgDict/orgDict";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            OrgDictModel orgDictModel = mapper.readValue(resultStr, OrgDictModel.class);
            result.setObj(orgDictModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
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
     * @param code
     * @param name
     * @param description
     * @param orgCode
     * @param user
     * @return
     */
    @RequestMapping(value = "createOrgDict",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createOrgDict(String orgCode,String code,String name,String description,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user){

        String url="";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgDict/isOrgDictExist";//todo:网关没有重复校验接口
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("字典已存在！");
                return result.toJson();
            }

            url="/orgDict/createOrgDict";
            params.put("description", description);
            params.put("userId",user.getId());
            //todo 失败，返回的错误信息怎么体现�?
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//创建字典
            ObjectMapper mapper = new ObjectMapper();
            OrgDictModel orgDictModel = mapper.readValue(resultStr, OrgDictModel.class);
            result.setObj(orgDictModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try {
//            boolean isExist = orgDictManager.isExistOrgDict(orgCode,code,name);   //重复校验
//
//            if(isExist){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典已存在�?");
//                return  result.toJson();
//            }
//            OrgDict orgDict = new OrgDict();
//            orgDict.setCode(code);
//            orgDict.setName(name);
//            orgDict.setDescription(description);
//            orgDict.setOrganization(orgCode);
//            orgDict.setCreateDate(new Date());
//            orgDict.setCreateUser(user);
//            if(orgDictManager.createOrgDict(orgDict)==null){
//
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建字典失败�?");
//                return  result.toJson();
//            }
//            OrgDictModel model = new OrgDictModel();
//            model.setCode(orgDict.getCode());
//            model.setName(orgDict.getName());
//            model.setDescription(orgDict.getDescription());
//            result.setSuccessFlg(true);
//            result.setObj(model);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return  result.toJson();
    }

    /**
     * 删除机构字典
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDict")
    @ResponseBody
    public String deleteOrgDict(long id) {

        String url = "/orgDict/deleteOrgDict";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            //todo:内部做级联删�?(删除关联的字典项)
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try {
//            XOrgDict orgDict = orgDictManager.getOrgDict(id);
//            if(orgDict == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典不存在�?");
//                return  result.toJson();
//            }
//            orgDictManager.deleteOrgDict(id);
//            orgDictItemManager.deleteOrgDictItemByDict(orgDict);
//            result.setSuccessFlg(true);
//            return  result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除字典失败�?");
//            return  result.toJson();
//        }
    }

    /**
     * 修改机构字典
     * @param id
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value="updateOrgDict",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateOrgDict(String orgCode,long id,String code,String name,String description,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user) {

        String url="";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgDict/orgDict";
//            Map<String, Object> param1 = new HashMap<>();
            params.put("id",id);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//数据已不存在
            if(resultStr==null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典已不存在，请刷新后重试�?");
                return result.toJson();
            }
            url="/orgDict/isOrgDictExist";//todo:网关没有重复校验接口
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("字典已存在！");
                return result.toJson();
            }

            url="/orgDict/updateOrgDict";
            params.put("description", description);
            params.put("userId",user.getId());
            //todo 失败，返回的错误信息怎么体现�?
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//更新字典
            ObjectMapper mapper = new ObjectMapper();
            OrgDictModel orgDictModel = mapper.readValue(resultStr, OrgDictModel.class);
            result.setObj(orgDictModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try{
//            XOrgDict orgDict = orgDictManager.getOrgDict(id);
//            if(orgDict == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典不存在�?");
//            }else {
//                //重复校验
//                boolean updateFlg = orgDict.getCode().equals(code) || !orgDictManager.isExistOrgDict(orgCode, code, name);
//                if (updateFlg) {
//                    orgDict.setCode(code);
//                    orgDict.setName(name);
//                    orgDict.setDescription(description);
//                    orgDict.setUpdateDate(new Date());
//                    orgDict.setUpdateUser(user);
//                    orgDictManager.updateOrgDict(orgDict);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该字典已存在�?");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改字典失败�?");
//            return result.toJson();
//        }
    }


    /**
     * 条件查询
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDicts")
    @ResponseBody
    public String searchOrgDicts(String orgCode,String codename,int page, int rows) {
        String url = "/orgDict/orgDicts";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode", orgCode);
        params.put("code", codename);
        params.put("name", codename);
        params.put("page", page);
        params.put("rows", rows);
        try {
            //todo 返回result.toJson()
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result=new Result();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgDictModel> detailModelList = orgDictManager.searchOrgDicts(conditionMap);
//            Integer totalCount = orgDictManager.searchTotalCount(conditionMap);
//            result = getResult(detailModelList, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    //---------------------------以上是机构字典部分，以下是机构字典详情部�?---------------------------

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    //todo �? 网关没有找到该方法的对应接口
    @RequestMapping("getOrgDictItem")
    @ResponseBody
    public String getOrgDictItem(String id) {
        String url = "/orgDictItem/orgDictItem";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            ObjectMapper mapper = new ObjectMapper();
            OrgDictItemModel orgDictItemModel = mapper.readValue(resultStr, OrgDictItemModel.class);
            result.setObj(orgDictItemModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
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
     * 创建机构字典数据
     * @param orgDictSeq
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value="createOrgDictItem",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createOrgDictItem(Integer orgDictSeq,String orgCode,String code,String name,String description,String sort,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user){

        String url;
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgDictItem/isOrgDictItemExist";//todo:网关没有重复校验接口
            params.put("orgDictSeq",orgDictSeq);
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("数据元已存在�?");
                return result.toJson();
            }

            url="/orgDict/createOrgDictItem";
            params.put("description", description);
            params.put("sort", sort);
            params.put("userId",user.getId());
            //todo 失败，返回的错误信息怎么体现�?
            //todo : 网关没有url的请求方�?
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//创建字典�?
            ObjectMapper mapper = new ObjectMapper();
            OrgDictItemModel orgDictItemModel = mapper.readValue(resultStr, OrgDictItemModel.class);
            result.setObj(orgDictItemModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try {
//            boolean isExist = orgDictItemManager.isExistOrgDictItem(orgDictSeq,orgCode,code, name);   //重复校验
//
//            if(isExist){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典项已存在！");
//                return  result.toJson();
//            }
//            OrgDictItem orgDictItem = new OrgDictItem();
//            int nextSort;
//            if(StringUtil.isEmpty(sort)){
//                nextSort = orgDictItemManager.getNextSort(orgDictSeq);
//            }else{
//                nextSort = Integer.parseInt(sort);
//            }
//            orgDictItem.setCode(code);
//            orgDictItem.setName(name);
//            orgDictItem.setSort(nextSort);
//            orgDictItem.setOrgDict(orgDictSeq);
//            orgDictItem.setCreateDate(new Date());
//            orgDictItem.setCreateUser(user);
//            orgDictItem.setDescription(description);
//            orgDictItem.setOrganization(orgCode);
//            if(orgDictItemManager.createOrgDictItem(orgDictItem)==null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建字典项失败！");
//                return  result.toJson();
//            }
//            OrgDictItemModel model = new OrgDictItemModel();
//            model.setCode(orgDictItem.getCode());
//            model.setName(orgDictItem.getName());
//            model.setDescription(orgDictItem.getDescription());
//            result.setSuccessFlg(true);
//            result.setObj(model);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//
//        return  result.toJson();
    }

    /**
     * 删除机构字典数据
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDictItem")
    @ResponseBody
    public String deleteOrgDictItem(long id) {
        //todo 可与批量删除整合�?�?
        String url = "/orgDict/deleteOrgDictItem";
        String resultStr = "";
        Result result = new Result();
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
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
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
    public String deleteOrgDictItemList(@RequestParam("ids[]") Long[] ids) {
        String url = "/orgDict/deleteOrgDictItem";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("ids",ids);
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
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
     * 修改机构字典数据
     * @param id
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value="updateDictItem",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateDictItem(Long id,Integer orgDictSeq,String orgCode,String code,String name,String description,String sort,@ModelAttribute(SessionAttributeKeys.CurrentUser)XUser user) {

        String url="";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgDict/orgDictItem";//todo:网关没有对应接口
//            Map<String, Object> param1 = new HashMap<>();
            params.put("id",id);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//数据已不存在
            if(resultStr==null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该字典项已不存在，请刷新后重试！");
                return result.toJson();
            }
            url="/orgDictItem/isOrgDictItemExist";//todo:网关没有重复校验接口
            params.put("orgDictSeq",orgDictSeq);
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("字典项已存在�?");
                return result.toJson();
            }

            url="/orgDict/updateDictItem";
            params.put("description", description);
            params.put("sort", sort);
            params.put("userId",user.getId());
            //todo 失败，返回的错误信息怎么体现�?
            //todo : 网关没有url的请求方�?
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//更新字典�?
            ObjectMapper mapper = new ObjectMapper();
            OrgDictItemModel orgDictItemModel = mapper.readValue(resultStr, OrgDictItemModel.class);
            result.setObj(orgDictItemModel);
            result.setSuccessFlg(true);
            return result.toJson();
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result = new Result();
//        try{
//            XOrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
//            if(orgDictItem == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典项不存在！");
//            }else {
//                //重复校验
//                boolean updateFlg = orgDictItem.getCode().equals(code) || !orgDictItemManager.isExistOrgDictItem(orgDictSeq, orgCode, code, name);
//                if (updateFlg) {
//                    orgDictItem.setCode(code);
//                    orgDictItem.setName(name);
//                    orgDictItem.setDescription(description);
//                    orgDictItem.setUpdateDate(new Date());
//                    orgDictItem.setUpdateUser(user);
//                    orgDictItem.setSort(Integer.parseInt(sort));
//                    orgDictItem.setOrganization(orgCode);
//                    orgDictItemManager.updateOrgDictItem(orgDictItem);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该字典项已存在！");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改字典项失败！");
//            return result.toJson();
//        }
    }


    /**
     * 条件查询
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDictItems")
    @ResponseBody
    public String searchOrgDictItems(Integer orgDictSeq,String orgCode,String codename,int page, int rows) {
        String url = "/orgDict/orgDictItems";
        String resultStr = "";
        Result result = new Result();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode", orgCode);
        params.put("orgDictSeq", orgDictSeq);
        params.put("code", codename);
        params.put("name", codename);
        params.put("page", page);
        params.put("rows", rows);
        try {
            //todo 返回result.toJson()
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return  resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result.toJson();
        }
//        Result result=new Result();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgDictSeq", orgDictSeq);
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgDictItemModel> detailModelList = orgDictItemManager.searchOrgDictItems(conditionMap);
//            Integer totalCount = orgDictItemManager.searchTotalCount(conditionMap);
//            result = getResult(detailModelList, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    //todo 没发现哪里用到以下这个方�?
    @RequestMapping(value = "getOrganizationList",produces = "text/html;charset=UTF-8")
    @ResponseBody
    //获取机构列表
    public String getOrganizationList(){
        Result result = new Result();
        try {
            List<XOrganization> organizations = orgDictItemManager.getOrganizationList();
            List<String> orgCodeName =new ArrayList<>();
            for (XOrganization organization : organizations) {
                orgCodeName.add(organization.getOrgCode()+','+organization.getFullName());
            }
            result.setObj(orgCodeName);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }


}
