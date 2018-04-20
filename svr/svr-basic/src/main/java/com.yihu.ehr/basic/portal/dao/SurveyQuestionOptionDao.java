package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyQuestionOption;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyQuestionOptionDao extends PagingAndSortingRepository<SurveyQuestionOption,Long>,JpaSpecificationExecutor<SurveyQuestionOption> {

    @Query("select a from SurveyQuestionOption a where a.questionCode=?1 and a.del='1' order by a.sort ")
    List<SurveyQuestionOption> findByQuestionCode(String questionCode);

    @Query("update SurveyQuestionOption set del=0 where questionCode =?1")
    @Modifying
    void deleteByQuestionCode(String questionCode);

}
