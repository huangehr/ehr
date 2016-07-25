package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.medicalRecord.model.PronunciationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/22.
 */
public interface PronunciationDao extends PagingAndSortingRepository<PronunciationEntity,Integer> {

    @Query("select count(*) from PronunciationEntity where pronunciation like ?1%")
    int findBypronunciation(String pinyin);

}
