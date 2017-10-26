package com.yihu.ehr.user.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.user.entity.Roles;
import com.yihu.ehr.user.service.RolesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
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
@Controller
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roles",description = "角色管理", tags = {"安全管理-角色管理"})
public class RolesEndPoint extends EnvelopRestEndPoint{
    @Autowired
    private RolesService rolesService;

    @RequestMapping(value = ServiceApi.Roles.RoleBatchAdd,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量新增角色组")
    public boolean roleBatchAdd(
            @ApiParam(name = "data_json",value = "新增角色组Json字符串")
            @RequestBody String dataJson,
            @ApiParam(name = "orgCodes", value = "多机构编码拼接字符串")
            @RequestParam(value = "orgCodes") String orgCodes){
        Roles roles = toEntity(dataJson,Roles.class);
        Roles rolesNew = null;
        if(StringUtils.isNotEmpty(orgCodes)){
            String [] orgs = orgCodes.split(";");
            for(int i=0;i < orgs.length ;i++){
                rolesNew = new Roles();
                rolesNew.setAppId(roles.getAppId());
                rolesNew.setCode(roles.getCode());
                rolesNew.setName(roles.getName());
                rolesNew.setDescription(roles.getDescription());
                rolesNew.setType(roles.getType());
                rolesNew.setOrgCode(orgs[i]);
                rolesNew = rolesService.save(rolesNew);
            }
        }
        if(rolesNew != null){
            return true;
        }else {
            return false;
        }
    }

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
        if(null == rolesService.retrieve(roles.getId())) throw  new ApiException(HttpStatus.NOT_FOUND,"角色组未找到！");
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
        if(roles == null) throw new ApiException(HttpStatus.NOT_FOUND,"角色组未找到！");
        return convertToModel(roles,MRoles.class);
    }
    @RequestMapping(value = ServiceApi.Roles.Roles,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组列表---分页")
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
        List<Roles> rolesList = rolesService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, rolesService.getCount(filters), page, size);
        return convertToModels(rolesList, new ArrayList<>(rolesList.size()), MRoles.class, fields);
    }
    @RequestMapping(value = ServiceApi.Roles.RolesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组列表---不分页")
    public Collection<MRoles> searchRolesNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<Roles> rolesList = rolesService.search(filters);
        return convertToModels(rolesList,new ArrayList<MRoles>(rolesList.size()),MRoles.class,"");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleNameExistence,method = RequestMethod.GET)
    @ApiOperation(value = "角色组名称是否已存在" )
    public boolean isNameExistence(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "name",value = "角色组名称")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "orgCode",value = "机构Code")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "type",value = "角色组类别")
            @RequestParam(value = "type",required = false) String type){
        String[] fields = {"appId","name","orgCode","type"};
        String[] values = {appId,name,orgCode,type};
        List<Roles> roles = rolesService.findByFields(fields,values);
        if(roles != null && roles.size() >0){
            return true;
        }
        return false;
    }
    @RequestMapping(value = ServiceApi.Roles.RoleCodeExistence,method = RequestMethod.GET)
    @ApiOperation(value = "角色组代码是否已存在" )
    public boolean isCodeExistence(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "code",value = "角色组代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "orgCode",value = "机构Code")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "type",value = "角色组类别")
            @RequestParam(value = "type",required = false) String type){
        String[] fields = {"appId","code","orgCode","type"};
        String[] values = {appId,code,orgCode,type};
        List<Roles> roles = rolesService.findByFields(fields, values);
        if(roles != null && roles.size() >0){
            return true;
        }
        return false;
    }
}
