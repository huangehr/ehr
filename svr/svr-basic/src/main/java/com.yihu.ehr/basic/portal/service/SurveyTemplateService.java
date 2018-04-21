package com.yihu.ehr.basic.portal.service;

import com.yihu.ehr.basic.dict.dao.SystemDictEntryRepository;
import com.yihu.ehr.basic.dict.dao.SystemDictRepository;
import com.yihu.ehr.basic.portal.dao.SurveyTemplateDao;
import com.yihu.ehr.basic.portal.model.SurveyLabelInfo;
import com.yihu.ehr.basic.portal.model.SurveyTemplate;
import com.yihu.ehr.basic.portal.model.SurveyTemplateOptions;
import com.yihu.ehr.basic.portal.model.SurveyTemplateQuestions;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.basic.user.service.UserService;
import com.yihu.ehr.basic.util.ClazzReflect;
import com.yihu.ehr.entity.dict.SystemDict;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.model.portal.MSurveyTemplate;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhangdan on 2018/4/17.
 */
@Service
public class SurveyTemplateService extends BaseJpaService<SurveyTemplate,Long> {

    private static final Integer USE_BY_TEMPLATE = 0;

    private static final String  SURVEY_LABEL="WJLX";

    @Autowired
    SurveyLabelInfoService surveyLabelInfoService;
    @Autowired
    SurveyTemplateQuestionService surveyTemplateQuestionService;

    @Autowired
    SurveyTemplateOptionService surveyTemplateOptionService;

    @Autowired
    SystemDictRepository systemDictRepository;

    @Autowired
    SystemDictEntryRepository systemDictEntryRepository;

    @Autowired
    SurveyTemplateDao surveyTemplateDao;

    @Autowired
    UserService userService;

    @Transactional
    public void saveOrUpdate(String jsonData,String loginUserId){
        JSONArray array = new JSONArray(jsonData);

        List<SurveyTemplate> surveyTemplates = new ArrayList<>();
        List<SurveyTemplateQuestions> surveyTemplateQuestions = new ArrayList<>();
        List<SurveyTemplateOptions> surveyTemplateOptions = new ArrayList<>();
        List<SurveyLabelInfo> surveyLabelInfos = new ArrayList<>();

        for(Object object:array){
            JSONObject jsonObject = (JSONObject)object;
            ClazzReflect clazzReflect = new ClazzReflect();
            SurveyTemplate surveyTemplate = new SurveyTemplate();
            if(!jsonObject.isNull("id")&& StringUtils.isNotBlank(jsonObject.getString("id"))){
                surveyTemplate = this.findById(jsonObject.getLong("id"));
            }else{
                surveyTemplate.setCode(UUID.randomUUID().toString().replaceAll("-", ""));
                surveyTemplate.setCreater(loginUserId+"");
                surveyTemplate.setCreateTime(new Date());
                surveyTemplate.setDel("1");
            }
            surveyTemplate.setUpdateTime(new Date());
            clazzReflect.formatToClazz(surveyTemplate,jsonObject);
            surveyTemplates.add(surveyTemplate);

            surveyTemplateQuestionService.deleteByTemplateCode(surveyTemplate.getCode(),true);
            surveyTemplateOptionService.deleteByTemplateCode(surveyTemplate.getCode(),true);
            surveyLabelInfoService.deleteByUseTypeAndRelationCode(USE_BY_TEMPLATE,surveyTemplate.getCode());
            //问题遍历
            if(!jsonObject.isNull("questions")){
                JSONArray questionsArray = (JSONArray)jsonObject.get("questions");
                for(Object questionObj:questionsArray){
                    SurveyTemplateQuestions surveyTemplateQuestion = new SurveyTemplateQuestions();
                    JSONObject questionJson = (JSONObject)questionObj;
                    clazzReflect.formatToClazz(surveyTemplateQuestion,questionJson);
                    surveyTemplateQuestion.setCreateTime(new Date());
                    surveyTemplateQuestion.setUpdateTime(new Date());
                    surveyTemplateQuestion.setDel("1");
                    surveyTemplateQuestion.setTemplateCode(surveyTemplate.getCode());
                    surveyTemplateQuestions.add(surveyTemplateQuestion);
                    //遍历选项
                    if(!questionJson.isNull("options")){
                        JSONArray optionsArray = (JSONArray)questionJson.get("options");
                        for(Object optionsObj:optionsArray){
                            JSONObject optionsJson = (JSONObject)optionsObj;
                            SurveyTemplateOptions surveyTemplateOption = new SurveyTemplateOptions();
                            clazzReflect.formatToClazz(surveyTemplateOption,optionsJson);
                            surveyTemplateOption.setTemplateCode(surveyTemplate.getCode());
                            surveyTemplateOption.setQuestionCode(surveyTemplateQuestion.getCode());
                            surveyTemplateOption.setDel("1");
                            surveyTemplateOptions.add(surveyTemplateOption);
                        }
                    }
                }
            }
            //遍历labels
            if(!jsonObject.isNull("labels")){
                JSONArray labelsArray = (JSONArray)jsonObject.get("labels");
                for(Object labelObj:labelsArray){
                    JSONObject labelJson = (JSONObject)labelObj;
                    SurveyLabelInfo surveyLabelInfo = new SurveyLabelInfo();
                    clazzReflect.formatToClazz(surveyLabelInfo,labelJson);
                    surveyLabelInfo.setRelationCode(surveyTemplate.getCode());
                    surveyLabelInfo.setCode(UUID.randomUUID().toString().replaceAll("-", ""));
                    surveyLabelInfos.add(surveyLabelInfo);
                }
            }
        }
        surveyTemplateDao.save(surveyTemplates);
        surveyTemplateQuestionService.save(surveyTemplateQuestions);
        surveyTemplateOptionService.save(surveyTemplateOptions);
        surveyLabelInfoService.save(surveyLabelInfos);
    }

    public SurveyTemplate findById(Long id){
         return  surveyTemplateDao.findOne(id);
    }


    public SurveyTemplate findByTitle(String title){
        return  surveyTemplateDao.findByTitle(title);
    }


    public Map<String,Object> queryList(int page, int pageSize, String title, Integer labelCode, String filters) {
        Map<String,Object> map = new HashedMap();
        if (page <= 0) {
            page = 1;
        }
        if (pageSize <= 0) {
            pageSize = 15;
        }

        Query queryCount = currentSession().createSQLQuery("select count(1) from portal_survey_templates t where 1=1 " +filters.toString());
        Query queryList = currentSession().createSQLQuery("select t.* from portal_survey_templates t where 1=1  " +filters.toString()).addEntity(SurveyTemplate.class);
        PageRequest pageRequest = new PageRequest(page - 1, pageSize);
        if (!StringUtils.isEmpty(title)) {
            queryCount.setString("title","%"+title+"%");
            queryList.setString("title", "%"+title+"%");
        }
        if (labelCode!=null&&labelCode!=0) {
            queryCount.setInteger("labelCode",labelCode);
            queryList.setInteger("labelCode", labelCode);
        }
        int count = Integer.parseInt(queryCount.list().get(0).toString());
        map.put("count",count);
        queryList.setFirstResult(pageSize * (page - 1));
        queryList.setMaxResults(pageSize);
        List<SurveyTemplate> list = queryList.list();
        List<MSurveyTemplate> array = new ArrayList<>();

        for(SurveyTemplate surveyTemplate:list){
            MSurveyTemplate mSurveyTemplate = new MSurveyTemplate();
            BeanUtils.copyProperties(surveyTemplate,mSurveyTemplate);

            List<SurveyLabelInfo> surveyLabelInfos =  surveyLabelInfoService.findByUseTypeAndRelationCode(USE_BY_TEMPLATE,surveyTemplate.getCode());
            List<Integer> label = new ArrayList<>();
            List<String> labelName = new ArrayList<>();
            User user = userService.getUser(surveyTemplate.getCreater());
            mSurveyTemplate.setCreateName(user==null?"":user.getRealName());
            SystemDict systemDict = systemDictRepository.findByPhoneticCode(SURVEY_LABEL);
            for(SurveyLabelInfo surveyLabelInfo:surveyLabelInfos){
                List<SystemDictEntry> systemDicts = systemDictEntryRepository.findByDictIdAndCode(systemDict.getId(),String.valueOf(surveyLabelInfo.getLabel()));
                label.add(surveyLabelInfo.getLabel());
                if(systemDicts!=null&&systemDicts.size()==1){
                    labelName.add(systemDicts.get(0).getValue());
                }else{
                    labelName.add("格式化字典异常");
                }
            }
            mSurveyTemplate.setLabel( StringUtils.join(label.toArray(),","));
            mSurveyTemplate.setLabelName(StringUtils.join(labelName.toArray(),","));
            array.add(mSurveyTemplate);
        }
        map.put("data",array);
        return map;
    }

    /**
     * 获取模板信息
     * @param id 模板id
     * @param loadQuestion 是否加载问题 是1 否0
     * @return
     */
    public Map<String,Object> getTemplate(Long id,Long loadQuestion){
        SurveyTemplate surveyTemplate = this.surveyTemplateDao.findOne(id);
        if (surveyTemplate==null){ throw new  RuntimeException("模板不存在！");}
        Map<String,Object> map = new HashedMap();
        map.put("surveyTemplate",surveyTemplate);
        List<Map<String,Object>> questionsList = new ArrayList<>();
        if(loadQuestion==1L) {
            List<SurveyTemplateQuestions> surveyTemplateQuestions = this.surveyTemplateQuestionService.findByTemplateCode(surveyTemplate.getCode());
            for (SurveyTemplateQuestions question : surveyTemplateQuestions) {
                Map<String, Object> questionMap = new HashMap<>();
                List<SurveyTemplateOptions> surveyTemplateOptions = this.surveyTemplateOptionService.findByTemplateCodeAndDelAndQuestionCode(surveyTemplate.getCode(), question.getCode());
                questionMap.put("question", question);
                questionMap.put("optipon", surveyTemplateOptions);
                questionsList.add(questionMap);
            }
        }
        map.put("questions",questionsList);
            /*JSONArray questions = new JSONArray(surveyTemplateQuestions);
            for(Object question : questions){
                JSONObject questionObj = (JSONObject)question;
                List<SurveyTemplateOptions> surveyTemplateOptions = this.surveyTemplateOptionService.findByTemplateCodeAndDelAndQuestionCode(surveyTemplate.getCode(),questionObj.getString("code"));
                JSONArray options = new JSONArray(surveyTemplateOptions);
                questionObj.put("options",options);
            }
            surveyTemplateJson.put("questions",questions);
            map.put("questions",questions);
        }*/
            List<SurveyLabelInfo> surveyLabelInfos = surveyLabelInfoService.findByUseTypeAndRelationCode(USE_BY_TEMPLATE, surveyTemplate.getCode());
/*        JSONArray labels = new JSONArray(surveyLabelInfos);
        surveyTemplateJson.put("labels",labels)*/
            ;
            map.put("labels", surveyLabelInfos);
            return map;

    }

    @Transactional
    public void saveLabel(Long templateId,String labelJson){
        SurveyTemplate surveyTemplate = this.surveyTemplateDao.findOne(templateId);
        if(surveyTemplate==null)throw new RuntimeException("模板不存在！");
        surveyLabelInfoService.deleteByUseTypeAndRelationCode(USE_BY_TEMPLATE,surveyTemplate.getCode());
        JSONArray labelsArray = new JSONArray(labelJson);
        ClazzReflect clazzReflect = new ClazzReflect();
        for(Object labelObj:labelsArray){
            JSONObject label = (JSONObject)labelObj;
            SurveyLabelInfo surveyLabelInfo = new SurveyLabelInfo();
            clazzReflect.formatToClazz(surveyLabelInfo,label);
            surveyLabelInfo.setRelationCode(surveyTemplate.getCode());
            surveyLabelInfo.setCode(UUID.randomUUID().toString().replaceAll("-", ""));
            surveyLabelInfoService.save(surveyLabelInfo);
        }
    }

    @Transactional
    public void deleteTemplate(Long templateId){
        SurveyTemplate surveyTemplate = this.surveyTemplateDao.findOne(templateId);
        if(surveyTemplate==null)throw new RuntimeException("模板不存在！");
        this.surveyTemplateQuestionService.deleteByTemplateCode(surveyTemplate.getCode(),false);
        this.surveyTemplateOptionService.deleteByTemplateCode(surveyTemplate.getCode(),false);
        this.surveyLabelInfoService.deleteByUseTypeAndRelationCode(USE_BY_TEMPLATE,surveyTemplate.getCode());
        this.surveyTemplateDao.deleteById(templateId);
    }


}
