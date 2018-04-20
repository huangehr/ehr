package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.portal.model.SurveyQuestion;
import com.yihu.ehr.basic.portal.service.SurveyQuestionOptionService;
import com.yihu.ehr.basic.portal.service.SurveyQuestionService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by zhangdan on 2018/4/17.
 */
@Controller
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "admin/surveyQuestion", description = "问题题库数据接口", tags = {"云门户-问卷调查"})
public class PortalSurveyQuestionAdminController extends EnvelopRestEndPoint {

    @Autowired
    private SurveyQuestionService surveyQuestionService;
    @Autowired
    private SurveyQuestionOptionService surveyQuestionOptionService;

    /*//---问题管理列表---
    @RequestMapping(value = "initial", method = RequestMethod.GET)
    @ApiIgnore
    @ApiOperation(value = "问题管理列表")
    public String initQuestionList(){
        return "questionnaire/question/question_list";
    }

    //页面跳转（新增问题页面）
    @RequestMapping(value ="infoInit",method = RequestMethod.GET)
    @ApiIgnore
    @ApiOperation(value = "页面跳转（新增问题页面）")
    public String infoInit(String id,String mode){
        request.setAttribute("id",id);
        request.setAttribute("mode",mode);
        return "questionnaire/question/question_add";
    }

    //页面跳转（编辑问题页面）
    @RequestMapping(value ="editQuestion",method = RequestMethod.GET)
    @ApiIgnore
    @ApiOperation(value = "页面跳转（编辑问题页面）")
    public String editQuestion(String id,String mode){
        request.setAttribute("id",id);
        request.setAttribute("mode",mode);
        return "questionnaire/question/question_edit";
    }

    //跳转到问题查看页
    @RequestMapping(value = "seeQuestion", method = RequestMethod.GET)
    @ApiIgnore
    @ApiOperation(value = "跳转到问题查看页")
    public String seeQuestion(String id,String type) {
        request.setAttribute("id",id);
        request.setAttribute("type",type);
        return "questionnaire/question/see_question";
    }*/

    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "问题列表数据")
    public Envelop searchQuestionList(
            @ApiParam(name = "title", value = "标题",defaultValue = "")
            @RequestParam(value = "title", required = false) String title,
            @ApiParam(name = "questionType", value = "问题类型（0单选 1多选 2问答）",defaultValue = "0")
            @RequestParam(value = "questionType", required = false) Integer questionType,
            @ApiParam(name = "page",value = "第几页",defaultValue = "1")
            @RequestParam(value = "page",required = true) int page,
            @ApiParam(name = "rows",value = "页面大小",defaultValue = "2")
            @RequestParam(value = "rows",required = true) int rows) {
        Page<SurveyQuestion> res = surveyQuestionService.findQuestion(page,rows,title,questionType);
        return success(res);
    }

    /**
     * 问题新增页面
     * @param
     * @return
     *//*
    @RequestMapping(value = "addQuestionInitial", method = RequestMethod.GET)
    @ApiIgnore
    @ApiOperation(value = "问题列表数据")
    public String addQuestionInitial(Model model) {
        return "survey/question/addQuestion";
    }*/

    @RequestMapping(value = "addQuestions", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新增问题（可批量新增）")
    public Result addQuestions(@ApiParam(name = "jsonData", value = "新增json",defaultValue = "")
                                @RequestParam(value = "jsonData", required = true) String jsonData) {
        try {
            System.out.println("jsonData:"+jsonData);
            surveyQuestionService.saveOrUpdateQuestion(jsonData);
            return Result.success("新增成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(-1, "新增失败！");
        }
    }

    @RequestMapping(value = "getQuestion", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据id获取单个问题")
    public Result getQuestion(@ApiParam(name = "id", value = "问题id",defaultValue = "1")
                               @RequestParam(value = "id", required = true) Long id) {
        try {
            SurveyQuestion question = surveyQuestionService.findById(id);
            if(question == null){
                return Result.error(-1, "获取问题Id不存在");
            }else if("0".equals(question.getDel())){
                return Result.error(-1, "该问题已被删除");
            }else{
                JSONObject json = surveyQuestionService.getQuestion(question);
                return Result.success("获取成功",json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(-1, "获取失败！");
        }
    }


    @RequestMapping(value = "getQuestions", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据id获取单个问题")
    public Result getQuestion(@ApiParam(name = "id", value = "问题id",defaultValue = "1")
                              @RequestParam(value = "ids", required = true) String  ids) {
        try {
            JSONArray questions = surveyQuestionService.findByIds(ids);
             return Result.success("获取成功",questions);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(-1, "获取失败！");
        }
    }

    @RequestMapping(value = "delQuestion", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据id删除单个问题")
    public Result delQuestion(@ApiParam(name = "id", value = "问题id",defaultValue = "1")
                              @RequestParam(value = "id", required = true) Long id) {
        try {
            surveyQuestionService.delQuestion(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(-1, "删除失败！");
        }
    }

    @RequestMapping(value = "delQuestions", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result delQuestions(@ApiParam(name = "ids", value = "问题ids",defaultValue = "1;2;3")
                              @RequestParam(value = "ids", required = true) String ids) {
        try {
            if(StringUtils.isEmpty(ids)){
                return Result.error(-1, "删除问题ids不能为空！");
            }
            surveyQuestionService.delQuestions(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(-1, "删除失败！");
        }
    }

    @RequestMapping(value = "updQuestion", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新问题")
    public Result updQuestion(@ApiParam(name = "questionData", value = "问题json",defaultValue = "{}")
                               @RequestParam(value = "questionData", required = true) String questionData) {
        try {
            System.out.println(questionData);
            return Result.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(-1, "更新失败！");
        }
    }



}
