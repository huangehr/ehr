package com.yihu.ehr.analyze.service;

import com.yihu.ehr.analyze.feign.PackageMgrClient;
import com.yihu.ehr.model.packs.MPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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
    private PackageQcService packageQcService;

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
            packageQcService.sendQcMsg(zipPackage);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());

            try {
                mgrClient.analyzeStatus(mPackage.getId(), 2);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            if (zipPackage != null) {
                zipPackage.houseKeep();
            }
        }
    }

}
