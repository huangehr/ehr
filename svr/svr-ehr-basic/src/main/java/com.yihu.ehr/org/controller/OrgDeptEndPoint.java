package com.yihu.ehr.org.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.org.MOrgDept;
import com.yihu.ehr.model.org.MOrgDeptDetail;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgDeptDetail;
import com.yihu.ehr.org.service.OrgDeptDetailService;
import com.yihu.ehr.org.service.OrgDeptService;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import com.yihu.ehr.util.file.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "orgDept", description = "组织机构部门管理服务", tags = {"机构管理-部门管理服务"})
public class OrgDeptEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgDeptService orgDeptService;
    @Autowired
    private OrgMemberRelationService relationService;
    @Autowired
    private OrgDeptDetailService deptDetailService;

    @RequestMapping(value = "/orgDept/getAllOrgDepts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有部门列表")
    public List<MOrgDept> getAllOrgDepts() throws Exception {
        String filters = "delFlag=0";
        String sorts = "+sortNo";
        List<OrgDept> orgDepts = orgDeptService.search(filters,sorts);
        return (List<MOrgDept>) convertToModels(orgDepts, new ArrayList<MOrgDept>(orgDepts.size()), MOrgDept.class, null);
    }

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

    @RequestMapping(value = "/orgDept/detail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询部门&科室详情")
    public MOrgDept searchDeptDetail(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    ) throws Exception {
        OrgDept dept = orgDeptService.searchBydeptId(deptId);
        MOrgDept mOrgDept = convertToModel(dept, MOrgDept.class);
        if (dept != null) {
            OrgDeptDetail orgDeptDetail = deptDetailService.searchByDeptId(Integer.valueOf(dept.getId()));
            MOrgDeptDetail mOrgDeptDetail = convertToModel(orgDeptDetail, MOrgDeptDetail.class);
            mOrgDept.setDeptDetail(mOrgDeptDetail);
        }
        return mOrgDept;
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
    @ApiOperation(value = "修改部门&科室详情")
    public MOrgDept updateOrgDept(
            @ApiParam(name = "deptJsonData", value = "部门&科室详情json信息")
            @RequestBody String deptJsonData
    )  {
        try {
            OrgDept dept = toEntity(deptJsonData, OrgDept.class);
            dept = orgDeptService.updateDept(dept);
            return convertToModel(dept, MOrgDept.class);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/orgDept/getCountByDeptName", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "检查机构下部门相同名称的个数")
    public int getCountByDeptName(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) Integer orgId,
            @ApiParam(name = "name", value = "部门名称")
            @RequestParam(value = "name", required = true) String name
    ) throws Exception {
        return orgDeptService.getCountByOrgIdAndName(orgId.toString(), name);
    }

    @RequestMapping(value = "/orgDept/resetName", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门名称")
    public MOrgDept resetDeptName(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId,
            @ApiParam(name = "name", value = "新部门名称")
            @RequestParam(value = "name", required = true) String name
    ) throws Exception {
        OrgDept dept = orgDeptService.updateOrgDeptName(deptId, name);
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
    @ApiOperation(value = "部门排序交换" ,notes = "交换两个部门的排序位置")
    public boolean changeSort(
            @ApiParam(name = "preDeptId", value = "第一个部门ID")
            @RequestParam(value = "preDeptId", required = true) Integer preDeptId,
            @ApiParam(name = "afterDeptId", value = "第二个部门ID")
            @RequestParam(value = "afterDeptId", required = true) Integer afterDeptId
    ) throws Exception {
        orgDeptService.changeOrgDeptSort(preDeptId, afterDeptId);
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


    /* ****************************************** excel 导入数据相关 *******************************************************  */
    //TODO  无用可去除
    @RequestMapping(value = "/orgDept/importDept", method = RequestMethod.POST)
    @ApiOperation(value = "导入部门信息", notes = "通过Excel文件导入部门信息到数据库(包含父部门ＩＤ）")
    public List<Map<Object,Object>> importDeptByExcel(
            @ApiParam(name = "pack", value = "部门数据excel文件", allowMultiple = true)
            @RequestPart() MultipartFile pack) throws Exception {

        List<Map<Object,Object>> list = ExcelUtils.readExcel(pack.getInputStream(),pack.getOriginalFilename());
        boolean b = orgDeptService.importDataByExcel(list);
        System.out.println(list.toString());
        return list;
    }

    @RequestMapping(value = "/orgDept/importDept2", method = RequestMethod.POST)
    @ApiOperation(value = "导入部门信息", notes = "通过Excel文件导入部门信息到数据库；excel头内容，orgId,parentName,name")
    public List<Map<Object,Object>> importDeptByExcel2(
            @ApiParam(name = "pack", value = "部门数据excel文件", allowMultiple = true)
            @RequestPart() MultipartFile pack) throws Exception {

        List<Map<Object,Object>> list = ExcelUtils.readExcel(pack.getInputStream(),pack.getOriginalFilename());
        boolean b = orgDeptService.importDataByExce2l(list);
        System.out.println(list.toString());
        return list;
    }
    @RequestMapping(value =ServiceApi.Org.getUserOrglistByUserId, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据用户ＩＤ获取部门列表")
    public List<String> getUserOrglistByUserId(
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId", required = true) String userId) throws Exception {
        List<String> orgDepts = orgDeptService.searchByUserId(userId);
        return orgDepts;
    }



}
