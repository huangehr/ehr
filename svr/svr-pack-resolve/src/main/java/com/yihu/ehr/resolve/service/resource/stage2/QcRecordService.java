package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by progr1mmer on 2018/5/23.
 */
@Service
public class QcRecordService {

    private static final String INDEX = "json_archives_qc";
    private static final String QC_METADATA_INFO = "qc_metadata_info";

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public void record(ResourceBucket resourceBucket) throws Exception {
        elasticSearchUtil.bulkIndex(INDEX, QC_METADATA_INFO, resourceBucket.getQcMetadataRecords().getRecords());
    }

}

