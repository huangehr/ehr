package com.yihu.ehr.archivrsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.RsAuthorizeSubject;
import com.yihu.ehr.archivrsecurity.service.AuthorizeSubjectService;
import com.yihu.ehr.model.archivesecurity.MRsAuthorizeSubject;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lyr on 2016/7/12.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "authorize_subject",description = "授权主题配置")
public class AuthorizeSubjectEndPoint extends BaseRestEndPoint{

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthorizeSubjectService authorizeSubjectService;

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeSubjects,method = RequestMethod.POST)
    @ApiOperation("授权主题新增")
    public MRsAuthorizeSubject saveSubject(
            @ApiParam(value="jsonData")
            @RequestBody String jsonData) throws Exception {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        RsAuthorizeSubject subject = objectMapper.readValue(jsonData,RsAuthorizeSubject.class);

        return convertToModel(authorizeSubjectService.save(subject),MRsAuthorizeSubject.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeSubjects,method = RequestMethod.PUT)
    @ApiOperation("授权主题更新")
    public MRsAuthorizeSubject updateSubject(
            @ApiParam(value="jsonData")
            @RequestBody String jsonData) throws Exception {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        RsAuthorizeSubject subject = objectMapper.readValue(jsonData,RsAuthorizeSubject.class);
        return convertToModel(authorizeSubjectService.save(subject),MRsAuthorizeSubject.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeSubjects,method = RequestMethod.DELETE)
    @ApiOperation("授权主题删除")
    public boolean deleteSubject(
            @ApiParam(value="subjectId")
            @RequestParam(value = "subjectId",required = true)
                    String subjectId)
    {
        authorizeSubjectService.deleteBySubjectId(subjectId);
        return true;
    }

    @ApiOperation("授权主题查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeSubjects,method = RequestMethod.GET)
    public Collection<MRsAuthorizeSubject> queryResources(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(value="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(value="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long total = 0;
        Collection<MRsAuthorizeSubject> rsList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<RsAuthorizeSubject> subjects = authorizeSubjectService.getSubjects(sorts,reducePage(page),size);
            total = subjects.getTotalElements();
            rsList = convertToModels(subjects.getContent(),new ArrayList<>(subjects.getNumber()),MRsAuthorizeSubject.class,fields);
        }
        else
        {
            List<RsAuthorizeSubject> subjects = authorizeSubjectService.search(fields,filters,sorts,page,size);
            total = authorizeSubjectService.getCount(filters);
            rsList = convertToModels(subjects,new ArrayList<>(subjects.size()),MRsAuthorizeSubject.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        return rsList;
    }
}
