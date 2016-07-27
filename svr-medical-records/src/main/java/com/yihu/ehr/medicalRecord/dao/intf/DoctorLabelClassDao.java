package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrLabelClassEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/27.
 */
public interface DoctorLabelClassDao extends PagingAndSortingRepository<MrLabelClassEntity,Integer> {

    MrLabelClassEntity findByid(int id);

    void deleteById(int id);

    List<MrLabelClassEntity> findByDoctoId(String doctorId);

}
