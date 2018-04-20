package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyLabelInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyLabelInfoDao extends PagingAndSortingRepository<SurveyLabelInfo,Long>,JpaSpecificationExecutor<SurveyLabelInfo> {
    /**
     * 根据类型查找标签列表
     * @param useType 0模板 1问卷
     * @return
     */
    List<SurveyLabelInfo> findByUseType(Integer useType);

    /**
     *
     * @param useType 0模板 1问卷
     * @param relationCode 关联编码
     * @return
     */
    List<SurveyLabelInfo> findByUseTypeAndRelationCode(Integer useType, String relationCode);

    /**
     *
     * @param useType 0模板 1问卷
     * @param relationCode 关联编码
     * @return
     */
    void deleteByUseTypeAndRelationCode(Integer useType, String relationCode);
}
