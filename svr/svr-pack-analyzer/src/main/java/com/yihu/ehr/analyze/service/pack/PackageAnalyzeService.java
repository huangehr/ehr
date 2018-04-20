package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.analyze.feign.PackageMgrClient;
import com.yihu.ehr.analyze.service.qc.PackageQcService;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.util.rest.Envelop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
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
    private final static Logger logger = LoggerFactory.getLogger(PackageAnalyzeService.class);
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private PackQueueService packQueueService;
    @Autowired
    private PackageMgrClient mgrClient;
    @Autowired
    private PackageQcService packageQcService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

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
        EsSimplePackage esSimplePackage = null;
        ZipPackage zipPackage = null;
        try {
            esSimplePackage = packQueueService.pop();
            if (esSimplePackage != null) {
                zipPackage = new ZipPackage(esSimplePackage);
                zipPackage.download();
                zipPackage.unZip();
                zipPackage.resolve();
                zipPackage.save();
                mgrClient.analyzeStatus(esSimplePackage.get_id(), 3);
                //处理质控
                packageQcService.qcHandle(zipPackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            try {
                mgrClient.analyzeStatus(esSimplePackage.get_id(), 2);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            if (zipPackage != null) {
                zipPackage.houseKeep();
            }
        }
    }

    public Envelop esSaveData(String index, String type, String dataList) {
        Envelop envelop = new Envelop();
        try {
            List<Map<String, Object>> list = objectMapper.readValue(dataList, List.class);
            for (Map<String, Object> map : list) {
                elasticSearchUtil.index(index, type, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return envelop;
    }

    @PostConstruct
    private void init() {
    }
}
