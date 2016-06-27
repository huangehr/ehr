package com.yihu.ehr.patient.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.family.MMembers;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by AndyCai on 2016/4/21.
 */
@FeignClient(name= MicroServices.Family)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface MembersClient {

    @RequestMapping(value = "/families/members", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭成员列表")
    ResponseEntity<Collection<MMembers>> searchMembers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) ;

    @RequestMapping(value = "/families/{families_id}members", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建家庭成员")
    MMembers createMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) ;

    @RequestMapping(value = "/families/{families_id}members", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改家庭成员")
    MMembers updateMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) ;

    @RequestMapping(value = "/families/{families_id}members{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取家庭成员")
    MMembers getMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") String id) ;

    @RequestMapping(value = "/families/{families_id}members/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除家庭成员")
    boolean deleteMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "id") String id);
}
