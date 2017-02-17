package com.yihu.ehr.portal.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MItResource;
import com.yihu.ehr.portal.model.ItResource;
import com.yihu.ehr.portal.service.ItResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/17.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "itResource", description = "可下载资源管理服务", tags = {"下载资源管理"})
public class ItResourceEndPoint  extends EnvelopRestEndPoint {

    @Autowired
    private ItResourceService resourceService;

    @RequestMapping(value = "/itResource/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件 查询可下载资源列表")
    public List<MItResource> searchItResources(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,deptId,deptName,dutyName,userName")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestBody(required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "+userName,+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        List<ItResource> itResourceList = resourceService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, resourceService.getCount(filters), page, size);
        return (List<MItResource>) convertToModels(itResourceList, new ArrayList<MItResource>(itResourceList.size()), MItResource.class, fields);
    }


    @RequestMapping(value = "/itResource/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增-可下载资源信息")
    public MItResource saveItResource(
            @ApiParam(name = "itResourceJsonData", value = "资源信息json数据")
            @RequestBody String itResourceJsonData
    ) throws Exception {
        ItResource itResource = toEntity(itResourceJsonData, ItResource.class);
        itResource.setStatus(0);
        resourceService.save(itResource);
        return convertToModel(itResource, MItResource.class);
    }

    @RequestMapping(value = "/itResource/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改-可下载资源信息")
    public MItResource updateItResource(
            @ApiParam(name = "itResourceJsonData", value = "资源json数据")
            @RequestBody String itResourceJsonData
    ) throws Exception {
        ItResource itResource = toEntity(itResourceJsonData, ItResource.class);
        resourceService.save(itResource);
        return convertToModel(itResource, MItResource.class);
    }

    @RequestMapping(value = "/itResource/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除-资源信息" )
    public boolean deleteItResource(
            @ApiParam(name = "itResourceId", value = "资源ID")
            @RequestParam(value = "itResourceId", required = true) Integer itResourceId
    ) throws Exception {

        resourceService.deleteItResource(itResourceId);
        return true;
    }

}
