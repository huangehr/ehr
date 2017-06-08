package com.yihu.ehr.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.entity.report.*;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.model.report.json.*;
import com.yihu.ehr.util.QcDatasetsParser;
import com.yihu.ehr.util.QcEventDataParser;
import com.yihu.ehr.util.QcMetadataParser;
import com.yihu.ehr.util.ResolveJsonFileUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by janseny on 2017/5/12.
 */
@Service
public class QcDailyReportResolveService {

    public final static String fileContainName = "datasets";
    public final static String TempPath = System.getProperty("java.io.tmpdir") + File.separator;

    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public JsonArchivesService jsonArchivesService;
    @Autowired
    QcDailyReportService qcDailyReportService;
    @Autowired
    QcDailyReportDetailService qcDailyReportDetailService;
    @Autowired
    QcDailyReportDatasetsService qcDailyReportDatasetsService;
    @Autowired
    QcDailyReportDatasetService qcDailyReportDatasetService;
    @Autowired
    QcDailyReportMetadataService qcDailyReportMetadataService;


    /**
     * 解析质控压缩包 并入库
     * @param zipFile
     * @param password
     */
    public void resolveFile(File zipFile,String password,String todir){
        ResolveJsonFileUtil rjfu = new ResolveJsonFileUtil();
        try {
            File[] files = rjfu.unzip(zipFile,password,todir);
            QcDailyDatasetsModel dataSetsModel = new QcDailyDatasetsModel();
            List<QcDailyDatasetModel> qcDailyDatasetModelList = null;
            List<QcDailyMetadataModel> metadataList  = null;
            Map<String,QcDailyReportDataset> datasetMap = null;
            for (File file :files){
                if(file.getName().contains(fileContainName)){
                    QcDailyReportDatasets qcDailyReportDatasets = new QcDailyReportDatasets();
                    //解析json文件
                    dataSetsModel =  generateDataSet(file);
                    String parten = "yyyy-MM-dd hh:mm:ss";
                    if(dataSetsModel.getCreateDate().contains("T")){
                        parten = "yyyy-MM-dd'T'HH:mm:ss";
                    }
                    qcDailyReportDatasets.setCreateDate(DateUtil.parseDate(dataSetsModel.getCreateDate(), parten));
                    qcDailyReportDatasets.setEventTime(DateUtil.parseDate(dataSetsModel.getEventTime(),parten));
                    qcDailyReportDatasets.setOrgCode(dataSetsModel.getOrgCode());
                    qcDailyReportDatasets.setInnerVersion(dataSetsModel.getInnerVersion());
                    qcDailyReportDatasets.setRealNum(dataSetsModel.getRealHospitalNum());
                    qcDailyReportDatasets.setTotalNum(dataSetsModel.getTotalHospitalNum());
                    qcDailyReportDatasets.setAddDate(new Date());
                    qcDailyReportDatasets = qcDailyReportDatasetsService.save(qcDailyReportDatasets);
                    if(qcDailyReportDatasets != null){
                        qcDailyDatasetModelList = dataSetsModel.getQcDailyDatasetModels();
                        ListIterator<QcDailyDatasetModel> it = qcDailyDatasetModelList.listIterator();
                        while (it.hasNext()) {
                            QcDailyDatasetModel qcDailyDatasetModel = it.next();
                            qcDailyDatasetModel.setReportId(qcDailyReportDatasets.getId());
                        }
                    }
                    datasetMap = addDailyDatasetModelList(qcDailyDatasetModelList);
                    file.delete();
                }
            }
            for (File file :files){
                if( !file.getName().contains(fileContainName)){
                    String parentDirectory = file.getParent();
                    //解析json文件
                    metadataList  =  generateMetadata(file,parentDirectory);
                    if(metadataList != null && metadataList.size() > 0){
                        for(QcDailyMetadataModel qcDailyMetadataModel : metadataList){
                            if(datasetMap.containsKey(qcDailyMetadataModel.getDataset())){
                                qcDailyMetadataModel.setDatasetId(datasetMap.get(qcDailyMetadataModel.getDataset()).getReportId());
                                qcDailyMetadataModel.setAcqFlag(datasetMap.get(qcDailyMetadataModel.getDataset()).getAcqFlag());
                            }
                        }
                        addDailyDatasetMetadataList(metadataList);
                        file.delete();
                    }
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * 解析日报压缩包 并入库
     * @param zipFile
     * @param password
     */
    public String resolveReportFile(File zipFile,String password,String todir){
        ResolveJsonFileUtil rjfu = new ResolveJsonFileUtil();
        try {
            File[] files = rjfu.unzip(zipFile, password, todir);
            QcDailyEventsModel eventsModel = null;
            for (File file : files) {
                eventsModel =  generateEventsData(file);
                QcDailyReport qcDailyReport = new QcDailyReport();
                List<MQcDailyReportDetail> totalList = new ArrayList<>();
                List<MQcDailyReportDetail> realList = new ArrayList<>();
                if(eventsModel != null){
                    if(StringUtils.isEmpty(eventsModel.getCreate_date())){
                        return "采集时间不能为空";
                    }
                    if(StringUtils.isEmpty(eventsModel.getOrg_code())){
                        return "机构编码不能为空";
                    }
                    String parten = "yyyy-MM-dd hh:mm:ss";
                    if(eventsModel.getCreate_date().contains("T")){
                        parten = "yyyy-MM-dd'T'HH:mm:ss";
                    }
                    Date createDate = DateUtil.parseDate(eventsModel.getCreate_date(), parten);
                    qcDailyReport.setCreateDate(createDate );
                    qcDailyReport.setOrgCode(eventsModel.getOrg_code());
                    qcDailyReport.setInnerVersion(eventsModel.getInner_version());
                    qcDailyReport.setRealHospitalNum(eventsModel.getReal_hospital_num());
                    qcDailyReport.setTotalHospitalNum(eventsModel.getTotal_hospital_num());
                    qcDailyReport.setRealOutpatientNum(eventsModel.getReal_outpatient_num());
                    qcDailyReport.setTotalOutpatientNum(eventsModel.getTotal_outpatient_num());
                    qcDailyReport.setAddDate(new Date());
                    qcDailyReport = qcDailyReportService.save(qcDailyReport);
                    if(eventsModel.getTotal_hospital() != null){
                        addList(totalList, eventsModel.getTotal_hospital(), qcDailyReport.getId(), createDate, "hospital");
                    }
                    if(eventsModel.getReal_hospital() != null){
                        addList(realList, eventsModel.getReal_hospital(), qcDailyReport.getId(), createDate, "hospital");
                    }
                    if(eventsModel.getTotal_outpatient() != null){
                        addList(totalList, eventsModel.getTotal_outpatient(), qcDailyReport.getId(), createDate, "outpatient");
                    }
                    if(eventsModel.getReal_outpatient() != null){
                        addList(realList, eventsModel.getReal_outpatient(), qcDailyReport.getId(), createDate, "outpatient");
                    }
                    //添加应采集数据
                    addQcDailyReportDetailList(totalList);
                    //判断实采 数据是否已录入到esb库
                    checkRealStorage(realList, qcDailyReport.getOrgCode(), createDate);
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return  null;
    }


    public void addDailyDatasetMetadataList(List<QcDailyMetadataModel> metadataList){
        for(QcDailyMetadataModel qcDailyMetadataModel : metadataList){
            QcDailyReportMetadata metadataModel = objectMapper.convertValue(qcDailyMetadataModel,QcDailyReportMetadata.class);
            metadataModel.setAddDate(new Date());
            qcDailyReportMetadataService.save(metadataModel);
        }
    }

    public  Map<String,QcDailyReportDataset> addDailyDatasetModelList(List<QcDailyDatasetModel> qcDailyDatasetModelList){
        Map<String,QcDailyReportDataset> map = new HashMap<>();
        for(QcDailyDatasetModel qcDailyDatasetModel : qcDailyDatasetModelList){
            QcDailyReportDataset qcDailyReportDataset = objectMapper.convertValue(qcDailyDatasetModel,QcDailyReportDataset.class);
            qcDailyReportDataset.setAddDate(new Date());
            QcDailyReportDataset dataset = qcDailyReportDatasetService.save(qcDailyReportDataset);
            map.put(dataset.getDataset(),dataset);
        }
        return map;
    }

    public void addQcDailyReportDetailList(List<MQcDailyReportDetail> qcDailyReportDetailList){
        for(MQcDailyReportDetail mQcDailyReportDetail : qcDailyReportDetailList){
            QcDailyReportDetail qcDailyReportDetail = objectMapper.convertValue(mQcDailyReportDetail,QcDailyReportDetail.class);
            qcDailyReportDetail.setAddDate(new Date());
            qcDailyReportDetailService.save(qcDailyReportDetail);
        }
    }


    /**
     * 解析日报数据
     * @param jsonFile
     * @return
     * @throws IOException
     */
    public QcDailyEventsModel generateEventsData(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate datasets");
        }
        QcDailyEventsModel eventsModel = QcEventDataParser.parseStructuredJsonQcDatasetsModel(jsonNode);
        return eventsModel;
    }

    /**
     * 解析数据集
     * @param jsonFile
     * @return
     * @throws IOException
     */
    public QcDailyDatasetsModel generateDataSet(File jsonFile) throws IOException {
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
    public List<QcDailyMetadataModel> generateMetadata(File jsonFile,String parentDirectory) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate Metadata");
        }
        List<QcDailyMetadataModel> metadataList = QcMetadataParser.parseStructuredJsonMetadateModel(jsonNode,parentDirectory);
        return metadataList;
    }


    //应踩数据
    public List<MQcDailyReportDetail> addList(List<MQcDailyReportDetail> list,List<QcDailyEventDetailModel> modelList,String qcDailyReportId,Date createTime,String archiveType){
        for(QcDailyEventDetailModel eventDetailModel : modelList){
            MQcDailyReportDetail mQcDailyReportDetail = new MQcDailyReportDetail();
            mQcDailyReportDetail.setEventNo(eventDetailModel.getEvent_no());
            mQcDailyReportDetail.setPatientId(eventDetailModel.getPatient_id());
            mQcDailyReportDetail.setReportId(qcDailyReportId);
            mQcDailyReportDetail.setArchiveType(archiveType);
            mQcDailyReportDetail.setAcqFlag(0);
            mQcDailyReportDetail.setAcqTime(createTime);
            int day = 0;
            if(StringUtils.isNotEmpty(eventDetailModel.getEvent_time())){
                String parten = "yyyy-MM-dd hh:mm:ss";
                if(eventDetailModel.getEvent_time().contains("T")){
                    parten = "yyyy-MM-dd'T'HH:mm:ss";
                }
                Date eventTime = DateUtil.parseDate(eventDetailModel.getEvent_time(), parten);
                mQcDailyReportDetail.setEventTime(eventTime);
                long intervalMilli = DateUtil.compareDateTime(createTime ,eventTime);
                day = (int) (intervalMilli / (24 * 60 * 60 * 1000));
            }
            mQcDailyReportDetail.setTimelyFlag(day > 2 ? 0 : 1);
            list.add(mQcDailyReportDetail);
        }
        return list;
    }

    public void checkRealStorage(List<MQcDailyReportDetail> realList ,String orgCode,Date createDate) throws JsonProcessingException, ParseException {

        //更新采集状态 及 入库状态
        for(MQcDailyReportDetail mQcDailyReportDetail:realList){
            StringBuffer filter = new StringBuffer();
            String eventNo = mQcDailyReportDetail.getEventNo();
            Date eventTime = mQcDailyReportDetail.getEventTime();
            String patientId = mQcDailyReportDetail.getPatientId();
            filter.append("eventNo=").append(eventNo).append( ";").
                    append("patientId=").append(patientId ).append(";");
            List<QcDailyReportDetail>  list = qcDailyReportDetailService.search(filter.toString());
            if (list != null && list.size() > 0){
                QcDailyReportDetail qDailyReportDetail = list.get(0);
                qDailyReportDetail.setAcqFlag(1);
                JsonArchives jsonArchives = checkIsInStorage(eventNo,patientId,orgCode);
                if(jsonArchives != null){
                    qDailyReportDetail.setStorageFlag(returnArchiveStatus(jsonArchives));
                    qDailyReportDetail.setStorageTime(jsonArchives.getFinishDate());
                }
                qcDailyReportDetailService.save(qDailyReportDetail);
            }else{//额外采集 的 新增
                QcDailyReportDetail newqDailyReportDetail = new QcDailyReportDetail();
                newqDailyReportDetail.setAddDate(new Date());
                newqDailyReportDetail.setAcqTime(createDate);
                newqDailyReportDetail.setAcqFlag(1);
                newqDailyReportDetail.setTimelyFlag(mQcDailyReportDetail.getTimelyFlag());
                newqDailyReportDetail.setEventNo(eventNo);
                newqDailyReportDetail.setEventTime(eventTime);
                newqDailyReportDetail.setPatientId(patientId);
                newqDailyReportDetail.setArchiveType(mQcDailyReportDetail.getArchiveType());
                newqDailyReportDetail.setReportId(mQcDailyReportDetail.getReportId());
                JsonArchives jsonArchives =  checkIsInStorage(eventNo, patientId, orgCode);
                if(jsonArchives != null){
                    newqDailyReportDetail.setStorageFlag(returnArchiveStatus(jsonArchives));
                    newqDailyReportDetail.setStorageTime(jsonArchives.getFinishDate());
                }
                qcDailyReportDetailService.save(newqDailyReportDetail);
            }

        }
    }

    //判断是否入库
    public JsonArchives checkIsInStorage(String eventNo,String patientId,String orgCode) throws ParseException {
        //查找是否入库
        StringBuffer str = new StringBuffer();
        str.append("eventNo=").append(eventNo).append( ";").
                append("patientId=").append(patientId).append(";").
                append("orgCode=").append(orgCode).append(";");
        List<JsonArchives> list = jsonArchivesService.search(str.toString());
        if(list.isEmpty()){
            return  null;
        }else{
            JsonArchives jsonArchives = list.get(0);
            return  jsonArchives;
        }
    }

    public int returnArchiveStatus(JsonArchives jsonArchives){
        if(jsonArchives != null){
            if(jsonArchives.getArchiveStatus()== ArchiveStatus.Received){
                return  0;
            }else if(jsonArchives.getArchiveStatus()== ArchiveStatus.Acquired){
                return  1;
            }else if(jsonArchives.getArchiveStatus()== ArchiveStatus.Failed){
                return  2;
            }else if(jsonArchives.getArchiveStatus()== ArchiveStatus.Finished){
                return  3;
            }else{
                return  0;
            }
        }else{
            return  0;
        }
    }


}
