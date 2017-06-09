package com.yihu.ehr.tj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjQuotaDimensionSlaveModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDimensionSlave;
import com.yihu.ehr.entity.tj.TjQuotaDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.tj.service.TjDimensionSlaveClient;
import com.yihu.ehr.tj.service.TjQuotaDimensionSlaveClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.6.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjQuotaDimensionSlave", description = "统计指标管理", tags = {"统计指标管理-从纬度关联信息"})
public class TjQuotaDimensionSlaveController extends ExtendController<TjQuotaDimensionSlave> {

    @Autowired
    TjQuotaDimensionSlaveClient tjQuotaDimensionSlaveClient;
    @Autowired
    TjDimensionSlaveClient tjDimensionSlaveClient;
    @Autowired
    ObjectMapper objectMapper;


    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionSlaveList, method = RequestMethod.GET)
    @ApiOperation(value = "从纬度关联信息列表")
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

        ListResult listResult = tjQuotaDimensionSlaveClient.search(fields, filters, sorts, size, page);
        List<TjQuotaDimensionSlaveModel>  tjQuotaDimensionSlaveModelList = new ArrayList<>();
        if (listResult.getTotalCount() != 0) {
            List<Map<String, Object>> modelList = listResult.getDetailModelList();
            for (Map<String, Object> map : modelList) {
                TjQuotaDimensionSlaveModel tjQuotaQuotaDimensionSlaveModel = objectMapper.convertValue(map, TjQuotaDimensionSlaveModel.class);
                TjDimensionSlave tjDimensionSlave = tjDimensionSlaveClient.getTjDimensionSlave(tjQuotaQuotaDimensionSlaveModel.getSlaveCode());
                if( tjDimensionSlave != null ){
                    tjQuotaQuotaDimensionSlaveModel.setName(tjDimensionSlave.getName());
                }
                tjQuotaDimensionSlaveModelList.add(tjQuotaQuotaDimensionSlaveModel);
            }
            return getResult(tjQuotaDimensionSlaveModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        } else {
            Envelop envelop = new Envelop();
            return envelop;
        }

    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionSlave, method = RequestMethod.POST)
    @ApiOperation(value = "新增从纬度关联信息")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjQuotaDimensionSlaveClient.add(model);
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

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionSlave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除从纬度关联信息")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            Result result = tjQuotaDimensionSlaveClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("从纬度关联信息删除失败！");
            }
        }catch (Exception e){

            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }
}
