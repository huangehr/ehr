package com.yihu.ehr.report.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.model.report.MQcQuotaResult;
import com.yihu.ehr.report.service.QcQuotaResultClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcQuotaResult", description = "数据统计指标结果", tags = {"报表管理-数据统计指标结果"})
public class QcQuotaResultController extends ExtendController<QcQuotaResult> {

    @Autowired
    QcQuotaResultClient qcQuotaResultClient;


    @RequestMapping(value = ServiceApi.Report.GetQcQuotaResultList, method = RequestMethod.GET)
    @ApiOperation(value = "数据统计指标结果列表")
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

        ResponseEntity<List<MQcQuotaResult>> responseEntity = qcQuotaResultClient.search(fields, filters, sorts, size, page);
        return getResult(responseEntity.getBody(), getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据统计指标结果")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            return success(qcQuotaResultClient.add(model) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据统计指标结果")
    public Envelop update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            MQcQuotaResult qcQuotaResult = convertToMModel(model, MQcQuotaResult.class);

            if( StringUtils.isNotEmpty(qcQuotaResult.getId()) )
                return failed("编号不能为空");
            return success(qcQuotaResultClient.update(qcQuotaResult) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据统计指标结果")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            qcQuotaResultClient.delete(id);
            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

}
