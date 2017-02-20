package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.ItResourceModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MItResource;
import com.yihu.ehr.portal.service.ItResourceClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/20.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "itResource", description = "可下载资源管理接口", tags = {"资源下载管理接口"})
public class InResourceController  extends BaseController {

    @Autowired
    private ItResourceClient itResourceClient;

    @RequestMapping(value = "/itResource/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取可下载资源列表", notes = "根据查询条件获取下载资源列表在前端表格展示")
    public Envelop searchItResources(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            List<ItResourceModel> itResourceModels = new ArrayList<>();
            ResponseEntity<List<MItResource>> responseEntity = itResourceClient.searchItResources(fields, filters, sorts, size, page, request, response);
            List<MItResource> itResources = responseEntity.getBody();
            for (MItResource itResource : itResources) {
                ItResourceModel itResourceModel = convertToModel(itResource,ItResourceModel.class);
                itResourceModels.add(itResourceModel);
            }
            int totalCount = getTotalCount(responseEntity);
            return getResult(itResourceModels, totalCount, page, size);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    /**
     * 新增资源信息
     * @param itResourceJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/itResource" , method = RequestMethod.POST)
    @ApiOperation(value = "新增资源信息")
    public Envelop createItResource(
            @ApiParam(name = "itResourceJsonData", value = " 资源信息Json", defaultValue = "")
            @RequestParam(value = "itResourceJsonData", required = false) String itResourceJsonData){
        try {
            String errorMsg = "";
            ItResourceModel itResourceModel = objectMapper.readValue(itResourceJsonData, ItResourceModel.class);
            MItResource mItResource = convertToModel(itResourceModel, MItResource.class);
            if (StringUtils.isEmpty(mItResource.getName())) {
                errorMsg+="资源名称不能为空！";
            }
            if (StringUtils.isEmpty(mItResource.getUrl())) {
                errorMsg+="资源下载地址不能为空！";
            }

            if(StringUtils.isNotEmpty(errorMsg))
            {
                return failed(errorMsg);
            }

            String deptMemberJsonStr = objectMapper.writeValueAsString(mItResource);
            MItResource itResource = itResourceClient.saveItResource(deptMemberJsonStr);
            if (itResource == null) {
                return failed("保存失败!");
            }
            return success(itResource);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    /**
     *  TODO 暂定接口，无用可删除
     * 新增资源信息
     * @param itResourceJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/itResource" , method = RequestMethod.PUT)
    @ApiOperation(value = "新增资源信息")
    public Envelop uopdateItResource(
            @ApiParam(name = "itResourceJsonData", value = " 资源信息Json", defaultValue = "")
            @RequestParam(value = "itResourceJsonData", required = false) String itResourceJsonData){
        try {
            String errorMsg = "";
            ItResourceModel itResourceModel = objectMapper.readValue(itResourceJsonData, ItResourceModel.class);
            MItResource mItResource = convertToModel(itResourceModel, MItResource.class);
            if (StringUtils.isEmpty(mItResource.getName())) {
                errorMsg+="资源名称不能为空！";
            }
            if (StringUtils.isEmpty(mItResource.getUrl())) {
                errorMsg+="资源下载地址不能为空！";
            }

            if(StringUtils.isNotEmpty(errorMsg))
            {
                return failed(errorMsg);
            }

            String deptMemberJsonStr = objectMapper.writeValueAsString(mItResource);
            MItResource itResource = itResourceClient.saveItResource(deptMemberJsonStr);
            if (itResource == null) {
                return failed("修改失败!");
            }
            return success(itResource);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/itResource/delete" , method = RequestMethod.POST)
    @ApiOperation(value = "删除可下载资源信息")
    public boolean deleteItResource(
            @ApiParam(name = "itResourceId", value = "下载资源ID")
            @RequestParam(value = "itResourceId", required = true) Integer itResourceId
    ){
        try {
            boolean succ = itResourceClient.deleteItResource(itResourceId);
            return succ;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
}
