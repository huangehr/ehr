package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrLabelEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/27.
 */
public interface DoctorLabelDao extends PagingAndSortingRepository<MrLabelEntity,Integer> {

    MrLabelEntity findByid(int id);

    void deleteById(int id);

    List<MrLabelEntity> findByDoctorId(String doctorId);
}
