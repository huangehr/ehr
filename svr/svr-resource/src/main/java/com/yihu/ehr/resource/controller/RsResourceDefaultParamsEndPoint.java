package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MResourceDefaultParam;
import com.yihu.ehr.resource.model.RsResourceDefaultParam;
import com.yihu.ehr.resource.service.RsResourceDefaultParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by shine on 2016/6/27.
 */
@RestController
@RequestMapping(value= ApiVersion.Version1_0)
@Api(value = "ResourceDefaultParams", description = "资源默认参数")
public class RsResourceDefaultParamsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsResourceDefaultParamService resourceDefaultParamService;

    @RequestMapping(value = ServiceApi.Resources.ParamById,method = RequestMethod.GET)
    @ApiOperation("根据id获取参数信息")
    public MResourceDefaultParam getResourceDefaultParamById(
            @ApiParam(name = "id", value = "资源默认参数信息id")
            @PathVariable(value = "id") String id){
        RsResourceDefaultParam resourceDefaultParam = resourceDefaultParamService.findById(id);
        if(resourceDefaultParam == null){
            return null;
        }
        return convertToModel(resourceDefaultParam, MResourceDefaultParam.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Param,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("增加资源默认参数")
    public MResourceDefaultParam addResourceDefaultParams(
            @ApiParam(name = "json_data", value = "资源默认参数json串")
            @RequestBody String jsonData){
        RsResourceDefaultParam resourceDefaultParam = toEntity(jsonData, RsResourceDefaultParam.class);
        RsResourceDefaultParam resourceDefaultParamNew = resourceDefaultParamService.save(resourceDefaultParam);
        return convertToModel(resourceDefaultParamNew, MResourceDefaultParam.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Param,method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新资源默认参数")
    public MResourceDefaultParam updateResourceDefaultParams(
            @ApiParam(name = "json_data", value = "资源默认参数json串")
            @RequestBody String jsonData){
        RsResourceDefaultParam resourceDefaultParam = toEntity(jsonData, RsResourceDefaultParam.class);
        RsResourceDefaultParam resourceDefaultParamNew = resourceDefaultParamService.save(resourceDefaultParam);
        return convertToModel(resourceDefaultParamNew, MResourceDefaultParam.class);
    }

    @RequestMapping(value = ServiceApi.Resources.ParamById,method = RequestMethod.DELETE)
    @ApiOperation("根据id删除资源默认参数")
    public boolean deleteResourceDefaultParams(
            @ApiParam(name="id",value="资源默认参数id")
            @PathVariable(value="id") String id){
        resourceDefaultParamService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.Params, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源默认参数列表，分页")
    public List<MResourceDefaultParam> searchRsDefaultParams(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,resourcesId,resourcesCode,paramKey,paramValue")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsResourceDefaultParam>  rsDefaultParams = resourceDefaultParamService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, resourceDefaultParamService.getCount(filters), page, size);
        return (List<MResourceDefaultParam>) convertToModels(rsDefaultParams, new ArrayList<MResourceDefaultParam>(rsDefaultParams.size()), MResourceDefaultParam.class, fields);
    }

    @RequestMapping(value = ServiceApi.Resources.ParamsNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源默认参数列表，不分页")
    public Collection<MResourceDefaultParam> searchRsDefaultParamsNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<RsResourceDefaultParam> rsDefaultParams = resourceDefaultParamService.search(filters);
        return convertToModels(rsDefaultParams, new ArrayList<MResourceDefaultParam>(rsDefaultParams.size()), MResourceDefaultParam.class,null);
    }

    @RequestMapping(value = ServiceApi.Resources.ParamKeyValueExistence, method = RequestMethod.GET)
    @ApiOperation(value = "同个资源下同个参数名所对应的参数值不重复验证")
    public boolean isExistenceRsParamKeyValue(
            @ApiParam(name = "resources_id", value = "过资源id")
            @RequestParam(value = "resources_id") String resourcesId,
            @ApiParam(name = "param_key", value = "默认参数名")
            @RequestParam(value = "param_key") String paramKey,
            @ApiParam(name = "param_value", value = "默认参数值")
            @RequestParam(value = "param_value") String paramValue){
        String[] fields = {"resourcesId","paramKey","paramValue"};
        String[] values = {resourcesId,paramKey,paramValue};
        List<RsResourceDefaultParam> list = resourceDefaultParamService.findByFields(fields, values);
        if(list != null && list.size()>0){
            return true;
        }
        return false;
    }
}
