package com.yihu.ehr.adaption.adapterorg.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrg;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrgManager;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.parm.FieldCondition;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/** 采集标准
 * Created by lincl on 2016.1.27
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapterorg")
@Api(protocols = "https", value = "adapterorg", description = "第三方标准管理接口", tags = {"第三方标准"})
public class AdapterOrgController extends BaseRestController {
    @Autowired
    private AdapterOrgManager adapterOrgManager;


    //适配采集标准
    @RequestMapping(value = "/page" , method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Object searchAdapterOrg(
            @ApiParam(name = "parmJson", value = "查询条件", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson){

        Result result = new Result();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
            pageModel.setModelClass(AdapterOrg.class);
            List<AdapterOrg> adapterOrgs = adapterOrgManager.searchAdapterOrg(pageModel);
            Integer totalCount = adapterOrgManager.searchAdapterOrgInt(pageModel);
            result = getResult(adapterOrgs, totalCount, pageModel.getPage(), pageModel.getRows());
            result.setSuccessFlg(true);
        }catch (Exception ex){
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "/info" , method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Object getAdapterOrg(
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
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "新增采集标准")
    public Object addAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "adapterOrgModel", value = "采集机构json模型", defaultValue = "")
            @RequestParam(value = "adapterOrgModel", required = false) String adapterOrgModel){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AdapterOrg adapterOrg = objectMapper.readValue(adapterOrgModel, AdapterOrg.class);
            if (adapterOrgManager.getAdapterOrg(adapterOrg.getCode())!=null){
                failed(ErrorCode.ExistOrgForCreate, "该机构已存在采集标准！");
            }
            adapterOrgManager.addAdapterOrg(adapterOrg, apiVersion);
            return true;
        }catch (ApiException e){
            failed(e.getErrorCode(), e.getErrMsg());
            return false;
        }catch (Exception e){
            failed(ErrorCode.SaveFailed, "保存失败！", e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "" , method = RequestMethod.PUT)
    @ApiOperation(value = "更新采集标准")
    public Object updateAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description){
        try {
            AdapterOrg adapterOrg = adapterOrgManager.getAdapterOrg(code);
            adapterOrg.setName(name);
            adapterOrg.setDescription(description);
            adapterOrgManager.saveAdapterOrg(adapterOrg);
            return true;
        }catch (Exception e){
            failed(ErrorCode.SaveFailed, "更新失败！", e.getMessage());
            return false;
        }
    }

    @RequestMapping(value = "" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除采集标准")
    public Object delAdapterOrg(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code){
        try {
            adapterOrgManager.deleteAdapterOrg(code.split(","));
        }catch (Exception e){
            failed(ErrorCode.SaveFailed, "删除失败！", e.getMessage());
            return false;
        }
        return true;
    }

    //获取初始标准列表  重复
    @RequestMapping(value = "/list" , method = RequestMethod.GET)
    @ApiOperation(value = "获取初始标准列表")
    public Object getAdapterOrgList(
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "")
            @RequestParam(value = "rows") int rows){
        //根据类型获取所有采集标准
        Result result = new Result();
        try {
            PageModel pageModel = new PageModel(page, rows);
            FieldCondition fieldCondition = new FieldCondition("type","in", "1");
            //厂商，初始标准只能是厂商
            if("2".equals(type)){
                //医院，初始标准没有限制
                fieldCondition.addVal("1", "3");
            }
            else if("3".equals(type)){
                //区域,初始标准只能选择厂商或区域
                fieldCondition.addVal("1");
            }
            pageModel.addFieldCondition(fieldCondition);
            List<AdapterOrg> ls = adapterOrgManager.searchAdapterOrg(pageModel);
            Integer totalCount = adapterOrgManager.searchAdapterOrgInt(pageModel);
            List<String> adapterOrgs = new ArrayList<>();
            for (AdapterOrg adapterOrg : ls) {
                adapterOrgs.add(adapterOrg.getCode() + ',' + adapterOrg.getName());
            }
            result = getResult(adapterOrgs, totalCount, pageModel.getPage(), pageModel.getRows());
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    /*********************************************************************************/
    /**                                                                            **/
    /**             到organization接口查询                                         **/
    /*********************************************************************************/
    //采集机构列表  到organization接口查询
    @RequestMapping(value = "/orgList" , method = RequestMethod.GET)
    @ApiOperation(value = "采集机构列表")
    public Object getOrgList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type){
        return "";
    }

    //查询机构列表  到organization接口调用
    @RequestMapping(value = "/orgList/search" , method = RequestMethod.GET)
    @ApiOperation(value = "查询机构列表")
    public Object searchOrgList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "param", value = "条件", defaultValue = "")
            @RequestParam(value = "param") String param,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows){
        return "";
    }
}
