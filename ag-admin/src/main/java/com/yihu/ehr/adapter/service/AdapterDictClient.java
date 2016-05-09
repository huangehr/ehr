package com.yihu.ehr.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MAdapterDict;
import com.yihu.ehr.model.adaption.MAdapterDictVo;
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
@RequestMapping(ApiVersion.Version1_0  + "/adapter")
@ApiIgnore
public interface AdapterDictClient {


    @RequestMapping(value = "/plan/{planId}/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "字典适配关系分页查询")
    ResponseEntity<Collection<MAdapterRelationship>> searchAdapterDict(
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
            @RequestParam(value = "page", required = false) int page) ;

    @RequestMapping(value = "/plan/{planId}/dict/{dictId}/entrys", method = RequestMethod.GET)
    @ApiOperation(value = "字典项适配关系分页查询")
    ResponseEntity<Collection<MAdapterDictVo>> searchAdapterDictEntry(
            @ApiParam(name = "planId", value = "适配方案id", defaultValue = "")
            @PathVariable(value = "planId") Long planId,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dictId") Long dictId,
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

    @RequestMapping(value = "/entry/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据字典ID获取字典项适配关系明细")
    MAdapterDict getAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) ;

    @RequestMapping(value = "/entry", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "保存字典项映射关系")
    public MAdapterDict createAdapterDictEntry(
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestBody String dictJsonModel) throws Exception ;

    @RequestMapping(value = "/entry/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改字典项映射关系")
    public MAdapterDict updateAdapterDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "adapterDictModel", value = "字典数据模型", defaultValue = "")
            @RequestBody String dictJsonModel) throws Exception ;

    @RequestMapping(value = "/entrys", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项映射")
    public boolean delDictEntry(
            @RequestParam("ids") String ids) throws Exception ;

    @RequestMapping(value = "/plan/{planId}/dict/{dictId}/std_entrys", method = RequestMethod.GET)
    @ApiOperation(value = "过滤后的标准字典项分页查询")
    ResponseEntity<Collection<MAdapterRelationship>> searchStdDictEntry(
            @PathVariable(value = "planId") Long planId,
            @PathVariable(value = "dictId") Long dictId,
            @RequestParam(value = "seach_name", required = false) String searchName,
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);
}
