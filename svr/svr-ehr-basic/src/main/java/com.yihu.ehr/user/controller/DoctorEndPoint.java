package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.org.MOrgDeptJson;
import com.yihu.ehr.model.user.MDoctor;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgMemberRelation;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.org.service.OrgDeptService;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import com.yihu.ehr.patient.service.demographic.DemographicService;
import com.yihu.ehr.user.entity.Doctors;
import com.yihu.ehr.user.entity.User;
import com.yihu.ehr.user.service.DoctorService;
import com.yihu.ehr.user.service.UserManager;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.hash.HashUtil;
import com.yihu.ehr.util.phonics.PinyinUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 2017-02-04 add  by hzp
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "doctors", description = "医生管理接口", tags = {"基础信息-医生管理"})
public class DoctorEndPoint extends EnvelopRestEndPoint {

    @Autowired
    DoctorService doctorService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private OrgDeptService orgDeptService;
    @Autowired
    private OrgMemberRelationService relationService;
    @Value("${default.password}")
    private String default_password = "123456";
    @Autowired
    private DemographicService demographicService;
    @Autowired
    private OrgService orgService;

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
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        List<Doctors> doctorsList = doctorService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, doctorService.getCount(filters), page, size);

        return (List<MDoctor>) convertToModels(doctorsList, new ArrayList<MDoctor>(doctorsList.size()), MDoctor.class, fields);
    }

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建医生", notes = "创建医生信息")
    public MDoctor createDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) throws Exception {
        Doctors doctor = toEntity(doctoJsonData, Doctors.class);
        doctor.setInsertTime(new Date());
        doctor.setUpdateTime(new Date());
        doctor.setStatus("1");
        doctor.setPyCode(PinyinUtil.getPinYinHeadChar(doctor.getName(), false));
        Doctors d= doctorService.save(doctor);
        //创建账户
        User user =new User();
        user.setId(getObjectId(BizObject.User));
        user.setCreateDate(new Date());
        user.setPassword(HashUtil.hash(default_password));
        user.setUserType("Doctor");
        user.setIdCardNo(d.getIdCardNo());
        user.setDoctorId(d.getId().toString());
        user.setEmail(d.getEmail());
        user.setGender(d.getSex());
        user.setTelephone(d.getPhone());
        user.setLoginCode(d.getPhone());
        user.setRealName(d.getName());
        user.setProvinceId(0);
        user.setCityId(0);
        user.setAreaId(0);
        user.setActivated(true);
        user.setImgRemotePath(d.getPhoto());
        user = userManager.saveUser(user);

        //创建居民
        DemographicInfo demographicInfo =new DemographicInfo();
        demographicInfo.setPassword(HashUtil.hash(default_password));
        demographicInfo.setRegisterTime(new Date());
        demographicInfo.setIdCardNo(d.getIdCardNo());
        demographicInfo.setName(d.getName());
        demographicInfo.setTelephoneNo("{\"联系电话\":\""+d.getPhone()+"\"}");
        demographicInfo.setGender(d.getSex());
        demographicService.savePatient(demographicInfo);
        //创建用户与机构关系
        List<MOrgDeptJson> orgDeptJsonList = objectMapper.readValue(model, new TypeReference<List<MOrgDeptJson>>() {});
        orgMemberRelationInfo(orgDeptJsonList, user);
        return convertToModel(doctor, MDoctor.class);
    }

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改医生", notes = "重新绑定医生信息")
    public MDoctor updateDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) throws Exception {
        Doctors doctors = toEntity(doctoJsonData, Doctors.class);
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
        DemographicInfo demographicInfo = demographicService.getDemographicInfoByIdCardNo(doctors.getIdCardNo());
        if (!StringUtils.isEmpty(demographicInfo)) {
            demographicInfo.setName(doctors.getName());
            demographicInfo.setGender(doctors.getSex());
            demographicInfo.setTelephoneNo("{\"联系电话\":\"" + doctors.getPhone() + "\"}");
            demographicService.save(demographicInfo);
        }
        //修改用户与机构关系
        List<MOrgDeptJson> orgDeptJsonList = objectMapper.readValue(model, new TypeReference<List<MOrgDeptJson>>() {});
        orgMemberRelationInfo(orgDeptJsonList, user);
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

    @RequestMapping(value = ServiceApi.Doctors.DoctorPhoneExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    public List idExistence(
            @ApiParam(name="phones",value="phones",defaultValue = "")
            @RequestBody String phones) throws Exception {

        List existPhones = doctorService.idExist(toEntity(phones, String[].class));
        return existPhones;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorBatch,method = RequestMethod.POST)
    @ApiOperation("批量导入医生")
    public boolean createDoctorsPatch(
            @ApiParam(name="doctors",value="医生JSON",defaultValue = "")
            @RequestBody String doctors) throws Exception
    {
        List models = objectMapper.readValue(doctors, new TypeReference<List>() {});
        String phones=doctorService.addDoctorBatch(models);
        List list =new ArrayList<>();
        if(!"".equals(phones)){
            phones="["+phones.substring(0,phones.length()-1)+"]";
             list = doctorService.getIdByPhone(toEntity(phones, String[].class));
        }
        List<Doctors> existPhonesList=new ArrayList<Doctors>();
        Doctors d;
        for(int i = 0 ;i < list.size() ; i++){
           Object[] objectList=(Object[])list.get(i);
          if(null!=objectList){
            d=new Doctors();
              //INSERT INTO users(login_code, real_name, gender, tech_title, email, telephone, password,doctor_id
              d.setId(Long.parseLong(objectList[0].toString()) );
              d.setName(objectList[3].toString());
              d.setCode(objectList[2].toString());
              d.setSex(objectList[5].toString());
              d.setSkill(objectList[7].toString());
              d.setEmail(objectList[9].toString());
              d.setPhone(objectList[10].toString());
              d.setIdCardNo(objectList[22].toString());

              //根据身份证和电话号码，判断账户表中是否存在该用户。若存在 将用户表与医生表关联；若不存在，为该医生初始化账户。
              StringBuffer stringBuffer = new StringBuffer();
              stringBuffer.append("idCardNo=" + d.getIdCardNo()+ ";");
              stringBuffer.append("telephone=" + d.getPhone()+ ";");
              String filters = stringBuffer.toString();
              List<User> userList = userManager.search("", filters, "", 1, 1);
              String userId = "";
              //若存在 将用户表与医生表关联
              if(null!=userList&&userList.size()>0){
                  for(User user:userList){
                      user.setDoctorId(String.valueOf(d.getId()));
                      user = userManager.saveUser(user);
                      userId = user.getId();
                  }
              }else{

                  //若不存在，为该医生初始化账户。
//                  existPhonesList.add(d);
                  User user = new User();
                  user.setId(getObjectId(BizObject.User));
                  user.setLoginCode(d.getPhone());
                  user.setTelephone(d.getPhone());
                  user.setRealName(d.getName());
                  user.setIdCardNo(d.getIdCardNo());
                  user.setGender(d.getSex());
                  user.setTechTitle(d.getSkill());
                  user.setEmail(d.getEmail());
                  user.setPassword(HashUtil.hash(default_password));
                  user.setCreateDate(DateUtil.strToDate(DateUtil.getNowDateTime()));
                  user.setActivated(true);
                  user.setUserType("Doctor");
                  user.setDType("Doctor");
                  user.setDoctorId(d.getId() + "");
                  user.setProvinceId(0);
                  user.setCityId(0);
                  user.setAreaId(0);
                  user = userManager.saveUser(user);
                  userId = user.getId();
              }
              String orgId="";
              if(!StringUtils.isEmpty(objectList[23])){
                  orgId=objectList[23].toString();
              }
              String deptName="";
              if(!StringUtils.isEmpty(objectList[27])){
                  deptName=objectList[27].toString();
              }
              // 根据机构id和部门名称 获取部门id
            int deptId=  orgDeptService.getOrgDeptByOrgIdAndName(orgId, deptName);

              OrgMemberRelation memberRelation = new OrgMemberRelation();
              memberRelation.setOrgId(orgId);
              if(!StringUtils.isEmpty(objectList[25])){
                  memberRelation.setOrgName(objectList[25].toString());
              }
              memberRelation.setDeptId(deptId);
              memberRelation.setDeptName(deptName);
              memberRelation.setUserId(String.valueOf(userId));
              memberRelation.setUserName(d.getName());
              memberRelation.setStatus(0);
              relationService.save(memberRelation);
          }
        }
//        if(null!=existPhonesList&&existPhonesList.size()>0){
//            userManager.addUserBatch(existPhonesList);
//        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorOnePhoneExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean isExistence(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) throws Exception {

        List<Doctors> doctor = doctorService.search("",filters,"", 1, 1);
        return doctor!=null && doctor.size()>0;
    }
    @RequestMapping(value = ServiceApi.Doctors.DoctorEmailExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在邮箱")
    public List emailsExistence(
            @ApiParam(name="emails",value="emails",defaultValue = "")
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

    @RequestMapping(value = ServiceApi.Doctors.DoctoridCardNoExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在身份证号码")
    public List idCardNoExistence(
            @ApiParam(name="idCardNos",value="idCardNos",defaultValue = "")
            @RequestBody String idCardNos) throws Exception {

        List existidCardNos = doctorService.idCardNosExist(toEntity(idCardNos, String[].class));
        return existidCardNos;
    }

    @RequestMapping(value = "/getStatisticsDoctorsByRoleType",method = RequestMethod.GET)
    @ApiOperation("根据角色获取医院、医生总数")
    public List getStatisticsDoctorsByRoleType(
            @ApiParam(name="roleType",value="roleType",defaultValue = "")
            @RequestParam(value="roleType") String roleType) throws Exception {

        List<Object> statisticsDoctors = doctorService.getStatisticsDoctorsByRoleType(roleType);
        return statisticsDoctors;
    }

    //创建用户与机构的关联
    private void orgMemberRelationInfo(List<MOrgDeptJson> orgDeptJsonList, User user) {
        if (null != orgDeptJsonList && orgDeptJsonList.size() > 0) {
            String[] orgIds = new String[orgDeptJsonList.size()];
            for (int i=0; i<orgDeptJsonList.size(); i++) {
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
}