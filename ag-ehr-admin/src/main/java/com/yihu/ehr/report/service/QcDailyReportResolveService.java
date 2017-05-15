package com.yihu.ehr.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.report.QcDailyDatasetModel;
import com.yihu.ehr.agModel.report.QcDailyDatasetsModel;
import com.yihu.ehr.agModel.report.QcDailyEventDetailModel;
import com.yihu.ehr.agModel.report.QcDailyMetadataModel;
import com.yihu.ehr.entity.report.QcDailyReportDataset;
import com.yihu.ehr.entity.report.QcDailyReportDatasets;
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
    public List<MQcDailyReportDetail> checkRealListFromTotal(List<MQcDailyReportDetail> totalList,List<MQcDailyReportDetail> realList){

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
