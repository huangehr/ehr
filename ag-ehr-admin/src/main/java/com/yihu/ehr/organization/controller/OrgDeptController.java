package com.yihu.ehr.organization.controller;

import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.org.OrgDeptDetailModel;
import com.yihu.ehr.agModel.org.OrgDeptMemberModel;
import com.yihu.ehr.agModel.org.OrgDeptModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.org.MOrgDept;
import com.yihu.ehr.model.org.MOrgDeptDetail;
import com.yihu.ehr.model.org.MOrgMemberRelation;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.organization.service.OrgDeptClient;
import com.yihu.ehr.organization.service.OrgDeptMemberClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/20.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "orgDept", description = "组织结构-部门管理接口，用于部门信息管理", tags = {"机构部门信息管理接口"})
public class OrgDeptController  extends BaseController {

    @Autowired
    private OrgDeptClient orgDeptClient;
    @Autowired
    private OrgDeptMemberClient orgDeptMemberClient;
    @Autowired
    private UserClient userClient;




    @ApiOperation(value = "获取所有部门列表")
    @RequestMapping(value = "/orgDept/getAllOrgDepts", method = RequestMethod.GET)
    public Envelop getAllOrgDepts() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(orgDeptClient.getAllOrgDepts() );
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @ApiOperation(value = "根据组织机构ＩＤ获取部门列表")
    @RequestMapping(value = "/orgDept/list", method = RequestMethod.GET)
    public Envelop getOrgDeptsByOrgId(
            @ApiParam(name = "orgId", value = "机构ID", defaultValue = "")
            @RequestParam(value = "orgId") String orgId) {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(orgDeptClient.searchOrgDepts(orgId));
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "根据父级部门ID获取子部门列表")
    @RequestMapping(value = "/orgDept/childs", method = RequestMethod.POST)
    public Envelop searchChildOrgDepts(
            @ApiParam(name = "parentDeptId", value = "父级部门ID" )
            @RequestParam(value = "parentDeptId") Integer parentDeptId) {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(orgDeptClient.searchChildOrgDepts(parentDeptId));
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/orgDept/detail" , method = RequestMethod.POST)
    @ApiOperation(value = "查询部门&科室详情")
    public Envelop detail(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    ){
        try {
            String errorMsg = "";

            if (deptId == null) {
                errorMsg+="部门不能为空！";
            }

            MOrgDept mOrgDeptNew = orgDeptClient.searchDeptDetail(deptId);
            if (mOrgDeptNew == null) {
                return failed("获取部门详情失败!");
            }
            return success(mOrgDeptNew);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/orgDept" , method = RequestMethod.POST)
    @ApiOperation(value = "新增机构部门")
    public Envelop create(
            @ApiParam(name = "orgDeptsJsonData", value = " 部门信息Json", defaultValue = "")
            @RequestParam(value = "orgDeptsJsonData", required = false) String orgDeptsJsonData){
        try {
            String errorMsg = "";
            OrgDeptModel orgDeptModel = objectMapper.readValue(orgDeptsJsonData, OrgDeptModel.class);
            MOrgDept mOrgDept = convertToModel(orgDeptModel, MOrgDept.class);

            if (StringUtils.isEmpty(mOrgDept.getCode())) {
                errorMsg+="部门代码不能为空！";
            }
            if (StringUtils.isEmpty(mOrgDept.getName())) {
                errorMsg+="部门不能为空！";
            }
            if (StringUtils.isEmpty(mOrgDept.getOrgId())) {
                errorMsg+="机构不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg))
            {
                return failed(errorMsg);
            }

            String mOrganizationJson = objectMapper.writeValueAsString(mOrgDept);
            MOrgDept mOrgDeptNew = orgDeptClient.saveOrgDept(mOrganizationJson);
            if (mOrgDeptNew == null) {
                return failed("保存失败!");
            }
            return success(mOrgDeptNew);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/orgDept" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改部门&科室详情")
    public Envelop update(
            @ApiParam(name = "orgDeptJsonData", value = " 部门信息Json", defaultValue = "")
            @RequestParam(value = "orgDeptJsonData", required = false) String orgDeptJsonData){
        try {
            String errorMsg = "";

            OrgDeptModel orgDeptModel = objectMapper.readValue(orgDeptJsonData, OrgDeptModel.class);
            OrgDeptDetailModel deptDetailModel = orgDeptModel.getDeptDetail();
            MOrgDeptDetail mOrgDeptDetail = convertToModel(deptDetailModel, MOrgDeptDetail.class);
            MOrgDept mOrgDept = convertToModel(orgDeptModel, MOrgDept.class);
            mOrgDept.setDeptDetail(mOrgDeptDetail);
            if (StringUtils.isEmpty(mOrgDept.getCode())) {
                errorMsg+="部门代码不能为空！";
            }
            if (StringUtils.isEmpty(mOrgDept.getName())) {
                errorMsg+="部门不能为空！";
            }
            if (StringUtils.isEmpty(mOrgDept.getOrgId())) {
                errorMsg+="机构不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg))
            {
                return failed(errorMsg);
            }

            String mOrganizationJson = objectMapper.writeValueAsString(mOrgDept);
            MOrgDept mOrgDeptNew = orgDeptClient.updateOrgDept(mOrganizationJson);
            if (mOrgDeptNew == null) {
                return failed("保存失败!");
            }
            return success(mOrgDeptNew);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/orgDept/checkDeptName" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查机构下部门名称是否唯一")
    public Envelop checkDeptName(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) Integer orgId,
            @ApiParam(name = "name", value = "新部门名称")
            @RequestParam(value = "name", required = true) String name
    ){
        try {
            Envelop envelop = new Envelop();
            String errorMsg = "";
            if (orgId == null) {
                envelop.setErrorMsg("机构不能为空！");
            }
            if (StringUtils.isEmpty(name)) {
                envelop.setErrorMsg("新部门名称不能为空！");
            }
            int num = orgDeptClient.getCountByDeptName(orgId, name);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("所在机构已经存在此部门!");
            }else{
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/orgDept/resetName" , method = RequestMethod.POST)
    @ApiOperation(value = "修改机构部们名称")
    public Envelop resetDeptName(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId,
            @ApiParam(name = "name", value = "新部门名称")
            @RequestParam(value = "name", required = true) String name
    ){
        try {
            String errorMsg = "";

            if (deptId == null) {
                errorMsg+="部门不能为空！";
            }
            if (StringUtils.isEmpty(name)) {
                errorMsg+="新部门名称不能为空！";
            }

            MOrgDept mOrgDeptNew = orgDeptClient.updateOrgDeptName(deptId, name);
            if (mOrgDeptNew == null) {
                return failed("修改部门失败!");
            }
            return success(mOrgDeptNew);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/orgDept/delete" , method = RequestMethod.POST)
    @ApiOperation(value = "删除机构部门")
    public boolean delete(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    ){
        try {
            boolean succ = orgDeptClient.deleteOrgDept(deptId);
            return succ;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/orgDept/checkMembers" , method = RequestMethod.POST)
    @ApiOperation(value = "机构部门下是否有成员存在")
    public boolean isHasMember(
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId ){
        try {
            boolean succ = orgDeptClient.isHasMember(deptId);
            return succ;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/orgDept/changeSort" , method = RequestMethod.POST)
    @ApiOperation(value = "部门排序")
    public boolean changeSort(
            @ApiParam(name = "preDeptId", value = "第一个部门ID")
            @RequestParam(value = "preDeptId", required = true) Integer preDeptId,
            @ApiParam(name = "afterDeptId", value = "第二个部门ID")
            @RequestParam(value = "afterDeptId", required = true) Integer afterDeptId){
        try {
            boolean succ = orgDeptClient.changeSort(preDeptId,afterDeptId);
            return succ;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }


    /* ****************************     部门成员相关   ********************************************/

    @RequestMapping(value = "/orgDeptMember/list", method = RequestMethod.POST)
    @ApiOperation(value = "获取部门下成员列表", notes = "根据查询条件获取部门成员列表在前端表格展示")
    public Envelop searchOrgDeptMembers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,userName,dutyName")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+deptId")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        try {
            List<OrgDeptMemberModel> orgMemberModels = new ArrayList<>();
            ResponseEntity<List<MOrgMemberRelation>> responseEntity = orgDeptMemberClient.searchOrgDeptMembers(fields, filters, sorts, size, page);
            List<MOrgMemberRelation> members = responseEntity.getBody();
            for (MOrgMemberRelation deptMember : members) {
                OrgDeptMemberModel memberModel = convertToModel(deptMember,OrgDeptMemberModel.class);
//                if (StringUtils.isNotEmpty(memberModel.getUserId()) ){
//                    MUser mUser = userClient.getUser(memberModel.getUserId());
//                    memberModel.setUserName(mUser == null ? "" : mUser.getRealName());
//                }
//                if (memberModel.getDeptId()!=null && memberModel.getDeptId()!=0 ){
//                    MOrgDept mOrgDept = orgDeptClient.searchDeptDetail(memberModel.getDeptId());
//                    memberModel.setDeptName(mOrgDept == null ? "" : mOrgDept.getName());
//                }
                orgMemberModels.add(memberModel);
            }
            int totalCount = getTotalCount(responseEntity);
            return getResult(orgMemberModels, totalCount, page, size);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @ApiOperation(value = "获取所有成员列表")
    @RequestMapping(value = "/orgDeptMember/getAllOrgDeptMember", method = RequestMethod.GET)
    public Envelop getAllOrgDeptMember() {
        try {
            Envelop envelop = new Envelop();
            List<MOrgMemberRelation> memberRelationList = new ArrayList<MOrgMemberRelation>();
            ResponseEntity<List<MOrgMemberRelation>> responseEntity = orgDeptMemberClient.getAllOrgDeptMember();
            memberRelationList = responseEntity.getBody();
            if (memberRelationList !=null && memberRelationList.size() > 0 ){
                for (MOrgMemberRelation  memberRelation : memberRelationList){
                    if (StringUtils.isNotEmpty(memberRelation.getUserId() ) && StringUtils.isEmpty(memberRelation.getUserName() ) ) {
                        MUser mUser = userClient.getUser(memberRelation.getUserId());
                        memberRelation.setUserName(mUser == null ? "" : mUser.getRealName());
                    }
                }
            }
            envelop.setDetailModelList( memberRelationList );
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/orgDept/checkUser" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查机构下用户是否唯一")
    public Envelop checkUser(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) Integer orgId,
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId", required = true) String userId
    ){
        try {
            Envelop envelop = new Envelop();
            String errorMsg = "";
            if (orgId == null) {
                errorMsg+="机构不能为空！";
                envelop.setErrorMsg(errorMsg);
            }
            if (StringUtils.isEmpty(userId)) {
                errorMsg+="用户不能为空！";
                envelop.setErrorMsg(errorMsg);
            }
            int num = orgDeptClient.getCountByUserId(orgId, userId);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("所在机构已经存在此用户!");
            }else{
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "orgDeptMember/admin/{memRelationId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取部门成员信息", notes = "部门成员信息")
    public Envelop getOrgMemberRelation(
            @ApiParam(name = "memRelationId", value = "", defaultValue = "")
            @PathVariable(value = "memRelationId") Long memRelationId) {
        try {
            MOrgMemberRelation mOrgMemberRelation = orgDeptMemberClient.getOrgMemberRelation(memRelationId);
            if (mOrgMemberRelation == null) {
                return failed("提醒消息信息获取失败!");
            }
            OrgDeptMemberModel detailModel = convertToModel(mOrgMemberRelation, OrgDeptMemberModel.class);
            if (StringUtils.isNotEmpty(detailModel.getUserId()) ){
                MUser mUser = userClient.getUser(detailModel.getUserId());
                detailModel.setUserName(mUser == null ? "" : mUser.getRealName());
            }
            if (StringUtils.isNotEmpty(detailModel.getParentUserId()) ){
                MUser mUser = userClient.getUser(detailModel.getParentUserId());
                detailModel.setParentUserName(mUser == null ? "" : mUser.getRealName());
            }
            if (detailModel.getDeptId()!=null && detailModel.getDeptId()!=0 ){
                MOrgDept mOrgDept = orgDeptClient.searchDeptDetail(detailModel.getDeptId());
                detailModel.setDeptName(mOrgDept == null ? "" : mOrgDept.getName());
            }
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }


    /**
     * 创建部门成员
     * @param memberRelationJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orgDeptMember" , method = RequestMethod.POST)
    @ApiOperation(value = "新增部门成员")
    public Envelop createDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = " 部门成员信息Json", defaultValue = "")
            @RequestParam(value = "memberRelationJsonData", required = false) String memberRelationJsonData){
        try {
            String errorMsg = "";
            OrgDeptMemberModel deptMemberModel = objectMapper.readValue(memberRelationJsonData, OrgDeptMemberModel.class);
            MOrgMemberRelation mDeptMember = convertToModel(deptMemberModel, MOrgMemberRelation.class);
            if (StringUtils.isEmpty(mDeptMember.getOrgId())) {
                errorMsg+="机构不能为空！";
            }
            if (StringUtils.isEmpty(mDeptMember.getUserId())) {
                errorMsg+="用户不能为空！";
            }
            if (mDeptMember.getDeptId() == null){
                errorMsg+="部门不能为空！";
            }

            if(StringUtils.isNotEmpty(errorMsg))
            {
                return failed(errorMsg);
            }

            MUser mUser = userClient.getUser(mDeptMember.getUserId());
            mDeptMember.setUserName(mUser == null ? "" : mUser.getRealName());

            MUser mUserp = userClient.getUser(mDeptMember.getParentUserId());
            mDeptMember.setParentUserName(mUserp == null ? "" : mUserp.getRealName());

            MOrgDept mOrgDept = orgDeptClient.searchDeptDetail(mDeptMember.getDeptId());
            mDeptMember.setDeptName(mOrgDept == null ? "" : mOrgDept.getName());

            String deptMemberJsonStr = objectMapper.writeValueAsString(mDeptMember);
            MOrgMemberRelation deptMember = orgDeptMemberClient.saveOrgDeptMember(deptMemberJsonStr);
            if (deptMember == null) {
                return failed("保存失败!");
            }
            return success(deptMember);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    /**
     *  TODO 重复接口，视情况删除
     * 创建部门成员
     * @param memberRelationJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/orgDeptMember" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改部门成员")
    public Envelop updateDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = " 部门成员信息Json", defaultValue = "")
            @RequestParam(value = "memberRelationJsonData", required = false) String memberRelationJsonData){
        try {
            String errorMsg = "";
            OrgDeptMemberModel deptMemberModel = objectMapper.readValue(memberRelationJsonData, OrgDeptMemberModel.class);
            MOrgMemberRelation mDeptMember = convertToModel(deptMemberModel, MOrgMemberRelation.class);
             if (StringUtils.isEmpty(mDeptMember.getOrgId())) {
                errorMsg+="机构不能为空！";
            }
            if (StringUtils.isEmpty(mDeptMember.getUserId())) {
                errorMsg+="用户不能为空！";
            }
            if (mDeptMember.getDeptId() == null){
                errorMsg+="部门不能为空！";
            }

            if(StringUtils.isNotEmpty(errorMsg))
            {
                return failed(errorMsg);
            }
            MUser mUser = userClient.getUser(mDeptMember.getUserId());
            mDeptMember.setUserName(mUser == null ? "" : mUser.getRealName());

            MUser mUserp = userClient.getUser(mDeptMember.getParentUserId());
            mDeptMember.setParentUserName(mUserp == null ? "" : mUserp.getRealName());

            MOrgDept mOrgDept = orgDeptClient.searchDeptDetail(mDeptMember.getDeptId());
            mDeptMember.setDeptName(mOrgDept == null ? "" : mOrgDept.getName());

            String deptMemberJsonStr = objectMapper.writeValueAsString(mDeptMember);
            MOrgMemberRelation deptMember = orgDeptMemberClient.saveOrgDeptMember(deptMemberJsonStr);
            if (deptMember == null) {
                return failed("保存失败!");
            }
            return success(deptMember);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/orgDeptMember/delete" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构部门成员")
    public Envelop deleteDeptMember(
            @ApiParam(name = "memberRelationId", value = "部门成员关系ID")
            @RequestParam(value = "memberRelationId", required = true) Integer memberRelationId
    ){
        try {
            boolean succ = orgDeptMemberClient.deleteOrgDeptMember(memberRelationId);
            if (!succ) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    /**
     * 更新成员状态
     * @param memberRelationId
     * @return
     */
    @RequestMapping(value = "/orgDeptMember/updateStatus" , method = RequestMethod.PUT)
    @ApiOperation(value = "更新成员状态")
    public boolean updateDeptMemberStatus(
            @ApiParam(name = "memberRelationId", value = "memberRelationId", defaultValue = "")
            @RequestParam(value = "memberRelationId") Integer memberRelationId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") Integer status) {
        try {
            return orgDeptMemberClient.updateStatusOrgDeptMember(memberRelationId, status);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }



    
}
