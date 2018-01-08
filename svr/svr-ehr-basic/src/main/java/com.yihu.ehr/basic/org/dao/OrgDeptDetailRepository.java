package com.yihu.ehr.basic.org.dao;

import com.yihu.ehr.basic.org.model.OrgDeptDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/24.
 */
public interface OrgDeptDetailRepository extends PagingAndSortingRepository<OrgDeptDetail,Integer> {

    @Query("select deptDetail from OrgDeptDetail deptDetail where deptDetail.deptId = :deptId order by deptDetail.insertTime desc ")
    List<OrgDeptDetail> searchByDeptId(@Param("deptId") Integer deptId);

}
