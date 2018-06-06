package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.portal.service.PortalMessageRemindService;
import com.yihu.ehr.basic.portal.service.QuestionnaireService;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.collections.map.HashedMap;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * Created by zhangdan on 2018/4/17.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "questionnaire", description = "问卷/满意度调查", tags = {"云门户-问卷调查"})
public class PortalQuestionnaireController extends EnvelopRestEndPoint {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private PortalMessageRemindService portalMessageRemindService;

    @PostMapping(value = ServiceApi.Questionnaire.GetAllQuestionsByTemplateCode)
    @ApiOperation(value = "根据模板id获取所有问题", notes = "根据模板id获取所有问题")
    public Envelop getAllQuestions(
            @ApiParam(name = "surveyTemplateCode",value = "问卷模板id")
            @RequestParam(value = "surveyTemplateCode",required = true)String surveyTemplateCode,
            @ApiParam(name = "messageId",value = "消息id")
            @RequestParam(value = "messageId",required = false)String messageId) throws Exception{
            Map<String, Object> map= questionnaireService.getAllQuestions(surveyTemplateCode);
            portalMessageRemindService.updateMessageRemind("notifie_flag","1",Long.valueOf(messageId));
            return success(map);
            //return Result.success("查询成功！",jsonObject);

    }


    @PostMapping(value = ServiceApi.Questionnaire.SaveAnswer)
    @ApiOperation(value = "保存问卷答案", notes = "保存问卷答案")
    public Result saveAnswerAndLotto(
            @RequestParam(value = "jsonData") String jsonData, HttpServletRequest request)throws Exception {
        JSONObject json = new JSONObject(jsonData);
        boolean flag = questionnaireService.saveAnswer(json);
        Map map = new HashedMap();
        if (flag){
            map.put("notifie_flag","1");
        }else {
            map.put("notifie_flag","0");
        }
        //User loginUser = (User) request.getSession().getAttribute(SessionAttributeKeys.CurrentUser);
        //questionnaireService.saveAnswer(json,loginUser.getId());
        return Result.success("填写问卷完成！",map);
    }
}
