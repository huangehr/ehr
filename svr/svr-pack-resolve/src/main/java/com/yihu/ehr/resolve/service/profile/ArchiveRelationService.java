package com.yihu.ehr.resolve.service.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.family.ResourceFamily;
import com.yihu.ehr.resolve.dao.ArchiveRelationDao;
import com.yihu.ehr.resolve.feign.ArchiveRelationClient;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;


/**
 * 档案关联
 * Created by hzp on 2017/4/11.
*/
@Service
public class ArchiveRelationService {

    @Autowired
    private HBaseDao hbaseDao;
    @Autowired
    private ArchiveRelationDao archiveRelationDao;
    @Autowired
    private ArchiveRelationClient archiveRelationClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 档案关联
     */
    public void archiveRelation(String profileId, String idCardNo) throws Exception {
        //判断记录是否存在
        String re = hbaseDao.get(ResourceCore.MasterTable,profileId);

        if (!StringUtils.isEmpty(re)) {
            hbaseDao.put(ResourceCore.MasterTable, profileId, ResourceFamily.Basic,MasterResourceFamily.BasicColumns.DemographicId, idCardNo);
        } else {
            throw new Exception("不存在改条记录");
        }
    }

    public void relation(ResourceBucket resourceBucket) throws Exception {
        if (!resourceBucket.isReUploadFlg()) {
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
            relation.setIdCardNo(idCardNo);
            relation.setRelationDate(new Date());
            if (idCardNo.indexOf("-") == -1) {
                relation.setStatus("1");
            } else {
                relation.setStatus("0");
            }
            archiveRelationClient.archiveRelation(objectMapper.writeValueAsString(relation));
        }
    }

    public List<String> findByCardNo(String cardNo){
        return archiveRelationDao.findByCardNo(cardNo);
    }

}
