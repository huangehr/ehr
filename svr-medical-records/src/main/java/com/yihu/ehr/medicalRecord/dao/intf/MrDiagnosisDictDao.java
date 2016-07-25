package com.yihu.ehr.medicalRecord.dao.intf;


import com.yihu.ehr.medicalRecord.model.MrDiagnosisDictEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MrDiagnosisDictDao extends PagingAndSortingRepository<MrDiagnosisDictEntity,String> {

    MrDiagnosisDictEntity findBycode(String code);

    @Query(value = "select * from mr_diagnosis_dict where PHONETIC_CODE like ?1", nativeQuery = true)
    List<MrDiagnosisDictEntity> findByPinyin(String pinyin);

    void deleteBycode(String code);
}
