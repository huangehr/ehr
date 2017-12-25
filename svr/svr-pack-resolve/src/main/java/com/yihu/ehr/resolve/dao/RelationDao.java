package com.yihu.ehr.resolve.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.resolve.feign.ArchiveClient;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author hzp
 * @created 20170519
 */
@Repository
public class RelationDao {

    @Autowired
    private ArchiveClient archiveClient;

    @Autowired
    private ObjectMapper objectMapper;

    public void save(ResourceBucket resourceBucket) throws Exception {
        if(!resourceBucket.isReUploadFlg()) {
            ArchiveRelation relation = new ArchiveRelation();
            relation.setName(resourceBucket.getPatientName());
            relation.setOrgCode(resourceBucket.getOrgCode());
            relation.setOrgName(resourceBucket.getOrgName());
            relation.setCardType(resourceBucket.getCardType());
            relation.setCardNo(resourceBucket.getCardId());
            relation.setEventNo(resourceBucket.getEventNo());
            relation.setEventDate(resourceBucket.getEventDate());
            relation.setEventType(StringUtils.isEmpty(resourceBucket.getEventType()) ? "" : String.valueOf(resourceBucket.getEventType().getType()));
            relation.setProfileId(resourceBucket.getId());
            relation.setCreateDate(new Date());
            String idCardNo = resourceBucket.getDemographicId();
            if (!StringUtils.isEmpty(idCardNo)) {
                relation.setIdCardNo(idCardNo);
                relation.setStatus("1");
                relation.setRelationDate(new Date());
            } else {
                relation.setStatus("0");
            }
            archiveClient.archiveRelation(objectMapper.writeValueAsString(relation));
        }
    }
}
