package com.yihu.ehr.report.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcDailyReportMetadata;
import com.yihu.ehr.model.report.MQcDailyReportMetadata;
import com.yihu.ehr.report.service.QcDailyReportMetadataClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcDailyReportMetadata", description = "质控包数据元日报", tags = {"报表管理-质控包数据元日报"})
public class QcDailyReportMetaDataController extends ExtendController<QcDailyReportMetadata> {

    @Autowired
    QcDailyReportMetadataClient qcDailyReportMetadataClient;


    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportMetadataList, method = RequestMethod.GET)
    @ApiOperation(value = "质控包数据元日报列表")
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

        ResponseEntity<List<MQcDailyReportMetadata>> responseEntity = qcDailyReportMetadataClient.search(fields, filters, sorts, size, page);
        return getResult(responseEntity.getBody(), getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.POST)
    @ApiOperation(value = "新增质控包数据元日报")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            return success(qcDailyReportMetadataClient.add(model) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.PUT)
    @ApiOperation(value = "修改质控包数据元日报")
    public Envelop update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            MQcDailyReportMetadata qcDailyReportDatasets = convertToMModel(model, MQcDailyReportMetadata.class);

            if( StringUtils.isNotEmpty(qcDailyReportDatasets.getId()) )
                return failed("编号不能为空");
            return success(qcDailyReportMetadataClient.update(qcDailyReportDatasets) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除质控包数据元日报")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        try {
            qcDailyReportMetadataClient.delete(id);
            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.GET)
    @ApiOperation(value = "获取质控包数据元日报信息")
    public Envelop getInfo(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        try {
            MQcDailyReportMetadata qcDailyReportDatasets = qcDailyReportMetadataClient.getInfo(id);
            if(qcDailyReportDatasets == null)
                return failed("没有找到该质控包数据元日报信息！");
            return success(qcDailyReportDatasets);
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
    }

}
