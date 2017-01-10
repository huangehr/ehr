package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.AuthorizeAppApply;
import com.yihu.ehr.model.archivesecurity.MAuthorizeAppApply;
import com.yihu.ehr.archivrsecurity.service.AuthorizeAppApplyService;
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
@Api(value = "AuthorizeAppApply", description = "应用授权申请", tags = {"应用授权申请"})
public class AuthorizeAppApplyEndPoint extends EnvelopRestEndPoint {

    @Autowired
    AuthorizeAppApplyService authorizeAppApplyService;

    @ApiOperation(value = "应用授权列表查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeApps, method = RequestMethod.GET)
    public Collection<MAuthorizeAppApply> searchAuthorizeAppApply(
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
        List<AuthorizeAppApply> authorizeAppApplyList = authorizeAppApplyService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response, authorizeAppApplyService.getCount(filters), page, size);

        return convertToModels(authorizeAppApplyList, new ArrayList<MAuthorizeAppApply>(authorizeAppApplyList.size()), MAuthorizeAppApply.class, fields);
    }

    @ApiOperation(value = "应用授权新增")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeApps, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MAuthorizeAppApply createAuthorizeAppApply(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) {
        AuthorizeAppApply authorizeAppApply = toEntity(jsonData, AuthorizeAppApply.class);
        authorizeAppApplyService.save(authorizeAppApply);
        return convertToModel(authorizeAppApply, MAuthorizeAppApply.class, null);
    }

    @ApiOperation(value = "应用授权查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeAppsId, method = RequestMethod.GET)
    public MAuthorizeAppApply getAuthorizeAppApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") long id) {
        AuthorizeAppApply authorizeAppApply = authorizeAppApplyService.retrieve(id);
        if (authorizeAppApply == null) {
            return null;
        }else {
            return convertToModel(authorizeAppApply, MAuthorizeAppApply.class);
        }

    }

    @ApiOperation(value = "应用授权修改")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeApps, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MAuthorizeAppApply updateAuthorizeAppApply(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) throws Exception {
        AuthorizeAppApply authorizeAppApply = toEntity(jsonData, AuthorizeAppApply.class);
        authorizeAppApplyService.save(authorizeAppApply);
        return convertToModel(authorizeAppApply, MAuthorizeAppApply.class, null);
    }

    @ApiOperation(value = "应用授权删除")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeAppsId, method = RequestMethod.DELETE)
    public boolean deleteAuthorizeAppApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{
        authorizeAppApplyService.delete(id);
        return true;
    }

}
