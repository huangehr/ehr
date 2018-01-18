package com.yihu.ehr.analyze.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.analyze.feign.PackageMgrClient;
import com.yihu.ehr.analyze.feign.RedisServiceClient;
import com.yihu.ehr.model.packs.MPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 档案解析引擎.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Service
public class PackageAnalyzeService {
    private final static Logger logger = LoggerFactory.getLogger(PackQueueService.class);

    @Autowired
    private PackQueueService packQueueService;
    @Autowired
    private PackageMgrClient mgrClient;
    @Autowired
    private RedisServiceClient redisServiceClient;
    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
    }

    /**
     * analyze 档案分析服务
     * 1.从队列中取出档案
     * 2.分析档案
     * 3.数据入库
     *
     * @author Airhead
     * @created 2018.01.15
     */
    public void analyze() {
        MPackage mPackage = null;
        ZipPackage zipPackage = null;
        try {
            mPackage = packQueueService.pop();
            zipPackage = new ZipPackage(mPackage);
            zipPackage.download();
            zipPackage.unZip();
            zipPackage.resolve();
            zipPackage.save();

            mgrClient.analyzeStatus(mPackage.getId(), 3);

            //发送Qc消息
            sendMsg(zipPackage);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            if (zipPackage != null) {
                zipPackage.houseKeep();
            }
        }
    }

    /**
     * 通过Redis的消息订阅发布来处理质控规则
     *
     * @param zipPackage 档案包
     */
    private void sendMsg(ZipPackage zipPackage) {
        Map<String, DataSetRecord> dataSets = zipPackage.getDataSets();
        dataSets.forEach((key, dataSetRecord) -> {
            Map<String, DataElementRecord> records = dataSetRecord.getRecords();
            records.forEach((elementCode, dataElement) -> {
                String rowKey = dataSetRecord.genRowKey(elementCode);
                Map<String, String> dataGroup = dataElement.getDataGroup();
                dataGroup.forEach((code, value) -> {
                    String channel = "dataElement_" + code;
                    ObjectNode msgNode = objectMapper.createObjectNode();
                    msgNode.put("rowKey", rowKey);
                    msgNode.put("table", dataSetRecord.getCode());
                    msgNode.put("columnFamily", ZipPackage.DATA);
                    msgNode.put("code", code);
                    msgNode.put("value", value);

                    redisServiceClient.sendMessage("", channel, msgNode.toString());
                });
            });
        });
    }

}
