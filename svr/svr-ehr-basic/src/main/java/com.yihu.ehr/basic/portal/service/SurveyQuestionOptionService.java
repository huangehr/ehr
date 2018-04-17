package com.yihu.ehr.basic.portal.service;


import com.yihu.ehr.basic.portal.dao.SurveyQuestionOptionDao;
import com.yihu.ehr.basic.portal.model.SurveyQuestionOption;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 问卷调查题库选项表
 * Created by zhangdan on 2018/4/17.
 */
@Service
public class SurveyQuestionOptionService extends BaseJpaService<SurveyQuestionOption,Long> {
    @Autowired
    SurveyQuestionOptionDao surveyQuestionOptionDao;

    public void save(List<SurveyQuestionOption> surveyQuestionOptions){
        surveyQuestionOptionDao.save(surveyQuestionOptions);
    }

    public SurveyQuestionOption save(SurveyQuestionOption surveyQuestionOption){
         return  surveyQuestionOptionDao.save(surveyQuestionOption);
    }

    public void deleteByQuestionCode(String questionCode){
          surveyQuestionOptionDao.deleteByQuestionCode(questionCode);
    }
}
