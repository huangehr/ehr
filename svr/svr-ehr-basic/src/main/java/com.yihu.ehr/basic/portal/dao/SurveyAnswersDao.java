package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyAnswers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyAnswersDao extends PagingAndSortingRepository<SurveyAnswers, Long> {
    @Query("select  t.content from SurveyAnswers t where t.content is not null and t.surveyCode=?1 and t.questionCode=?2 ")
    Page<String> findByRelationCode(String id, String questionId, Pageable request);

    @Query("select  count(t.surveyCode) from SurveyAnswers t where t.content is not null and t.surveyCode=?1 and t.questionCode=?2 ")
    int findByRelationCode(String id, String questionId);

    @Query("select  count(distinct t.patient) from SurveyAnswers t where t.content is not null and t.surveyCode=?1 and t.questionCode=?2 ")
    int countByRelationCode(String surveyCode, String questionId);
}
