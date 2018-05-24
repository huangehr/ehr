package com.yihu.ehr.analyze.service.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.deploy.security.ValidationState;
import com.yihu.ehr.analyze.feign.HosAdminServiceClient;
import com.yihu.ehr.analyze.feign.RedisServiceClient;
import com.yihu.ehr.analyze.service.pack.ZipPackage;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
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
import java.text.ParseException;
import java.util.*;

/**
 * @author Airhead
 * @created 2018-01-19
 */
@Service
public class PackageQcService {
    private static final Logger logger = LoggerFactory.getLogger(PackageQcService.class);
    private static final String INDEX = "json_archives";
    private static final String QC_TYPE = "qc_info";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
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
        List<Map<String, Object>> qcRecords = new ArrayList<>();
        Map<String, PackageDataSet> dataSets = zipPackage.getDataSets();
        dataSets.forEach((dataSetCode, dataSetRecord) -> {
            Map<String, MetaDataRecord> records = dataSetRecord.getRecords();
            int size = records.size();
            Map<String, Object> map = new HashMap<>(0);
            map.put("orgCode", dataSetRecord.getOrgCode());
            map.put("dataSet", dataSetRecord.getCode());
            map.put("dataSetRow", size);
            map.put("receiveTime", esSimplePackage.getReceive_date());
            map.put("packId", esSimplePackage.get_id());
            map.put("_id", esSimplePackage.get_id() + ":" + dataSetRecord.getCode());
            try {
                elasticSearchUtil.index("qc", "receive_data_set", map);
            } catch (ParseException e) {
                logger.error("receive_data_set" + e.getMessage());
            }
            records.forEach((recordKey, dataElement) -> {
                Map<String, String> dataGroup = dataElement.getDataGroup();
                List<String> listDataElement = getDataElementList(dataSetRecord.getCdaVersion(), dataSetRecord.getCode());
                for (String metadata : listDataElement) {
                    Serializable serializable = redisTemplate.opsForValue().get("qc:" + zipPackage.getCdaVersion() + ":" + dataSetCode + ":" + metadata);
                    if (serializable != null) {
                        String method = serializable.toString();
                        Class clazz = qcRuleCheckService.getClass();
                        try {
                            Method _method = clazz.getMethod(method, new Class[]{String.class, String.class, String.class});
                            Boolean legal = (Boolean)_method.invoke(qcRuleCheckService, dataSetCode, metadata, dataGroup.get(metadata));
                            if (!legal) {
                                Map<String, Object> qcRecord = new HashMap<>();
                                qcRecords.add(qcRecord);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            });
        });
        elasticSearchUtil.bulkIndex(INDEX, QC_TYPE, qcRecords);
    }

    private List<String> getDataElementList(String version, String dataSetCode) {
        String metadataCodes = hosAdminServiceClient.getMetadataCodes(version, dataSetCode);
        String[] metadataList = StringUtils.split(metadataCodes, ",");
        if(metadataList==null){
            logger.error("version:"+version +",dataSetCode:"+dataSetCode);
            return new ArrayList<>();
        }
        return Arrays.asList(metadataList);
    }

}
