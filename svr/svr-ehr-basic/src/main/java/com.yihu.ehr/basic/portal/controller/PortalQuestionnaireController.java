package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.portal.service.QuestionnaireService;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by zhangdan on 2018/4/17.
 */
@Controller
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "questionnaire", description = "问卷/满意度调查", tags = {"云门户-问卷调查"})
public class PortalQuestionnaireController extends EnvelopRestEndPoint {

    @Autowired
    private QuestionnaireService questionnaireService;

    @PostMapping(value = ServiceApi.Questionnaire.GetAllQuestionsByTemplateCode)
    @ResponseBody
    @ApiOperation(value = "根据模板id获取所有问题", notes = "根据模板id获取所有问题")
    public Result getAllQuestions(@ApiParam(name = "surveyTemplateCode",value = "问卷模板id")@RequestParam(value = "surveyTemplateCode",required = true)String surveyTemplateCode){
        try {
            JSONObject jsonObject = questionnaireService.getAllQuestions(surveyTemplateCode);
            return Result.success("查询成功！",jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败！");
        }
    }


    @PostMapping(value = ServiceApi.Questionnaire.SaveAnswer)
    @ResponseBody
    @ApiOperation(value = "保存问卷答案", notes = "保存问卷答案")
    public Result saveAnswerAndLotto(
            @RequestParam(value = "jsonData") String jsonData, HttpServletRequest request) {
        try {
            JSONObject json = new JSONObject(jsonData);
            User loginUser = (User) request.getSession().getAttribute(SessionAttributeKeys.CurrentUser);
            questionnaireService.saveAnswer(json,loginUser.getId());
            return Result.success("填写问卷完成！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("填写问卷失败！");
        }
    }
}
