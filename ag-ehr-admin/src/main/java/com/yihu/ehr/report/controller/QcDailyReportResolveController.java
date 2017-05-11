package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.report.*;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcDailyReportDataset;
import com.yihu.ehr.entity.report.QcDailyReportDatasets;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.model.report.MQcDailyReportDatasets;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.report.service.QcDailyReportClient;
import com.yihu.ehr.report.service.QcDailyReportDatasetClient;
import com.yihu.ehr.report.service.QcDailyReportDatasetsClient;
import com.yihu.ehr.report.service.QcDailyReportMetadataClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.QcDatasetsParser;
import com.yihu.ehr.util.QcMetadataParser;
import com.yihu.ehr.util.ResolveJsonFileUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcDailyReportResolve", description = "质控包数据文件解析", tags = {"报表管理-质控包数据文件解析"})
public class QcDailyReportResolveController extends ExtendController<QcDailyReport> {

    private final  static String fileContainName = "datasets";
    @Autowired
    QcDailyReportClient qcDailyReportClient;
    @Autowired
    QcDailyReportDatasetsClient qcDailyReportDatasetsClient;
    @Autowired
    QcDailyReportDatasetClient qcDailyReportDatasetClient;
    @Autowired
    QcDailyReportMetadataClient qcDailyReportMetadataClient;

    @RequestMapping(value = ServiceApi.Report.QcDailyReportReolve, method = RequestMethod.POST)
    @ApiOperation(value = "质控包数据文件解析")
    public Envelop add(
            @ApiParam(name = "filePath", value = "压缩包地址", defaultValue = "")
            @RequestParam("filePath") String filePath) {
        try {
            ResolveJsonFileUtil rj = new ResolveJsonFileUtil();
            try {
                File[] filse = rj.unzip(filePath, "");
                QcDailyDatasetsModel dataSetsModel = new QcDailyDatasetsModel();
                List<QcDailyDatasetModel>  qcDailyDatasetModelList = null;
                List<QcDailyMetadataModel> metadataList  = null;
                Map<String,QcDailyReportDataset> datasetMap = null;
                for (File file :filse){
                    if(file.getName().contains(fileContainName)){
                        QcDailyReportDatasets qcDailyReportDatasets = new QcDailyReportDatasets();
                        dataSetsModel =  generateDataSet(file);
                        qcDailyReportDatasets.setCreateDate(DateUtil.formatYMDToYMDHMS(dataSetsModel.getCreateDate()));
                        qcDailyReportDatasets.setEventTime(DateUtil.formatYMDToYMDHMS(dataSetsModel.getEventTime()));
                        qcDailyReportDatasets.setOrgCode(dataSetsModel.getOrgCode());
                        qcDailyReportDatasets.setInnerVersion(dataSetsModel.getInnerVersion());
                        qcDailyReportDatasets.setRealNum(dataSetsModel.getRealHospitalNum());
                        qcDailyReportDatasets.setTotalNum(dataSetsModel.getTotalHospitalNum());
                        qcDailyReportDatasets = qcDailyReportDatasetsClient.add(objectMapper.writeValueAsString(qcDailyReportDatasets));
                        if(qcDailyReportDatasets != null){
                            qcDailyDatasetModelList = dataSetsModel.getQcDailyDatasetModels();
                            ListIterator<QcDailyDatasetModel> it = qcDailyDatasetModelList.listIterator();
                            while (it.hasNext()) {
                                QcDailyDatasetModel qcDailyDatasetModel = it.next();
                                qcDailyDatasetModel.setReportId(qcDailyReportDatasets.getId());
                            }
                        }
                        datasetMap = qcDailyReportDatasetClient.addDailyDatasetModelList(objectMapper.writeValueAsString(qcDailyDatasetModelList));
                        try {
                            FileUtils.deleteQuietly(file);
                        } catch (Exception e) {
                            LogService.getLogger(QcDailyReportResolveController.class).warn("file delete failed after json file resolve: " + e.getMessage());
                        }
                    }
                }
                for (File file :filse){
                    if( !file.getName().contains(fileContainName)){
                        metadataList  =  generateMetadata(file);
                        for(QcDailyMetadataModel qcDailyMetadataModel : metadataList){
                            if(datasetMap.containsKey(qcDailyMetadataModel.getDataset())){
                                qcDailyMetadataModel.setDatasetId(datasetMap.get(qcDailyMetadataModel.getDataset()).getReportId());
                                qcDailyMetadataModel.setAcqFlag(datasetMap.get(qcDailyMetadataModel.getDataset()).getAcqFlag());
                            }
                        }
                        qcDailyReportMetadataClient.addDailyDatasetMetadataList(objectMapper.writeValueAsString(metadataList));
                        try {
                            FileUtils.deleteQuietly(file);
                        } catch (Exception e) {
                            LogService.getLogger(QcDailyReportResolveController.class).warn("file delete failed after json file resolve: " + e.getMessage());
                        }
                    }
                }
                rj.resolveContent(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return success("入库成功");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    /**
     * 解析数据集
     *
     * @param jsonFile
     * @return
     * @throws IOException
     */
    private QcDailyDatasetsModel generateDataSet(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate datasets");
        }
        QcDailyDatasetsModel dataSet = QcDatasetsParser.parseStructuredJsonQcDatasetsModel(jsonNode);
        return dataSet;
    }

    /**
     * 解析数据元
     *
     * @param jsonFile
     * @return
     * @throws IOException
     */
    private List<QcDailyMetadataModel> generateMetadata(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate Metadata");
        }
        List<QcDailyMetadataModel> metadataList = QcMetadataParser.parseStructuredJsonMetadateModel(jsonNode);
        return metadataList;
    }


    @RequestMapping(value = ServiceApi.Report.GetEventDataReport, method = RequestMethod.POST)
    @ApiOperation(value = "日报数据采集上传")
    public Envelop  getEventDataReport(
            @ApiParam(name = "eventsData", value = "采集json数据", defaultValue = "")
            @RequestParam("eventsData") String collectionData) {
        try {
            QcDailyEventsModel eventsModel = objectMapper.readValue(collectionData, QcDailyEventsModel.class);
            MQcDailyReport qcDailyReport = new MQcDailyReport();
            List<MQcDailyReportDetail> totalList = new ArrayList<>();
            List<MQcDailyReportDetail> realList = new ArrayList<>();
            if(eventsModel != null){
                if(StringUtils.isEmpty(eventsModel.getCreateDate())){
                    return failed("采集时间不能为空");
                }
                if(StringUtils.isEmpty(eventsModel.getOrgCode())){
                    return failed("机构编码不能为空");
                }
                Date createDate = DateUtil.parseDate(eventsModel.getCreateDate(), "yyyy-MM-dd hh:mm:ss");
                qcDailyReport.setOrgCode(eventsModel.getOrgCode());
                qcDailyReport.setCreateDate(createDate );
                qcDailyReport.setInnerVersion(eventsModel.getInnerVersion());
                qcDailyReport.setRealHospitalNum(eventsModel.getRealHospitalNum());
                qcDailyReport.setTotalHospitalNum(eventsModel.getTotalHospitalNum());
                qcDailyReport.setRealOutpatientNum(eventsModel.getRealOutpatientNum());
                qcDailyReport.setTotalOutpatientNum(eventsModel.getTotalOutpatientNum());
                qcDailyReport = qcDailyReportClient.add(objectMapper.writeValueAsString(qcDailyReport));
                if(eventsModel.getTotal_hospital() != null){
                    addList(totalList, eventsModel.getTotal_hospital(), qcDailyReport.getId(), createDate, "hospital");
                }
                if(eventsModel.getReal_hospital() != null){
                    addList(realList,eventsModel.getReal_hospital(),qcDailyReport.getId(),createDate,"hospital");
                }
                if(eventsModel.getTotal_outpatient() != null){
                    addList(totalList, eventsModel.getTotal_outpatient(), qcDailyReport.getId(), createDate, "outpatient");
                }
                if(eventsModel.getReal_outpatient() != null){
                    addList(realList,eventsModel.getReal_outpatient(),qcDailyReport.getId(),createDate,"outpatient");
                }
                List<MQcDailyReportDetail> dailyReportDetailList = checkRealListFromTotal(totalList, realList);
                qcDailyReportClient.addQcDailyReportDetailList(objectMapper.writeValueAsString(dailyReportDetailList));
            }
            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    //应踩数据
    protected List<MQcDailyReportDetail> addList(List<MQcDailyReportDetail> list,List<QcDailyEventDetailModel> modelList,String qcDailyReportId,Date createTime,String archiveType){
        for(QcDailyEventDetailModel eventDetailModel : modelList){
            MQcDailyReportDetail qcDailyReportDetail = new MQcDailyReportDetail();
            qcDailyReportDetail.setEventNo(eventDetailModel.getEvent_no());
            qcDailyReportDetail.setEventTime(eventDetailModel.getEvent_time());
            qcDailyReportDetail.setPatientId(eventDetailModel.getPatient_id());
            qcDailyReportDetail.setReportId(qcDailyReportId);
            qcDailyReportDetail.setArchiveType(archiveType);
            qcDailyReportDetail.setAcqFlag(0);
            int day = 0;
            if(StringUtils.isNotEmpty(eventDetailModel.getEvent_time())){
                Date eventDate = DateUtil.parseDate(eventDetailModel.getEvent_time(), "yyyy-MM-dd HH:mm:ss");
                long intervalMilli = DateUtil.compareDateTime(createTime ,eventDate);
                day = (int) (intervalMilli / (24 * 60 * 60 * 1000));
            }
            qcDailyReportDetail.setTimelyFlag(day>2?1:0);
            list.add(qcDailyReportDetail);
        }
        return list;
    }

    //检验是否采集
    protected List<MQcDailyReportDetail> checkRealListFromTotal(List<MQcDailyReportDetail> totalList,List<MQcDailyReportDetail> realList){

        List<MQcDailyReportDetail> qcDailyReportDetailList = new ArrayList<>();
        for (MQcDailyReportDetail totalReportDetail: totalList){
            for (MQcDailyReportDetail realReportDetail: realList){
                boolean eventNoFlag = totalReportDetail.getEventNo().equals(realReportDetail.getEventNo());
                boolean eventTimeFlag = totalReportDetail.getEventTime().equals(realReportDetail.getEventTime());
                boolean patientIdFlag = totalReportDetail.getPatientId().equals(realReportDetail.getPatientId());
                if(eventNoFlag && eventTimeFlag && patientIdFlag){
                    totalReportDetail.setAcqFlag(1);
                    break;
                }
            }
            qcDailyReportDetailList.add(totalReportDetail);
        }
        return qcDailyReportDetailList;
    }



}
