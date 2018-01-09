package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.Members;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XMembersRepository extends PagingAndSortingRepository<Members, String> {

    void deleteByFamilyId(String familyId);

    void deleteByFamilyIdAndIdCardNo(String familyId, String idCardNo);

    List<Members> findByFamilyId(String familyId);

    Members findByFamilyIdAndIdCardNo(String familyId, String idCardNo);

}
