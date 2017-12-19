package com.yihu.ehr.resource.client;

import com.yihu.ehr.agModel.resource.ResourceQuotaModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.resource.MResourceQuota;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
@FeignClient(value = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0 + "/resourcesQuota")
@ApiIgnore
public interface RsResourceQuotaClient {

    @RequestMapping(value = ServiceApi.Resources.SearchInfo, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标")
    ListResult search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Resources.GetRQNameByResourceId, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标-根据resourceId查询")
    List<Integer> getRQNameByResourceId(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = ServiceApi.Resources.BatchAddResourceQuota, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改资源视图-关联指标表")
    ObjectResult batchAddResourceQuota(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);

    @RequestMapping(value = ServiceApi.Resources.GetQuotaChartByQuotaId, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标-获取已选图表值")
    public String getQuotaChartByQuotaId(
            @ApiParam(name = "quotaId", value = "指标ID")
            @RequestParam(value = "quotaId", required = false) Integer quotaId,
            @ApiParam(name = "resourceId", value = "资源Id")
            @RequestParam(value = "resourceId") String resourceId);

    @RequestMapping(value = ServiceApi.Resources.GetByResourceId, method = RequestMethod.GET)
    @ApiOperation(value = "根据资源Id获取资源视图 关联指标列表")
    public List<ResourceQuotaModel> getByResourceId(
            @ApiParam(name = "resourceId", value = "过滤器", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId);

    @RequestMapping(value = ServiceApi.Resources.SearchByQuotaId, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标-根据quotaId查询")
    public List<MResourceQuota> searchByQuotaId (
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "0")
            @RequestParam(value = "quotaId") Integer quotaId);

    @RequestMapping(value = ServiceApi.Resources.DelRQNameByResourceId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源视图-关联指标表")
    Result deleteByResourceId(
            @ApiParam(name = "resourceId", value = "资源Id", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId);

    @RequestMapping(value = ServiceApi.Resources.SearchQuotaByResourceId, method = RequestMethod.GET)
    @ApiOperation(value = "根据resourceId获取该资源下的指标列表")
    List<TjQuota> getQuotaByResourceId(@RequestParam(value = "resourceId") String resourceId);

    @RequestMapping(value = ServiceApi.Resources.SearchTreeByResourceId, method = RequestMethod.GET)
    @ApiOperation(value = "根据resourceId获取该资源下的指标列表树")
    Envelop searchTreeByResourceId(@RequestParam(value = "resourceId") String resourceId);

    @RequestMapping(value = ServiceApi.Resources.UpdateResourceQuota, method = RequestMethod.POST)
    @ApiOperation(value = "根据resourceId修改该资源下的指标关系")
    Envelop updateResourceQuota(
            @RequestParam(value = "resourceId") String resourceId,
            @RequestBody String jsonRelation);
}
