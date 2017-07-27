package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MResourceDefaultParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by yww on 2016/7/20.
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface ResourceDefaultParamClient {

    @RequestMapping(value = ServiceApi.Resources.ParamById,method = RequestMethod.GET)
    @ApiOperation("根据id获取参数信息")
    MResourceDefaultParam getResourceDefaultParamById(
            @ApiParam(name = "id", value = "资源默认参数信息id")
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.Param,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("增加资源默认参数")
    MResourceDefaultParam addResourceDefaultParams(
            @ApiParam(name = "json_data", value = "资源默认参数json串")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.Param,method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新资源默认参数")
    MResourceDefaultParam updateResourceDefaultParams(
            @ApiParam(name = "json_data", value = "资源默认参数json串")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.ParamById,method = RequestMethod.DELETE)
    @ApiOperation("根据id删除资源默认参数")
    boolean deleteResourceDefaultParams(
            @ApiParam(name="id",value="资源默认参数id")
            @PathVariable(value="id") String id);

    @RequestMapping(value = ServiceApi.Resources.Params, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源默认参数列表，分页")
    ResponseEntity<List<MResourceDefaultParam>> searchRsDefaultParams(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,resourcesId,resourcesCode,paramKey,paramValue")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Resources.ParamsNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源默认参数列表，不分页")
    List<MResourceDefaultParam> searchRsDefaultParamsNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = ServiceApi.Resources.ParamKeyValueExistence, method = RequestMethod.GET)
    @ApiOperation(value = "同个资源下同个参数名所对应的参数值不重复验证")
    boolean isExistenceRsParamKeyValue(
            @ApiParam(name = "resources_id", value = "过资源id")
            @RequestParam(value = "resources_id") String resourcesId,
            @ApiParam(name = "param_key", value = "默认参数名")
            @RequestParam(value = "param_key") String paramKey,
            @ApiParam(name = "param_value", value = "默认参数值")
            @RequestParam(value = "param_value") String paramValue);
}
