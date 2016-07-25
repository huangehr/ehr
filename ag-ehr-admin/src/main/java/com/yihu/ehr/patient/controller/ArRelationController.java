package com.yihu.ehr.patient.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.patient.ArRelationModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.patient.MArRelation;
import com.yihu.ehr.patient.service.ArRelationClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.util.FeignExceptionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(protocols = "https", value = "archive_relation", description = "档案关联", tags = {"档案关联"})
public class ArRelationController extends ExtendController<ArRelationModel> {

    @Autowired
    ArRelationClient arRelationClient;

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.GET)
    @ApiOperation(value = "档案关联列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ResponseEntity<List<MArRelation>> responseEntity = arRelationClient.search(fields, filters, sorts, size, page);
        List ls = (List) convertToModels(responseEntity.getBody(), new ArrayList<>(), ArRelationModel.class, null);
        return getResult(ls, getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.POST)
    @ApiOperation(value = "新增档案关联")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {

        try {
            return success(getModel(arRelationClient.add(model)) );
        }catch (Exception e){
            e.printStackTrace();
            return failed("新增出错！");
        }
    }


    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.PUT)
    @ApiOperation(value = "修改档案关联")
    public Envelop update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {

        try {
            MArRelation arRelation = toEntity(model, MArRelation.class);
            if(arRelation.getId()==0)
                return failed("编号不能为空");

            return success(getModel(arRelationClient.update(arRelation)) );
        }catch (Exception e){
            e.printStackTrace();
            return failed("新增出错！");
        }
    }


    @RequestMapping(value = ServiceApi.Patients.ArRelation, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        arRelationClient.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联")
    public boolean batchDelete(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids) {

        return arRelationClient.batchDelete(ids.split(","));
    }


    @RequestMapping(value = ServiceApi.Patients.ArRelation, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联信息")
    public Envelop getInfo(
            @ApiParam(name = "id", value = "档案关联编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        try {
            MArRelation arRelation = arRelationClient.getInfo(id);
            if(arRelation==null)
                return failed("查无数据！");
            return success(getModel(arRelation));
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取数据错误！");
        }
    }

    @RequestMapping(value = ServiceApi.Patients.ArRelationsExistence, method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public Envelop isExistenceFilters(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) throws Exception {

        try {
            return success(arRelationClient.isExistenceFilters(filters));
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }
}
