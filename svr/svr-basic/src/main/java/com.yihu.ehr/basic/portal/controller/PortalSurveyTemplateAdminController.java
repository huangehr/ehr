package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.dict.service.SystemDictEntryService;
import com.yihu.ehr.basic.dict.service.SystemDictService;
import com.yihu.ehr.basic.portal.service.SurveyTemplateService;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.dict.SystemDict;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.portal.MSurveyTemplate;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangdan on 2018/4/13.。
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "survey", description = "问卷/满意度调查后台配置问题和答案", tags = {"云门户-问卷调查后台配置"})
public class PortalSurveyTemplateAdminController extends EnvelopRestEndPoint {
    @Autowired
    SurveyTemplateService surveyTemplateService;

    @Autowired
    private SystemDictService dictService;

    @Autowired
    private SystemDictEntryService systemDictEntryService;

    /*//---问卷模板管理列表---
    @RequestMapping(value = "initial", method = RequestMethod.GET)
    public String initTeamList(){
        return "questionnaire/template/template_list";
    }

    //跳转到问题查看页
    @RequestMapping(value = "importQuestion", method = RequestMethod.GET)
    @ApiIgnore
    @ApiOperation(value = "跳转到问题查看页")
    public String importQuestion() {
        return "questionnaire/template/import_question";
    }*/


    @RequestMapping(value = ServiceApi.SurveyAdminManage.GetSurveyTemplateList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有问卷模板列表", notes = "管理界面展示所有的模板问卷")
    public ListResult list(
            @ApiParam(name = "title", value = "标题",defaultValue = "")
            @RequestParam(value = "title", required = false) String title,
            @ApiParam(name = "label", value = "标签信息",defaultValue = "0")
            @RequestParam(value = "label", required = false) Integer labelCode,
            @ApiParam(name = "page",value = "第几页",defaultValue = "1")
            @RequestParam(value = "page",required = true) String page,
            @ApiParam(name = "rows",value = "页面大小",defaultValue = "2")
            @RequestParam(value = "rows",required = true) String rows)throws Exception {
        ListResult listResult = new ListResult();
        StringBuffer filters = new StringBuffer();
        filters.append(" and t.del =1 ");
        if(StringUtils.isNotBlank(title)){
            filters.append(" and t.title like :title ");
        }
        if(labelCode!=null&&labelCode!=0){
            filters.append(" and t.code in(select relation_code from  portal_survey_label_info w where w.use_type=0 and w.label=:labelCode)");
        }
        Map<String,Object> map = surveyTemplateService.queryList(Integer.valueOf(page),Integer.valueOf(rows),title,labelCode,filters.toString());
        if(map != null){
            listResult.setDetailModelList((List<MSurveyTemplate>)map.get("data"));
            listResult.setTotalCount((int)map.get("count"));
            listResult.setCode(200);
            listResult.setCurrPage(Integer.valueOf(page));
            listResult.setPageSize(Integer.valueOf(rows));
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }


    @RequestMapping(value = ServiceApi.SurveyAdminManage.GetSurveyTemplateOptionsList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有问题选项列表", notes = "管理界面展示所有的问题的选项")
    public ListResult listOption(
            @ApiParam(name = "q", value = "标题",defaultValue = "")
            @RequestParam(value = "q", required = false) String title,
            @ApiParam(name = "label", value = "标签信息",defaultValue = "0")
            @RequestParam(value = "label", required = false) Integer labelCode,
            @ApiParam(name = "page",value = "第几页",defaultValue = "1")
            @RequestParam(value = "page",required = true) int page,
            @ApiParam(name = "rows",value = "页面大小",defaultValue = "2")
            @RequestParam(value = "rows",required = true) int rows)throws Exception{
        ListResult listResult = new ListResult();
        List<MSurveyTemplate> list = new ArrayList<>();
        StringBuffer filters = new StringBuffer();
        filters.append(" and t.del =1 ");
        if(StringUtils.isNotBlank(title)){
            filters.append(" and t.title like :title ");
        }
        if(labelCode!=null&&labelCode!=0){
            filters.append(" and t.code in(select relation_code from  portal_survey_label_info w where w.use_type=0 and w.label=:labelCode)");
        }
        Map<String,Object> map = surveyTemplateService.queryList(page,rows,title,labelCode,filters.toString());
        List<MSurveyTemplate> res = (List<MSurveyTemplate>)map.get("data");

        for (MSurveyTemplate m : res){
            MSurveyTemplate mSurveyTemplate = new MSurveyTemplate();
            mSurveyTemplate.setId(m.getId());
            mSurveyTemplate.setText(m.getTitle());
            list.add(mSurveyTemplate);
        }
        if (map!=null){
            listResult.setDetailModelList(list);
            listResult.setTotalCount((int)map.get("count"));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(rows);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }

        return listResult;

    }


    @RequestMapping(value = ServiceApi.SurveyAdminManage.GetTemplateLabel, method = RequestMethod.POST)
    @ApiOperation(value = "获取问卷类型", notes = "获取所有问卷类型")
    public Envelop getTemplateLabel( @ApiParam(name = "phoneticCode", value = "拼音编码", required = true)
                                         @PathVariable(value = "phoneticCode") String phoneticCode){
        SystemDict dict = dictService.findByPhoneticCode(phoneticCode);
        if (dict ==null){
            return  success("没有问卷类型");
        }
        Envelop envelop = new Envelop();
        int page = 0;
        int size = 1000;
        //ListResult re = new ListResult(page, size);
        Page<SystemDictEntry> page1 = systemDictEntryService.findByDictId(dict.getId(), page,size);
        if(page1 != null) {
            envelop.setDetailModelList(page1.getContent());
            envelop.setSuccessFlg(true);
        }
        return envelop;
    }


    //---新增问卷模板---
   /* @RequestMapping(value = "addTemplate", method = RequestMethod.GET)
    public String addTemplate(String id,String mode){
        request.setAttribute("templateId",id);
        request.setAttribute("mode",mode);
        return "questionnaire/template/add_template";
    }

    //---编辑问卷模板---
    @RequestMapping(value = "editTemplate", method = RequestMethod.GET)
    public String editTemplate(String id,String mode){
        request.setAttribute("id",id);
        request.setAttribute("mode",mode);
        return "questionnaire/template/edit_template";
    }
*/
    //---保存标签---
    @RequestMapping(value = ServiceApi.SurveyAdminManage.SaveLabelInfo, method = RequestMethod.POST)
    @ApiOperation(value = "保存标签", notes = "保存标签")
    public Result editLabel(@ApiParam(name = "模板id", value = "模板id",defaultValue = "0") @RequestParam(value = "templateId", required = true) long templateId,
                            @ApiParam(name = "标签JSON", value = "标签JSON",defaultValue = "") @RequestParam(value = "jsonData", required = true) String jsonData)throws Exception{
        surveyTemplateService.saveLabel(templateId,jsonData);
        return Result.success("保存成功！");
    }

    @RequestMapping(value = ServiceApi.SurveyAdminManage.SaveTemplate, method = RequestMethod.POST)
    @ApiOperation(value = "保存问卷模板", notes = "保存问卷模板")
    public Result saveTemplate(@ApiParam(name = "jsonData", value = "新增json",defaultValue = "") @RequestParam(value = "jsonData", required = true) String jsonData,
                               @ApiParam(name = "userId", value = "用户id",defaultValue = "") @RequestParam(value = "userId", required = false) String userId,
                               HttpServletRequest request)throws Exception{
        surveyTemplateService.saveOrUpdate(jsonData,userId);
        return Result.success("保存成功");
    }


    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ResponseBody
    public Envelop getTemplate(@ApiParam(name = "id", value = "模板id",defaultValue = "0") @RequestParam(value = "id", required = true) String id,
                              @ApiParam(name = "question", value = "是否加载问题",defaultValue = "0") @RequestParam(value = "question", required = false) String question,
                              HttpServletRequest request)throws Exception{
        Map<String,Object> map =  surveyTemplateService.getTemplate(Long.valueOf(id),Long.valueOf(question));
        return success(map);
    }

    /*@RequestMapping(value = "template/title", method = RequestMethod.GET)
    @ResponseBody
    public String getTemplate(@ApiParam(name = "title", value = "模板title",defaultValue = "")
                              @RequestParam(value = "title", required = true) String  title){
        try {
            SurveyTemplate surveyTemplate =  surveyTemplateService.findByTitle(title);
            return write(200,"获取成功","data",surveyTemplate);
        }catch (Exception e){
            return write(-1,"获取失败");
        }
    }*/

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@ApiParam(name = "模板ID", value = "模板ID",defaultValue = "0") @RequestParam(value = "templateId", required = true) String templateId)throws Exception{
        surveyTemplateService.deleteTemplate(Long.valueOf(templateId));
        return Result.success("问卷模板删除成功！");
    }
}
