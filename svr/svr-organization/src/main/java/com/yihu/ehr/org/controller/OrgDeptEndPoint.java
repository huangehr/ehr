package com.yihu.ehr.org.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.org.MOrgDept;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.service.OrgDeptService;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "orgDept", description = "组织机构部门管理服务", tags = {"部门管理"})
public class OrgDeptEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgDeptService orgDeptService;
    @Autowired
    private OrgMemberRelationService relationService;

    @RequestMapping(value = "/orgDept/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件查询机构下的部门列表")
    public List<MOrgDept> searchOrgDepts(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) String orgId) throws Exception {
        List<OrgDept> orgDepts = orgDeptService.searchByOrgId(orgId);
        return (List<MOrgDept>) convertToModels(orgDepts, new ArrayList<MOrgDept>(orgDepts.size()), MOrgDept.class, null);
    }


    @RequestMapping(value = "/orgDept/childs", method = RequestMethod.POST)
    @ApiOperation(value = "根据父级部门ID查询其下的子部门列表")
    public List<MOrgDept> searchChildOrgDepts(
            @ApiParam(name = "parentDeptId", value = "父级部门ID")
            @RequestParam(value = "parentDeptId", required = true) Integer parentDeptId
    ) throws Exception {
        List<OrgDept> orgDepts = orgDeptService.searchByParentId(parentDeptId);
        return (List<MOrgDept>) convertToModels(orgDepts, new ArrayList<MOrgDept>(orgDepts.size()), MOrgDept.class, null);
    }


    @RequestMapping(value = "/orgDept", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增部门")
    public MOrgDept saveOrgDept(
            @ApiParam(name = "deptJsonData", value = "新增部门json信息")
            @RequestBody String deptJsonData
    )  {
        try {
            OrgDept dept = toEntity(deptJsonData, OrgDept.class);
            dept = orgDeptService.saveOrgDept(dept);
            return convertToModel(dept, MOrgDept.class);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/orgDept", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门")
    public MOrgDept updateOrgDept(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId,
            @ApiParam(name = "name", value = "新部门名称")
            @RequestParam(value = "name", required = true) String name
    ) throws Exception {
        OrgDept dept = orgDeptService.updateOrgDept(deptId, name);
        return convertToModel(dept, MOrgDept.class);
    }

    @RequestMapping(value = "/orgDept/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除部门" ,notes = "可以根据前端需求修改该接口，添加判断部门下是否有成员")
    public boolean deleteOrgDept(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    ) throws Exception {
        orgDeptService.deleteOrgDept(deptId);
        return true;
    }

    @RequestMapping(value = "/orgDept/changeSort", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询部门下是否有成员" ,notes = "可以根据前端需求修改该接口，和删除部门接口合并")
    public boolean changeSort(
            @ApiParam(name = "preDeptId", value = "第一个部门ID")
            @RequestParam(value = "preDeptId", required = true) Integer preDeptId,
            @ApiParam(name = "afterDeptId", value = "第二个部门ID")
            @RequestParam(value = "afterDeptId", required = true) Integer afterDeptId
    ) throws Exception {
        orgDeptService.changeOrgDeptSort(preDeptId,afterDeptId);
        return true;
    }


    @RequestMapping(value = "/orgDept/checkMembers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询部门下是否有成员" ,notes = "可以根据前端需求修改该接口，和删除部门接口合并")
    public boolean isHasMember(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    ) throws Exception {
        boolean succ = relationService.hadMemberRelation(deptId);
        return succ;
    }



}
