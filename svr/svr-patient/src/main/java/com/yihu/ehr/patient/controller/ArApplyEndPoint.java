package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.patient.MArApply;
import com.yihu.ehr.patient.service.arapply.ArApply;
import com.yihu.ehr.patient.service.arapply.ArApplyService;
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
@Api(value = "archive_apply", description = "档案关联申请")
public class ArApplyEndPoint extends EnvelopRestEndPoint {
    @Autowired
    ArApplyService arApplyService;

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.GET)
    @ApiOperation(value = "档案关联申请列表")
    public Collection<MArApply> search(
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

        List appList = arApplyService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, arApplyService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(), MArApply.class, fields);
    }


    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增档案关联申请")
    public MArApply add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody ArApply model) throws Exception{

        model.setApplyDate(new Date());
        return getModel( arApplyService.save(model) );
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改档案关联申请")
    public MArApply update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody ArApply model) throws Exception{

        return getModel(arApplyService.save(model));
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplication, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联申请")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) throws Exception{

        arApplyService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联申请")
    public boolean batchDelete(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") Integer[] ids) throws Exception{

        return arApplyService.delete(ids) > 0;
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplication, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联申请信息")
    public MArApply getInfo(
            @ApiParam(name = "id", value = "档案关联申请编号", defaultValue = "")
            @PathVariable(value = "id") int id) throws Exception{

        return getModel(arApplyService.retrieve(id));
    }

    protected MArApply getModel(ArApply o){
        return convertToModel(o, MArApply.class);
    }

}
