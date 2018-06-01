package com.yihu.ehr.analyze.service.qc;

import com.yihu.ehr.analyze.feign.HosAdminServiceClient;
import com.yihu.ehr.analyze.service.pack.ZipPackage;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private HosAdminServiceClient hosAdminServiceClient;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private QcRuleCheckService qcRuleCheckService;


    /**
     * 处理质控消息，统一入口，减少异步消息的数量
     * 1.通过Redis的消息订阅发布来处理质控规则
     * 2.通过ES统计接收包的情况
     *
     * @param zipPackage 档案包
     */
    public void qcHandle(ZipPackage zipPackage) throws Exception {
        EsSimplePackage esSimplePackage = zipPackage.getEsSimplePackage();
        /*Map<String, Object> packMap = new HashMap<>(0);
        packMap.put("orgCode", zipPackage.getOrgCode());
        packMap.put("patientId", zipPackage.getPatientId());
        packMap.put("eventNo", zipPackage.getEventNo());
        packMap.put("eventTime", zipPackage.getEventDate());
        packMap.put("eventType", zipPackage.getEventType());
        packMap.put("receiveTime", esSimplePackage.getReceive_date());
        packMap.put("packId", esSimplePackage.get_id());
        packMap.put("_id", esSimplePackage.get_id());
        try {
            elasticSearchUtil.index("qc", "receive_data_pack", packMap);
        } catch (ParseException e) {
            logger.error("receive_data_pack," + e.getMessage());
        }*/
        Map<String, Object> qcDataSetRecord = zipPackage.getQcDataSetRecord();
        qcDataSetRecord.put("_id", esSimplePackage.get_id());
        qcDataSetRecord.put("patient_id", zipPackage.getPatientId());
        qcDataSetRecord.put("pack_id", esSimplePackage.get_id());
        qcDataSetRecord.put("org_code", zipPackage.getOrgCode());
        qcDataSetRecord.put("org_name", zipPackage.getOrgName());
        qcDataSetRecord.put("org_area", zipPackage.getOrgArea());
        qcDataSetRecord.put("dept", zipPackage.getDeptCode());
        qcDataSetRecord.put("diagnosis_name", zipPackage.getDiagnosisNameList() == null ? "" : StringUtils.join(zipPackage.getDiagnosisNameList().toArray(),";"));
        qcDataSetRecord.put("receive_date", DATE_FORMAT.format(esSimplePackage.getReceive_date()));
        qcDataSetRecord.put("event_date", DateUtil.toStringLong(zipPackage.getEventDate()));
        qcDataSetRecord.put("event_type", zipPackage.getEventType() == null ? -1 : zipPackage.getEventType().getType());
        qcDataSetRecord.put("event_no", zipPackage.getEventNo());
        qcDataSetRecord.put("version", zipPackage.getCdaVersion());
        qcDataSetRecord.put("count", zipPackage.getDataSets().size());
        List<String> details = new ArrayList<>();
        Map<String, PackageDataSet> dataSets = zipPackage.getDataSets();
        for (String dataSetCode : dataSets.keySet()) {
            details.add(dataSetCode);
            Map<String, MetaDataRecord> records = dataSets.get(dataSetCode).getRecords();
            for (String recordKey : records.keySet()) {
                Map<String, String> dataGroup = records.get(recordKey).getDataGroup();
                List<String> listDataElement = getDataElementList(dataSets.get(dataSetCode).getCdaVersion(), dataSetCode);
                for (String metadata : listDataElement) {
                    Serializable serializable = redisTemplate.opsForValue().get("qc_" + zipPackage.getCdaVersion() + ":" + dataSetCode + ":" + metadata);
                    if (serializable != null) {
                        Class clazz = qcRuleCheckService.getClass();
                        String[] methods = serializable.toString().split(";");
                        for(int i=0; i<methods.length; i++) {
                            Method _method = clazz.getMethod(methods[i], new Class[]{String.class, String.class, String.class, String.class});
                            _method.setAccessible(true);
                            int result = (int) _method.invoke(qcRuleCheckService, zipPackage.getCdaVersion(), dataSetCode, metadata, dataGroup.get(metadata));
                            if (result != 0) {
                                Map<String, Object> qcMetadataRecord = new HashMap<>();
                                qcMetadataRecord.put("_id", esSimplePackage.get_id() + "_" + dataSetCode + "_" + metadata);
                                qcMetadataRecord.put("patient_id", zipPackage.getPatientId());
                                qcMetadataRecord.put("pack_id", esSimplePackage.get_id());
                                qcMetadataRecord.put("org_code", zipPackage.getOrgCode());
                                qcMetadataRecord.put("org_name", zipPackage.getOrgName());
                                qcMetadataRecord.put("org_area", zipPackage.getOrgArea());
                                qcMetadataRecord.put("dept", zipPackage.getDeptCode());
                                qcMetadataRecord.put("diagnosis_name", zipPackage.getDiagnosisNameList() == null ? "" : StringUtils.join(zipPackage.getDiagnosisNameList().toArray(), ";"));
                                qcMetadataRecord.put("receive_date", DATE_FORMAT.format(esSimplePackage.getReceive_date()));
                                qcMetadataRecord.put("event_date", DateUtil.toStringLong(zipPackage.getEventDate()));
                                qcMetadataRecord.put("event_type", zipPackage.getEventType() == null ? -1 : zipPackage.getEventType().getType());
                                qcMetadataRecord.put("event_no", zipPackage.getEventNo());
                                qcMetadataRecord.put("version", zipPackage.getCdaVersion());
                                qcMetadataRecord.put("dataset", dataSetCode);
                                qcMetadataRecord.put("metadata", metadata);
                                qcMetadataRecord.put("value", dataGroup.get(metadata));
                                qcMetadataRecord.put("qc_step", 1); //标准质控环节
                                qcMetadataRecord.put("qc_error_type", result); //标准质控错误类型
                                qcMetadataRecord.put("qc_error_message", String.format("%s failure for meta data %s of %s in %s", methods[i], metadata, dataSetCode, zipPackage.getCdaVersion()));
                                zipPackage.getQcMetadataRecords().add(qcMetadataRecord);
                            }
                        }
                    }
                }
            }
        }
        qcDataSetRecord.put("details", details);
        qcDataSetRecord.put("missing", new ArrayList<>());
    }

    private List<String> getDataElementList(String version, String dataSetCode) {
        long starttime = System.currentTimeMillis();
        String metadataCodes = hosAdminServiceClient.getMetadataCodes(version, dataSetCode);
        String[] metadataList = StringUtils.split(metadataCodes, ",");
        if (metadataList == null){
            logger.error("version:" + version + ",dataSetCode:" + dataSetCode);
            return new ArrayList<>();
        }
        long endtime = System.currentTimeMillis();
        logger.info("获取数据元耗时：" + (endtime - starttime) + "ms");
        return Arrays.asList(metadataList);
    }

}
