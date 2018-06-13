package com.yihu.ehr.analyze.dao;

import com.yihu.ehr.entity.quality.DqPaltformResourceWarning;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 数据质量-平台资源化预警值
 * @author yeshijie on 2018/5/28.
 */
public interface DqPaltformResourceWarningDao extends PagingAndSortingRepository<DqPaltformResourceWarning, Long> {

    DqPaltformResourceWarning findByOrgCode(String orgCode);

    List<DqPaltformResourceWarning> findAll();
}
