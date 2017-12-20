package com.yihu.ehr.resource.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.resource.ResourceQuotaModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.resource.MResourceQuota;
import com.yihu.ehr.resource.client.RsResourceQuotaClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/10.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "rs_resources_quota",  description = "资源视图指标", tags = {"资源视图指标"})
public class RsResourceQuotaController extends ExtendController {

    @Autowired
    private RsResourceQuotaClient resourceQuotaClient;

    @RequestMapping(value = ServiceApi.Resources.SearchInfo, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        ListResult listResult = resourceQuotaClient.search(fields, filters, sorts, size, page);
        List<ResourceQuotaModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                ResourceQuotaModel resourceQuotaModel = objectMapper.convertValue(map,ResourceQuotaModel.class);
                mainModelList.add(resourceQuotaModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.Resources.BatchAddResourceQuota, method = RequestMethod.POST)
    @ApiOperation(value = "新增&修改资源视图-关联指标表")
    public Envelop batchAddResourceQuota(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam String model) {
        try {
            Envelop envelop = new Envelop();
            ObjectResult objectResult = resourceQuotaClient.batchAddResourceQuota(model);
            if (objectResult.getCode() == 200) {
                return successObj(objectResult.getData());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Resources.SearchByQuotaId, method = RequestMethod.GET)
    @ApiOperation(value = "资源视图指标-根据quotaId查询")
    public Envelop searchByQuotaId (
            @ApiParam(name = "quotaId", value = "过滤器", defaultValue = "0")
            @RequestParam(value = "quotaId") Integer quotaId) {
        List<MResourceQuota> resourceQuotaModels = resourceQuotaClient.searchByQuotaId(quotaId);
        return successObj(resourceQuotaModels);
    }

    @RequestMapping(value = ServiceApi.Resources.DelRQNameByResourceId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源视图-关联指标表")
    public Envelop deleteByResourceId(
            @ApiParam(name = "resourceId", value = "资源Id", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId) {
        try {
            Result result = resourceQuotaClient.deleteByResourceId(resourceId);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("资源视图-关联指标表删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Resources.SearchQuotaByResourceId, method = RequestMethod.GET)
    @ApiOperation(value = "根据resourceId获取该资源下的指标列表")
    public Envelop getQuotaByResourceId(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        List<TjQuota> quotaList = null;
        Envelop envelop = new Envelop();
        try {
            quotaList = resourceQuotaClient.getQuotaByResourceId(resourceId);
            Envelop quotaEnvelop = resourceQuotaClient.searchTreeByResourceId(resourceId);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(quotaEnvelop.getDetailModelList());
            envelop.setObj(quotaList);
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.UpdateResourceQuota, method = RequestMethod.POST)
    @ApiOperation(value = "根据resourceId修改该资源下的指标关系")
    public Envelop updateResourceQuota(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) throws Exception {
        Envelop envelop = new Envelop();
        try {
            envelop = resourceQuotaClient.updateResourceQuota(model);
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }
}
