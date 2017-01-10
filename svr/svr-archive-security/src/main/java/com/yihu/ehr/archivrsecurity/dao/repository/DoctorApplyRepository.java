package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.ScAuthorizeDoctorApply;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/7/12.
 */
public interface DoctorApplyRepository extends PagingAndSortingRepository<ScAuthorizeDoctorApply,String> {

}
