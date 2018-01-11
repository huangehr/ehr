package com.yihu.ehr.basic.report.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.report.service.QcDailyReportDatasetService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.QcDailyReportDataset;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.report.MQcDailyReportDataset;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "QcDailyReportDataset", description = "质控包数据集详情", tags = {"质控包数据集详情"})
public class QcDailyReportDatasetEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    QcDailyReportDatasetService qcDailyReportDatasetService;

    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportDatasetList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询质控包数据集详情")
    public ListResult getQcDailyReportDatasetList(
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
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<QcDailyReportDataset> qcDailyReportDatasetList = qcDailyReportDatasetService.search(fields, filters, sorts, page, size);
        if(qcDailyReportDatasetList != null){
            listResult.setDetailModelList(qcDailyReportDatasetList);
            listResult.setTotalCount((int)qcDailyReportDatasetService.getCount(filters));
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

    @RequestMapping(value = ServiceApi.Report.QcDailyReportDataset, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据集详情")
    public MQcDailyReportDataset add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody QcDailyReportDataset model) throws Exception{
        model.setAddDate(new Date());
        return getModel( qcDailyReportDatasetService.save(model) );
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReportDataset, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改质控包数据集详情")
    public MQcDailyReportDataset update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody QcDailyReportDataset model) throws Exception{
        return getModel(qcDailyReportDatasetService.save(model));
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReportDataset, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除质控包数据集详情")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        qcDailyReportDatasetService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Report.AddQcDailyDatasetDetailList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据集详情列表")
    Map<String,QcDailyReportDataset> addDailyDatasetModelList(@RequestBody String details ) throws IOException {
        Map<String,QcDailyReportDataset> map = new HashMap<>();
        List<QcDailyReportDataset> list =  getModelList(details);
        for(QcDailyReportDataset qcDailyReportDataset:list){
            qcDailyReportDatasetService.save(qcDailyReportDataset);
            map.put(qcDailyReportDataset.getDataset(),qcDailyReportDataset);
        }
        return map;
    }

    protected MQcDailyReportDataset getModel(QcDailyReportDataset o){
        return convertToModel(o, MQcDailyReportDataset.class);
    }

    protected List<QcDailyReportDataset> getModelList(String modelStr) throws IOException {
        List<Map<String, Object>> models = new ArrayList<>();
        models = objectMapper.readValue(modelStr, new TypeReference<List>() {});
        List<QcDailyReportDataset> list =  new ArrayList<>();
        for(int i=0; i < models.size(); i++) {
            QcDailyReportDataset qcDailyReportDataset = new QcDailyReportDataset();
            Map<String, Object> model = models.get(i);
            qcDailyReportDataset.setAddDate(new Date());
            if(model.get("dataset") != null){
                qcDailyReportDataset.setDataset(model.get("dataset").toString());
            }
            if(model.get("reportId") != null){
                qcDailyReportDataset.setReportId(model.get("reportId").toString());
            }
            if(model.get("acqFlag") != null){
                int af = Integer.valueOf(model.get("acqFlag").toString());
                qcDailyReportDataset.setAcqFlag(af);
            }
            list.add(qcDailyReportDataset);
        }
        return  list;
    }

}
