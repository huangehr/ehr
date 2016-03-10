package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/orgdataset")
@Controller(RestAPI.OrgDataSetController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class OrgDataSetController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;
    @RequestMapping("/initialOld")
    public String orgDataSetInitOld(HttpServletRequest request,String adapterOrg){
        request.setAttribute("adapterOrg",adapterOrg);
        return "/adapter/orgCollection";
    }

    @RequestMapping("/initial")
    public String orgDataSetInit(Model model, String adapterOrg){
        model.addAttribute("adapterOrg",adapterOrg);
        model.addAttribute("contentPage","/adapter/orgCollection/grid");
        return "pageView";
    }

    @RequestMapping("template/orgDataInfo")
    public Object orgDataInfoTemplate(Model model, String id, String mode) {
        String url = "/orgDataSet/orgDataSet";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            if(mode.equals("view") || mode.equals("modify")) {
                //todo 后台转换成model后传前台
                resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
                model.addAttribute("rs", "success");
            }else{
                resultStr="";
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
//        OrgDataSetModel orgDataSetModel = new OrgDataSetModel();
//        //mode定义：new modify view三种模式，新增，修改，查看
//        if(mode.equals("view") || mode.equals("modify")){
//            try{
//                OrgDataSet orgDataSet = (OrgDataSet) orgDataSetManager.getOrgDataSet(Long.parseLong(id));
//                orgDataSetModel.setId(String.valueOf(orgDataSet.getId()));
//                orgDataSetModel.setCode(StringUtil.latinString(orgDataSet.getCode()));
//                orgDataSetModel.setName(StringUtil.latinString(orgDataSet.getName()));
//                orgDataSetModel.setDescription(StringUtil.latinString(orgDataSet.getDescription()));
//                model.addAttribute("rs","success");
//            }catch (Exception es){
//                model.addAttribute("rs", "error");
//            }
//        }
//        model.addAttribute("sort","");
//        model.addAttribute("info", orgDataSetModel);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
//        return "simpleView";
    }

    @RequestMapping("template/orgMetaDataInfo")
    public Object orgMetaDataInfoTemplate(Model model, String id, String mode) {
        String url = "/orgDataSet/orgMetaData";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("metaDataId",id);
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
            return result;
        }
//        OrgMetaDataModel orgMetaDataModel = new OrgMetaDataModel();
//        //mode定义：new modify view三种模式，新增，修改，查看
//        if(mode.equals("view") || mode.equals("modify")){
//            try{
//                OrgMetaData orgMetaData = (OrgMetaData) orgMetaDataManager.getOrgMetaData(Long.parseLong(id));
//                orgMetaDataModel.setId(String.valueOf(orgMetaData.getId()));
//                orgMetaDataModel.setCode(StringUtil.latinString(orgMetaData.getCode()));
//                orgMetaDataModel.setName(StringUtil.latinString(orgMetaData.getName()));
//                orgMetaDataModel.setDescription(StringUtil.latinString(orgMetaData.getDescription()));
//                model.addAttribute("rs","success");
//            }catch (Exception ex){
//                model.addAttribute("rs", "error");
//            }
//        }
//        model.addAttribute("sort","");
//        model.addAttribute("info", orgMetaDataModel);
//        model.addAttribute("mode",mode);
//        model.addAttribute("contentPage","/adapter/orgCollection/dialog");
//        return "simpleView";
    }

    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     */
    @RequestMapping("getOrgDataSet")
    @ResponseBody
    public Object getOrgDataSet(String id) {
        String url = "/orgDataSet/orgDataSet";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            OrgDataSetModel orgDataSetModel = mapper.readValue(resultStr, OrgDataSetModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try{
//            OrgDataSet orgDataSet = (OrgDataSet) orgDataSetManager.getOrgDataSet(Long.parseLong(id));
//            OrgDataSetModel model = new OrgDataSetModel();
//            model.setCode(orgDataSet.getCode());
//            model.setName(orgDataSet.getName());
//            model.setDescription(orgDataSet.getDescription());
//            result.setObj(model);
//            result.setSuccessFlg(true);
//        }catch (Exception es){
//            result.setSuccessFlg(false);
//        }
//
//        return result.toJson();
    }

    /**
     * 创建机构数据集
     *
     * @param code
     * @param name
     * @param description
     * @param orgCode
     * @param userId
     * @return
     */
    @RequestMapping(value = "createOrgDataSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object createOrgDataSet(String code,
                                   String name,
                                   String description,
                                   String orgCode,
                                   String userId) {

        String url="";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgDataSet/isOrgDataSetExist";//todo:网关没有重复校验接口
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("数据集已存在！");
                return result;
            }

            url="/orgDataSet/orgDataSet";
            params.put("description", description);
            params.put("userId",userId);
            //todo 失败，返回的错误信息怎么体现？
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//创建数据集
//            ObjectMapper mapper = new ObjectMapper();
//            OrgDataSetModel orgDataSetModel = mapper.readValue(resultStr, OrgDataSetModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//
//        try {
//            boolean isExist = orgDataSetManager.isExistOrgDataSet(orgCode, code, name);   //重复校验
//            if (isExist) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据集已存在！");
//                return result.toJson();
//            }
//            OrgDataSet orgDataSet = new OrgDataSet();
//            orgDataSet.setCode(code);
//            orgDataSet.setName(name);
//            orgDataSet.setDescription(description);
//            orgDataSet.setOrganization(orgCode);
//            orgDataSet.setCreateDate(new Date());
//            orgDataSet.setCreateUser(user);
//
//            if (orgDataSetManager.createOrgDataSet(orgDataSet) == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建数据集失败！");
//                return result.toJson();
//            }
//
//            OrgDataSetModel model = new OrgDataSetModel();
//            model.setCode(orgDataSet.getCode());
//            model.setName(orgDataSet.getName());
//            model.setDescription(orgDataSet.getDescription());
//            result.setSuccessFlg(true);
//            result.setObj(model);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    /**
     * 删除机构数据集
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgDataSet")
    @ResponseBody
    public Object deleteOrgDataSet(long id) {
        String url = "/orgDataSet/deleteOrgDataSet";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",id);
        try {
            //todo:内部做级联删除(删除关联的数据元)
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
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
//        XOrgDataSet orgDataSet = orgDataSetManager.getOrgDataSet(id);
//
//        if (orgDataSet == null) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("该数据集不存在！");
//            return result.toJson();
//        }
//        try {
//            orgDataSetManager.deleteOrgDataSet(id);
//            //删除关联的数据元
//            orgMetaDataManager.deleteOrgMetaDataBySet(orgDataSet);
//            result.setSuccessFlg(true);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除数据集失败！");
//            return result.toJson();
//        }
    }

    /**
     * 修改机构数据集
     *
     * @param id
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "updateOrgDataSet", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updateOrgDataSet(String orgCode,
                                   long id,
                                   String code,
                                   String name,
                                   String description,
                                   String userId) {

        String url="";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgDataSet/orgDataSet";
//            Map<String, Object> param1 = new HashMap<>();
            params.put("id",id);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//数据已不存在
            if(resultStr==null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该数据集已不存在，请刷新后重试！");
                return result;
            }
            url="/orgDataSet/isOrgDataSetExist";//todo:网关没有重复校验接口
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("数据集已存在！");
                return result;
            }

            url="/orgDataSet/updateOrgDataSet";
            params.put("description", description);
            params.put("userId",userId);
            //todo 失败，返回的错误信息怎么体现？
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//更新数据集
//            ObjectMapper mapper = new ObjectMapper();
//            OrgDataSetModel orgDataSetModel = mapper.readValue(resultStr, OrgDataSetModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try{
//            XOrgDataSet orgDataSet = orgDataSetManager.getOrgDataSet(id);
//            if(orgDataSet == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据集不存在！");
//            }else {
//                //重复校验
//                boolean updateFlg = orgDataSet.getCode().equals(code) || !orgDataSetManager.isExistOrgDataSet(orgCode, code, name);
//                if (updateFlg) {
//                    orgDataSet.setCode(code);
//                    orgDataSet.setName(name);
//                    orgDataSet.setDescription(description);
//                    orgDataSet.setUpdateDate(new Date());
//                    orgDataSet.setUpdateUser(user);
//                    orgDataSetManager.updateOrgDataSet(orgDataSet);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该数据集已存在！");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改数据集失败！");
//            return result.toJson();
//        }
    }


    /**
     * 条件查询
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgDataSets")
    @ResponseBody
    public Object searchOrgDataSets(String orgCode, String codename, int page, int rows) {
        String url = "/orgDataSet/orgDataSets";
        String resultStr = "";
        Envelop result = new Envelop();
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
            return result;
        }

//        Result result = new Result();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgDataSetModel> orgDataSetModel = orgDataSetManager.searchOrgDataSets(conditionMap);
//            Integer totalCount = orgDataSetManager.searchTotalCount(conditionMap);
//            result = getResult(orgDataSetModel, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }


    //---------------------------以上是机构数据集部分，以下是机构数据元详情部分---------------------------


    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     */
    @RequestMapping("getOrgMetaData")
    @ResponseBody
    public Object getOrgMetaData(String id) {
        String url = "/orgMetaData/orgMetaData";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("metaDataId",id);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
//            ObjectMapper mapper = new ObjectMapper();
//            OrgMetaDataModel orgMetaDataModel = mapper.readValue(resultStr, OrgMetaDataModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//        try{
//            OrgMetaData orgMetaData = (OrgMetaData) orgMetaDataManager.getOrgMetaData(Long.parseLong(id));
//            OrgMetaDataModel model = new OrgMetaDataModel();
//            model.setCode(orgMetaData.getCode());
//            model.setName(orgMetaData.getName());
//            model.setDescription(orgMetaData.getDescription());
//            result.setObj(model);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//
//        return result.toJson();
    }


    /**
     * 创建机构数据元
     *
     * @param orgDataSetSeq
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "createOrgMetaData", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object createOrgMetaData(Integer orgDataSetSeq,
                                    String orgCode,
                                    String code,
                                    String name,
                                    String description,
                                    String userId) {

        String url;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgMetaData/isOrgMetaDataExist";//todo:网关没有重复校验接口
            params.put("orgDataSetSeq",orgDataSetSeq);
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("数据元已存在！");
                return result;
            }

            url="/orgDataSet/createOrgMetaData";
            params.put("description", description);
            params.put("userId",userId);
            //todo 失败，返回的错误信息怎么体现？
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//创建数据元
//            ObjectMapper mapper = new ObjectMapper();
//            OrgMetaDataModel orgMetaDataModel = mapper.readValue(resultStr, OrgMetaDataModel.class);
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
//            boolean isExist = orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq,orgCode, code, name);   //重复校验
//
//            if (isExist) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据元已存在！");
//                return result.toJson();
//            }
//            OrgMetaData orgMetaData = new OrgMetaData();
//            orgMetaData.setCode(code);
//            orgMetaData.setName(name);
//            orgMetaData.setOrgDataSet(orgDataSetSeq);
//            orgMetaData.setCreateDate(new Date());
//            orgMetaData.setCreateUser(user);
//            orgMetaData.setOrganization(orgCode);
//            orgMetaData.setDescription(description);
//            if (orgMetaDataManager.createOrgMetaData(orgMetaData) == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建数据元失败！");
//                return result.toJson();
//            }
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }

    /**
     * 删除机构数据元
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteOrgMetaData")
    @ResponseBody
    public Object deleteOrgMetaData(long id) {
        //todo 可与批量删除整合一起
        String url = "/orgDataSet/deleteOrgMetaData";
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
//            XOrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);
//
//            if (orgMetaData == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据元不存在！");
//                return result.toJson();
//            }
//            orgMetaDataManager.deleteOrgMetaData(id);
//            result.setSuccessFlg(true);
//            return result.toJson();
//
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除数据元失败！");
//            return result.toJson();
//        }
    }

    /**
     * 批量删除机构数据元
     *
     * @param ids
     * @return
     */
    @RequestMapping("deleteOrgMetaDataList")
    @ResponseBody
    public Object deleteOrgMetaDataList(@RequestParam("ids[]") Long[] ids) {
        String url = "/orgDataSet/deleteOrgMetaData";
        String resultStr = "";
        Envelop result = new Envelop();
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
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Result result = new Result();
//
//        if (StringUtil.isEmpty(ids)) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        } else {
//            try {
//                orgMetaDataManager.deleteOrgMetaDataList(ids);
//            } catch (Exception e) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("删除数据元失败！");
//                return result.toJson();
//            }
//            result.setSuccessFlg(true);
//
//            return result.toJson();
//        }
    }

    /**
     * 修改机构数据元
     *
     * @param id
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "updateOrgMetaData", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updateOrgMetaData(Integer orgDataSetSeq,
                                    String orgCode,
                                    long id,
                                    String code,
                                    String name,
                                    String description,
                                    String userId) {

        String url="";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        try{
            url="/orgDataSet/orgMetaData";
//            Map<String, Object> param1 = new HashMap<>();
            params.put("metaDataId",id);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//数据已不存在
            if(resultStr==null){
                result.setSuccessFlg(false);
                result.setErrorMsg("该数据元已不存在，请刷新后重试！");
                return result;
            }
            url="/orgMetaData/isOrgMetaDataExist";//todo:网关没有重复校验接口
            params.put("orgDataSetSeq",orgDataSetSeq);
            params.put("orgCode",orgCode);
            params.put("code",code);
            params.put("name",name);
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);//重复校验
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(false);
                result.setErrorMsg("数据元已存在！");
                return result;
            }

            url="/orgDataSet/updateOrgMetaData";
            params.put("description", description);
            params.put("userId",userId);
            //todo 失败，返回的错误信息怎么体现？
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);//更新数据元
//            ObjectMapper mapper = new ObjectMapper();
//            OrgMetaDataModel orgMetaDataModel = mapper.readValue(resultStr, OrgMetaDataModel.class);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

//        Result result = new Result();
//        try{
//            XOrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);
//            if(orgMetaData == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据元不存在！");
//            }else {
//                //重复校验
//                boolean updateFlg = orgMetaData.getCode().equals(code) || !orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq, orgCode, code, name);
//                if (updateFlg) {
//                    orgMetaData.setCode(code);
//                    orgMetaData.setName(name);
//                    orgMetaData.setDescription(description);
//                    orgMetaData.setUpdateDate(new Date());
//                    orgMetaData.setUpdateUser(user);
//                    orgMetaData.setOrganization(orgCode);
//                    orgMetaDataManager.updateOrgMetaData(orgMetaData);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该数据元已存在！");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改数据元失败！");
//            return result.toJson();
//        }
    }

    /**
     * 条件查询
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchOrgMetaDatas")
    @ResponseBody
    public Object searchOrgMetaDatas(String orgCode,Integer orgDataSetSeq, String codename, int page, int rows) {
        String url = "/orgDataSet/orgMetaDatas";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("orgCode", orgCode);
        params.put("orgDataSetSeq", orgDataSetSeq);
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
            return result;
        }
//        Result result =new Result();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("orgDataSetSeq", orgDataSetSeq);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgMetaDataModel> orgMetaDatas = orgMetaDataManager.searchOrgMetaDatas(conditionMap);
//            Integer totalCount = orgMetaDataManager.searchTotalCount(conditionMap);
//
//            result = getResult(orgMetaDatas, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception es){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
    }


}
