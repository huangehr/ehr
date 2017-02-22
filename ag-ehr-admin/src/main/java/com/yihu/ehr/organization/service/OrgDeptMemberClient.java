package com.yihu.ehr.organization.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrgMemberRelation;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/20.
 */
@FeignClient(name= MicroServices.Organization)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgDeptMemberClient {

    @RequestMapping(value = "/orgDeptMember/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件 查询部门下成员列表")
    ResponseEntity<List<MOrgMemberRelation>> searchOrgDeptMembers(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestBody(required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page
    ) ;


    @RequestMapping(value = "/orgDeptMember/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增部门成员")
    MOrgMemberRelation saveOrgDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = "新增部门成员信息")
            @RequestBody String memberRelationJsonData
    );

    @RequestMapping(value = "/orgDeptMember/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门成员")
    MOrgMemberRelation updateOrgDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = "修改部门成员信息")
            @RequestBody String memberRelationJsonData
    ) ;

    @RequestMapping(value = "/orgDeptMember/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除部门成员" )
    boolean deleteOrgDeptMember(
            @ApiParam(name = "memberRelationId", value = "部门成员ID")
            @RequestParam(value = "memberRelationId", required = true) Integer memberRelationId
    ) ;
}
