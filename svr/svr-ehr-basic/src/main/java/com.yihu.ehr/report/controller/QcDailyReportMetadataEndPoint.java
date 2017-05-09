package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.QcDailyReportMetadata;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.report.MQcDailyReportMetadata;
import com.yihu.ehr.report.service.QcDailyReportMetadataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "qcDailyReportMetadata", description = "目标数据日报", tags = {"目标数据日报"})
public class QcDailyReportMetadataEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    QcDailyReportMetadataService qcDailyReportMetaDataService;

    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportMetadataList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询目标数据日报")
    public ListResult getQcDailyReportMetadataList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<QcDailyReportMetadata> qcDailyReportMetaDataList = qcDailyReportMetaDataService.search(fields, filters, sorts, page, size);
        if(qcDailyReportMetaDataList != null){
            listResult.setDetailModelList(qcDailyReportMetaDataList);
            listResult.setTotalCount((int)qcDailyReportMetaDataService.getCount(filters));
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

    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增目标数据日报")
    public MQcDailyReportMetadata add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody QcDailyReportMetadata model) throws Exception{
        return getModel( qcDailyReportMetaDataService.save(model) );
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改目标数据日报")
    public MQcDailyReportMetadata update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody QcDailyReportMetadata model) throws Exception{
        return getModel(qcDailyReportMetaDataService.save(model));
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除目标数据日报")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        qcDailyReportMetaDataService.delete(id);
        return true;
    }

    protected MQcDailyReportMetadata getModel(QcDailyReportMetadata o){
        return convertToModel(o, MQcDailyReportMetadata.class);
    }

}
