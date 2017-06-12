package com.yihu.ehr.tj.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.tj.service.TjDimensionSlaveClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.6.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjDimensionSlave", description = "统计指标管理", tags = {"统计指标管理-从维度"})
public class TjDimensionSlaveController extends ExtendController<TjDimensionSlave> {

    @Autowired
    TjDimensionSlaveClient tjDimensionSlaveClient;


    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionSlaveList, method = RequestMethod.GET)
    @ApiOperation(value = "从维度列表")
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
            @RequestParam(value = "page", required = false) int page){

        ListResult listResult = tjDimensionSlaveClient.search(fields, filters, sorts, size, page);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.POST)
    @ApiOperation(value = "新增/修改从维度")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjDimensionSlaveClient.add(model);
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

    @RequestMapping(value =  ServiceApi.TJ.TjDimensionSlaveId, method = RequestMethod.GET)
    @ApiOperation(value = "获取从维度信息", notes = "从维度")
    public Envelop getTjDimensionSlave(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") Integer id) {
        try {
            TjDimensionSlave tjDimensionSlave = tjDimensionSlaveClient.getTjDimensionSlave(id);
            if (tjDimensionSlave == null) {
                return failed("从维度信息获取失败!");
            }
            return success(tjDimensionSlave);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除从维度")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            Result result = tjDimensionSlaveClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("从维度删除失败！");
            }
        }catch (Exception e){

            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }
}
