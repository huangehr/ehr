package com.yihu.ehr.medicalRecord.dao;

import com.yihu.ehr.medicalRecord.model.Entity.MrLabelClassEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/27.
 */
public interface DoctorLabelClassDao extends PagingAndSortingRepository<MrLabelClassEntity,Integer> {

    MrLabelClassEntity findByid(int id);

    void deleteById(int id);

    List<MrLabelClassEntity> findByDoctorId(String doctorId);

}
