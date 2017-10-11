package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgMemberRelation;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("select r from OrgMemberRelation r where r.status=0 and r.orgId =?1")
    List<OrgMemberRelation> findByOrgId(String orgId);


    @Query("select r from OrgMemberRelation r where r.status=0 and r.orgId = :orgId and r.userId=:userId and r.deptId = :deptId")
    List<OrgMemberRelation> searchByOrgIdAndUserId(@Param("orgId") String orgId,@Param("userId") String userId, @Param("deptId") Integer deptId);

    @Query("select r.orgId from OrgMemberRelation r where r.status=0 and r.userId = :userId")
    List<String> findOrgIdByUserId(@Param("userId") String userId);

    @Query("select r.userId from OrgMemberRelation r where r.status=0 and r.orgId = :orgId")
    List<String> findUserIdByOrgId(@Param("orgId") List<String> orgId);

    @Query("select distinct(r.orgId) from OrgMemberRelation r where r.userId = :userId and r.status=0")
    List<String> searchByUserId(@Param("userId") String userId);

    @Query("select r.deptId from OrgMemberRelation r where r.status=0 and r.userId = :userId")
    List<Integer> findDeptIdByUserId(@Param("userId") String userId);

    @Modifying
    @Query("update OrgMemberRelation r set r.status = 1 where r.orgId = :orgId")
    void updateByOrgId(@Param("orgId") String orgId);

    @Query("select r from OrgMemberRelation r where r.status=0 and r.userId=:userId")
    List<OrgMemberRelation> findByUserId(@Param("userId") String userId);
}
