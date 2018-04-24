package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyTemplateOptions;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyTemplateOptionsDao extends PagingAndSortingRepository<SurveyTemplateOptions,Long>,JpaSpecificationExecutor<SurveyTemplateOptions> {

    @Query("update SurveyTemplateOptions set del = 0 where templateCode=?1 and del = 1")
    @Modifying
    void updateDelByTemplateCode(String templateCode);

    void deleteByTemplateCode(String templateCode);

    @Query("select t from  SurveyTemplateOptions t where t.templateCode = ?1 and t.del = ?2 and t.questionCode=?3 ")
    List<SurveyTemplateOptions>  findByTemplateCodeAndDelAndQuestionCode(String templateCode, String del, String questionCode);

    @Query("select t from  SurveyTemplateOptions t where t.templateCode = ?1 and t.del = 1 ")
    List<SurveyTemplateOptions> findByQuestionCode(String qstcode);

}
