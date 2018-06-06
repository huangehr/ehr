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
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
	private PortalMessageRemindService portalMessageRemindService;

	@Resource
	private RedisTemplate<String,Object> redisTemplate;


	/**
	 * 抽奖问卷调查获取所有的问卷题目
	 * @param surveyTemplateCode
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getAllQuestions(String surveyTemplateCode)throws Exception{
		Map<String,Object> resultMap = new HashedMap();
        List<SurveyTemplateQuestions> questionList =null;
        List<SurveyTemplateOptions> optionsList = null;
        //从redis获取题目和选项
		Object questionObj = redisTemplate.opsForValue().get("questionnaire:quetion:code:"+surveyTemplateCode);
        if(questionObj != null){
            questionList = (List<SurveyTemplateQuestions>)net.sf.json.JSONArray.toCollection(net.sf.json.JSONArray.fromObject(questionObj.toString()),SurveyTemplateQuestions.class);
        }
		Object optionObj = redisTemplate.opsForValue().get("questionnaire:option:code:"+surveyTemplateCode);
        if (optionObj != null){
            optionsList = (List<SurveyTemplateOptions>) net.sf.json.JSONArray.toCollection(net.sf.json.JSONArray.fromObject(optionObj.toString()),SurveyTemplateOptions.class);
        }
        //如果redis没有则从数据库抽取
        if (questionList==null || optionsList==null){
            questionList = surveyTemplateQuestionsDao.findById(surveyTemplateCode);
            optionsList = surveyTemplateOptionsDao.findByQuestionCode(surveyTemplateCode);
			//list转成json
			//将题目数据存储到redis
			if (questionList!=null){
				String questionJson = net.sf.json.JSONArray.fromObject(questionList).toString();
				redisTemplate.opsForValue().set("questionnaire:quetion:code:" + surveyTemplateCode, questionJson);
			}
            //将选项数据存储到redis
			if (optionsList!=null){
				String optionJson = net.sf.json.JSONArray.fromObject(optionsList).toString();
				redisTemplate.opsForValue().set("questionnaire:option:code:" + surveyTemplateCode, optionJson);
			}
        }

		List<Map<String,Object>> resultList = new ArrayList<>();
        for (SurveyTemplateQuestions surveyTemplateQuestions : questionList){
            Map<String,Object> map = new HashMap<>();
			List<Map<String,Object>> optionList = new ArrayList<>();
			String qusCode = surveyTemplateQuestions.getCode();
			for (SurveyTemplateOptions option : optionsList){
				if (option.getQuestionCode().equals(qusCode)){
					Map<String,Object> optianMap = new HashMap<>();
					optianMap.put("id",option.getId());
					optianMap.put("sort",option.getSort());
					optianMap.put("code",option.getCode());
					optianMap.put("content",option.getContent());
					optionList.add(optianMap);
				}
			}
			map.put("qstCode",qusCode);
			map.put("title",surveyTemplateQuestions.getTitle());
			map.put("type",surveyTemplateQuestions.getQuestionType());
			map.put("sort",surveyTemplateQuestions.getSort());
			map.put("options",optionList);
			resultList.add(map);
		}
		resultMap.put("questions",resultList);
		return  resultMap;
	}

	public boolean saveAnswer(JSONObject jsonData) throws Exception {
		System.out.println("********jsonData********* " + jsonData);

		//解析json保存各种答案
		String surveyCode = String.valueOf(jsonData.get("surveyCode"));
		String userId = String.valueOf(jsonData.get("userId"));
		String messageId = String.valueOf(jsonData.get("messageId"));
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
					//JSONArray options = question.getJSONArray("options");
					//获取每道题的所有选项
					/*for (int j = 0; j < optionJson.size(); j++) {
						JSONObject option = new JSONObject(optionJson.get(j).toString());
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
					}*/
					JSONObject option = question.getJSONObject("options");
					String code = getCode();
					String optionCode = option.get("code").toString();
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
		//评价完更改就诊信息评价状态
		return portalMessageRemindService.updateMessageRemind("appraise_flag","1",Long.valueOf(messageId));
	}
}
