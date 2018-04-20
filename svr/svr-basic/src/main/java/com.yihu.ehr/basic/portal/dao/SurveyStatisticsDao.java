package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyStatistics;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyStatisticsDao extends PagingAndSortingRepository<SurveyStatistics, Long> {
    @Modifying
    @Query("update SurveyStatistics t set t.amount = amount+1 where t.surveyCode = ?1 and  t.questionCode = ?2 and t.optionsCode = ?3 ")
    void modifyAmount(String surveyCode, String qstCode, String optCode);

    @Modifying
    @Query("update SurveyStatistics t set t.amount = amount+1 where t.surveyCode = ?1 and  t.questionCode = ?2 ")
    void modifyAmount(String surveyCode, String qstCode);

    @Query("select distinct t.amount from SurveyStatistics t where t.surveyCode = ?1 and  t.questionCode = ?2 ")
    int[] findByIdAndQstId(String surveyCode, String qstCode);

    @Query("select t.amount from SurveyStatistics t where t.surveyCode = ?1 and  t.questionCode = ?2 and t.optionsCode = ?3 ")
    int findByAllId(String surveyCode, String qstCode, String optCode);
}
