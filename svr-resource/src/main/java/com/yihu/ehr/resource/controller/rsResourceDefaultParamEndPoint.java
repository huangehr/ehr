package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MResourceDefaultParam;
import com.yihu.ehr.resource.model.ResourceDefaultParam;
import com.yihu.ehr.resource.service.ResourceDefaultParamService;
import com.yihu.ehr.util.controller.EnvelopRestEndPoint;
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
 * @author linaz
 * @created 2016.05.17 16:33
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "resourceDefaultParams", description = "资源默认参数")
public class RsResourceDefaultParamEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourceDefaultParamService resourceDefaultParamService;

    @RequestMapping(value = ServiceApi.Resources.Params, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源默认参数列表", notes = "根据查询条件获取资源默认参数列表")
    public List<MResourceDefaultParam> searchResourceDefaultParams(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<ResourceDefaultParam> resourceDefaultParams = resourceDefaultParamService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, resourceDefaultParamService.getCount(filters), page, size);

        return (List<MResourceDefaultParam>) convertToModels(resourceDefaultParams, new ArrayList<MResourceDefaultParam>(resourceDefaultParams.size()), MResourceDefaultParam.class, fields);
    }



    @RequestMapping(value = ServiceApi.Resources.Params, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建资源默认参数", notes = "创建资源默认参数")
    public MResourceDefaultParam createResourceDefaultParam(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        ResourceDefaultParam resourceDefaultParam = toEntity(jsonData, ResourceDefaultParam.class);
        resourceDefaultParam.setId(getObjectId(BizObject.RsParam));
        resourceDefaultParamService.save(resourceDefaultParam);
        return convertToModel(resourceDefaultParam, MResourceDefaultParam.class, null);

    }

    @RequestMapping(value = ServiceApi.Resources.Params, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改资源默认参数", notes = "修改资源默认参数")
    public MResourceDefaultParam updateResourceDefaultParam(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        ResourceDefaultParam resourceDefaultParam = toEntity(jsonData, ResourceDefaultParam.class);
        resourceDefaultParamService.save(resourceDefaultParam);
        return convertToModel(resourceDefaultParam, MResourceDefaultParam.class, null);
    }


    @RequestMapping(value = ServiceApi.Resources.Param, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源默认参数", notes = "删除资源默认参数")
    public boolean deleteResourceDefaultParam(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        
        resourceDefaultParamService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.Param, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取资源默认参数", notes = "根据id获取获取资源默认参数")
    public MResourceDefaultParam getResourceDefaultParamById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @RequestParam(value = "id") String id) {
        ResourceDefaultParam resourceDefaultParam = resourceDefaultParamService.findById(id);
        return convertToModel(resourceDefaultParam, MResourceDefaultParam.class);
    }


}