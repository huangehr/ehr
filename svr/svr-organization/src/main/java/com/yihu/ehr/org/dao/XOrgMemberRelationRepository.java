package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgMemberRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/16.
 */
public interface XOrgMemberRelationRepository extends PagingAndSortingRepository<OrgMemberRelation,Integer> {

    @Query("select relation from OrgMemberRelation relation where relation.deptId = :deptId ")
    List<OrgMemberRelation> searchByDeptId(@Param("deptId") Integer deptId);

    @Query("select count(relation.id) from OrgMemberRelation relation where relation.deptId= :deptId")
    public Integer countByDeptId(@Param("deptId") Integer deptId);

}
