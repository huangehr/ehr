package com.yihu.ehr.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.report.QcDailyDatasetModel;
import com.yihu.ehr.agModel.report.QcDailyDatasetsModel;
import com.yihu.ehr.agModel.report.QcDailyEventDetailModel;
import com.yihu.ehr.agModel.report.QcDailyMetadataModel;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.entity.report.JsonArchives;
import com.yihu.ehr.entity.report.QcDailyReportDataset;
import com.yihu.ehr.entity.report.QcDailyReportDatasets;
import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.security.service.SecurityClient;
import com.yihu.ehr.util.QcDatasetsParser;
import com.yihu.ehr.util.QcMetadataParser;
import com.yihu.ehr.util.ResolveJsonFileUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by janseny on 2017/5/12.
 */
@Service
public class QcDailyReportResolveService {

    private final  static String fileContainName = "datasets";
    @Autowired
    QcDailyReportClient qcDailyReportClient;
    @Autowired
    QcDailyReportDatasetsClient qcDailyReportDatasetsClient;
    @Autowired
    QcDailyReportDatasetClient qcDailyReportDatasetClient;
    @Autowired
    QcDailyReportMetadataClient qcDailyReportMetadataClient;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public  JsonArchivesClient jsonArchivesClient;


    /**
     * 解析压缩包 并入库
     * @param zipFile
     * @param password
     */
    public void resolveFile(File zipFile,String password){
        ResolveJsonFileUtil rjfu = new ResolveJsonFileUtil();
        try {
            File[] filse = rjfu.unzip(zipFile,password);
            QcDailyDatasetsModel dataSetsModel = new QcDailyDatasetsModel();
            List<QcDailyDatasetModel> qcDailyDatasetModelList = null;
            List<QcDailyMetadataModel> metadataList  = null;
            Map<String,QcDailyReportDataset> datasetMap = null;
            for (File file :filse){
                if(file.getName().contains(fileContainName)){
                    QcDailyReportDatasets qcDailyReportDatasets = new QcDailyReportDatasets();
                    //解析json文件
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
                    file.delete();
                }
            }
            for (File file :filse){
                if( !file.getName().contains(fileContainName)){
                    //解析json文件
                    metadataList  =  generateMetadata(file);
                    for(QcDailyMetadataModel qcDailyMetadataModel : metadataList){
                        if(datasetMap.containsKey(qcDailyMetadataModel.getDataset())){
                            qcDailyMetadataModel.setDatasetId(datasetMap.get(qcDailyMetadataModel.getDataset()).getReportId());
                            qcDailyMetadataModel.setAcqFlag(datasetMap.get(qcDailyMetadataModel.getDataset()).getAcqFlag());
                        }
                    }
                    qcDailyReportMetadataClient.addDailyDatasetMetadataList(objectMapper.writeValueAsString(metadataList));
                    file.delete();
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }




    /**
     * 解析数据集
     *
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
    public List<QcDailyMetadataModel> generateMetadata(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate Metadata");
        }
        List<QcDailyMetadataModel> metadataList = QcMetadataParser.parseStructuredJsonMetadateModel(jsonNode);
        return metadataList;
    }


    //应踩数据
    public List<MQcDailyReportDetail> addList(List<MQcDailyReportDetail> list,List<QcDailyEventDetailModel> modelList,String qcDailyReportId,Date createTime,String archiveType){
        for(QcDailyEventDetailModel eventDetailModel : modelList){
            MQcDailyReportDetail mQcDailyReportDetail = new MQcDailyReportDetail();
            mQcDailyReportDetail.setEventNo(eventDetailModel.getEvent_no());
            mQcDailyReportDetail.setEventTime(eventDetailModel.getEvent_time());
            mQcDailyReportDetail.setPatientId(eventDetailModel.getPatient_id());
            mQcDailyReportDetail.setReportId(qcDailyReportId);
            mQcDailyReportDetail.setArchiveType(archiveType);
            mQcDailyReportDetail.setAcqFlag(0);
            mQcDailyReportDetail.setAcqTime(createTime);
            int day = 0;
            if(StringUtils.isNotEmpty(eventDetailModel.getEvent_time())){
                Date eventDate = DateUtil.parseDate(eventDetailModel.getEvent_time(), "yyyy-MM-dd HH:mm:ss");
                long intervalMilli = DateUtil.compareDateTime(createTime ,eventDate);
                day = (int) (intervalMilli / (24 * 60 * 60 * 1000));
            }
            mQcDailyReportDetail.setTimelyFlag(day > 2 ? 0 : 1);
            list.add(mQcDailyReportDetail);
        }
        return list;
    }

    public void checkRealStorage(List<MQcDailyReportDetail> realList ,String orgCode,Date createDate) throws JsonProcessingException {

        //更新采集状态 及 入库状态
        for(MQcDailyReportDetail mQcDailyReportDetail:realList){
            StringBuffer filter = new StringBuffer();
            String eventNo = mQcDailyReportDetail.getEventNo();
            String eventTime = mQcDailyReportDetail.getEventTime();
            String patientId = mQcDailyReportDetail.getPatientId();
            filter.append("eventNo=").append(eventNo).append( ";").
                    append("patientId=").append(patientId ).append(";");
            QcDailyReportDetail qDailyReportDetail = qcDailyReportClient.searchQcDailyReportDetail(filter.toString());
            if(qDailyReportDetail != null){
                qDailyReportDetail.setAcqFlag(1);
                JsonArchives jsonArchives = checkIsInStorage(eventNo,patientId,orgCode);
                if(jsonArchives != null){
                    qDailyReportDetail.setStorageFlag(returnArchiveStatus(jsonArchives));
                    qDailyReportDetail.setStorageTime(jsonArchives.getFinishDate());
                }
                qcDailyReportClient.addOrUpdateQcDailyReportDetail(objectMapper.writeValueAsString(qDailyReportDetail));
            }else{//额外采集 的 新增
                QcDailyReportDetail newqDailyReportDetail = new QcDailyReportDetail();
                newqDailyReportDetail.setAddDate(new Date());
                newqDailyReportDetail.setAcqTime(createDate);
                newqDailyReportDetail.setAcqFlag(1);
                newqDailyReportDetail.setTimelyFlag(mQcDailyReportDetail.getTimelyFlag());
                newqDailyReportDetail.setEventNo(eventNo);
                newqDailyReportDetail.setEventTime(DateUtil.formatCharDateYMDHMS(eventTime));
                newqDailyReportDetail.setPatientId(patientId);
                newqDailyReportDetail.setArchiveType(mQcDailyReportDetail.getArchiveType());
                newqDailyReportDetail.setReportId(mQcDailyReportDetail.getReportId());
                JsonArchives jsonArchives =  checkIsInStorage(eventNo, patientId, orgCode);
                if(jsonArchives != null){
                    newqDailyReportDetail.setStorageFlag(returnArchiveStatus(jsonArchives));
                    newqDailyReportDetail.setStorageTime(jsonArchives.getFinishDate());
                }
                qcDailyReportClient.addOrUpdateQcDailyReportDetail(objectMapper.writeValueAsString(newqDailyReportDetail));
            }

        }
    }
    
    //判断是否入库
    public JsonArchives checkIsInStorage(String eventNo,String patientId,String orgCode){
        //查找是否入库
        StringBuffer str = new StringBuffer();
        str.append("eventNo=").append(eventNo).append( ";").
                append("patientId=").append(patientId).append(";").
                append("orgCode=").append(orgCode).append(";");
        JsonArchives jsonArchives = jsonArchivesClient.search(str.toString());
        return  jsonArchives;
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
