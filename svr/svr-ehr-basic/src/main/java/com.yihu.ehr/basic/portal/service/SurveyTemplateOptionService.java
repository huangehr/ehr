package com.yihu.ehr.basic.portal.service;


import com.yihu.ehr.basic.portal.dao.SurveyTemplateOptionsDao;
import com.yihu.ehr.basic.portal.model.SurveyTemplateOptions;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 问卷调查模板选项表
 * Created by zhangdan on 2018/4/17.
 */
@Service
public class SurveyTemplateOptionService extends BaseJpaService<SurveyTemplateOptions,Long> {
    @Autowired
    SurveyTemplateOptionsDao surveyTemplateOptionsDao;

    public void deleteByTemplateCode(String templateCode,Boolean del){
        if(del){
             surveyTemplateOptionsDao.deleteByTemplateCode(templateCode);
        }else{
            surveyTemplateOptionsDao.updateDelByTemplateCode(templateCode);
        }
    }

    /**
     * 根据问题code获取选项表有效的集合
     * @param templateCode
     * @return
     */
    public List<SurveyTemplateOptions> findByTemplateCodeAndDelAndQuestionCode(String templateCode, String questionCode){
        return surveyTemplateOptionsDao.findByTemplateCodeAndDelAndQuestionCode(templateCode,"1",questionCode);
    }


    public void save(List<SurveyTemplateOptions> surveyTemplateOptions){
         surveyTemplateOptionsDao.save(surveyTemplateOptions);
    }

    public SurveyTemplateOptions save(SurveyTemplateOptions surveyTemplateOptions){
        return  surveyTemplateOptionsDao.save(surveyTemplateOptions);
    }

}
