package com.yihu.ehr.basic.portal.service;


import com.yihu.ehr.basic.portal.dao.SurveyTemplateQuestionsDao;
import com.yihu.ehr.basic.portal.model.SurveyTemplateQuestions;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 问卷调查模板题目表
 * Created by zhangdan on 2018/4/17.
 */
@Service
public class SurveyTemplateQuestionService extends BaseJpaService<SurveyTemplateQuestions,Long> {
    @Autowired
    SurveyTemplateQuestionsDao surveyTemplateQuestionsDao;

    /**
     * 根据模板code删除数据
     * @param templateCode
     */
    public void deleteByTemplateCode(String templateCode,Boolean del){
        if(del){
            surveyTemplateQuestionsDao.deleteByTemplateCode(templateCode);
        }else{
            surveyTemplateQuestionsDao.updateDelByTemplateCode(templateCode);
        }

    }

    /**
     * 根据模板CODE获取数据
     * @param templateCode
     * @return
     */
    public List<SurveyTemplateQuestions> findByTemplateCode(String templateCode){
        return surveyTemplateQuestionsDao.findByTemplateCodeAndDel(templateCode,"1");
    }

    public void save(List<SurveyTemplateQuestions> surveyTemplateQuestions){
        surveyTemplateQuestionsDao.save(surveyTemplateQuestions);
    }

    public SurveyTemplateQuestions save(SurveyTemplateQuestions surveyTemplateQuestions){
       return surveyTemplateQuestionsDao.save(surveyTemplateQuestions);
    }

    public SurveyTemplateQuestions findByCode(String code){
        return  surveyTemplateQuestionsDao.findByCode(code);
    }
}
