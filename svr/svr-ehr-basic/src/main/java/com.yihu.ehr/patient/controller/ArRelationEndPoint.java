package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.patient.MArRelation;
import com.yihu.ehr.patient.service.arapply.ArRelation;
import com.yihu.ehr.patient.service.arapply.ArRelationService;
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
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "archive_apply_relation", description = "档案关联")
public class ArRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    ArRelationService arRelationService;

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.GET)
    @ApiOperation(value = "档案关联列表")
    public Collection<MArRelation> search(
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

        List ls = arRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, arRelationService.getCount(filters), page, size);
        return convertToModels(ls, new ArrayList<>(), MArRelation.class, fields);
    }


    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增档案关联")
    public MArRelation add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody ArRelation model) throws Exception{

        return getModel( arRelationService.saveModel(model) );
    }

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改档案关联")
    public MArRelation update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody ArRelation model) throws Exception{

        return getModel(arRelationService.saveModel(model));
    }

    @RequestMapping(value = ServiceApi.Patients.ArRelation, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) throws Exception{

        arRelationService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联")
    public boolean batchDelete(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") Integer[] ids) throws Exception{

        return arRelationService.delete(ids) > 0;
    }

    @RequestMapping(value = ServiceApi.Patients.ArRelation, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联信息")
    public MArRelation getInfo(
            @ApiParam(name = "id", value = "档案关联编号", defaultValue = "")
            @PathVariable(value = "id") int id) throws Exception{

        return getModel(arRelationService.retrieve(id));
    }

    @RequestMapping(value = ServiceApi.Patients.ArRelationsExistence, method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean isExistenceFilters(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) throws Exception {

        return arRelationService.getCount(filters)>0;
    }

    protected MArRelation getModel(ArRelation o){
        return convertToModel(o, MArRelation.class);
    }

}
