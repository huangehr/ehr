package com.yihu.ehr.medicalRecord.dao.intf;

import com.yihu.ehr.medicalRecord.model.MrFileRelationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
public interface MrFileRelationDao extends CrudRepository<MrFileRelationEntity,String> {

    List<MrFileRelationEntity> findByownerId(String reportId);

    List<MrFileRelationEntity> findByownerIdIn(List reportIds);

    List<MrFileRelationEntity> findByfileId(String fileId);
}
