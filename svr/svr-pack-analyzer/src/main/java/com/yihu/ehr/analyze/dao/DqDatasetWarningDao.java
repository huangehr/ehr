package com.yihu.ehr.analyze.dao;

import com.yihu.ehr.entity.quality.DqDatasetWarning;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 数据质量-数据集预警值
 * @author yeshijie on 2018/5/28.
 */
public interface DqDatasetWarningDao extends PagingAndSortingRepository<DqDatasetWarning, Long> {


}
