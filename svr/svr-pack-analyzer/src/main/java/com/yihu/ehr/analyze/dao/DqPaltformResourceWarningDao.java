package com.yihu.ehr.analyze.dao;

import com.yihu.ehr.entity.government.GovernmentBrowseLog;
import com.yihu.ehr.entity.quality.DqPaltformResourceWarning;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 数据质量-平台资源化预警值
 * @author yeshijie on 2018/5/28.
 */
public interface DqPaltformResourceWarningDao extends PagingAndSortingRepository<DqPaltformResourceWarning, Long> {

    DqPaltformResourceWarning findByOrgCode(String orgCode);

    List<DqPaltformResourceWarning> findAll();

    @Query(value = "select a.* from dq_paltform_resource_warning a order by a.create_time desc limit 1", nativeQuery = true)
    DqPaltformResourceWarning findByFirst();
}
