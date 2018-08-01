package com.yihu.ehr.basic.org.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.basic.user.service.UserService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.org.MJkzlOrgMemberRelation;
import com.yihu.ehr.model.org.MOrgDeptData;
import com.yihu.ehr.model.org.MOrgDeptJson;
import com.yihu.ehr.model.org.MOrgMemberRelation;
import com.yihu.ehr.basic.org.model.OrgMemberRelation;
import com.yihu.ehr.basic.org.service.OrgMemberRelationService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/16.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "orgDeptMember", description = "组织机构部门成员管理服务", tags = {"机构管理-部门成员管理"})
public class OrgMemberRelationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgMemberRelationService relationService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrgMemberRelationService orgMemberRelationService;

    @RequestMapping(value = "/orgDeptMember/getAllOrgDeptMember", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有成员列表")
    public List<MOrgMemberRelation> getAllOrgDeptMember(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters
    ) throws Exception {
        filters= filters +";status=0";
        List<OrgMemberRelation> orgMemberRelations = relationService.search(filters);
        return (List<MOrgMemberRelation>) convertToModels(orgMemberRelations, new ArrayList<MOrgMemberRelation>(orgMemberRelations.size()), MOrgMemberRelation.class, null);
    }

    @RequestMapping(value = "/orgDeptMember/getAllOrgDeptMemberDistinct", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有成员列表去重复")
    public List<MOrgMemberRelation> getAllOrgDeptMemberDistinct(
            @ApiParam(name = "orgId", value = "机构ID", defaultValue = "")
            @RequestParam(value = "orgId",required = false) String orgId,
            @ApiParam(name = "searchNm", value = "关键字查询", defaultValue = "")
            @RequestParam(value = "searchNm",required = false) String searchNm
    ) throws Exception {
        List<OrgMemberRelation> orgMemberRelations = relationService.getAllOrgDeptMemberDistinct(orgId,searchNm);
        return (List<MOrgMemberRelation>) convertToModels(orgMemberRelations, new ArrayList<MOrgMemberRelation>(orgMemberRelations.size()), MOrgMemberRelation.class, null);
    }

    @RequestMapping(value = "/orgDeptMember/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件 查询部门下成员列表")
    public List<MOrgMemberRelation> searchOrgDeptMembers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,deptId,deptName,dutyName,userName")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "+userName,+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        List<OrgMemberRelation> orgMemRelations = relationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, relationService.getCount(filters), page, size);
        return (List<MOrgMemberRelation>) convertToModels(orgMemRelations, new ArrayList<MOrgMemberRelation>(orgMemRelations.size()), MOrgMemberRelation.class, fields);
    }

    @RequestMapping(value = "/orgDeptMember/getOrgDeptMembers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件 查询机构成员列表去重复")
    public List<MOrgMemberRelation> getOrgDeptMembers(
            @ApiParam(name = "orgId", value = "机构ID", defaultValue = "")
            @RequestParam(value = "orgId", required = false) String orgId,
            @ApiParam(name = "searchParm", value = "关键字查询", defaultValue = "")
            @RequestParam(value = "searchParm",required = false) String searchParm,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        List<OrgMemberRelation> orgMemRelations = relationService.getOrgDeptMembers(orgId,searchParm,size,page);
        Long totalCount = Long.parseLong(relationService.getOrgDeptMembersInt(orgId,searchParm).toString());
        pagedResponse(request,response,totalCount,page,size);
        return (List<MOrgMemberRelation>) convertToModels(orgMemRelations, new ArrayList<MOrgMemberRelation>(orgMemRelations.size()), MOrgMemberRelation.class,"userId,userName");
    }

    @RequestMapping(value ="orgDeptMember/admin/{memRelationId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取部门成员信息")
    public MOrgMemberRelation getMessageRemindInfo(
            @ApiParam(name = "memRelationId", value = "", defaultValue = "")
            @PathVariable(value = "memRelationId") Long memRelationId) {
        OrgMemberRelation orgMemberRelation = relationService.getOrgMemberRelation(memRelationId);
        MOrgMemberRelation mOrgMemberRelation   = convertToModel(orgMemberRelation, MOrgMemberRelation.class);
        return mOrgMemberRelation;
    }


    @RequestMapping(value = "/orgDeptMember", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增部门成员")
    public MOrgMemberRelation saveOrgDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = "新增部门成员信息")
            @RequestBody String memberRelationJsonData
    ) throws Exception {
        OrgMemberRelation memberRelation = toEntity(memberRelationJsonData, OrgMemberRelation.class);
        memberRelation.setStatus(0);
        relationService.save(memberRelation);
        return convertToModel(memberRelation, MOrgMemberRelation.class);
    }

    @RequestMapping(value = "/updateOrgDeptMemberParent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门成员上级成员")
    public boolean updateOrgDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = "修改部门成员信息")
            @RequestBody String memberRelationJsonData
    ) throws Exception {
        OrgMemberRelation memberRelation = toEntity(memberRelationJsonData, OrgMemberRelation.class);
        relationService.updateOrgDeptMemberParent(memberRelation);
        return true;
    }

    @RequestMapping(value = "/orgDeptMember/updateStatus", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门成员状态" )
    public boolean updateStatusOrgDeptMember(
            @ApiParam(name = "memberRelationId", value = "部门成员ID")
            @RequestParam(value = "memberRelationId", required = true) Integer memberRelationId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") int status
    ) throws Exception {
        relationService.updateStatusDeptMember(memberRelationId,status);
        return true;
    }


    @RequestMapping(value = "/orgDeptMember/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除部门成员" )
    public boolean deleteOrgDeptMember(
            @ApiParam(name = "memberRelationId", value = "部门成员ID")
            @RequestParam(value = "memberRelationId", required = true) Integer memberRelationId
    ) throws Exception {

        relationService.deleteDeptMember(memberRelationId);
        return true;
    }

    @RequestMapping(value = "/orgDeptMember/getCountByUserId", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "检查机构下部门相同用户的个数")
    public int getCountByUserId(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) Integer orgId,
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId", required = true) String userId,
            @ApiParam(name = "deptId", value = "部门ID")
            @RequestParam(value = "deptId", required = true) Integer deptId
    ) throws Exception {
        return relationService.getCountByOrgIdAndUserId(orgId.toString(), userId, deptId);
    }

    @RequestMapping(value = "/orgDeptMember/getOrgIds", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取orgId列表")
    public List<String> getOrgIds(String userId) {
        List<String> list = relationService.getOrgIds(userId);
        return list;
    }

    @RequestMapping(value = "/orgDeptMember/getDeptIds", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取DeptId列表")
    public List<Integer> getDeptIds(String userId) {
        List<Integer> list = relationService.getDeptIds(userId);
        return list;
    }

    @RequestMapping(value = "/orgDeptMember/getByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取orgDeptJson列表")
    public List<MOrgDeptJson> getByUserId(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam(value = "userId") String userId) {
        List<OrgMemberRelation> memberRelationList = relationService.getByUserId(userId);
        List<MOrgDeptJson> list = new ArrayList<>();
        for (OrgMemberRelation r : memberRelationList) {
            MOrgDeptJson orgDeptJson = new MOrgDeptJson();
            orgDeptJson.setOrgId(r.getOrgId());
            orgDeptJson.setDeptIds(r.getDeptId() + "");
            list.add(orgDeptJson);
        }
        return list;
    }
    @RequestMapping(value = ServiceApi.Users.GetOrgAndDeptRelation, method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取机构及部门列表")
    public List<MOrgDeptData> getOrgAndDeptRelation(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam(value = "userId") String userId) {
        List<OrgMemberRelation> memberRelationList = relationService.getByUserId(userId);
        List<MOrgDeptData> list = new ArrayList<>();
        Map<String,MOrgDeptData> mOrgDeptDataMap = new HashMap<>();
        //将机构与部门实装到 MOrgDeptData 对象中
        for (OrgMemberRelation r : memberRelationList) {
            //机构
            MOrgDeptData mOrg =new MOrgDeptData();
            mOrg.setId(Integer.valueOf(r.getOrgId()));
            mOrg.setName(r.getOrgName());
            //若存在该机构，则判断是否存在部门，若部门存在则不做处理，否则添加部门。
            if(null != mOrgDeptDataMap.get(r.getOrgId())){
                //部门
                MOrgDeptData mOrgDept =new MOrgDeptData();
                mOrgDept.setId(r.getDeptId());
                mOrgDept.setName(r.getDeptName());
                List<MOrgDeptData> childrenList = mOrgDeptDataMap.get(r.getOrgId()).getChildren();
                //JAVA 8直接用流的方法将list转换成map
                Map<Integer, MOrgDeptData> mapOrgDeptData = childrenList.stream().collect(Collectors.toMap(MOrgDeptData::getId, (p) -> p));
                if(null == mapOrgDeptData.get(mOrgDept.getId())){
                    childrenList.add(mOrgDept);
                }
            }else{
                //部门
                MOrgDeptData mOrgDept = new MOrgDeptData();
                mOrgDept.setId(r.getDeptId());
                mOrgDept.setName(r.getDeptName());
                List<MOrgDeptData> childrenList = new ArrayList<>();
                childrenList.add(mOrgDept);
                mOrg.setChildren(childrenList);
                //该机构不存在，则添加该机构
                mOrgDeptDataMap.put(r.getOrgId(),mOrg);
            }
        }
        Collection<MOrgDeptData> mOrgDeptDataCollection = mOrgDeptDataMap.values();
        list = new ArrayList<MOrgDeptData>(mOrgDeptDataCollection);
        return list;
    }


    @RequestMapping(value ="orgDeptMember/admin/listOrgDeptMemberByOrgIdAndDeptId", method = RequestMethod.GET)
    @ApiOperation(value = "根据部门id获取部门成员信息")
    public List<MOrgMemberRelation> listOrgDeptMemberByOrgIdAndDeptId(
            @ApiParam(name = "deptId", value = "", defaultValue = "")
            @RequestParam(value = "deptId") Integer deptId) {
        List<OrgMemberRelation> orgMemberRelations  = relationService.getOrgMemberByDeptId(deptId);
        return (List<MOrgMemberRelation>) convertToModels(orgMemberRelations, new ArrayList<MOrgMemberRelation>(orgMemberRelations.size()), MOrgMemberRelation.class, null);
    }

    @RequestMapping(value = "/orgDeptMember/getJkzlOrgIds", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取总部orgId列表")
    public Envelop getJkzlOrgIds(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "loginCode", value = "用户登录账号")
            @RequestParam(value = "loginCode", required = false) String loginCode) throws Exception{
        Envelop envelop = new Envelop();
        String seaUserId="";
        if(StringUtils.isNotEmpty(userId)){
            seaUserId = userId;
        } else if(StringUtils.isNotEmpty(loginCode)){
             List<User> users = userService.getUserForLogin(loginCode);
             if(null != users && users.size()>0){
                 seaUserId = users.get(0).getId();
             }else{
                 envelop.setErrorMsg("用户不存在！");
                 envelop.setSuccessFlg(false);
                 return envelop;
             }
        }else {
            envelop.setErrorMsg("用户id和登录账户不能同时为空！");
            envelop.setSuccessFlg(false);
            return envelop;
        }
        //医生在总部库中的对应关系
        MJkzlOrgMemberRelation mJkzlOrgMemberRelation = new MJkzlOrgMemberRelation();
        List<OrgMemberRelation> memberRelationList = relationService.getByUserId(seaUserId);
        if(null != memberRelationList && memberRelationList.size()>0){
            OrgMemberRelation  orgMemberRelation = memberRelationList.get(0);
            mJkzlOrgMemberRelation.setJkzlDoctorSn(orgMemberRelation.getJkzlDoctorSn());
            mJkzlOrgMemberRelation.setJkzlDoctorUid(orgMemberRelation.getJkzlDoctorUid());
            mJkzlOrgMemberRelation.setJkzlHosDeptId(orgMemberRelation.getJkzlHosDeptId());
            mJkzlOrgMemberRelation.setJkzlUserId(orgMemberRelation.getJkzlUserId());
        }
        String jkzlOrgId = relationService.getJkzlOrgIds(seaUserId);
        mJkzlOrgMemberRelation.setJkzlHosId(jkzlOrgId);
        envelop.setObj(mJkzlOrgMemberRelation);
        envelop.setSuccessFlg(true);
        return envelop;
    }


    @RequestMapping(value = "/orgDeptMember/getOrgDepts", method = RequestMethod.GET)
    @ApiOperation(value = "根据userId获取总部orgId列表")
    public Envelop getOrgDepts(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam(value = "userId", required = false) String userId) throws Exception{
        Envelop envelop = new Envelop();

        List<OrgMemberRelation> orgMemberRelations = new ArrayList<>();
        orgMemberRelations = orgMemberRelationService.getByUserId(userId);
        if(orgMemberRelations != null && orgMemberRelations.size()>0){
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(orgMemberRelations);
        }else {
            envelop.setSuccessFlg(true);
        }
        return envelop;
    }

}
