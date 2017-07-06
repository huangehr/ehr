package com.yihu.ehr.tj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjQuotaDimensionMainModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDimensionMain;
import com.yihu.ehr.entity.tj.TjQuotaDimensionMain;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.tj.service.TjDimensionMainClient;
import com.yihu.ehr.tj.service.TjQuotaDimensionMainClient;
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
@Api( value = "TjQuotaDimensionMain", description = "统计指标", tags = {"统计指标管理-主维度关联信息"})
public class TjQuotaDimensionMainController extends ExtendController<TjQuotaDimensionMain> {

    @Autowired
    TjQuotaDimensionMainClient tjQuotaDimensionMainClient;
    @Autowired
    TjDimensionMainClient tjDimensionMainClient;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionMainList, method = RequestMethod.GET)
    @ApiOperation(value = "主维度关联信息列表")
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

        ListResult listResult = tjQuotaDimensionMainClient.search(fields, filters, sorts, size, page);
        List<TjQuotaDimensionMainModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                TjQuotaDimensionMainModel tjQuotaDimensionMainModel = objectMapper.convertValue(map,TjQuotaDimensionMainModel.class);
                TjDimensionMain tjDimensionMain = tjDimensionMainClient.getTjDimensionMain(tjQuotaDimensionMainModel.getMainCode());
                if( tjDimensionMain != null ){
                    tjQuotaDimensionMainModel.setName(tjDimensionMain.getName());
                }
                mainModelList.add(tjQuotaDimensionMainModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionMain, method = RequestMethod.POST)
    @ApiOperation(value = "新增/修改主维度关联信息")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjQuotaDimensionMainClient.add(model);
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

    @RequestMapping(value = ServiceApi.TJ.AddTjQuotaDimensionMain, method = RequestMethod.POST)
    @ApiOperation(value = "新增主维度关联信息")
    public Envelop addTjQuotaDimensionMain(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjQuotaDimensionMainClient.addTjQuotaDimensionMain(model);
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

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionMain, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除主维度关联信息")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            Result result = tjQuotaDimensionMainClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("主维度关联信息删除失败！");
            }
        }catch (Exception e){

            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = "/tj/deleteMainByQuotaCode", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据指标ID删除统计指标主维度关联信息")
    public Envelop deleteMainByQuotaCode(
            @ApiParam(name = "quotaCode", value = "指标Id")
            @RequestParam(value = "quotaCode") String quotaCode) throws Exception {
        try {
            Result result = tjQuotaDimensionMainClient.deleteMainByQuotaCode(quotaCode);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("删除失败！");
            }
        }catch (Exception e){
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }
}
