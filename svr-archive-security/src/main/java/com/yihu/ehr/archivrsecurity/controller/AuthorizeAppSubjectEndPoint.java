package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.AuthorizeAppSubject;
import com.yihu.ehr.model.archivesecurity.MAuthorizeAppSubject;
import com.yihu.ehr.archivrsecurity.service.AuthorizeAppSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AuthorizeAppSubject", description = "应用授权主题管理", tags = {"应用授权主题管理"})
public class AuthorizeAppSubjectEndPoint extends EnvelopRestEndPoint {

    @Autowired
    AuthorizeAppSubjectService authorizeAppSubjectService;

    @ApiOperation(value = "应用授权主题列表查询")
    @RequestMapping(value = "/authorize_subjects", method = RequestMethod.GET)
    public Collection<MAuthorizeAppSubject> searchAuthorizeAppApply(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<AuthorizeAppSubject> authorizeAppApplyList = authorizeAppSubjectService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response, authorizeAppSubjectService.getCount(filters), page, size);

        return convertToModels(authorizeAppApplyList, new ArrayList<MAuthorizeAppSubject>(authorizeAppApplyList.size()), MAuthorizeAppSubject.class, fields);
    }

    @ApiOperation(value = "应用授权主题新增")
    @RequestMapping(value = "/authorize_subjects", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MAuthorizeAppSubject createAuthorizeAppApply(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) {
        AuthorizeAppSubject authorizeAppSubject = toEntity(jsonData, AuthorizeAppSubject.class);
        authorizeAppSubjectService.save(authorizeAppSubject);
        return convertToModel(authorizeAppSubject, MAuthorizeAppSubject.class, null);
    }


    @ApiOperation(value = "应用授权主题修改")
    @RequestMapping(value = "/authorize_subjects", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MAuthorizeAppSubject updateAuthorizeAppApply(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) throws Exception {
        AuthorizeAppSubject authorizeAppSubject = toEntity(jsonData, AuthorizeAppSubject.class);
        authorizeAppSubjectService.save(authorizeAppSubject);
        return convertToModel(authorizeAppSubject, MAuthorizeAppSubject.class, null);
    }

    @ApiOperation(value = "应用授权主题删除")
    @RequestMapping(value = "/authorize_subjects/{id}", method = RequestMethod.DELETE)
    public boolean deleteAuthorizeAppApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{
        authorizeAppSubjectService.delete(id);
        return true;
    }

}
