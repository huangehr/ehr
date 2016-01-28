package com.yihu.ehr.adaption.adapterorg.controller;


import com.yihu.ehr.adaption.adapterorg.service.AdapterOrg;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrgManager;
import com.yihu.ehr.adaption.commons.FieldCondition;
import com.yihu.ehr.adaption.commons.JSONUtil;
import com.yihu.ehr.adaption.commons.ParmModel;
import com.yihu.ehr.adaption.feignclient.AddressClient;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 采集标准
 * Created by lincl on 2016.1.27
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterorg")
@Api(protocols = "https", value = "adapterorg", description = "第三方标准管理接口", tags = {"第三方标准"})
public class AdapterOrgController extends BaseController {
    @Autowired
    private AdapterOrgManager adapterOrgManager;


//    @Resource(name = Services.OrgManager)
//    private XOrgManager orgManager;
//    @Resource(name = Services.ConventionalDictEntry)
//    private XConventionalDictEntry conventionalDictEntry;

    //适配采集标准
    @RequestMapping(value = "/page" , method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Object searchAdapterOrg(
            @ApiParam(name = "parmJson", value = "查询条件", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson){

        Result result = new Result();
        ParmModel parmModel = (ParmModel) JSONUtil.jsonToObj(parmJson, ParmModel.class);
        try {
            List<AdapterOrg> adapterOrgs = adapterOrgManager.searchAdapterOrgs(parmModel);
            Integer totalCount = adapterOrgManager.searchAdapterOrgInt(parmModel);
            result = getResult(adapterOrgs,totalCount, parmModel.getPage(), parmModel.getRows());
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "/info" , method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Object getAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code){

        Result result = new Result();
        try{
            AdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
            result.setObj(adapterOrg);
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result;
    }


    //新增采集标准
    @RequestMapping(value = "/addAdapterOrg")
    @ApiOperation(value = "新增采集标准")
    public Object addAdapterOrg(AdapterOrg adapterOrgModel){
        adapterOrgManager.addAdapterOrg(adapterOrgModel);
//        Result result = new Result();
//        try {
//            XAdapterOrg adapterOrg = new AdapterOrg();
//            String code=adapterOrgModel.getCode();
//            if (adapterOrgManager.getAdapterOrg(code)!=null){
//                result.setErrorMsg("该机构已存在采集标准");
//                result.setSuccessFlg(false);
//            }else {
//                adapterOrg.setCode(code);
//                adapterOrg.setName(adapterOrgModel.getName());
//                adapterOrg.setDescription(adapterOrgModel.getDescription());
//                adapterOrg.setParent(adapterOrgModel.getParent());
//                adapterOrg.setOrg(orgManager.getOrg(adapterOrgModel.getOrg()));
//                adapterOrg.setType(conventionalDictEntry.getAdapterType(adapterOrgModel.getType()));
//                adapterOrg.setArea(adapterOrgModel.getArea());
//                adapterOrgManager.addAdapterOrg(adapterOrg);
//                result.setSuccessFlg(true);
//            }
//            return result.toJson();
//        }catch (Exception e){
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
        return null;
    }

//    //更新采集标准
//    @RequestMapping(value = "/adapterOrg" , method = RequestMethod.PUT)
//    @ApiOperation(value = "更新采集标准")
//    public Object updateAdapterOrg(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "code", value = "代码", defaultValue = "")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "名称", defaultValue = "")
//            @RequestParam(value = "name") String name,
//            @ApiParam(name = "description", value = "描述", defaultValue = "")
//            @RequestParam(value = "description") String description){
////        try {
////            XAdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
////            adapterOrg.setName(name);
////            adapterOrg.setDescription(description);
////            adapterOrgManager.updateAdapterOrg(adapterOrg);
////            Result result = getSuccessResult(true);
////            return result.toJson();
////        }catch (Exception e){
////            Result result = getSuccessResult(false);
////            return result.toJson();
////        }
//        return null;
//    }
//
//    //删除采集标准
//    @RequestMapping(value = "/adapterOrg" , method = RequestMethod.DELETE)
//    @ApiOperation(value = "删除采集标准")
//    public Object delAdapterOrg(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "code", value = "代码", defaultValue = "")
//            @RequestParam(value = "code") String code){
////        String codeTemp[] = code.split(",");
////        List<String> codes = Arrays.asList(codeTemp);
////        int rtn = adapterOrgManager.deleteAdapterOrg(codes);
////        Result result = rtn>0?getSuccessResult(true):getSuccessResult(false);
////        return result.toJson();
//        return null;
//    }
//
//    //获取初始标准列表
//    @RequestMapping(value = "/adapterOrgList" , method = RequestMethod.GET)
//    @ApiOperation(value = "获取初始标准列表")
//    public Object getAdapterOrgList(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "type", value = "类型", defaultValue = "")
//            @RequestParam(value = "type") String type){
//        //根据类型获取所有采集标准
////        Result result = new Result();
////        try{
////            List<XAdapterOrg> adapterOrgList=adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType(type));
////            switch (type) {
////                case "1":
////                    //厂商，初始标准只能是厂商
////                    break;
////                case "2":
////                    //医院，初始标准没有限制
////                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1")));
////                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("3")));
////                    break;
////                case "3":
////                    //区域,初始标准只能选择厂商或区域
////                    adapterOrgList.addAll(adapterOrgManager.searchAdapterOrg(conventionalDictEntry.getAdapterType("1")));
////                    break;
////            }
////            List<String> adapterOrgs = new ArrayList<>();
////            if (!adapterOrgList.isEmpty()){
////                for (XAdapterOrg adapterOrg : adapterOrgList) {
////                    adapterOrgs.add(adapterOrg.getCode()+','+adapterOrg.getName());
////                }
////            }
////            result.setObj(adapterOrgs);
////            result.setSuccessFlg(true);
////        }catch (Exception ex){
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//        return null;
//    }
//
//    //采集机构列表
//    @RequestMapping(value = "/orgList" , method = RequestMethod.GET)
//    @ApiOperation(value = "采集机构列表")
//    public Object getOrgList(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "type", value = "类型", defaultValue = "")
//            @RequestParam(value = "type") String type){
////        Result result = new Result();
////        String searchWay="";
////        if (type.equals("1")){
////            //厂商
////            searchWay = "ThirdPartyPlatform";
////        }else if (type.equals("2")){
////            //医院
////            searchWay="Hospital";
////        }
////        try{
////            List<XOrganization> organizations = orgManager.search(searchWay);
////            List<String> orgs = new ArrayList<>();
////            if (!organizations.isEmpty()){
////                for (XOrganization organization : organizations) {
////                    orgs.add(organization.getOrgCode() + ',' + organization.getFullName());
////                }
////            }
////            result.setObj(orgs);
////            result.setSuccessFlg(true);
////        }catch (Exception ex){
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//        return null;
//    }
//
//    //查询机构列表
//    @RequestMapping(value = "/orgList/search" , method = RequestMethod.GET)
//    @ApiOperation(value = "查询机构列表")
//    public Object searchOrgList(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "type", value = "类型", defaultValue = "")
//            @RequestParam(value = "type") String type,
//            @ApiParam(name = "param", value = "条件", defaultValue = "")
//            @RequestParam(value = "param") String param,
//            @ApiParam(name = "page", value = "当前页", defaultValue = "")
//            @RequestParam(value = "page") int page,
//            @ApiParam(name = "rows", value = "行数", defaultValue = "")
//            @RequestParam(value = "rows") int rows){
////        param = param==null?"":param;
////        String searchWay="";
////        if (type.equals("1")){
////            //厂商
////            searchWay = "ThirdPartyPlatform";
////        }else if (type.equals("2")){
////            //医院
////            searchWay="Hospital";
////        }
////        Map<String, Object> conditionMap = new HashMap<>();
////        conditionMap.put("orgCode", param);
////        conditionMap.put("fullName", param);
////        conditionMap.put("orgType", searchWay);
////        conditionMap.put("page", page);
////        conditionMap.put("pageSize", rows);
////
////        Result result = new Result();
////        try{
////            List<XOrganization> organizations = orgManager.search(conditionMap);
////            int total = orgManager.searchInt(conditionMap);
////            result = getResult(organizations, total, page, rows);
////            result.setSuccessFlg(true);
////        }catch (Exception ex){
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//        return null;
//    }
//
//    @RequestMapping(value = "/adapterOrgList" , method = RequestMethod.GET)
//    @ApiOperation(value = "适配器机构列表")
//    public Object searchAdapterOrgList(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "type", value = "类型", defaultValue = "")
//            @RequestParam(value = "type") String type,
//            @ApiParam(name = "param", value = "条件", defaultValue = "")
//            @RequestParam(value = "param") String param,
//            @ApiParam(name = "page", value = "当前页", defaultValue = "")
//            @RequestParam(value = "page") int page,
//            @ApiParam(name = "rows", value = "行数", defaultValue = "")
//            @RequestParam(value = "rows") int rows){
////        Result result = new Result();
////        Map<String, Object> conditionMap = new HashMap<>();
////        param = param==null?"":param;
////        conditionMap.put("key", param);
////        conditionMap.put("page", page);
////        conditionMap.put("pageSize", rows);
////        try{
////            List typeLs = new ArrayList<>();
////            conditionMap.put("typeLs", conventionalDictEntry.getAdapterType(type));
////            switch (type) {
////                case "1":
////                    //厂商，初始标准只能是厂商
////                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
////                    break;
////                case "2":
////                    //医院，初始标准没有限制
////                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
////                    typeLs.add(conventionalDictEntry.getAdapterType("2"));
////                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
////                    break;
////                case "3":
////                    //区域,初始标准只能选择厂商或区域
////                    typeLs.add(conventionalDictEntry.getAdapterType("1"));
////                    typeLs.add(conventionalDictEntry.getAdapterType("3"));
////                    break;
////            }
////            conditionMap.put("typeLs", typeLs);
////            List<XAdapterOrg> adapterOrgList=adapterOrgManager.searchAdapterOrg(conditionMap);
////            int total = adapterOrgManager.searchAdapterOrgInt(conditionMap);
////            result = getResult(adapterOrgList, total, page, rows);
////            result.setSuccessFlg(true);
////        }catch (Exception ex){
////            result.setSuccessFlg(false);
////        }
////        return result.toJson();
//        return null;
//    }
}
