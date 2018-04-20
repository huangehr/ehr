package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyOptionAnswers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyOptionAnswersDao extends PagingAndSortingRepository<SurveyOptionAnswers, Long> {
    @Query("select  t.optionComment from SurveyOptionAnswers t where t.optionComment is not null and t.surveyCode =?1 and t.questionCode=?2 and t.optionsCode=?3 ")
    Page<String> findByRelationCode(String id, String questionId, String optionId, Pageable request);

    @Query("select  count(t.userId) from SurveyOptionAnswers t where t.optionComment is not null and t.surveyCode =?1 and t.questionCode=?2 and t.optionsCode=?3 ")
    int findByRelationCode(String id, String questionId, String optionId);

    @Query("select  count(distinct  t.userId) from SurveyOptionAnswers t where t.surveyCode =?1 and t.questionCode=?2 ")
    int countByRelationCode(String id, String questionId);
}
