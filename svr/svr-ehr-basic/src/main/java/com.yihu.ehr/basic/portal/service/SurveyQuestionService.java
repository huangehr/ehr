package com.yihu.ehr.basic.portal.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.portal.dao.SurveyQuestionDao;
import com.yihu.ehr.basic.portal.dao.SurveyQuestionOptionDao;
import com.yihu.ehr.basic.portal.model.SurveyQuestion;
import com.yihu.ehr.basic.portal.model.SurveyQuestionOption;
import com.yihu.ehr.basic.util.MapUtill;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import java.util.*;

/**
 * 问卷调查题库题目表
 * Created by zhangdan on 2018/4/17.
 */
@Service
public class SurveyQuestionService extends BaseJpaService<SurveyQuestion,Long> {

    @Autowired
    private SurveyQuestionDao surveyQuestionDao;
    @Autowired
    private SurveyQuestionOptionDao surveyQuestionOptionDao;
    @Autowired
    private SurveyQuestionOptionService surveyQuestionOptionService;


    /**
     * 分页查找问题
     * @param page
     * @param rows
     * @param title
     * @param questionType
     * @return
     */
    public Page<SurveyQuestion> findQuestion(int page, int rows, String title, Integer questionType){
        if (page <= 0) {
            page = 1;
        }
        if (rows <= 0) {
            rows = 15;
        }
        // 排序
        Sort sort = new Sort(Direction.DESC, "id");
        // 分页信息
        PageRequest pageRequest = new PageRequest(page - 1, rows, sort);
        // 设置查询条件
        Map<String, SearchFilter> filters = new HashMap<>();
        if (questionType != null) {
            // 查询大医院
            filters.put("questionType", new SearchFilter("questionType", SearchFilter.Operator.EQ, questionType));
        }
        if (StringUtils.isNotEmpty(title)) {
            filters.put("title", new SearchFilter("title", SearchFilter.Operator.LIKE, title));
        }
        // 未删除
        filters.put("del", new SearchFilter("del", SearchFilter.Operator.EQ, "1"));

        Specification<SurveyQuestion> spec = DynamicSpecifications.bySearchFilter(filters.values(), SurveyQuestion.class);
        return surveyQuestionDao.findAll(spec,pageRequest);
    }

    /**
     * 新增问题
     * @param jsonData
     * @throws Exception
     */
    @Transactional
    public void saveOrUpdateQuestion(String jsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, Map.class);
        List<Map<String, Object>> list = objectMapper.readValue(jsonData, javaType);
        if(list!=null&&list.size()>0){
            List<SurveyQuestion> questionList = new ArrayList<>();
            List<SurveyQuestionOption> questionOptionList = new ArrayList<>();
            SurveyQuestion question = null;
            SurveyQuestionOption questionOption = null;
            for(Map<String,Object> map:list){
                //问题
                question = new SurveyQuestion();
                if(map.get("id")!=null){
                    question = this.findById(MapUtill.getMapLong(map,"id"));
                }else{
                    question.setCode(getCode());
                }
                question.setDel("1");
                String title = MapUtill.getMapString(map,"title");//标题
                String comment = MapUtill.getMapString(map,"comment");//问题说明
                Integer questionType = MapUtill.getMapInt(map,"questionType");//问题类型（0单选 1多选 2问答）
                Integer isRequired = MapUtill.getMapInt(map,"isRequired");//是否必答（0否 1是）
                Long minNum = MapUtill.getMapLong(map,"minNum");//最小答案个数（多选有效）
                Long maxNum = MapUtill.getMapLong(map,"maxNum");//最大答案个数（多选有效）
                question.setComment(comment);
                if((map.get("id")==null)){
                    question.setCreateTime(new Date());
                }
                question.setIsRequired(isRequired==null?0:isRequired);
                question.setMaxNum(maxNum);
                question.setMinNum(minNum);
                question.setQuestionType(questionType);
                question.setTitle(title);
                question.setUpdateTime(new Date());
                questionList.add(question);
                surveyQuestionOptionService.deleteByQuestionCode(question.getCode());
                if(questionType!=2){//选项
                    List<Map<String, Object>> optionList = (List<Map<String, Object>>)map.get("optionData");
                    for (Map<String,Object> optionMap:optionList){
                        Integer haveComment = MapUtill.getMapInt(optionMap,"haveComment");//是否有选项说明（0没有 1有）
                        String content = MapUtill.getMapString(optionMap,"content");//选项内容
                        Integer isRequiredOption = MapUtill.getMapInt(optionMap,"isRequired");//选项说明是否必填（0否 1是）
                        Integer sort = MapUtill.getMapInt(optionMap,"sort");//单题内排序
                        questionOption = new SurveyQuestionOption();
                        questionOption.setIsRequired(isRequiredOption);
                        questionOption.setCode(getCode());
                        questionOption.setContent(content);
                        questionOption.setDel("1");
                        questionOption.setHaveComment(haveComment);
                        questionOption.setQuestionCode(question.getCode());
                        questionOption.setSort(sort);
                        questionOptionList.add(questionOption);
                    }
                }
            }
            surveyQuestionDao.save(questionList);
            surveyQuestionOptionDao.save(questionOptionList);
        }
    }



    /**
     * 根据id查找
     * @param id
     * @return
     */
    public SurveyQuestion findById(Long id){
        return surveyQuestionDao.findOne(id);
    }

    public JSONArray findByIds(String ids){
        String hql = "from SurveyQuestion where id in (:ids) order by id desc";
        List<Long> idArray  = new ArrayList<>();
        for(String id : ids.split(","))
            idArray.add(Long.valueOf(id));
        List<SurveyQuestion>  lists =  currentSession().createQuery(hql).setParameterList("ids",idArray).list();
        JSONArray array = new JSONArray(lists);
        for(Object questionObj:array){
            JSONObject questionJson = (JSONObject) questionObj;
            List<SurveyQuestionOption> options = surveyQuestionOptionService.findByField("questionCode",questionJson.get("code"));
            questionJson.put("options",new JSONArray(options));
        }
        return array;
    }

    /**
     * 获取单个问题
     * @param question
     * @return
     * @throws Exception
     */
    public JSONObject getQuestion(SurveyQuestion question) throws Exception{
        JSONObject json = new JSONObject();

        json.put("id",question.getId());
        json.put("code",question.getCode());
        json.put("comment",question.getComment());
        json.put("createTime",question.getCreateTime());
        json.put("isRequired",question.getIsRequired());
        json.put("maxNum",question.getMaxNum());
        json.put("minNum",question.getMinNum());
        json.put("questionType",question.getQuestionType());
        json.put("title",question.getTitle());
        json.put("updateTime",question.getUpdateTime());
        if(question.getQuestionType()!=2){
            //选择题查找选项
            List<SurveyQuestionOption> surveyQuestionOptionList = surveyQuestionOptionDao.findByQuestionCode(question.getCode());
            JSONArray jsonArray = new JSONArray();
            for (SurveyQuestionOption option:surveyQuestionOptionList){
                JSONObject js = new JSONObject();
                js.put("id",option.getId());
                js.put("isRequired",option.getIsRequired());
                js.put("code",option.getCode());
                js.put("content",option.getContent());
                js.put("haveComment",option.getHaveComment());
                js.put("sort",option.getSort());
                jsonArray.put(js);
            }
            json.put("options",jsonArray);
        }else{
            json.put("options","");
        }

        return json;
    }

    /**
     * 单个删除
     * @param id
     */
    @Transactional
    public void delQuestion(Long id) throws Exception{
        SurveyQuestion question = surveyQuestionDao.findOne(id);
        if(question!=null){
            question.setDel("0");
            surveyQuestionDao.save(question);
            if(question.getQuestionType()!=2){
                List<SurveyQuestionOption> surveyQuestionOptionList = surveyQuestionOptionDao.findByQuestionCode(question.getCode());
                if(surveyQuestionOptionList.size()>0){
                    for (SurveyQuestionOption option:surveyQuestionOptionList){
                        option.setDel("0");
                    }
                    surveyQuestionOptionDao.save(surveyQuestionOptionList);
                }
            }
        }
    }

    /**
     * 批量删除问题
     * @param ids
     * @throws Exception
     */
    @Transactional
    public void delQuestions(String ids) throws Exception{
        String[] idStr = ids.split(";");
        List<SurveyQuestion> questions = new ArrayList<>();
        List<SurveyQuestionOption> options = new ArrayList<>();
        for (String id:idStr){
            SurveyQuestion question = surveyQuestionDao.findOne(Long.parseLong(id));
            question.setDel("0");
            questions.add(question);
            if(question.getQuestionType()!=2){
                List<SurveyQuestionOption> surveyQuestionOptionList = surveyQuestionOptionDao.findByQuestionCode(question.getCode());
                if(surveyQuestionOptionList.size()>0){
                    for (SurveyQuestionOption option:surveyQuestionOptionList){
                        option.setDel("0");
                        options.add(option);
                    }
                }
            }
        }
        surveyQuestionDao.save(questions);
        if(options.size()>0){
            surveyQuestionOptionDao.save(options);
        }

    }

}
