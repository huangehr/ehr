package com.yihu.ehr.medicalRecord.dao.intf;


import com.yihu.ehr.medicalRecord.model.MrDiagnosisDictEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/14.
 */
public interface MrDiagnosisDictDao extends PagingAndSortingRepository<MrDiagnosisDictEntity,String> {

    MrDiagnosisDictEntity findBycode(String code);

    void deleteBycode(String code);
}
