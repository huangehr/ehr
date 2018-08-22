package com.yihu.ehr.basic.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.basic.getui.ConstantUtil;
import com.yihu.ehr.basic.org.model.OrgDept;
import com.yihu.ehr.basic.org.model.OrgMemberRelation;
import com.yihu.ehr.basic.org.model.Organization;
import com.yihu.ehr.basic.org.service.OrgDeptService;
import com.yihu.ehr.basic.org.service.OrgMemberRelationService;
import com.yihu.ehr.basic.org.service.OrgService;
import com.yihu.ehr.basic.patient.service.DemographicService;
import com.yihu.ehr.basic.user.entity.Doctors;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.basic.user.service.DoctorService;
import com.yihu.ehr.basic.user.service.RoleUserService;
import com.yihu.ehr.basic.user.service.RolesService;
import com.yihu.ehr.basic.user.service.UserService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.patient.DemographicInfo;
import com.yihu.ehr.model.org.MOrgDeptJson;
import com.yihu.ehr.model.user.MDoctor;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.phonics.PinyinUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 2017-02-04 add  by hzp
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "doctors", description = "医生管理接口", tags = {"基础信息-医生管理"})
public class DoctorEndPoint extends EnvelopRestEndPoint {

    Logger logger = LoggerFactory.getLogger(DoctorEndPoint.class);

    @Autowired
    DoctorService doctorService;
    @Autowired
    private UserService userManager;
    @Autowired
    private OrgDeptService orgDeptService;
    @Autowired
    private OrgMemberRelationService relationService;
    @Value("${default.password}")
    private String default_password = "12345678";
    @Autowired
    private DemographicService demographicService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private RoleUserService roleUserService;

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生列表", notes = "根据查询条件获取医生列表在前端表格展示")
    public List<MDoctor> searchDoctors(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        List<Doctors> doctorsList = new ArrayList<>();
        if (!StringUtils.isEmpty(orgCode)) {
            String[] orgCodes = orgCode.split(",");
            String filter = "";
            if (!StringUtils.isEmpty(filters)) {
                filter = filters.substring(filters.indexOf("?") + 1, filters.indexOf(";"));
            }
            doctorsList = doctorService.searchDoctors(filter, orgCodes, page, size);
            Long totalCount = doctorService.getDoctorsCount(filter, orgCodes);
            pagedResponse(request, response, totalCount, page, size);
        } else {
            doctorsList = doctorService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, doctorService.getCount(filters), page, size);
        }
        for (Doctors doctors : doctorsList) {
            User user = userManager.getUserByDoctorId(doctors.getId().toString());
            if (user != null) {
                doctors.setUserId(user.getId());
            }
        }

        return (List<MDoctor>) convertToModels(doctorsList, new ArrayList<MDoctor>(doctorsList.size()), MDoctor.class, fields);
    }

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "创建医生", notes = "创建医生信息")
    public MDoctor createDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData,
            @ApiParam(name = "model", value = "所属机构部门关系", defaultValue = "")
            @RequestParam("model") String model) throws Exception {

        List<MOrgDeptJson> orgDeptJsonList = objectMapper.readValue(model, new TypeReference<List<MOrgDeptJson>>() {
        });
        MOrgDeptJson mOrgDeptJson = orgDeptJsonList.get(0);
        Organization organization = orgService.getOrgById(mOrgDeptJson.getOrgId());
        OrgDept orgDept = orgDeptService.searchBydeptId(Integer.parseInt(mOrgDeptJson.getDeptIds().split(",")[0]));
        Doctors doctor = toEntity(doctoJsonData, Doctors.class);
        doctor.setStatus("1");
        doctor.setPyCode(PinyinUtil.getPinYinHeadChar(doctor.getName(), false));
        doctor.setOrgId(organization.getId().toString());
        doctor.setOrgCode(organization.getOrgCode());
        doctor.setOrgFullName(organization.getFullName());
        doctor.setDeptName(orgDept.getName());
        doctor.setInsertTime(new Date());
        Doctors d = doctorService.save(doctor);

        String idCardNo = d.getIdCardNo();
        User user = null;
        if (!StringUtils.isEmpty(idCardNo)) {
            //通过身份证 判断居民是否存在
            user = userManager.getUserByIdCardNo(idCardNo);
        }
        String defaultPassword = "";
        if (user == null) {
            user = new User();
            user.setId(getObjectId(BizObject.User));
            user.setCreateDate(new Date());
            if (!StringUtils.isEmpty(doctor.getIdCardNo()) && doctor.getIdCardNo().length() > 9) {
                defaultPassword = doctor.getIdCardNo().substring(doctor.getIdCardNo().length() - 8);
                user.setPassword(DigestUtils.md5Hex(defaultPassword));
            } else {
                user.setPassword(DigestUtils.md5Hex(default_password));
            }
            if (StringUtils.isEmpty(d.getRoleType())) {
                user.setUserType("5");
            } else {
                user.setUserType(d.getRoleType());
            }
            user.setIdCardNo(d.getIdCardNo());
            user.setDoctorId(d.getId().toString());
            user.setEmail(d.getEmail());
            user.setGender(d.getSex());
            user.setTelephone(d.getPhone());
            user.setLoginCode(d.getIdCardNo());
            user.setRealName(d.getName());
            user.setProvinceId(0);
            user.setCityId(0);
            user.setAreaId(0);
            user.setActivated(true);
            user.setImgRemotePath(d.getPhoto());
            user.setUserType(ConstantUtil.DOCTORUSERTYPEID);
            user = userManager.saveUser(user);
            //卫生人员初始化授权
            userManager.initializationAuthorization(Integer.valueOf(ConstantUtil.DOCTORUSERTYPEID),user.getId());
        } else {
            //todo 是否修改user信息
            //......
            defaultPassword = user.getPassword();
        }

        d.setUserId(user.getId());
        d.setInsertTime(new Date());
        d = doctorService.save(d);

        //创建居民
        DemographicInfo demographicInfo = new DemographicInfo();
        if (!StringUtils.isEmpty(doctor.getIdCardNo()) && doctor.getIdCardNo().length() > 9) {
            defaultPassword = doctor.getIdCardNo().substring(doctor.getIdCardNo().length() - 8);
            demographicInfo.setPassword(DigestUtils.md5Hex(defaultPassword));
        } else {
            demographicInfo.setPassword(DigestUtils.md5Hex(default_password));
        }
        demographicInfo.setRegisterTime(new Date());
        demographicInfo.setIdCardNo(d.getIdCardNo());
        demographicInfo.setName(d.getName());
        demographicInfo.setTelephoneNo("{\"联系电话\":\"" + d.getPhone() + "\"}");
        demographicInfo.setGender(d.getSex());
        demographicService.savePatient(demographicInfo);

        //创建用户与机构关系
        orgMemberRelationInfo(orgDeptJsonList, user, d);

        //TODO 角色有可能没有关联机构，此部分需要待确认 根据机构获取医生角色id,保存到role_users表中,appId是健康上饶APP对应的id:WYo0l73F8e
//        List<String> orgList = orgService.getOrgList(orgDeptJsonList);
//        if (null != orgList && orgList.size() > 0) {
//            List<Roles> rolesList = rolesService.findByCodeAndAppIdAndOrgCode(orgList, "WYo0l73F8e", "Doctor");
//            if (null != rolesList && rolesList.size() > 0) {
//                roleUserService.batchCreateRoleUsersRelation(user.getId(), String.valueOf(rolesList.get(0).getId()));
//            } else {
//                // 不存在 则往角色表中插入该应用的医生角色
//                Roles roles = new Roles();
//                roles.setCode("Doctor");
//                roles.setName("医生");
//                roles.setAppId("WYo0l73F8e");
//                roles.setType("1");
//                roles.setOrgCode(orgList.get(0));
//                roles = rolesService.save(roles);
//                roleUserService.batchCreateRoleUsersRelation(user.getId(), String.valueOf(roles.getId()));
//            }
//        }
        return convertToModel(doctor, MDoctor.class);

    }

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "修改医生", notes = "重新绑定医生信息")
    public MDoctor updateDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) throws Exception {
        List<MOrgDeptJson> orgDeptJsonList = objectMapper.readValue(model, new TypeReference<List<MOrgDeptJson>>() {
        });
        MOrgDeptJson mOrgDeptJson = orgDeptJsonList.get(0);
        Organization organization = orgService.getOrgById(mOrgDeptJson.getOrgId());
        OrgDept orgDept = orgDeptService.searchBydeptId(Integer.parseInt(mOrgDeptJson.getDeptIds().split(",")[0]));

        Doctors doctors = toEntity(doctoJsonData, Doctors.class);
        doctors.setOrgId(organization.getId().toString());
        doctors.setOrgCode(organization.getOrgCode());
        doctors.setOrgFullName(organization.getFullName());
        doctors.setDeptName(orgDept.getName());
        doctors.setUpdateTime(new Date());
        doctorService.save(doctors);

        //同时修改用户表
        User user = userManager.getUserByIdCardNo(doctors.getIdCardNo());
        if (!StringUtils.isEmpty(user)) {
            user.setRealName(doctors.getName());
            user.setGender(doctors.getSex());
            user.setTelephone(doctors.getPhone());
            userManager.save(user);
        }
        //修改居民
        DemographicInfo demographicInfo = demographicService.getDemographicInfoByIdCardNo(doctors.getIdCardNo());
        if (!StringUtils.isEmpty(demographicInfo)) {
            demographicInfo.setName(doctors.getName());
            demographicInfo.setGender(doctors.getSex());
            demographicInfo.setTelephoneNo("{\"联系电话\":\"" + doctors.getPhone() + "\"}");
            demographicService.save(demographicInfo);
        }
        //修改用户与机构关系
        orgMemberRelationInfo(orgDeptJsonList, user, doctors);
        return convertToModel(doctors, MDoctor.class);
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorsExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断医生code是否存在")
    public boolean isDoctorCodeExists(
            @ApiParam(name = "doctor_code", value = "医生code", defaultValue = "")
            @PathVariable(value = "doctor_code") String doctorCode) {
        return doctorService.getByCode(doctorCode) != null;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取医生信息")
    public MDoctor getDoctor(
            @ApiParam(name = "doctor_id", value = "", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId) {
        Doctors doctors = doctorService.getDoctor(doctorId);
        MDoctor doctorModel = convertToModel(doctors, MDoctor.class);
        return doctorModel;
    }


    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除医生", notes = "根据id删除医生")
    public boolean deleteDoctor(
            @ApiParam(name = "doctor_id", value = "医生编号", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId) throws Exception {
        doctorService.deleteDoctor(doctorId);
        return true;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变医生状态", notes = "根据id更新医生")
    public boolean updDoctorStatus(
            @ApiParam(name = "doctor_id", value = "id", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status) throws Exception {
        doctorService.updDoctorStatus(doctorId, status);
        return true;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorPhoneExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    public List idExistence(
            @ApiParam(name = "phones", value = "phones", defaultValue = "")
            @RequestBody String phones) throws Exception {

        List existPhones = doctorService.idExist(toEntity(phones, String[].class));
        return existPhones;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorBatch, method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation("批量导入医生")
    public boolean createDoctorsPatch(
            @ApiParam(name = "doctors", value = "医生JSON", defaultValue = "")
            @RequestBody String doctors) throws Exception {
        List<Map<String, Object>> doctorMapList = objectMapper.readValue(doctors, new TypeReference<List>() {
        });
        String idCardNosStr = doctorService.addDoctorBatch(doctorMapList);

        List list = new ArrayList<>();
        if (!"".equals(idCardNosStr)) {
            idCardNosStr = "[" + idCardNosStr.substring(0, idCardNosStr.length() - 1) + "]";
            list = doctorService.getIdByIdCardNos(toEntity(idCardNosStr, String[].class));
        }
        Doctors d;
        for (int i = 0; i < list.size(); i++) {
            Object[] objectList = (Object[]) list.get(i);
            if (null != objectList) {
                d = new Doctors();
                //INSERT INTO users(login_code, real_name, gender, tech_title, email, telephone, password,doctor_id
                d.setId(Long.parseLong(objectList[0].toString()));
                d.setName(objectList[3].toString());
                d.setCode(objectList[22].toString());//卫统没有医生code
                d.setSex(objectList[5].toString());
                if (null != objectList[7]) {
                    d.setSkill(objectList[7].toString());
                }
                if (null != objectList[9]) {
                    d.setEmail(objectList[9].toString());
                }
                if (null != objectList[10]) {
                    d.setPhone(objectList[10].toString());
                }
                d.setIdCardNo(objectList[22].toString());

                //根据身份证和电话号码，判断账户表中是否存在该用户。若存在 将用户表与医生表关联；若不存在，为该医生初始化账户。
                StringBuffer stringBuffer = new StringBuffer();
                if (!StringUtils.isEmpty(d.getIdCardNo())) {
                    stringBuffer.append("idCardNo=" + d.getIdCardNo() + ";");
                }
                if (!StringUtils.isEmpty(d.getPhone())) {
                    stringBuffer.append("telephone=" + d.getPhone() + ";");
                }
                String filters = stringBuffer.toString();
                List<User> userList = userManager.search("", filters, "", 1, 1);
                String userId = "";
                //若存在 将用户表与医生表关联
                if (null != userList && userList.size() > 0) {
                    for (User user : userList) {
                        user.setDoctorId(String.valueOf(d.getId()));
                        user.setUserType(ConstantUtil.DOCTORUSERTYPEID);
                        user = userManager.saveUser(user);
                        userId = user.getId();
                    }
                } else {
                    //若不存在，为该医生初始化账户。
                    User user = new User();
                    user.setId(getObjectId(BizObject.User));
                    user.setLoginCode(d.getIdCardNo());
                    user.setTelephone(d.getPhone());
                    user.setRealName(d.getName());
                    user.setIdCardNo(d.getIdCardNo());
                    user.setGender(d.getSex());
                    user.setTechTitle(d.getSkill());
                    user.setEmail(d.getEmail());
                    String defaultPassword = "";
                    if (!StringUtils.isEmpty(d.getIdCardNo()) && d.getIdCardNo().length() > 9) {
                        defaultPassword = d.getIdCardNo().substring(d.getIdCardNo().length() - 8);
                        user.setPassword(DigestUtils.md5Hex(defaultPassword));
                    } else {
                        user.setPassword(DigestUtils.md5Hex(default_password));
                    }
                    user.setCreateDate(DateUtil.strToDate(DateUtil.getNowDateTime()));
                    user.setActivated(true);
                    user.setUserType(ConstantUtil.DOCTORUSERTYPEID);
                    user.setDoctorId(d.getId() + "");
                    user.setProvinceId(0);
                    user.setCityId(0);
                    user.setAreaId(0);
                    user = userManager.saveUser(user);
                    userId = user.getId();
                }
                //卫生人员初始化授权
                userManager.initializationAuthorization(Integer.valueOf(ConstantUtil.DOCTORUSERTYPEID),userId);

                String orgId = "";
                if (!StringUtils.isEmpty(objectList[23])) {
                    orgId = objectList[23].toString();
                }
                String deptCode = "";
                if (!StringUtils.isEmpty(objectList[37])) {
                    deptCode = objectList[37].toString();
                }
                int deptId = orgDeptService.getOrgDeptByOrgIdAndDeptCode(orgId, deptCode);
                String deptName="";
                if (!StringUtils.isEmpty(objectList[27])) {
                    deptName =objectList[27].toString();
                }
                OrgMemberRelation memberRelation = new OrgMemberRelation();
                // 同步科室医生信息到福州总部，随后返回总部的科室医生信息
                // 对 主任医师、副主任医师、主治医师、医师 才做同步
                Map<String, Object> deptDoc = doctorService.syncDoctor(d, String.valueOf(orgId), deptName);
                if ("10000".equals(deptDoc.get("Code").toString())) {
                    memberRelation.setJkzlUserId(deptDoc.get("userId").toString());
                    memberRelation.setJkzlDoctorUid(deptDoc.get("doctorUid").toString());
                    memberRelation.setJkzlDoctorSn(deptDoc.get("doctorSn").toString());
                    memberRelation.setJkzlHosDeptId(deptDoc.get("hosDeptId").toString());
                }

                memberRelation.setOrgId(orgId);
                if (!StringUtils.isEmpty(objectList[25])) {
                    memberRelation.setOrgName(objectList[25].toString());
                }
                memberRelation.setDeptId(deptId);//卫统数据-机构没有关联科室，卫生人员提供的科室代码在系统字典中管理--考虑是否改为字典编码值
                memberRelation.setDeptName(deptName);
                memberRelation.setUserId(String.valueOf(userId));
                memberRelation.setUserName(d.getName());
                memberRelation.setStatus(0);
                relationService.save(memberRelation);
            }
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorOnePhoneExistence, method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean isExistence(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters) throws Exception {

        List<Doctors> doctor = doctorService.search("", filters, "", 1, 1);
        return doctor != null && doctor.size() > 0;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorEmailExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在邮箱")
    public List emailsExistence(
            @ApiParam(name = "emails", value = "emails", defaultValue = "")
            @RequestBody String emails) throws Exception {

        List existPhones = doctorService.emailsExistence(toEntity(emails, String[].class));
        return existPhones;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorsIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证号码是否存在")
    public boolean isCardNoExists(
            @ApiParam(name = "doctor_idCardNo", value = "身份证号码", defaultValue = "")
            @PathVariable(value = "doctor_idCardNo") String idCardNo) {
        return doctorService.getByIdCardNo(idCardNo) != null;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctoridCardNoExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在身份证号码")
    public List idCardNoExistence(
            @ApiParam(name = "idCardNos", value = "idCardNos", defaultValue = "")
            @RequestBody String idCardNos) throws Exception {

        List existidCardNos = doctorService.idCardNosExist(toEntity(idCardNos, String[].class));
        return existidCardNos;
    }

    @RequestMapping(value = "/getStatisticsDoctorsByRoleType", method = RequestMethod.GET)
    @ApiOperation("根据角色获取医院、医生总数")
    public List getStatisticsDoctorsByRoleType(
            @ApiParam(name = "roleType", value = "roleType", defaultValue = "")
            @RequestParam(value = "roleType") String roleType) throws Exception {

        List<Object> statisticsDoctors = doctorService.getStatisticsDoctorsByRoleType(roleType);
        return statisticsDoctors;
    }

    //创建用户与机构的关联
    private void orgMemberRelationInfo(List<MOrgDeptJson> orgDeptJsonList, User user, Doctors doctor) throws Exception {
        if (null != orgDeptJsonList && orgDeptJsonList.size() > 0) {
            String[] orgIds = new String[orgDeptJsonList.size()];
            for (int i = 0; i < orgDeptJsonList.size(); i++) {
                orgIds[i] = orgDeptJsonList.get(i).getOrgId();
            }
            relationService.updateByOrgId(orgIds, user.getId());
        }

        for (MOrgDeptJson orgDeptJson : orgDeptJsonList) {
            Organization organization = orgService.getOrgById(orgDeptJson.getOrgId());
            String deptIds = orgDeptJson.getDeptIds();
            if (!StringUtils.isEmpty(deptIds)) {
                String[] deptIdArr = deptIds.split(",");
                for (String deptId : deptIdArr) {
                    OrgDept orgDept = orgDeptService.searchBydeptId(Integer.parseInt(deptId));
                    if (null != organization && null != orgDept) {
                        OrgMemberRelation memberRelation = new OrgMemberRelation();

                        // 同步科室医生信息到福州总部，随后返回总部的科室医生信息
                        // 对 主任医师、副主任医师、主治医师、医师 才做同步
                        if ("1".equals(doctor.getLczc()) || "2".equals(doctor.getLczc()) || "3".equals(doctor.getLczc()) || "4".equals(doctor.getLczc())) {
                            Map<String, Object> deptDoc = doctorService.syncDoctor(doctor, orgDeptJson.getOrgId(), orgDept.getName());
                            if ("10000".equals(deptDoc.get("Code").toString())) {
                                memberRelation.setJkzlUserId(deptDoc.get("userId").toString());
                                memberRelation.setJkzlDoctorUid(deptDoc.get("doctorUid").toString());
                                memberRelation.setJkzlDoctorSn(deptDoc.get("doctorSn").toString());
                                memberRelation.setJkzlHosDeptId(deptDoc.get("hosDeptId").toString());
                            }
                        }

                        memberRelation.setOrgId(orgDeptJson.getOrgId());
                        memberRelation.setOrgName(organization.getFullName());
                        memberRelation.setDeptId(Integer.parseInt(deptId));
                        memberRelation.setDeptName(orgDept.getName());
                        memberRelation.setUserId(user.getId());
                        memberRelation.setUserName(user.getRealName());
                        memberRelation.setStatus(0);
                        relationService.save(memberRelation);
                    }
                }
            }
        }
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorOnlyUpdateD, method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "更新医生信息，只更新医生表信息", notes = "只更新医生表信息")
    public Envelop updateDoctor(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @RequestParam(value = "id", required = true) Long id,
            @ApiParam(name = "name", value = "", defaultValue = "")
            @RequestParam(value = "photo", required = false) String photo,
            @ApiParam(name = "skill", value = "", defaultValue = "")
            @RequestParam(value = "skill", required = false) String skill,
            @ApiParam(name = "officeTel", value = "", defaultValue = "")
            @RequestParam(value = "officeTel", required = false) String officeTel,
            @ApiParam(name = "workPortal", value = "", defaultValue = "")
            @RequestParam(value = "workPortal", required = false) String workPortal) throws Exception {
        Doctors doctors = doctorService.getDoctor(id);
        if (!StringUtils.isEmpty(photo)) {
            doctors.setPhoto(photo);
        }
        if (!StringUtils.isEmpty(skill)) {
            doctors.setSkill(skill);
        }
        if (!StringUtils.isEmpty(officeTel)) {
            doctors.setOfficeTel(officeTel);
        }
        if (!StringUtils.isEmpty(workPortal)) {
            doctors.setWorkPortal(workPortal);
        }
        doctors.setUpdateTime(new Date());
        doctorService.save(doctors);
        //更改用户表里的头像
        User user = userManager.getUserByIdCardNo(doctors.getIdCardNo());
        if (!StringUtils.isEmpty(user)) {
            user.setImgRemotePath(doctors.getPhoto());
            userManager.save(user);
        }
        return success(convertToModel(doctors, MDoctor.class));
    }


    @RequestMapping(value = ServiceApi.Doctors.DoctorByIdCardNo, method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证获取获取医生信息")
    public MDoctor getDoctorByIdCardNo(
            @ApiParam(name = "idCardNo", value = "", defaultValue = "")
            @PathVariable(value = "idCardNo") String idCardNo) {
        Doctors doctors = doctorService.getByIdCardNo(idCardNo);
        MDoctor doctorModel = convertToModel(doctors, MDoctor.class);
        return doctorModel;
    }
}