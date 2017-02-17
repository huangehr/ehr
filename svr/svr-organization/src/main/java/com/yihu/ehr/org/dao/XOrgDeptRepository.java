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

    @Query("select dept from OrgDept dept where dept.parentDeptId = :parentDeptId order by dept.sortNo desc ")
    List<OrgDept> searchByParentDeptId(@Param("parentDeptId") Integer parentDeptId);

    @Query("select dept from OrgDept dept where dept.orgId = :orgId order by dept.sortNo desc ")
    List<OrgDept> searchByOrgId(@Param("orgId") String orgId);

}
