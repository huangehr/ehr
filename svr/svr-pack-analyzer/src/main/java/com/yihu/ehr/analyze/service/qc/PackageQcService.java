package com.yihu.ehr.analyze.service.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.model.ZipPackage;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.ErrorType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.redis.client.RedisClient;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.string.StringBuilderEx;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Airhead
 * @created 2018-01-19
 */
@Service
public class PackageQcService {
    private static final Logger logger = LoggerFactory.getLogger(PackageQcService.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Class clazz = QcRuleCheckService.class;


    @Autowired
    private QcRuleCheckService qcRuleCheckService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    /**
     * 处理质控消息，统一入口，减少异步消息的数量
     * 1.通过Redis的消息订阅发布来处理质控规则
     * 2.通过ES统计接收包的情况
     *
     * @param zipPackage 档案包
     */
    public void qcHandle(ZipPackage zipPackage) throws Throwable {
        if(zipPackage.getEventType() == null){
            throw new IllegalJsonDataException("event type is empty");
        }
        EsSimplePackage esSimplePackage = zipPackage.getEsSimplePackage();
        Map<String, Object> qcDataSetRecord = zipPackage.getQcDataSetRecord();
        qcDataSetRecord.put("_id", esSimplePackage.get_id());
        qcDataSetRecord.put("patient_id", zipPackage.getPatientId());
        qcDataSetRecord.put("pack_id", esSimplePackage.get_id());
        qcDataSetRecord.put("org_code", zipPackage.getOrgCode());
        qcDataSetRecord.put("org_name", zipPackage.getOrgName());
        qcDataSetRecord.put("org_area", zipPackage.getOrgArea());
        qcDataSetRecord.put("dept", zipPackage.getDeptCode());
        qcDataSetRecord.put("diagnosis_name", StringUtils.join(zipPackage.getDiagnosisName().toArray(),";"));
        qcDataSetRecord.put("receive_date", DATE_FORMAT.format(esSimplePackage.getReceive_date()));
        qcDataSetRecord.put("event_date", DateUtil.toStringLong(zipPackage.getEventDate()));
        qcDataSetRecord.put("event_type", zipPackage.getEventType() == null ? -1 : zipPackage.getEventType().getType());
        qcDataSetRecord.put("event_no", zipPackage.getEventNo());
        qcDataSetRecord.put("version", zipPackage.getCdaVersion());
        qcDataSetRecord.put("count", zipPackage.getDataSets().size());
        qcDataSetRecord.put("qc_step", 1);
        qcDataSetRecord.put("create_date", DATE_FORMAT.format(new Date()));
        List<Map<String, Object>> details = new ArrayList<>();
        Map<String, PackageDataSet> dataSets = zipPackage.getDataSets();
        for (String dataSetCode : dataSets.keySet()) {
            updateDatasetDetail(zipPackage.getOrgCode(), DATE_FORMAT.format(esSimplePackage.getReceive_date()),dataSetCode,zipPackage.getCdaVersion()
                    ,zipPackage.getEventType().getType() ,dataSets.get(dataSetCode).getRecords().size());
            Map<String, Object> dataSet = new HashMap<>();
            dataSet.put(dataSetCode, dataSets.get(dataSetCode).getRecords().size());
            details.add(dataSet);
            Map<String, MetaDataRecord> records = dataSets.get(dataSetCode).getRecords();
            Set<String> existSet = new HashSet<>(); //存放已经生成了质控信息的数据元
            for (String recordKey : records.keySet()) {
                Map<String, String> dataGroup = records.get(recordKey).getDataGroup();
                List<String> listDataElement = getDataElementList(dataSets.get(dataSetCode).getCdaVersion(), dataSetCode);
                for (String metadata : listDataElement) {
                    if (existSet.contains(dataSetCode + "$" + metadata)) { //如果该数据元已经有质控数据则跳过
                        continue;
                    }
                    String method = redisClient.get("qc_" + zipPackage.getCdaVersion() + ":" + dataSetCode + ":" + metadata);
                    if (method != null) {
                        Method _method = clazz.getMethod(method, new Class[]{String.class, String.class, String.class, String.class});
                        _method.setAccessible(true);
                        ErrorType errorType;
                        try {
                            errorType = (ErrorType) _method.invoke(qcRuleCheckService, zipPackage.getCdaVersion(), dataSetCode, metadata, dataGroup.get(metadata));
                        } catch (Exception e) {
                            throw e.getCause();
                        }
                        if (errorType != ErrorType.Normal) {
                            Map<String, Object> qcMetadataRecord = new HashMap<>();
                            StringBuilder _id = new StringBuilder();
                            _id.append(esSimplePackage.get_id())
                                    .append("$")
                                    .append(dataSetCode)
                                    .append("$")
                                    .append(metadata);
                            qcMetadataRecord.put("_id", _id.toString());
                            qcMetadataRecord.put("patient_id", zipPackage.getPatientId());
                            qcMetadataRecord.put("pack_id", esSimplePackage.get_id());
                            qcMetadataRecord.put("org_code", zipPackage.getOrgCode());
                            qcMetadataRecord.put("org_name", zipPackage.getOrgName());
                            qcMetadataRecord.put("org_area", zipPackage.getOrgArea());
                            qcMetadataRecord.put("dept", zipPackage.getDeptCode());
                            qcMetadataRecord.put("diagnosis_name", StringUtils.join(zipPackage.getDiagnosisName().toArray(), ";"));
                            qcMetadataRecord.put("receive_date", DATE_FORMAT.format(esSimplePackage.getReceive_date()));
                            qcMetadataRecord.put("event_date", DateUtil.toStringLong(zipPackage.getEventDate()));
                            qcMetadataRecord.put("event_type", zipPackage.getEventType() == null ? -1 : zipPackage.getEventType().getType());
                            qcMetadataRecord.put("event_no", zipPackage.getEventNo());
                            qcMetadataRecord.put("version", zipPackage.getCdaVersion());
                            qcMetadataRecord.put("dataset", dataSetCode);
                            qcMetadataRecord.put("metadata", metadata);
                            qcMetadataRecord.put("value", dataGroup.get(metadata));
                            qcMetadataRecord.put("qc_step", 1); //标准质控环节
                            qcMetadataRecord.put("qc_error_type", errorType.getType()); //标准质控错误类型
                            qcMetadataRecord.put("qc_error_name", errorType.getName()); //标准质控错误名称
                            qcMetadataRecord.put("qc_error_message", String.format("%s failure for meta data %s of %s in %s", method, metadata, dataSetCode, zipPackage.getCdaVersion()));
                            qcMetadataRecord.put("create_date", DATE_FORMAT.format(new Date()));
                            zipPackage.getQcMetadataRecords().add(qcMetadataRecord);
                            existSet.add(dataSetCode + "$" + metadata);
                        }
                    }
                }
            }
        }
        qcDataSetRecord.put("details", objectMapper.writeValueAsString(details));
        qcDataSetRecord.put("missing", "[]");
    }

    private List<String> getDataElementList(String version, String dataSetCode) {
        String metadataCodes = redisClient.get(makeKey("std_data_set_" + version, dataSetCode, "metada_code"));
        String[] metadataList = StringUtils.split(metadataCodes, ",");
        if (metadataList == null){
            logger.error("version:" + version + ",dataSetCode:" + dataSetCode);
            return new ArrayList<>();
        }
        return Arrays.asList(metadataList);
    }

    /**
     * 获取key
     */
    private String makeKey(String table, String key, String column) {
        return new StringBuilderEx("%1:%2:%3")
                .arg(table)
                .arg(key)
                .arg(column)
                .toString();
    }

    private void updateDatasetDetail(String orgCode, String receiveDate, String dataset,
                                     String version, int eventType, int row) throws Exception{
        String date = receiveDate.substring(0,10);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("dataset="+dataset+";");
        stringBuilder.append("receive_date>=" + date + " 00:00:00;");
        stringBuilder.append("receive_date<" + date + " 23:59:59;");
        stringBuilder.append("org_code=" + orgCode+";");
        stringBuilder.append("event_type=" + eventType+";");
        List<Map<String, Object>> list = elasticSearchUtil.list("json_archives_qc","qc_dataset_detail",stringBuilder.toString());
        if(list!=null&&list.size()>0){
            Map<String, Object> map = list.get(0);
            map.put("row", Integer.parseInt(map.get("row").toString())+row);
            map.put("count", Integer.parseInt(map.get("count").toString())+1);
            elasticSearchUtil.update("json_archives_qc", "qc_dataset_detail", map.get("_id")+"", map);
        }else{
            Map<String, Object> map = new HashMap<>();
            map.put("org_code", orgCode);
            map.put("event_type", eventType);
            map.put("receive_date", date+" 00:00:00");
            map.put("dataset", dataset);
            map.put("dataset_name", redisClient.get("std_data_set_" + version + ":" + dataset + ":name"));
            map.put("row", row);
            map.put("count", 1);
            elasticSearchUtil.index("json_archives_qc", "qc_dataset_detail", map);
        }
    }
}
