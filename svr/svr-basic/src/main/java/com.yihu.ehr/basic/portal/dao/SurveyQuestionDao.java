package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyQuestionDao extends PagingAndSortingRepository<SurveyQuestion,Long>,JpaSpecificationExecutor<SurveyQuestion> {


}
