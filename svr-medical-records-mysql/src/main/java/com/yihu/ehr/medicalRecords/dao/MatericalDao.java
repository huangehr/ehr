package com.yihu.ehr.medicalRecords.dao;

import com.yihu.ehr.medicalRecords.model.Entity.MrTextEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hzp on 2016/7/14.
 */
public interface MatericalDao extends PagingAndSortingRepository<MrTextEntity,Integer> {

    void deleteById(String reportId);

    List<MrTextEntity> findByCreaterAndBusinessClassAndPatientId(String CreatorId,String BusinessClass, String PatientId,Pageable pageable);
    List<MrTextEntity> findByCreaterAndBusinessClass(String CreatorId,String BusinessClass,Pageable pageable);
    //List<MrTextEntity> findByCreaterAndBusinessClassAndContent(String CreatorId,String BusinessClass,String content);
    List<MrTextEntity> findByCreaterAndPatientId(String CreatorId,String PatientId,Pageable pageable);

    MrTextEntity findByCreaterAndBusinessClassAndContent(String CreatorId,String BusinessClass,String Content);
}
