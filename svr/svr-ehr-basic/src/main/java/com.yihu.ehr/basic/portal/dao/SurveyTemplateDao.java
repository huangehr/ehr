package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyTemplate;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyTemplateDao extends PagingAndSortingRepository<SurveyTemplate,Long>,JpaSpecificationExecutor<SurveyTemplate> {
        @Query("from SurveyTemplate where title=?1 and del = 1")
        SurveyTemplate findByTitle(String title);
        @Query("update SurveyTemplate set del = 0 where id=?1 and del = 1")
        @Modifying
        void deleteById(Long id);
}
