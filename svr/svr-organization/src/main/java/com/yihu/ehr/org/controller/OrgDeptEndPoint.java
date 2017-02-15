package com.yihu.ehr.org.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.org.MOrgDept;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.service.OrgDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class OrgDeptEndPoint  extends EnvelopRestEndPoint {

    @Autowired
    private OrgDeptService orgDeptService;

    @RequestMapping(value = "/orgDept/list", method = RequestMethod.POST)
    @ApiOperation(value = "根据条件查询机构下的部门列表")
    public List<MOrgDept> searchOrgDepts(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) String orgId) throws Exception {
        List<OrgDept> orgDepts = orgDeptService.searchByOrgId(orgId);
        return  (List<MOrgDept>) convertToModels(orgDepts, new ArrayList<MOrgDept>(orgDepts.size()), MOrgDept.class, null);
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


}
