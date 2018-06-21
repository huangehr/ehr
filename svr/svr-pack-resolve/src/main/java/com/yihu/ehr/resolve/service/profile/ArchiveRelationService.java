package com.yihu.ehr.resolve.service.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.model.packs.EsArchiveRelation;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.family.ResourceFamily;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 档案关联
 * Created by hzp on 2017/4/11.
 * Modified by Progr1mmer
*/
@Service
public class ArchiveRelationService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String INDEX = "archive_relation";
    private static final String TYPE = "info";

    @Autowired
    private HBaseDao hbaseDao;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    /**
     * 档案关联
     */
    public void archiveRelation(String profileId, String idCardNo) throws Exception {
        //判断记录是否存在
        String re = hbaseDao.get(ResourceCore.MasterTable, profileId);
        if (!StringUtils.isEmpty(re)) {
            hbaseDao.put(ResourceCore.MasterTable, profileId, ResourceFamily.Basic, ResourceCells.DEMOGRAPHIC_ID, idCardNo);
        }
    }

    public void relation (ResourceBucket resourceBucket, OriginalPackage originalPackage) throws Exception {
        if (!originalPackage.isReUploadFlg()) {
            EsArchiveRelation relation = new EsArchiveRelation();
            relation.set_id(resourceBucket.getId());
            ProfileType profileType = originalPackage.getProfileType();
            if (profileType != null){
                relation.setProfile_type(profileType.getType());
            }
            relation.setName(resourceBucket.getBasicRecord(ResourceCells.PATIENT_NAME));
            relation.setOrg_code(resourceBucket.getBasicRecord(ResourceCells.ORG_CODE));
            relation.setOrg_name(resourceBucket.getBasicRecord(ResourceCells.ORG_NAME));
            relation.setId_card_no( resourceBucket.getBasicRecord(ResourceCells.DEMOGRAPHIC_ID));
            int gender = resourceBucket.getMasterRecord().getResourceValue("EHR_000019") == null ||  "".equals(resourceBucket.getMasterRecord().getResourceValue("EHR_000019"))  ? 0 : new Integer(resourceBucket.getMasterRecord().getResourceValue("EHR_000019"));
            relation.setGender(gender);
            String telephone = resourceBucket.getMasterRecord().getResourceValue("EHR_000003") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000003").toString();
            relation.setTelephone(telephone);
            relation.setCard_type(resourceBucket.getBasicRecord(ResourceCells.CARD_TYPE));
            relation.setCard_no(resourceBucket.getBasicRecord(ResourceCells.CARD_ID));
            relation.setEvent_type(resourceBucket.getBasicRecord(ResourceCells.EVENT_TYPE) == null ? -1 : new Integer(resourceBucket.getBasicRecord(ResourceCells.EVENT_TYPE)));
            relation.setEvent_no(resourceBucket.getBasicRecord(ResourceCells.EVENT_NO));
            relation.setEvent_date(DateUtil.strToDate(resourceBucket.getBasicRecord(ResourceCells.EVENT_DATE)));
            char prefix = CHARS.charAt((int)(Math.random() * 26));
            relation.setSn(prefix + "" + new Date().getTime());
            relation.setRelation_date(new Date());
            relation.setCreate_date(new Date());
            //relation.setApply_id(null);
            //relation.setCard_id(null);
            if (originalPackage.isIdentifyFlag()) {
                relation.setIdentify_flag(1);
            } else {
                relation.setIdentify_flag(0);
            }
            elasticSearchUtil.index(INDEX, TYPE, objectMapper.readValue(objectMapper.writeValueAsString(relation), Map.class ));
        }
    }

    public List<String> findIdCardNoByCardNo(String cardNo){
        List<String> result = new ArrayList<>();
        List<Map<String, Object>> data = elasticSearchUtil.findByField(INDEX, TYPE, "card_no", cardNo);
        data.forEach(item -> {
            if (!StringUtils.isEmpty(item.get("id_card_no"))) {
                result.add(String.valueOf(item.get("id_card_no")));
            }
        });
        return result;
    }

}
