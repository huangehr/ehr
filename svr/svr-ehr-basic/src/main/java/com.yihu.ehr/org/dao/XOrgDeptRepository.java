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

    @Query("select dept from OrgDept dept where dept.orgId = :orgId and dept.delFlag=0 order by dept.sortNo desc ")
    List<OrgDept> searchByOrgId(@Param("orgId") String orgId);

    @Query("select dept from OrgDept dept where dept.orgId = :orgId and dept.name=:name order by dept.sortNo desc ")
    List<OrgDept> searchByOrgIdAndName(@Param("orgId") String orgId,@Param("name") String name);

    @Query("select dept from OrgDept dept where dept.orgId=?1 and dept.parentDeptId = ?2 and dept.delFlag = 0 order by dept.sortNo desc ")
    List<OrgDept> findByOrgIdAndParentDeptId(String orgId,Integer parentDeptId);

    @Query("select case when  max(dept.sortNo) is null then 1 else max(dept.sortNo) end from OrgDept dept where dept.parentDeptId = :parentDeptId")
    int searchParentIdOfMaxSortNo(@Param("parentDeptId") Integer parentDeptId);

    @Query("select dept from OrgDept dept where dept.orgId in (:orgId) and dept.delFlag=0 order by dept.sortNo desc ")
    List<OrgDept> searchByOrgIds(@Param("orgId") String[] orgId);
}
