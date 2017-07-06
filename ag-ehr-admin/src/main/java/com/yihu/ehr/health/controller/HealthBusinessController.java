package com.yihu.ehr.health.controller;

import com.yihu.ehr.agModel.health.HealthBusinessModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.health.service.HealthBusinessClient;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.health.MHealthBusiness;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/22.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "HealthBusiness", description = "指标分类管理", tags = {"指标分类-管理"})
public class HealthBusinessController extends BaseController {
    @Autowired
    HealthBusinessClient healthBusinessClient;

    @RequestMapping(value = "/healthBusiness/pageList", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询指标分类列表")
    public Envelop getHealthBusinessList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        ListResult listResult = healthBusinessClient.getHealthBusinessList(fields, filters, sorts, size, page);

        List<HealthBusinessModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                HealthBusinessModel mHealthBusiness = objectMapper.convertValue(map,HealthBusinessModel.class);
                if (mHealthBusiness.getParentId() != 0) {
                    MHealthBusiness parent = healthBusinessClient.searchHealthBusinessDetail(mHealthBusiness.getParentId());
                    mHealthBusiness.setParentName(parent.getName());
                }
                mainModelList.add(mHealthBusiness);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @ApiOperation(value = "指标分类列表，不分页")
    @RequestMapping(value = "/healthBusiness/list", method = RequestMethod.GET)
    public Envelop getAllHealthBusiness() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(healthBusinessClient.getAllHealthBusiness());
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "根据父ID获取子指标分类列表")
    @RequestMapping(value = "/healthBusiness/childs", method = RequestMethod.GET)
    public Envelop searchChildHealthBusiness(
            @ApiParam(name = "parentId", value = "父ID" )
            @RequestParam(value = "parentId") Integer parentId) {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(healthBusinessClient.searchChildHealthBusiness(parentId));
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/healthBusiness/detailById" , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    public Envelop detail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id){
        try {
            String errorMsg = "";

            if (id == null) {
                errorMsg += "id不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MHealthBusiness mHealthBusiness = healthBusinessClient.searchHealthBusinessDetail(id);
            if (mHealthBusiness == null) {
                return failed("获取详情失败!");
            }
            if (mHealthBusiness.getParentId() != 0) {
                MHealthBusiness parent = healthBusinessClient.searchHealthBusinessDetail(mHealthBusiness.getParentId());
                mHealthBusiness.setParentName(parent.getName());
            }
            return success(mHealthBusiness);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/healthBusiness/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标分类")
    boolean deleteHealthBusiness(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id) {
        try {
            boolean succ = healthBusinessClient.deleteHealthBusiness(id);
            return succ;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/healthBusiness/checkName" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查名称是否唯一")
    public Envelop checkName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name", required = true) String name){
        try {
            Envelop envelop = new Envelop();
            if (StringUtils.isEmpty(name)) {
                envelop.setErrorMsg("名称不能为空！");
            }
            int num = healthBusinessClient.getCountByName(name);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("已经存在此名称!");
            }else{
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/healthBusiness/checkCode" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查编码是否唯一")
    public Envelop checkCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code", required = true) String code){
        try {
            Envelop envelop = new Envelop();
            if (StringUtils.isEmpty(code)) {
                envelop.setErrorMsg("编码不能为空！");
            }
            int num = healthBusinessClient.getCountByCode(code);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("已经存在此编码!");
            }else{
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/healthBusiness/add" , method = RequestMethod.POST)
    @ApiOperation(value = "新增指标分类")
    public Envelop create(
            @ApiParam(name = "jsonData", value = " 指标分类信息Json", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData){
        try {
            String errorMsg = "";
            HealthBusinessModel healthBusinessModel = objectMapper.readValue(jsonData, HealthBusinessModel.class);

            if (StringUtils.isEmpty(healthBusinessModel.getCode())) {
                errorMsg+="编码不能为空！";
            }
            if (StringUtils.isEmpty(healthBusinessModel.getName())) {
                errorMsg+="名称不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            String json = objectMapper.writeValueAsString(healthBusinessModel);
            MHealthBusiness mHealthBusiness = healthBusinessClient.saveHealthBusinesst(json);
            if (mHealthBusiness == null) {
                return failed("保存失败!");
            }
            return success(mHealthBusiness);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/healthBusiness/update" , method = RequestMethod.POST)
    @ApiOperation(value = "修改指标分类")
    public Envelop resetInfo(
            @ApiParam(name = "jsonData", value = " 指标分类信息Json", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData){
        try {
            String errorMsg = "";
            HealthBusinessModel healthBusinessModel = objectMapper.readValue(jsonData, HealthBusinessModel.class);
            if (null == healthBusinessModel) {
                errorMsg += "内容出错！";
            }
            if (StringUtils.isEmpty(healthBusinessModel.getName())) {
                errorMsg += "名称不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            String json = objectMapper.writeValueAsString(healthBusinessModel);
            MHealthBusiness mHealthBusiness = healthBusinessClient.updateHealthBusiness(json);
            if (mHealthBusiness == null) {
                return failed("修改指标分类失败!");
            }
            return success(mHealthBusiness);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }
}
