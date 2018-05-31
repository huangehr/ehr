package com.yihu.ehr.analyze.dao;

import com.yihu.ehr.entity.quality.DqPaltformUploadWarning;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 数据质量-平台上传预警值
 * @author yeshijie on 2018/5/28.
 */
public interface DqPaltformUploadWarningDao extends PagingAndSortingRepository<DqPaltformUploadWarning, Long> {

    DqPaltformUploadWarning findByOrgCode(String orgCode);
}
