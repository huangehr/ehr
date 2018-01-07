package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resolve.dao.RelationDao;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;


/**
 * Created by progr1mmer on 2018/1/6.
 */
@Service
@Transactional
public class RelationService extends BaseJpaService<ArchiveRelation, RelationDao> {

    @Autowired
    private RelationDao relationDao;

    public void relation(ResourceBucket resourceBucket) throws Exception {
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
            ArchiveRelation archiveRelation = relationDao.findByProfileId(relation.getProfileId());
            if(archiveRelation != null) {
                relation.setId(archiveRelation.getId());
            }
            relationDao.save(relation);
        }
    }

}
