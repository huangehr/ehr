package com.yihu.ehr.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.model.adaption.MAdapterDataVo;
import com.yihu.ehr.model.adaption.MAdapterRelationship;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@FeignClient(name=MicroServices.Adaption)
@RequestMapping(ApiVersion.Version1_0 + "/adapter")
@ApiIgnore
public interface AdapterDataSetClient {


    @RequestMapping(value = "/plan/{planId}/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "根据方案ID及查询条件查询数据集适配关系")
    ResponseEntity<Collection<MAdapterRelationship>> searchAdapterDataSet(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/plan/{planId}/datasets/{dataSetId}/datametas", method = RequestMethod.GET)
    @ApiOperation(value = "根据dataSetId搜索数据元适配关系")
    ResponseEntity<Collection<MAdapterDataVo>> searchAdapterMetaData(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dataSetId", value = "数据集id", defaultValue = "")
            @PathVariable(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "code", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "code", required = false) String code,
            @ApiParam(name = "name", value = "名称查询值", defaultValue = "")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = "/datameta/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集ID获取数据元适配关系明细")
    MAdapterDataSet getAdapterMetaData(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id);


    @RequestMapping(value = "/datameta/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改数据元映射关系")
    MAdapterDataSet updateAdapterMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "jsonModel", value = "数据元模型", defaultValue = "")
            @RequestBody String jsonModel);


    @RequestMapping(value = "/datameta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增数据元映射关系")
    MAdapterDataSet createAdapterMetaData(
            @ApiParam(name = "jsonModel", value = "数据元模型", defaultValue = "")
            @RequestBody String jsonModel);

    @RequestMapping(value = "/datametas", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元映射关系")
    boolean delMetaData(
            @RequestParam("ids") String ids);

    @RequestMapping(value = "/data_set/{data_set_id}/is_left/meta", method = RequestMethod.GET)
    @ApiOperation(value = "判断除了metaIds之外是否还存在其他的数据元")
    boolean isLeftMeta(
            @RequestParam("plan_id") long planId,
            @PathVariable("data_set_id") long dataSetId,
            @RequestParam("meta_ids") String metaIds);

    @RequestMapping(value = "/plan/{planId}/data_set/{data_set_id}/std_meta", method = RequestMethod.GET)
    @ApiOperation(value = "过滤后的标准字典项分页查询")
    ResponseEntity<Collection<MAdapterRelationship>> searchStdMeta(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "data_set_id", value = "字典编号", defaultValue = "")
            @PathVariable(value = "data_set_id") Long dataSetId,
            @ApiParam(name = "seach_name", value = "代码查询值", defaultValue = "")
            @RequestParam(value = "seach_name", required = false) String searchName,
            @ApiParam(name = "mode", value = "编辑模式（new/modify）", defaultValue = "")
            @RequestParam(value = "mode", required = false) String mode,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);
}
