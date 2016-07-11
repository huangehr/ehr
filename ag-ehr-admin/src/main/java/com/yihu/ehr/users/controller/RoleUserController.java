package com.yihu.ehr.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.RoleUserModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.users.service.RoleUserClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "roleUser",description = "角色组人员配置",tags = "")
public class RoleUserController extends BaseController {
    @Autowired
    private RoleUserClient roleUserClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组添加人员")
    public Envelop createRoleUser(
            @ApiParam(name = "data_json",value = "角色组-用户关系Json串")
            @RequestBody String dataJson){
        MRoleUser mRoleUser = roleUserClient.createRoleUser(dataJson);
        if(null == mRoleUser){
            return failed("角色组添加人员失败！");
        }
        return success(null);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleUserId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除角色组的人员")
    public Envelop deleteRoleUser(
            @ApiParam(name = "id",value = "角色组-用户关系id")
            @PathVariable long id){
        boolean bo = roleUserClient.deleteRoleUser(id);
        if(bo){
            return success(null);
        }
        return failed("角色组删除人员失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色人员关系列表---分页")
    public Envelop searchRoleUser(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,userId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        List<RoleUserModel> roleUserModelList = new ArrayList<>();
        ResponseEntity<Collection<MRoleUser>> responseEntity = roleUserClient.searchRoleUser(fields, filters, sorts, size, page);
        Collection<MRoleUser> mRoleUsers  = responseEntity.getBody();
        for (MRoleUser m : mRoleUsers){
            RoleUserModel roleUserModel = changeToModel(m);
            roleUserModelList.add(roleUserModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(roleUserModelList,totalCount,page,size);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleUserNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色人员关系列表---不分页")
    public Envelop searchRoleUserNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RoleUserModel> roleUserModelList = new ArrayList<>();
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging(filters);
        for (MRoleUser m : mRoleUsers){
            RoleUserModel roleUserModel = changeToModel(m);
            roleUserModelList.add(roleUserModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(roleUserModelList);
        return envelop;
    }

    private RoleUserModel changeToModel(MRoleUser m) {
        RoleUserModel model = convertToModel(m, RoleUserModel.class);
        //获取角色组类别字典
        return model;
    }
}
