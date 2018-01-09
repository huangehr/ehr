package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.entity.patient.Demographic;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by progr1mmer on 2018/1/6.
 */
public interface PatientDao extends PagingAndSortingRepository<Demographic, String> {
}
