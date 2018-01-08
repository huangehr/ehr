package com.yihu.ehr.basic.report.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.report.service.QcDailyReportMetadataService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.QcDailyReportMetadata;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.report.MQcDailyReportMetadata;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        model.setAddDate(new Date());
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

    @RequestMapping(value = ServiceApi.Report.AddQcDailyMetadataDetailList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据元日报列表")
    boolean addDailyDatasetMetadataList(@RequestBody String models ) throws IOException {
        List<QcDailyReportMetadata> list =  getModelList(models);
        for(QcDailyReportMetadata qcDailyReportMetadata:list){
            qcDailyReportMetaDataService.save(qcDailyReportMetadata);
        }
        return true;
    }

    protected MQcDailyReportMetadata getModel(QcDailyReportMetadata o){
        return convertToModel(o, MQcDailyReportMetadata.class);
    }

    protected List<QcDailyReportMetadata> getModelList(String modelStr) throws IOException {
        List<Map<String, Object>> models = new ArrayList<>();
        models = objectMapper.readValue(modelStr, new TypeReference<List>() {});
        List<QcDailyReportMetadata> list =  new ArrayList<>();
        for(int i=0; i < models.size(); i++) {
            QcDailyReportMetadata qcDailyReportDetail = new QcDailyReportMetadata();
            Map<String, Object> model = models.get(i);
            if(model.get("eventTime") != null){
                qcDailyReportDetail.setEventTime(DateUtil.parseDate(model.get("eventTime").toString(), "yyyy-MM-dd HH:mm:ss"));
            }
            if(model.get("createDate") != null){
                qcDailyReportDetail.setCreateDate(DateUtil.parseDate(model.get("createDate").toString(), "yyyy-MM-dd HH:mm:ss"));
            }
            if(model.get("orgCode") != null){
                qcDailyReportDetail.setOrgCode(model.get("orgCode").toString());
            }
            if(model.get("metadate") != null){
                qcDailyReportDetail.setMetadate(model.get("metadate").toString());
            }
            if(model.get("datasetId") != null){
                qcDailyReportDetail.setDatasetId(model.get("datasetId").toString());
            }
            if(model.get("innerVersion") != null){
                qcDailyReportDetail.setInnerVersion(model.get("innerVersion").toString());
            }
            if(model.get("dataset") != null){
                qcDailyReportDetail.setDataset(model.get("dataset").toString());
            }
            if(model.get("totalQty") != null){
                qcDailyReportDetail.setTotalQty(model.get("totalQty").toString());
            }
            if(model.get("totalQty") != null){
                int tf = Integer.valueOf(model.get("errorQty").toString());
                qcDailyReportDetail.setErrorQty(tf);
            }
            if(model.get("acqFlag") != null){
                int af = Integer.valueOf(model.get("acqFlag").toString());
                qcDailyReportDetail.setAcqFlag(af);
            }
            if(model.get("errCode") != null){
                qcDailyReportDetail.setErrCode(model.get("errCode").toString());
            }
            qcDailyReportDetail.setAddDate(new Date());
            list.add(qcDailyReportDetail);
        }
        return  list;
    }

}
