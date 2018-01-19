package com.yihu.ehr.basic.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.report.feign.PackMgrClient;
import com.yihu.ehr.basic.report.feign.PackResolveClient;
import com.yihu.ehr.basic.report.feign.RedisServiceClient;
import com.yihu.ehr.basic.util.QcDatasetsParser;
import com.yihu.ehr.basic.util.QcEventDataParser;
import com.yihu.ehr.basic.util.QcMetadataParser;
import com.yihu.ehr.basic.util.ResolveJsonFileUtil;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.entity.report.*;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.model.report.json.*;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.model.standard.MStdMetaData;
import com.yihu.ehr.basic.report.feign.StandardClient;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public static final String BEGIN_DATE = "begin_date";

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
    @Autowired
    private RedisServiceClient redisServiceClient;
    @Autowired
    private PackMgrClient packMgrClient;
    @Autowired
    private PackResolveClient packResolveClient;
    @Autowired
    private StandardClient standardClient;

    /**
     * 解析质控压缩包 并入库
     *
     * @param zipFile
     * @param password
     */
    public String resolveFile(File zipFile, String password, String todir) {
        ResolveJsonFileUtil rjfu = new ResolveJsonFileUtil();
        String orgCode = "";
        try {
            File[] files = rjfu.unzip(zipFile, password, todir);
            QcDailyDatasetsModel dataSetsModel = new QcDailyDatasetsModel();
            List<QcDailyDatasetModel> qcDailyDatasetModelList = null;
            List<QcDailyMetadataModel> metadataList = null;
            Map<String, QcDailyReportDataset> datasetMap = null;
            for (File file : files) {
                if (file.getName().contains(fileContainName)) {
                    QcDailyReportDatasets qcDailyReportDatasets = new QcDailyReportDatasets();
                    //解析json文件
                    dataSetsModel = generateDataSet(file);
                    orgCode = dataSetsModel.getOrgCode();
                    String parten = "yyyy-MM-dd hh:mm:ss";
                    if (dataSetsModel.getCreateDate().contains("T")) {
                        parten = "yyyy-MM-dd'T'HH:mm:ss";
                    }
                    qcDailyReportDatasets.setCreateDate(DateUtil.parseDate(dataSetsModel.getCreateDate(), parten));
                    qcDailyReportDatasets.setEventTime(DateUtil.parseDate(dataSetsModel.getEventTime(), parten));
                    qcDailyReportDatasets.setOrgCode(dataSetsModel.getOrgCode());
                    qcDailyReportDatasets.setInnerVersion(dataSetsModel.getInnerVersion());
                    qcDailyReportDatasets.setRealNum(dataSetsModel.getRealHospitalNum());
                    qcDailyReportDatasets.setTotalNum(dataSetsModel.getTotalHospitalNum());
                    qcDailyReportDatasets.setAddDate(new Date());
                    qcDailyReportDatasets = qcDailyReportDatasetsService.save(qcDailyReportDatasets);
                    if (qcDailyReportDatasets != null) {
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
            for (File file : files) {
                if (!file.getName().contains(fileContainName)) {
                    String parentDirectory = file.getParent();
                    //解析json文件
                    metadataList = generateMetadata(file, parentDirectory);
                    if (metadataList != null && metadataList.size() > 0) {
                        for (QcDailyMetadataModel qcDailyMetadataModel : metadataList) {
                            if (datasetMap.containsKey(qcDailyMetadataModel.getDataset())) {
                                qcDailyMetadataModel.setDatasetId(datasetMap.get(qcDailyMetadataModel.getDataset()).getReportId());
                                qcDailyMetadataModel.setAcqFlag(datasetMap.get(qcDailyMetadataModel.getDataset()).getAcqFlag());
                            }
                        }
                        addDailyDatasetMetadataList(metadataList);
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return orgCode;
    }

    /**
     * 解析日报压缩包 并入库
     *
     * @param zipFile
     * @param password
     */
    public String resolveReportFile(File zipFile, String password, String todir) {
        ResolveJsonFileUtil rjfu = new ResolveJsonFileUtil();
        String orgCode = "";
        try {
            File[] files = rjfu.unzip(zipFile, password, todir);
            QcDailyEventsModel eventsModel = null;
            for (File file : files) {
                eventsModel = generateEventsData(file);
                orgCode = eventsModel.getOrg_code();
                QcDailyReport qcDailyReport = new QcDailyReport();
                List<MQcDailyReportDetail> totalList = new ArrayList<>();
                List<MQcDailyReportDetail> realList = new ArrayList<>();
                if (eventsModel != null) {
                    String parten = "yyyy-MM-dd hh:mm:ss";
                    if (eventsModel.getCreate_date().contains("T")) {
                        parten = "yyyy-MM-dd'T'HH:mm:ss";
                    }
                    Date createDate = DateUtil.parseDate(eventsModel.getCreate_date(), parten);
                    qcDailyReport.setCreateDate(createDate);
                    qcDailyReport.setOrgCode(eventsModel.getOrg_code());
                    qcDailyReport.setInnerVersion(eventsModel.getInner_version());
                    qcDailyReport.setRealHospitalNum(eventsModel.getReal_hospital_num());
                    qcDailyReport.setTotalHospitalNum(eventsModel.getTotal_hospital_num());
                    qcDailyReport.setRealOutpatientNum(eventsModel.getReal_outpatient_num());
                    qcDailyReport.setTotalOutpatientNum(eventsModel.getTotal_outpatient_num());
                    qcDailyReport.setAddDate(new Date());
                    qcDailyReport = qcDailyReportService.save(qcDailyReport);
                    if (eventsModel.getTotal_hospital() != null) {
                        addList(totalList, eventsModel.getTotal_hospital(), qcDailyReport.getId(), createDate, "hospital");
                    }
                    if (eventsModel.getReal_hospital() != null) {
                        addList(realList, eventsModel.getReal_hospital(), qcDailyReport.getId(), createDate, "hospital");
                    }
                    if (eventsModel.getTotal_outpatient() != null) {
                        addList(totalList, eventsModel.getTotal_outpatient(), qcDailyReport.getId(), createDate, "outpatient");
                    }
                    if (eventsModel.getReal_outpatient() != null) {
                        addList(realList, eventsModel.getReal_outpatient(), qcDailyReport.getId(), createDate, "outpatient");
                    }
                    //添加应采集数据
                    addQcDailyReportDetailList(totalList);
                    //判断实采 数据是否已录入到esb库
                    checkRealStorage(realList, qcDailyReport.getOrgCode(), createDate);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return orgCode;
    }


    public void addDailyDatasetMetadataList(List<QcDailyMetadataModel> metadataList) {
        for (QcDailyMetadataModel qcDailyMetadataModel : metadataList) {
            QcDailyReportMetadata metadataModel = objectMapper.convertValue(qcDailyMetadataModel, QcDailyReportMetadata.class);
            metadataModel.setAddDate(new Date());
            qcDailyReportMetadataService.save(metadataModel);
        }
    }

    public Map<String, QcDailyReportDataset> addDailyDatasetModelList(List<QcDailyDatasetModel> qcDailyDatasetModelList) {
        Map<String, QcDailyReportDataset> map = new HashMap<>();
        for (QcDailyDatasetModel qcDailyDatasetModel : qcDailyDatasetModelList) {
            QcDailyReportDataset qcDailyReportDataset = objectMapper.convertValue(qcDailyDatasetModel, QcDailyReportDataset.class);
            qcDailyReportDataset.setAddDate(new Date());
            QcDailyReportDataset dataset = qcDailyReportDatasetService.save(qcDailyReportDataset);
            map.put(dataset.getDataset(), dataset);
        }
        return map;
    }

    public void addQcDailyReportDetailList(List<MQcDailyReportDetail> qcDailyReportDetailList) {
        for (MQcDailyReportDetail mQcDailyReportDetail : qcDailyReportDetailList) {
            QcDailyReportDetail qcDailyReportDetail = objectMapper.convertValue(mQcDailyReportDetail, QcDailyReportDetail.class);
            qcDailyReportDetail.setAddDate(new Date());
            qcDailyReportDetailService.save(qcDailyReportDetail);
        }
    }


    /**
     * 解析日报数据
     *
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
    public List<QcDailyMetadataModel> generateMetadata(File jsonFile, String parentDirectory) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate Metadata");
        }
        List<QcDailyMetadataModel> metadataList = QcMetadataParser.parseStructuredJsonMetadateModel(jsonNode, parentDirectory);
        return metadataList;
    }


    //应踩数据
    public List<MQcDailyReportDetail> addList(List<MQcDailyReportDetail> list, List<QcDailyEventDetailModel> modelList, String qcDailyReportId, Date createTime, String archiveType) {
        for (QcDailyEventDetailModel eventDetailModel : modelList) {
            MQcDailyReportDetail mQcDailyReportDetail = new MQcDailyReportDetail();
            mQcDailyReportDetail.setEventNo(eventDetailModel.getEvent_no());
            mQcDailyReportDetail.setPatientId(eventDetailModel.getPatient_id());
            mQcDailyReportDetail.setReportId(qcDailyReportId);
            mQcDailyReportDetail.setArchiveType(archiveType);
            mQcDailyReportDetail.setAcqFlag(0);
            mQcDailyReportDetail.setAcqTime(createTime);
            int day = 0;
            if (StringUtils.isNotEmpty(eventDetailModel.getEvent_time())) {
                String parten = "yyyy-MM-dd hh:mm:ss";
                if (eventDetailModel.getEvent_time().contains("T")) {
                    parten = "yyyy-MM-dd'T'HH:mm:ss";
                }
                Date eventTime = DateUtil.parseDate(eventDetailModel.getEvent_time(), parten);
                mQcDailyReportDetail.setEventTime(eventTime);
                long intervalMilli = DateUtil.compareDateTime(createTime, eventTime);
                day = (int) (intervalMilli / (24 * 60 * 60 * 1000));
            }
            mQcDailyReportDetail.setTimelyFlag(day > 2 ? 0 : 1);
            list.add(mQcDailyReportDetail);
        }
        return list;
    }

    public void checkRealStorage(List<MQcDailyReportDetail> realList, String orgCode, Date createDate) throws JsonProcessingException, ParseException {

        //更新采集状态 及 入库状态
        for (MQcDailyReportDetail mQcDailyReportDetail : realList) {
            StringBuffer filter = new StringBuffer();
            String eventNo = mQcDailyReportDetail.getEventNo();
            Date eventTime = mQcDailyReportDetail.getEventTime();
            String patientId = mQcDailyReportDetail.getPatientId();
            filter.append("eventNo=").append(eventNo).append(";").
                    append("patientId=").append(patientId).append(";");
            List<QcDailyReportDetail> list = qcDailyReportDetailService.search(filter.toString());
            if (list != null && list.size() > 0) {
                QcDailyReportDetail qDailyReportDetail = list.get(0);
                qDailyReportDetail.setAcqFlag(1);
                JsonArchives jsonArchives = checkIsInStorage(eventNo, patientId, orgCode);
                if (jsonArchives != null) {
                    qDailyReportDetail.setStorageFlag(returnArchiveStatus(jsonArchives));
                    qDailyReportDetail.setStorageTime(jsonArchives.getFinishDate());
                }
                qcDailyReportDetailService.save(qDailyReportDetail);
            } else {//额外采集 的 新增
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
                JsonArchives jsonArchives = checkIsInStorage(eventNo, patientId, orgCode);
                if (jsonArchives != null) {
                    newqDailyReportDetail.setStorageFlag(returnArchiveStatus(jsonArchives));
                    newqDailyReportDetail.setStorageTime(jsonArchives.getFinishDate());
                }
                qcDailyReportDetailService.save(newqDailyReportDetail);
            }

        }
    }

    //判断是否入库
    public JsonArchives checkIsInStorage(String eventNo, String patientId, String orgCode) throws ParseException {
        //查找是否入库
        StringBuffer str = new StringBuffer();
        str.append("eventNo=").append(eventNo).append(";").
                append("patientId=").append(patientId).append(";").
                append("orgCode=").append(orgCode).append(";");
        List<JsonArchives> list = jsonArchivesService.search(str.toString());
        if (list.isEmpty()) {
            return null;
        } else {
            JsonArchives jsonArchives = list.get(0);
            return jsonArchives;
        }
    }

    public int returnArchiveStatus(JsonArchives jsonArchives) {
        if (jsonArchives != null) {
            if (jsonArchives.getArchiveStatus() == ArchiveStatus.Received) {
                return 0;
            } else if (jsonArchives.getArchiveStatus() == ArchiveStatus.Acquired) {
                return 1;
            } else if (jsonArchives.getArchiveStatus() == ArchiveStatus.Failed) {
                return 2;
            } else if (jsonArchives.getArchiveStatus() == ArchiveStatus.Finished) {
                return 3;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Envelop setQcBeginDate(String date) {
        return redisServiceClient.set(MicroServices.Basic, date, BEGIN_DATE, "");
    }

    public void loadQcData() {
        Envelop envelop = redisServiceClient.get(BEGIN_DATE, "");
        if (!envelop.isSuccessFlg()) {
            return; //
        }
        String beginDate = (String) envelop.getObj();
        Date date = DateUtil.strToDate(beginDate);
        Date addDate = DateUtil.addDate(1, date);
        String endDate = DateUtil.toString(addDate);
        int page = 1;
        int pageSize = 1000;
        Map<String,List<Map<String,Object>>> map = new HashMap<String,List<Map<String,Object>>>();
        Collection<MPackage> mPackages = packMgrClient.packageList("", "receiveDate>=" + beginDate + ";receiveDate<" + endDate, "+receiveDate", page, pageSize);
        while (!mPackages.isEmpty()) {
            for (MPackage mPackage : mPackages) {
                try{
                    ResponseEntity<String> entity = packResolveClient.fetch(mPackage.getId());
                    if (entity.getStatusCode() != HttpStatus.OK) {
                        continue;
                    }

                    String body = entity.getBody();
                    JsonNode jsonNode = objectMapper.readTree(body);
                    String patientId = jsonNode.get("patientId").asText();
                    String eventNo = jsonNode.get("eventNo").asText();
                    String innerVersion = jsonNode.get("cdaVersion").asText();
                    String eventTime = jsonNode.get("eventTime").asText();
                    String orgCode = jsonNode.get("orgCode").asText();
                    String eventType = jsonNode.get("eventType").asText();
                    JsonNode dataSets = jsonNode.get("dataSets");
                    Map<String,Object> patient = new HashMap<String,Object>();
                    patient.put("patient_id",patientId);
                    patient.put("event_no",eventNo);
                    patient.put("event_time",eventTime);
                    patient.put("inner_version",innerVersion);
                    patient.put("event_type",eventType);
                    patient.put("dataSets",dataSets);
                    List<Map<String,Object>> list = map.get(orgCode);
                    //根据orgCode分组
                    if(list!=null){
                        list.add(patient);
                    }else{
                        list = new ArrayList<Map<String,Object>>();
                        list.add(patient);
                        map.put(orgCode, list);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
            }

            page++;
            mPackages = packMgrClient.packageList("", "receiveDate>=" + beginDate + ";receiveDate<" + endDate, "+receiveDate", page, pageSize);
        }

        getData(map);

        this.setQcBeginDate(endDate);

        //TODO:需要发送1个消息，通知统计，不发消息直接统计也是可以。
    }

    /**
     * 根据分组好的orgCode统计数据
     * @param map
     */
    public void getData(Map<String,List<Map<String,Object>>> map){
        if(map!=null) {
            for (String key : map.keySet()) {
                Map<String,Object> qc = new HashMap<String,Object>();
                Map<String,Object> datasets = new HashMap<String,Object>();
                qc.put("org_code", key);
                qc.put("create_date", new Date());
                datasets.put("org_code", key);
                datasets.put("create_date", new Date());
                List<Map<String,Object>> list = map.get(key);//获取orgcode下的数据
                List<Map<String,Object>> inpatient = new ArrayList<Map<String,Object>>();//住院
                List<Map<String,Object>> outpatient = new ArrayList<Map<String,Object>>();//门诊
                List<String> realList = new ArrayList<String>();//上传的数据集
                List<String> totalList = new ArrayList<String>();//全部数据集
                Map<String,List<String>> dataSetsMap = new HashMap<String,List<String>>();//所以数据集对应的数据源
                if(list!=null&&list.size()>0) {
                    Collection<MStdDataSet> sets = standardClient.searchSourcesWithoutPaging("", list.get(0).get("inner_version") + "");//获取所有数据集
                    Collection<MStdMetaData> mStdMetaData = standardClient.searchDataSets("", "", "", -1, 15, list.get(0).get("inner_version") + "");
                    for (MStdDataSet stdDataSet : sets) {
                        totalList.add(stdDataSet.getCode());//所以数据集
                    }
                    for (int i = 0; i < list.size(); i++) {
                        //获取版本号和事件时间
                        if (i == 0) {
                            qc.put("inner_version", list.get(0).get("inner_version"));
                            datasets.put("event_time", list.get(0).get("event_time"));
                            datasets.put("inner_version", list.get(0).get("inner_version"));
                        }
                        Map<String, Object> patient = list.get(i);
                        JsonNode dataSetsNode = (JsonNode) patient.get("dataSets");
                        patient.remove("dataSets");
                        Iterator<Map.Entry<String, JsonNode>> iterator = dataSetsNode.fields();

                        while (iterator.hasNext()) {
                            Map.Entry<String, JsonNode> item = iterator.next();
                            realList.add(item.getKey());//获取数据集
                            JsonNode records = item.getValue().get("records");
                            //遍历数据元
                            Iterator<Map.Entry<String, JsonNode>> iterator1 = records.fields();
                            while (iterator1.hasNext()) {
                                Map.Entry<String, JsonNode> item1 = iterator1.next();
                                Iterator<Map.Entry<String, JsonNode>> iterator2 = item1.getValue().fields();
                                while (iterator2.hasNext()) {
                                    Map.Entry<String, JsonNode> item2 = iterator2.next();
                                    List<String> dataSetsList = dataSetsMap.get(item.getKey());
                                    if (dataSetsList != null) {
                                        dataSetsList.add(item2.getKey());
                                    } else {
                                        dataSetsList = new ArrayList<String>();
                                        dataSetsList.add(item2.getKey());
                                        dataSetsMap.put(item.getKey(), dataSetsList);
                                    }
                                }
                            }
                        }
                        if ("Resident".equals(patient.get("event_type"))) {//住院
                            patient.remove("inner_version");
                            patient.remove("event_type");
                            inpatient.add(patient);
                        } else {//门诊
                            patient.remove("inner_version");
                            patient.remove("event_type");
                            outpatient.add(patient);
                        }
                    }
                    qc.put("total_outpatient_num", outpatient.size());
                    qc.put("real_outpatient_num", outpatient.size());
                    qc.put("total_hospital_num", inpatient.size());
                    qc.put("real_hospital_num", inpatient.size());
                    qc.put("total_outpatient", outpatient);
                    qc.put("real_outpatient", outpatient);
                    qc.put("total_hospital", inpatient);
                    qc.put("real_hospital", inpatient);
                    datasets.put("real_num", realList.size());
                    datasets.put("real", realList);
                    datasets.put("total_num", totalList.size());
                    datasets.put("total", totalList);
                    try {
                        //数据元统计情况
                        for (String datasetsKey : dataSetsMap.keySet()) {
                            Map<String,Object> dataSetMap = new HashMap<String,Object>();
                            dataSetMap.put("org_code",key);
                            dataSetMap.put("event_time",datasets.get("event_time"));
                            dataSetMap.put("inner_version",datasets.get("inner_version"));
                            dataSetMap.put("create_date",datasets.get("create_date"));
                            dataSetMap.put("dataset",datasetsKey);
                            dataSetMap.put("data",getDateSets(mStdMetaData,datasetsKey,dataSetsMap.get(datasetsKey),datasets.get("inner_version")+""));
                            System.out.println(objectMapper.writeValueAsString(dataSetMap));
                        }
                        System.out.println(objectMapper.writeValueAsString(qc));
                        System.out.println(objectMapper.writeValueAsString(datasets));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * 获取数据元
     * @param mStdMetaData
     * @param dataSet
     * @return
     */
    public List<Map<String,Object>> getDateSets(Collection<MStdMetaData> mStdMetaData, String dataSet, List<String> dataSetList, String innerVersion){
        Map<String,Object> datasetMap = new HashMap<String,Object>();
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        List<String> all = new ArrayList<String>();
        //去掉重复的数据源
        HashSet h = new HashSet(dataSetList);
        dataSetList.clear();
        dataSetList.addAll(h);
        //根据code获取数据集
        Map<String,Object> map = standardClient.getDataSetByCode(innerVersion,dataSet);
        for(MStdMetaData data : mStdMetaData){
            if(data.getDataSetId()==Integer.parseInt(map.get("id")+"")){
                all.add(data.getCode());
            }
        }
        datasetMap.put("total",all.size());
        datasetMap.put("error",all.size()-dataSetList.size());
        //把不存在的数据集错误代码设置为empty
        for(String str1 : all){
            boolean flag = false;
            for(String str2 : dataSetList){
                if(str1.equals(str2)){
                    flag = true;
                }
            }
            if(!flag){
                datasetMap.put(str1,"empty");
            }
        }
        list.add(datasetMap);
        return list;
    }

    private void loadQcDetailData(MPackage mPackage) {
        try {
            ResponseEntity<String> entity = packResolveClient.fetch(mPackage.getId());
            if (entity.getStatusCode() != HttpStatus.OK) {
                return;
            }

            String body = entity.getBody();
            JsonNode jsonNode = objectMapper.readTree(body);
            String patientId = jsonNode.get("patientId").asText();
            String eventNo = jsonNode.get("eventNo").asText();
            String orgCode = jsonNode.get("orgCode").asText();
            String eventType = jsonNode.get("eventType").asText();
            JsonNode dataSets = jsonNode.get("dataSets");

            List<QcDailyReport> dailyList = qcDailyReportService.getData(orgCode, mPackage.getReceiveDate());
            String dailyId = "";
            if(dailyList!=null && dailyList.size()>0){
                dailyId = dailyList.get(0).getId();
            }else{
                QcDailyReport qcDailyReport = new QcDailyReport();
                qcDailyReport.setOrgCode(orgCode);
                qcDailyReport.setCreateDate(mPackage.getReceiveDate());
                qcDailyReport.setAddDate(new Date());
                qcDailyReport.setRealHospitalNum(0);
                qcDailyReport.setTotalHospitalNum(0);
                qcDailyReport.setTotalOutpatientNum(0);
                qcDailyReport.setRealOutpatientNum(0);
                qcDailyReport = qcDailyReportService.save(qcDailyReport);
                dailyId = qcDailyReport.getId();
            }
            QcDailyReportDetail qcDailyReportDetail = new QcDailyReportDetail();
            qcDailyReportDetail.setReportId(dailyId);
            qcDailyReportDetail.setPatientId(patientId);
            qcDailyReportDetail.setEventNo(eventNo);
            qcDailyReportDetail.setArchiveType(eventType.equals("Resident") ? "inpatient" : "outpatient");
            qcDailyReportDetail.setEventTime(mPackage.getReceiveDate());
            qcDailyReportDetail.setAcqFlag(1);
            qcDailyReportDetail.setAcqTime(mPackage.getReceiveDate());
            qcDailyReportDetail.setStorageFlag(mPackage.getArchiveStatus() == ArchiveStatus.Finished ? 1 : 0);
            qcDailyReportDetail.setStorageTime(mPackage.getFinishDate());
            qcDailyReportDetail.setAddDate(new Date());

            qcDailyReportDetailService.save(qcDailyReportDetail);   //TODO:需要过滤重复？

            String dataSetsId="";
            List<QcDailyReportDatasets> datasetsList = qcDailyReportDatasetsService.getTodayData(orgCode, mPackage.getReceiveDate());
            if(datasetsList!=null && datasetsList.size()>0){
                dataSetsId = datasetsList.get(0).getId();
            }else{
                QcDailyReportDatasets qcDailyReportDatasets = new QcDailyReportDatasets();
                qcDailyReportDatasets.setCreateDate(mPackage.getReceiveDate());
                qcDailyReportDatasets.setOrgCode(orgCode);
                qcDailyReportDatasets.setEventTime(mPackage.getReceiveDate());
                qcDailyReportDatasets.setTotalNum(0);
                qcDailyReportDatasets.setRealNum(0);
                qcDailyReportDatasets.setAddDate(new Date());
                qcDailyReportDatasets = qcDailyReportDatasetsService.save(qcDailyReportDatasets);
                dataSetsId = qcDailyReportDatasets.getId();
            }
            Iterator<String> fieldNames = dataSets.fieldNames();
            while (fieldNames.hasNext()) {
                String dateSet = fieldNames.next();
                QcDailyReportDataset qcDailyReportDataset = new QcDailyReportDataset();
                qcDailyReportDataset.setDataset(dateSet);
                qcDailyReportDataset.setAcqFlag(1);
                qcDailyReportDataset.setReportId(dataSetsId);
                qcDailyReportDataset.setAddDate(new Date());

                qcDailyReportDatasetService.save(qcDailyReportDataset);   //TODO:需要过滤重复？
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
