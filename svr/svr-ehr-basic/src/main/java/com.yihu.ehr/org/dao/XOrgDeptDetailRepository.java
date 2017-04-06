package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgDeptDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/24.
 */
public interface XOrgDeptDetailRepository extends PagingAndSortingRepository<OrgDeptDetail,Integer> {

    @Query("select deptDetail from OrgDeptDetail deptDetail where deptDetail.deptId = :deptId order by deptDetail.insertTime desc ")
    List<OrgDeptDetail> searchByDeptId(@Param("deptId") Integer deptId);

}
