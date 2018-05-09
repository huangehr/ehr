package com.yihu.ehr.analyze.service.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.analyze.feign.RedisServiceClient;
import com.yihu.ehr.analyze.service.pack.DataElementRecord;
import com.yihu.ehr.analyze.service.pack.DataSetRecord;
import com.yihu.ehr.analyze.service.pack.ZipPackage;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Airhead
 * @created 2018-01-19
 */
@Service
public class PackageQcService {
    @Autowired
    private RedisServiceClient redisServiceClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    /**
     * 处理质控消息，统一入口，减少异步消息的数量
     *
     * @param zipPackage 档案包
     */
    //@Async
    public void qcHandle(ZipPackage zipPackage) {
        sendQcMsg(zipPackage);
        qcReceive(zipPackage);
    }

    /**
     * 通过Redis的消息订阅发布来处理质控规则
     *
     * @param zipPackage 档案包
     */
    public void sendQcMsg(ZipPackage zipPackage) {
        EsSimplePackage esSimplePackage = zipPackage.getEsSimplePackage();
        Map<String, DataSetRecord> dataSets = zipPackage.getDataSets();
        dataSets.forEach((key, dataSetRecord) -> {
            Map<String, DataElementRecord> records = dataSetRecord.getRecords();
            records.forEach((elementCode, dataElement) -> {
                String rowKey = dataSetRecord.genRowKey(elementCode);
                Map<String, String> dataGroup = dataElement.getDataGroup();
                dataGroup.forEach((code, value) -> {
                    String channel = "qc_channel_" + code;
                    ObjectNode msgNode = objectMapper.createObjectNode();
                    msgNode.put("rowKey", rowKey);
                    msgNode.put("table", dataSetRecord.getCode());
                    msgNode.put("columnFamily", ZipPackage.DATA);
                    msgNode.put("version", dataSetRecord.getVersion());
                    msgNode.put("code", code);
                    msgNode.put("value", value);
                    msgNode.put("orgCode", dataSetRecord.getOrgCode());
                    msgNode.put("patientId", dataSetRecord.getPatientId());
                    msgNode.put("eventNo", dataSetRecord.getEventNo());
                    String eventTime = DateUtil.toString(dataSetRecord.getEventTime(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT);
                    msgNode.put("eventTime", eventTime);
                    String receiveTime = DateUtil.toString(esSimplePackage.getReceive_date(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT);
                    msgNode.put("receiveTime", receiveTime);

                    redisServiceClient.sendMessage("svr-pack-analyzer", channel, msgNode.toString());
                });
            });
        });
    }

    /**
     * 实时统计接收包情况及数据集情况
     *
     * @param zipPackage
     */
    public void qcReceive(ZipPackage zipPackage) {
        EsSimplePackage esSimplePackage = zipPackage.getEsSimplePackage();
        Map<String, DataSetRecord> dataSets = zipPackage.getDataSets();
        final int[] i = {0};
        dataSets.forEach((key, dataSetRecord) -> {
            if (key.contains(DataSetUtil.OriginDataSetFlag)) {
                return;
            }

            if (i[0] == 0) {
                i[0]++;

                Map<String, Object> map = new HashMap<>();
                map.put("orgCode", dataSetRecord.getOrgCode());
                map.put("patientId", dataSetRecord.getPatientId());
                map.put("eventNo", dataSetRecord.getEventNo());
                map.put("eventTime", dataSetRecord.getEventTime());
                //注意，此处的EventType可能不准确，包中未提供
                map.put("eventType", dataSetRecord.getEventType());
                map.put("receiveTime", esSimplePackage.getReceive_date());
                map.put("packId", esSimplePackage.get_id());
                try {
                    elasticSearchUtil.index("qc", "receive_data_pack", map);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Map<String, DataElementRecord> records = dataSetRecord.getRecords();
            int size = records.size();
            Map<String, Object> map = new HashMap<>();
            map.put("orgCode", dataSetRecord.getOrgCode());
            map.put("dataSet", dataSetRecord.getCode());
            map.put("dataSetRow", size);
            map.put("receiveTime", esSimplePackage.getReceive_date());
            map.put("packId", esSimplePackage.get_id());
            try {
                elasticSearchUtil.index("qc", "receive_data_set", map);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 重新统计某段时间接收包情况及数据集情况，需要提供端点
     * 根据Hbase进行重新计算，还需要修改Hbase的接收时间
     *
     * @param beginDate
     * @param endDate
     */
    public void qcReceive(String beginDate, String endDate) {
        //TODO:
    }
}
