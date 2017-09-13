package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.ResourceDefaultParamModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.resource.MResourceDefaultParam;
import com.yihu.ehr.resource.client.RsResourceDefaultParamClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yww on 2016/7/20.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0+"/admin")
@Api(value = "resourceDefaultParam", description = "资源默认参数配置", tags = {"资源管理-资源默认参数配置"})
public class RsResourceDefaultParamController extends BaseController {
    @Autowired
    private RsResourceDefaultParamClient rsDefaultParamclient;

    @RequestMapping(value = ServiceApi.Resources.ParamById,method = RequestMethod.GET)
    @ApiOperation("根据id获取参数信息")
    public Envelop getResourceDefaultParamById(
            @ApiParam(name = "id", value = "资源默认参数信息id")
            @PathVariable(value = "id") String id){
        MResourceDefaultParam mResourceDefaultParam = rsDefaultParamclient.getResourceDefaultParamById(id);
        if(mResourceDefaultParam == null){
            return failed("获取失败！");
        }
        return success(convertToModel(mResourceDefaultParam, ResourceDefaultParamModel.class));
    }

    @RequestMapping(value = ServiceApi.Resources.Param,method = RequestMethod.POST)
    @ApiOperation("增加资源默认参数")
    public Envelop addResourceDefaultParams(
            @ApiParam(name = "json_data", value = "资源默认参数json串")
            @RequestParam(value = "json_data") String jsonData){
        try{
            MResourceDefaultParam model = objectMapper.readValue(jsonData,MResourceDefaultParam.class);
//            boolean bo = rsDefaultParamclient.isExistenceRsParamKeyValue(model.getResourcesId(),model.getParamKey(),model.getParamValue());
//            if(bo){
//                return failed("参数值不能重复！");
//            }
            MResourceDefaultParam modelNew = rsDefaultParamclient.addResourceDefaultParams(jsonData);
            if(modelNew == null){
                return failed("新增失败！");
            }
            return success(convertToModel(modelNew, ResourceDefaultParamModel.class));
        }catch (Exception ex){
            ex.printStackTrace();
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @RequestMapping(value = ServiceApi.Resources.Param,method = RequestMethod.PUT)
    @ApiOperation("更新资源默认参数")
    public Envelop updateResourceDefaultParams(
            @ApiParam(name = "json_data", value = "资源默认参数json串")
            @RequestParam(value = "json_data") String jsonData){
        try{
            MResourceDefaultParam model = objectMapper.readValue(jsonData,MResourceDefaultParam.class);
           /* boolean bo = rsDefaultParamclient.isExistenceRsParamKeyValue(model.getResourcesId(),model.getParamKey(),model.getParamValue());
            if(bo){
                return failed("参数值不能重复！");
            }*/
            MResourceDefaultParam modelNew = rsDefaultParamclient.addResourceDefaultParams(jsonData);
            if(modelNew == null){
                return failed("更新失败！");
            }
            return success(convertToModel(modelNew, ResourceDefaultParamModel.class));
        }catch (Exception ex){
            ex.printStackTrace();
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @RequestMapping(value = ServiceApi.Resources.ParamById,method = RequestMethod.DELETE)
    @ApiOperation("根据id删除资源默认参数")
    public Envelop deleteResourceDefaultParams(
            @ApiParam(name="id",value="资源默认参数id")
            @PathVariable(value="id") String id){
        boolean bo = rsDefaultParamclient.deleteResourceDefaultParams(id);
        if(bo){
            return success(null);
        }
        return failed("删除失败！");
    }

    @RequestMapping(value = ServiceApi.Resources.Params, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源默认参数列表，分页")
    public Envelop searchRsDefaultParams(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,resourcesId,resourcesCode,paramKey,paramValue")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size){
        try{
            ResponseEntity<List<MResourceDefaultParam>> responseEntity  = rsDefaultParamclient.searchRsDefaultParams(fields, filters, sorts, page, size);
            List<MResourceDefaultParam> mResourceDefaultParams = responseEntity.getBody();
            return getResult(mResourceDefaultParams, getTotalCount(responseEntity), page, size);
        }catch (Exception ex){
            ex.printStackTrace();
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @RequestMapping(value = ServiceApi.Resources.ParamsNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源默认参数列表，不分页")
    public Envelop searchRsDefaultParamsNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters){
        try{
            List<MResourceDefaultParam> mResourceDefaultParams  = rsDefaultParamclient.searchRsDefaultParamsNoPage(filters);
            return getResult(mResourceDefaultParams, mResourceDefaultParams.size(), 1, mResourceDefaultParams.size());
        }catch (Exception ex){
            ex.printStackTrace();
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @RequestMapping(value = ServiceApi.Resources.ParamKeyValueExistence, method = RequestMethod.GET)
    @ApiOperation(value = "同个资源下同个参数名所对应的参数值不重复验证")
    public Envelop isExistenceRsParamKeyValue(
            @ApiParam(name = "resources_id", value = "过资源id")
            @RequestParam(value = "resources_id") String resourcesId,
            @ApiParam(name = "param_key", value = "默认参数名")
            @RequestParam(value = "param_key") String paramKey,
            @ApiParam(name = "param_value", value = "默认参数值")
            @RequestParam(value = "param_value") String paramValue){
        boolean bo = rsDefaultParamclient.isExistenceRsParamKeyValue(resourcesId,paramKey,paramValue);
        if(bo){
            return success(null);
        }
        return failed("");
    }
}
