package com.yihu.ehr.ha.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MOrgDict;
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
 * Created by AndyCai on 2016/3/2.
 */
@FeignClient(name=MicroServices.Adaption)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgDictClient {
    @RequestMapping(value = "/adapter/org/dict", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    MOrgDict getOrgDict(
            @ApiParam(name = "id", value = "查询条件", defaultValue = "")
            @RequestParam(value = "id", required = false) long id);

    @RequestMapping(value = "/adapter/org/dict", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构字典")
    MOrgDict createOrgDict(
            @ApiParam(name = "model", value = "字典信息", defaultValue = "")
            @RequestParam(value = "model") String jsonData);


    @RequestMapping(value = "/adapter/org/dict/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典")
    boolean deleteOrgDict(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);


    @RequestMapping(value = "/adapter/org/dict", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构字典")
    MOrgDict updateOrgDict(
            @ApiParam(name = "model", value = "字典信息", defaultValue = "")
            @RequestParam(value = "model") String jsonData);


    @RequestMapping(value = "/adapter/org/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    ResponseEntity<Collection<MOrgDict>> searchOrgDicts(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/adapter/org/dict/combo", method = RequestMethod.GET)
    @ApiOperation(value = "机构字典下拉")
    List<String> getOrgDict(
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode);

    @RequestMapping(value = "/adapter/org/dict/is_exist",method = RequestMethod.GET)
    boolean isExistDict(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "dict_code") String dictCode);

    @RequestMapping(value = "/adapter/org/dict/org_dict",method = RequestMethod.GET)
    MOrgDict getOrgDictBySequence(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "sequence") int sequence);
}
