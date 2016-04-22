package com.yihu.ehr.std.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MCDAType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/3/1.
 */
@FeignClient(name=MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface CDATypeClient {

    @RequestMapping(value = ServiceApi.Standards.TypeChildren, method = RequestMethod.GET)
    @ApiOperation(value = "根据父级ID获取下级")
    List<MCDAType> getChildrenByPatientId(
            @ApiParam(name = "id", value = "父级id",defaultValue = "")
            @RequestParam(value = "id",required = false) String parentId);


    @RequestMapping(value = ServiceApi.Standards.TypesChildren, method = RequestMethod.GET)
    @ApiOperation(value = "根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）")
    List<MCDAType> getChildIncludeSelfByParentIdsAndKey(
            @ApiParam(name = "patient_ids", value = "父级id")
            @RequestParam(value = "patient_ids") String patientIds,
            @ApiParam(name = "key", value = "查询条件")
            @RequestParam(value = "key") String key);


    @RequestMapping(value = ServiceApi.Standards.Type, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取CDAType")
    MCDAType getCdaTypeById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id);



    @RequestMapping(value = ServiceApi.Standards.Types, method = RequestMethod.POST)
    @ApiOperation(value = "新增CDAType")
    MCDAType saveCDAType(
            @ApiParam(name = "model", value = "model")
            @RequestParam(value = "model") String jsonData);


    @RequestMapping(value = ServiceApi.Standards.Type,method = RequestMethod.PUT)
    @ApiOperation(value = "修改CDAType")
    MCDAType updateCDAType(
            @ApiParam(name = "id", value = "编号")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "model", value = "json模型")
            @RequestParam(value = "model") String jsonData);


    @RequestMapping(value = ServiceApi.Standards.TypesCodeExistence , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    boolean isCDATypeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code);


    @RequestMapping(value = ServiceApi.Standards.Types, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除CDA类别，若该类别存在子类别，将一并删除子类别")
    boolean deleteCDATypeByPatientIds(
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = ServiceApi.Standards.Types, method = RequestMethod.GET)
    @ApiOperation(value = "标准类别分页搜索")
    ResponseEntity<Collection<MCDAType>> searchType(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Standards.NoPageTypes, method = RequestMethod.GET)
    ResponseEntity<Collection<MCDAType>> search(
            @RequestParam(value = "filters", required = false) String filters);


    @RequestMapping(value = ServiceApi.Standards.TypeParent, method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    List<MCDAType> getCdaTypeExcludeSelfAndChildren(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id);


}
