package com.yihu.ehr.service.resource.stage2.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.feign.ArchiveClient;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author hzp
 * @created 20170519
 */
@Service
public class RelationDao {

    @Autowired
    private ArchiveClient archiveClient;

    @Autowired
    private ObjectMapper objectMapper;

    public void save(ResourceBucket resourceBucket) {
        try {
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
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.print("Create relation fail!"+ex.getMessage());
        }
    }

}
