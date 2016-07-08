package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.user.service.RoleUser;
import com.yihu.ehr.user.service.RoleUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roleUser",description = "角色组人员配置")
public class RoleUserEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleUserService roleUserService;

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组添加人员")
    public MRoleUser createRoleUser(
            @ApiParam(name = "data_json",value = "角色组-用户关系Json串")
            @RequestBody String dataJson){
        RoleUser roleUser = toEntity(dataJson,RoleUser.class);
        RoleUser roleUserNew = roleUserService.save(roleUser);
        return convertToModel(roleUserNew,MRoleUser.class);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleUserId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除角色组的人员")
    public boolean deleteRoleUser(
            @ApiParam(name = "id",value = "角色组-用户关系id")
            @PathVariable(value = "id") long id){
        roleUserService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色人员关系列表---分页")
    public Collection<MRoleUser> searchRoleUser(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,userId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        page = reducePage(page);
        if (StringUtils.isEmpty(filters)) {
            Page<RoleUser> roleUserPage = roleUserService.getRoleUserList(sorts, page, size);
            pagedResponse(request, response, roleUserPage.getTotalElements(), page, size);
            return convertToModels(roleUserPage.getContent(), new ArrayList<>(roleUserPage.getNumber()), MRoleUser.class, fields);
        } else {
            List<RoleUser> roleUserList = roleUserService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, roleUserService.getCount(filters), page, size);
            return convertToModels(roleUserList, new ArrayList<>(roleUserList.size()), MRoleUser.class, fields);
        }
    }
    @RequestMapping(value = ServiceApi.Roles.RoleUserNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色人员关系列表---不分页")
    public Collection<MRoleUser> searchRoleUserNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleUser> roleUserList = roleUserService.search(filters);
        return convertToModels(roleUserList,new ArrayList<MRoleUser>(roleUserList.size()),MRoleUser.class,"");
    }
}
