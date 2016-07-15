package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrSystemDictEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/14.
 */
public interface MrSystemDictDao extends PagingAndSortingRepository<MrSystemDictEntity,String> {

    MrSystemDictEntity findBydictCode(String dictCode);

    void deleteBydictCode(String dictCode);
}
