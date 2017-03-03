package com.yihu.ehr.organization.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrgDept;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
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
public interface OrgDeptClient {

    @RequestMapping(value = "/orgDept/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件查询机构下的部门列表")
    List<MOrgDept> searchOrgDepts(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) String orgId);


    @RequestMapping(value = "/orgDept/childs", method = RequestMethod.POST)
    @ApiOperation(value = "根据父级部门ID查询其下的子部门列表")
    List<MOrgDept> searchChildOrgDepts(
            @RequestParam(value = "parentDeptId", required = true) Integer parentDeptId
    );


    @RequestMapping(value = "/orgDept/detail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询部门&科室详情")
    public MOrgDept searchDeptDetail(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    );

    @RequestMapping(value = "/orgDept", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增部门")
    MOrgDept saveOrgDept(
            @ApiParam(name = "deptJsonData", value = "新增部门json信息")
            @RequestBody String deptJsonData
    ) ;

    @RequestMapping(value = "/orgDept", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门&科室详情")
    public MOrgDept updateOrgDept(
            @ApiParam(name = "deptJsonData", value = "部门&科室详情json信息")
            @RequestBody String deptJsonData
    ) ;

    @RequestMapping(value = "/orgDept/resetName", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门名称")
    MOrgDept updateOrgDeptName(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId,
            @ApiParam(name = "name", value = "新部门名称")
            @RequestParam(value = "name", required = true) String name
    );

    @RequestMapping(value = "/orgDept/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除部门" ,notes = "可以根据前端需求修改该接口，添加判断部门下是否有成员")
    boolean deleteOrgDept(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    ) ;

    @RequestMapping(value = "/orgDept/checkMembers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询部门下是否有成员" ,notes = "检查部门下是否有成员存在")
    boolean isHasMember(
            @RequestParam(value = "deptId", required = true) Integer deptId
    ) ;

    @RequestMapping(value = "/orgDept/changeSort", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门的排序" ,notes = "修改部门的排序")
    boolean changeSort(
            @ApiParam(name = "preDeptId", value = "第一个部门ID")
            @RequestParam(value = "preDeptId", required = true) Integer preDeptId,
            @ApiParam(name = "afterDeptId", value = "第二个部门ID")
            @RequestParam(value = "afterDeptId", required = true) Integer afterDeptId
    );

}
