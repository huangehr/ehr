package com.yihu.ehr.basic.portal.service;

import com.alibaba.fastjson.JSON;
import com.yihu.ehr.basic.portal.dao.*;
import com.yihu.ehr.basic.portal.model.SurveyAnswers;
import com.yihu.ehr.basic.portal.model.SurveyOptionAnswers;
import com.yihu.ehr.basic.portal.model.SurveyTemplateOptions;
import com.yihu.ehr.basic.portal.model.SurveyTemplateQuestions;
import com.yihu.ehr.basic.report.feign.RedisServiceClient;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.client.RedisClient;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 问卷/满意度调查
 * @author zhangdan
 * @date 2018/4/17
 */
@Service
@Transactional
public class QuestionnaireService extends BaseJpaService {
	
	private static final Logger logger = LoggerFactory.getLogger(QuestionnaireService.class);

	@Autowired
	private SurveyTemplateQuestionsDao surveyTemplateQuestionsDao;
	@Autowired
	private SurveyTemplateOptionsDao surveyTemplateOptionsDao;
	@Autowired
	private SurveyOptionAnswersDao surveyOptionAnswersDao;
	@Autowired
	private SurveyStatisticsDao surveyStatisticsDao;
	@Autowired
	private SurveyAnswersDao surveyAnswersDao;
	@Autowired
	private RedisServiceClient redisServiceClient;

	@Autowired
	private RedisClient redisClient;


	/**
	 * 抽奖问卷调查获取所有的问卷题目
	 * @param surveyTemplateCode
	 * @return
	 * @throws Exception
	 */
	public JSONObject getAllQuestions(String surveyTemplateCode)throws Exception{
		JSONObject json = new JSONObject();
        List<SurveyTemplateQuestions> questionList =null;
        List<SurveyTemplateOptions> optionsList = null;
        //从redis获取题目和选项
        String resultQeustionJson = redisClient.get("questionnaire:quetion:code:"+surveyTemplateCode);
        if(StringUtils.isNotBlank(resultQeustionJson)){
            questionList = (List<SurveyTemplateQuestions>)net.sf.json.JSONArray.toCollection(net.sf.json.JSONArray.fromObject(resultQeustionJson),SurveyTemplateQuestions.class);
        }
        String resultOptinoJson = redisClient.get("questionnaire:option:code:"+surveyTemplateCode);
        if (StringUtils.isNotBlank(resultOptinoJson)){
            optionsList = (List<SurveyTemplateOptions>) net.sf.json.JSONArray.toCollection(net.sf.json.JSONArray.fromObject(resultOptinoJson),SurveyTemplateOptions.class);
        }
        //如果redis没有则从数据库抽取
        if (questionList==null || optionsList==null){
            questionList = surveyTemplateQuestionsDao.findById(surveyTemplateCode);
            optionsList = surveyTemplateOptionsDao.findByQuestionCode(surveyTemplateCode);
			//list转成json
			//将题目数据存储到redis
			if (questionList!=null){
				String questionJson = net.sf.json.JSONArray.fromObject(questionList).toString();
				redisClient.set("questionnaire:quetion:code:" + surveyTemplateCode, questionJson);
			}
            //将选项数据存储到redis
			if (optionsList!=null){
				String optionJson = net.sf.json.JSONArray.fromObject(optionsList).toString();
				redisClient.set("questionnaire:option:code:" + surveyTemplateCode, optionJson);
			}
        }

        for (SurveyTemplateQuestions surveyTemplateQuestions : questionList){
            Map<String,Object> map = new HashMap<>();
			String qusCode = surveyTemplateQuestions.getCode();
			List<SurveyTemplateOptions>  resultOptionList = new ArrayList<>();
			for (SurveyTemplateOptions option : optionsList){
				if (option.getQuestionCode().equals(qusCode)){
					resultOptionList.add(option);
				}
			}
			map.put("question",surveyTemplateQuestions);
			map.put("option",resultOptionList);
			json.put(surveyTemplateQuestions.getSort()+"",map);
		}
		return  json;
	}

	public void saveAnswerAndLotto(JSONObject jsonData, String userId) throws Exception {
		System.out.println("********jsonData********* " + jsonData);

		//解析json保存各种答案
		String surveyCode = jsonData.get("surveyCode").toString();
		Date createTime = new Date();
		//获取一维数组
		JSONArray questions = jsonData.getJSONArray("questions");
		for (int i = 0; i < questions.length(); i++) {
			//获取每一道题的信息
			JSONObject question = new JSONObject(questions.get(i).toString());
			String qstCode = question.get("qstCode").toString();
			int type = Integer.parseInt(question.get("type").toString());
			if (type != 2) {
				if (question.has("options")) {
					//获取每道题的所有选项
					JSONArray options = question.getJSONArray("options");
					for (int j = 0; j < options.length(); j++) {
						JSONObject option = new JSONObject(options.get(j).toString());
						String code = getCode();
						String optionCode = option.get("optionCode").toString();
						String comment = null;
						int haveComment = 0;
						if (option.has("comment")) {
							comment = option.get("comment").toString();
							haveComment = 1;
						}
						//保存到选择题答案表
						SurveyOptionAnswers optionAnswer = new SurveyOptionAnswers(code, surveyCode, qstCode, optionCode, comment, type, createTime,userId);
						surveyOptionAnswersDao.save(optionAnswer);
						//选择题修改统计表数量
						surveyStatisticsDao.modifyAmount(surveyCode, qstCode, optionCode);
					}
				}

			}else {
				String content = question.get("content").toString();
				if (!StringUtils.isEmpty(content)) {
//                  保存到问答题答案表
					String code = getCode();
					SurveyAnswers surveyAnswer = new SurveyAnswers(code, surveyCode, qstCode, content, createTime,userId);
					surveyAnswersDao.save(surveyAnswer);
//                	问答题保存到统计表(只负责更改数量不负责创建)
					surveyStatisticsDao.modifyAmount(surveyCode, qstCode);
				}
			}
		}
	}
}
