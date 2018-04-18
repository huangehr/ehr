package com.yihu.ehr.basic.portal.dao;

import com.yihu.ehr.basic.portal.model.SurveyTemplateQuestions;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zhangdan on 2018/4/17.
 */
public interface SurveyTemplateQuestionsDao extends PagingAndSortingRepository<SurveyTemplateQuestions,Long>,JpaSpecificationExecutor<SurveyTemplateQuestions> {

    @Modifying
    @Query("update SurveyTemplateQuestions t set t.del = 0 where t.templateCode = :templateCode and t.del = 1")
    void updateDelByTemplateCode(@Param("templateCode") String templateCode);

    @Modifying
    void deleteByTemplateCode(String templateCode);

    List<SurveyTemplateQuestions> findByTemplateCodeAndDel(String templateCode, String del);

    SurveyTemplateQuestions findByCode(String code);

    @Query("select  t from SurveyTemplateQuestions t where t.templateCode = ?1 and t.del = 1 ")
    List<SurveyTemplateQuestions> findById(String tempCode);
}
