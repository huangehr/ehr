package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.user.service.Roles;
import com.yihu.ehr.user.service.RolesService;
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
@Api(value = "roles",description = "用户角色管理")
public class RolesEndPoint extends EnvelopRestEndPoint{
    @Autowired
    private RolesService rolesService;

    @RequestMapping(value = ServiceApi.Roles.Role,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增角色组")
    public MRoles createRoles(
            @ApiParam(name = "data_json",value = "新增角色组Json字符串")
            @RequestBody String dataJson){
        Roles roles = toEntity(dataJson,Roles.class);
        Roles rolesNew = rolesService.save(roles);
        return convertToModel(rolesNew,MRoles.class,null);
    }
    @RequestMapping(value = ServiceApi.Roles.Role,method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改角色组")
    public MRoles updateRoles(
            @ApiParam(name = "data_json",value = "修改角色组Json字符串")
            @RequestBody String dataJson){
        Roles roles = toEntity(dataJson,Roles.class);
        if(null == rolesService.retrieve(roles.getId())) throw  new ApiException(ErrorCode.GetDictFaild,"角色组不存在");
        Roles rolesNew = rolesService.save(roles);
        return convertToModel(rolesNew,MRoles.class,null);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id删除")
    public boolean deleteRoles(
            @ApiParam(name = "id",value = "角色组id")
            @PathVariable(value = "id") long id){
        rolesService.delete(id);
        return true;
    }
    @RequestMapping(value = ServiceApi.Roles.RoleId,method = RequestMethod.GET)
    @ApiOperation(value = "根据角色组id查询")
    public MRoles getRolesById(
            @ApiParam(name = "id",value = "角色组id")
            @PathVariable(value = "id") long id){
        Roles roles = rolesService.retrieve(id);
        if(roles == null) throw new ApiException(ErrorCode.GetDictFaild, "角色组不存在");
        return convertToModel(roles,MRoles.class);
    }
    @RequestMapping(value = ServiceApi.Roles.Roles,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色列表---分页")
    public Collection<MRoles> searchRoles(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,description,appId,type")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+code,+name")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        page = reducePage(page);
        if (StringUtils.isEmpty(filters)) {
            Page<Roles> rolesPage = rolesService.getRolesList(sorts, page, size);
            pagedResponse(request, response, rolesPage.getTotalElements(), page, size);
            return convertToModels(rolesPage.getContent(), new ArrayList<>(rolesPage.getNumber()), MRoles.class, fields);
        } else {
            List<Roles> rolesList = rolesService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, rolesService.getCount(filters), page, size);
            return convertToModels(rolesList, new ArrayList<>(rolesList.size()), MRoles.class, fields);
        }
    }
    @RequestMapping(value = ServiceApi.Roles.RolesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色列表---不分页")
    public Collection<MRoles> searchRolesNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<Roles> rolesList = rolesService.search(filters);
        return convertToModels(rolesList,new ArrayList<MRoles>(rolesList.size()),MRoles.class,"");
    }
}
