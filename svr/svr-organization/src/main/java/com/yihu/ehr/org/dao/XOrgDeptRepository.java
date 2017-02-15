package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
public interface XOrgDeptRepository extends PagingAndSortingRepository<OrgDept, Integer> {

    @Query("select org from OrgDept org where org.parentDeptId = :parentDeptId ")
    List<Organization> searchByParentDeptId(@Param("parentDeptId") Integer parentDeptId);

}
