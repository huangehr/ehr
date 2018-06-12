package com.yihu.ehr.analyze.dao;

import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 平台接收预警值
 * @author yeshijie on 2018/5/28.
 */
public interface DqPaltformReceiveWarningDao extends PagingAndSortingRepository<DqPaltformReceiveWarning, Long> {

    DqPaltformReceiveWarning findByOrgCode(String orgCode);

    List<DqPaltformReceiveWarning> findAll();
}
