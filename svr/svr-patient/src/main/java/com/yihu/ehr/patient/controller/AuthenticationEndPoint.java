package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.patient.MAuthentication;
import com.yihu.ehr.patient.service.authentication.Authentication;
import com.yihu.ehr.patient.service.authentication.AuthenticationService;
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
import java.util.Date;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "authentication", description = "人口身份认证申请")
public class AuthenticationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.GET)
    @ApiOperation(value = "人口身份认证申请列表")
    public Collection<MAuthentication> search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        List ls = authenticationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, authenticationService.getCount(filters), page, size);
        return convertToModels(ls, new ArrayList<>(), MAuthentication.class, fields);
    }


    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增认证申请")
    public MAuthentication add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody Authentication model) throws Exception{

        model.setApplyDate(new Date());
        return getModel( authenticationService.save(model) );
    }


    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改认证申请")
    public MAuthentication update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody Authentication model) throws Exception{

        model.setAuditDate(new Date());
        return getModel(authenticationService.save(model));
    }

    @RequestMapping(value = ServiceApi.Patients.Authentication, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除认证申请")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) throws Exception{

        authenticationService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除认证申请")
    public boolean batchDelete(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") Integer[] ids) throws Exception{

        return authenticationService.delete(ids) > 0;
    }

    @RequestMapping(value = ServiceApi.Patients.Authentication, method = RequestMethod.GET)
    @ApiOperation(value = "获取认证申请信息")
    public MAuthentication getInfo(
            @ApiParam(name = "id", value = "档案关联编号", defaultValue = "")
            @PathVariable(value = "id") int id) throws Exception{

        return getModel(authenticationService.retrieve(id));
    }

    protected MAuthentication getModel(Authentication o){
        return convertToModel(o, MAuthentication.class);
    }

}
