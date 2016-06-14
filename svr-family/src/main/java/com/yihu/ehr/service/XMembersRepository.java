package com.yihu.ehr.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XMembersRepository extends PagingAndSortingRepository<Members, String> {

    void deleteByFamilyId(String familyId);

    void deleteByFamilyIdAndIdCardNo(String familyId,String idCardNo);

    List<Members> findByFamilyId(String familyId);

    Members findByFamilyIdAndIdCardNo(String familyId,String idCardNo);

}
