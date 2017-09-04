package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.resource.MResourceQuota;
import com.yihu.ehr.resource.model.ResourceQuota;
import com.yihu.ehr.resource.service.ResourceQuotaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/resourcesQuota")
@Api(value = "rs_resources_quota", description = "资源视图指标")
public class ResourceQuotaEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private ResourceQuotaService resourceQuotaService;

    @RequestMapping(value = ServiceApi.Resources.SearchInfo, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标")
    public ListResult search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        ListResult listResult = new ListResult();
        List<ResourceQuota> list = resourceQuotaService.search(fields, filters, sorts, page, size);
        if(list != null){
            listResult.setDetailModelList(list);
            listResult.setTotalCount((int)resourceQuotaService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Resources.GetRQNameByResourceId, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标-根据resourceId查询")
    public List<Integer> getRQNameByResourceId(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<ResourceQuota> list = resourceQuotaService.search(filters);
        List<Integer> quotaIds = new ArrayList<>();
        for (ResourceQuota rq : list) {
            quotaIds.add(rq.getQuotaId());
        }
        return quotaIds;
    }

    @RequestMapping(value = ServiceApi.Resources.BatchAddResourceQuota, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改资源视图-关联指标表")
    public ObjectResult batchAddResourceQuota(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        List<ResourceQuota> list = objectMapper.readValue(model, new TypeReference<List<ResourceQuota>>(){});
        //先删除表中resourceId=XX的数据
        if (list != null && list.size() > 0) {
            resourceQuotaService.deleteByResourceId(list.get(0).getResourceId());
        }
        for (int i=0; i<list.size(); i++) {
            resourceQuotaService.save(list.get(i));
        }
        return Result.success("资源视图-关联指标表更新成功！", list);
    }

    @RequestMapping(value = ServiceApi.Resources.GetQuotaChartByQuotaId, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标-获取已选图表值")
    public String getQuotaChartByQuotaId(
            @ApiParam(name = "quotaId", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "quotaId", required = false) Integer quotaId,
            @ApiParam(name = "resourceId", value = "资源Id")
            @RequestParam(value = "resourceId") String resourceId) {
        return resourceQuotaService.getQuotaChartByQuotaId(quotaId, resourceId);
    }

    @RequestMapping(value = ServiceApi.Resources.GetByResourceId, method = RequestMethod.GET)
    @ApiOperation(value = "根据资源Id获取资源视图 关联指标列表")
    public List<ResourceQuota> getByResourceId(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId) throws Exception{
        List<ResourceQuota> list = resourceQuotaService.search("resourceId=" + resourceId);
        return list;
    }

    @RequestMapping(value = ServiceApi.Resources.SearchByQuotaId, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标-根据quotaId查询,此指标有被多少个视图选中")
    public List<MResourceQuota> searchByQuotaId (
            @ApiParam(name= "quotaId", value = "过滤器", defaultValue = "0")
            @RequestParam(value = "quotaId") Integer quotaId) throws Exception{
        List<ResourceQuota> list = resourceQuotaService.search("quotaId=" + quotaId);
        List<MResourceQuota> models = new ArrayList<>();
        if( list != null && list.size() > 0){
            for(ResourceQuota resourceQuota: list){
                MResourceQuota mResourceQuota = objectMapper.convertValue(resourceQuota,MResourceQuota.class);
                models.add(mResourceQuota);
            }
            return models;
        }else {
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.Resources.DelRQNameByResourceId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源视图-关联指标表")
    public Result deleteByResourceId(
            @ApiParam(name = "resourceId", value = "资源Id", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId) {
        resourceQuotaService.deleteByResourceId(resourceId);
        return Result.success("资源视图-关联指标表删除成功！");
    }
}
