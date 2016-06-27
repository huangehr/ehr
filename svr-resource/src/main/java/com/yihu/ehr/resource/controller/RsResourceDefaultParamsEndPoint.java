package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.model.ResourceDefaultParam;
import com.yihu.ehr.resource.service.ResourceDefaultParamService;
import com.yihu.ehr.resource.service.RsAdapterDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shine on 2016/6/27.
 */
@RestController
@RequestMapping(value= ApiVersion.Version1_0)
@Api(value = "ResourceDefaultParams", description = "资源默认参数")
public class RsResourceDefaultParamsEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private ResourceDefaultParamService resourceDefaultParamService;

    @Autowired
    private ResourcesDao resourcesDao;

    @RequestMapping(value = ServiceApi.Resources.Params,method = RequestMethod.POST)
    @ApiOperation("增加资源默认参数")
    public ResourceDefaultParam addResourceDefaultParams(
            @ApiParam(name="resourcesId",value="资源ID",defaultValue = "")
            @RequestParam(value="resourcesId" ,required = false) String resourcesId,
            @ApiParam(name="resourcesCode",value="资源代码",defaultValue = "")
            @RequestParam(value="resourcesCode" ,required = false) String resourcesCode,
            @ApiParam(name="paramKey",value="默认参数key",defaultValue = "")
            @RequestParam(value="paramKey" ,required = true) String paramKey,
            @ApiParam(name="paramValue",value="默认参数value",defaultValue = "")
            @RequestParam(value="paramValue" ,required = true) String paramValue)throws Exception{
        if(resourcesCode==null&&resourcesId==null)
            throw new Exception("资源代码和资源ID不能全为空");
        if(resourcesCode==null&&resourcesDao.findById(resourcesId)!=null){
            resourcesCode=resourcesDao.findById(resourcesId).getCode();
        }
        else if(resourcesId==null && resourcesDao.findByCode(resourcesCode)!=null){
            resourcesId = resourcesDao.findByCode(resourcesCode).getId();
        }
        ResourceDefaultParam resourceDefaultParam=new ResourceDefaultParam();
        resourceDefaultParam.setParamKey(paramKey);
        resourceDefaultParam.setParamValue(paramValue);
        resourceDefaultParam.setResourcesId(resourcesId);
        resourceDefaultParam.setResourcesCode(resourcesCode);
        resourceDefaultParamService.save(resourceDefaultParam);
        return convertToModel(resourceDefaultParam, ResourceDefaultParam.class);
    }

    @RequestMapping(value = ServiceApi.Resources.Params,method = RequestMethod.GET)
    @ApiOperation("查找资源默认参数")
    public List<ResourceDefaultParam> searchResourceDefaultParams(
            @ApiParam(name="resourcesId",value="资源ID",defaultValue = "")
            @RequestParam(value="resourcesId" ,required = false) String resourcesId,
            @ApiParam(name="resourcesCode",value="资源代码",defaultValue = "")
            @RequestParam(value="resourcesCode" ,required = false) String resourcesCode,
            @ApiParam(name="paramKey",value="默认参数key",defaultValue = "")
            @RequestParam(value="paramKey" ,required = false) String paramKey)throws Exception{
        if(resourcesCode==null&&resourcesId==null)
            throw new Exception("资源代码和资源ID不能全为空");
        if(paramKey==null){
            List<ResourceDefaultParam> r= resourceDefaultParamService.findByResourcesIdOrResourcesCode(resourcesId, resourcesCode);
            return (List<ResourceDefaultParam>) convertToModels(r, new ArrayList<ResourceDefaultParam>(r.size()), ResourceDefaultParam.class, null);

        }
        else {
            List<ResourceDefaultParam> r = resourceDefaultParamService.findByResourcesIdOrResourcesCodeWithParamKey(resourcesId, resourcesCode, paramKey);
            return (List<ResourceDefaultParam>) convertToModels(r, new ArrayList<ResourceDefaultParam>(r.size()), ResourceDefaultParam.class, null);
        }
    }

    @RequestMapping(value = ServiceApi.Resources.Params,method = RequestMethod.PUT)
    @ApiOperation("更新资源默认参数")
    public ResourceDefaultParam updataResourceDefaultParams(
            @ApiParam(name="resourcesId",value="资源ID",defaultValue = "")
            @RequestParam(value="resourcesId" ,required = false) String resourcesId,
            @ApiParam(name="resourcesCode",value="资源代码",defaultValue = "")
            @RequestParam(value="resourcesCode" ,required = false) String resourcesCode,
            @ApiParam(name="paramKey",value="默认参数key",defaultValue = "")
            @RequestParam(value="paramKey" ,required = true) String paramKey,
            @ApiParam(name="paramValue",value="默认参数value",defaultValue = "")
            @RequestParam(value="paramValue" ,required = true) String paramValue)throws Exception{
        if(resourcesCode==null&&resourcesId==null)
            throw new Exception("资源代码和资源ID不能全为空");
        if(resourcesCode==null){
            resourcesCode=resourcesDao.findById(resourcesId).getCode();
        }
        else if(resourcesId==null){
            resourcesId=resourcesDao.findByCode(resourcesCode).getId();
        }
        ResourceDefaultParam resourceDefaultParam=new ResourceDefaultParam();
        resourceDefaultParam.setParamKey(paramKey);
        resourceDefaultParam.setParamValue(paramValue);
        resourceDefaultParam.setResourcesId(resourcesId);
        resourceDefaultParam.setResourcesCode(resourcesCode);
        List<ResourceDefaultParam> r=resourceDefaultParamService.findByResourcesIdOrResourcesCodeWithParamKey(resourcesId, resourcesCode, paramKey);
        if(r==null)
           throw new Exception("找不到该参数");
        else
             resourceDefaultParamService.save(resourceDefaultParam);
        return convertToModel(resourceDefaultParamService, ResourceDefaultParam.class);

    }
    @RequestMapping(value = ServiceApi.Resources.Params,method = RequestMethod.DELETE)
    @ApiOperation("删除资源默认参数")
    public boolean deleteResourceDefaultParams(
            @ApiParam(name="resourcesId",value="资源ID",defaultValue = "")
            @RequestParam(value="resourcesId" ,required = false) String resourcesId,
            @ApiParam(name="resourcesCode",value="资源代码",defaultValue = "")
            @RequestParam(value="resourcesCode" ,required = false) String resourcesCode,
            @ApiParam(name="paramKey",value="默认参数key",defaultValue = "")
            @RequestParam(value="paramKey" ,required = true) String paramKey)throws Exception{
        if(resourcesCode==null&&resourcesId==null)
            throw new Exception("资源代码和资源ID不能全为空");
        List<ResourceDefaultParam> r=resourceDefaultParamService.findByResourcesIdOrResourcesCodeWithParamKey(resourcesId, resourcesCode, paramKey);
        if(r==null||r.size()==0)
            throw new Exception("找不到该参数");
        else
            resourceDefaultParamService.delete(r.get(0).getId());
        return true;
    }
}
