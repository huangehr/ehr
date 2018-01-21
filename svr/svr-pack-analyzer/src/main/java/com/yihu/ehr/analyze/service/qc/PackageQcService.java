package com.yihu.ehr.analyze.service.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.analyze.feign.RedisServiceClient;
import com.yihu.ehr.analyze.service.pack.DataElementRecord;
import com.yihu.ehr.analyze.service.pack.DataSetRecord;
import com.yihu.ehr.analyze.service.pack.ZipPackage;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.util.DataSetUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    /**
     * 通过Redis的消息订阅发布来处理质控规则
     *
     * @param zipPackage 档案包
     */
    @Async
    public void sendQcMsg(ZipPackage zipPackage) {
        MPackage mPackage = zipPackage.getmPackage();
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
                    String receiveTime = DateUtil.toString(mPackage.getReceiveDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT);
                    msgNode.put("receiveTime", receiveTime);

                    redisServiceClient.sendMessage("", channel, msgNode.toString());
                });
            });
        });
    }

    /**
     * 统计包数据集
     *
     * @param zipPackage
     */
    @Async
    public void qcReceive(ZipPackage zipPackage) {
        MPackage mPackage = zipPackage.getmPackage();
        Map<String, DataSetRecord> dataSets = zipPackage.getDataSets();
        final int[] i = {0};
        dataSets.forEach((key, dataSetRecord) -> {
            if (key.contains(DataSetUtil.OriginDataSetFlag)) {
                return;
            }

            if (i[0] == 0) {
                i[0]++;
                //TODO: save receive patient

            }

            Map<String, DataElementRecord> records = dataSetRecord.getRecords();
            int size = records.size();
            //TODO: save


        });
    }
}
