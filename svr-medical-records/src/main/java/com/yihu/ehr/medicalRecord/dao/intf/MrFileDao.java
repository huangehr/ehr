package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrDoctorsEntity;
import com.yihu.ehr.medicalRecord.model.MrFileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shine on 2016/7/14.
 */
public interface MrFileDao extends CrudRepository<MrFileEntity,String> {

    MrFileEntity findByid(int id);

    MrFileEntity findByfilePath(String path);
}
