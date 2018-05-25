package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by progr1mmer on 2018/5/23.
 */
@Service
public class QcRecordService {

    private static final String INDEX = "json_archives";
    private static final String QC_METADATA_INFO = "qc_metadata_info";

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public void record(ResourceBucket resourceBucket) throws Exception {
        System.out.println(resourceBucket.getQcMetadataRecords().getRecords().size());
        //elasticSearchUtil.bulkIndex(INDEX, QC_METADATA_INFO, resourceBucket.getQcRecords().getRecords());
    }

}

