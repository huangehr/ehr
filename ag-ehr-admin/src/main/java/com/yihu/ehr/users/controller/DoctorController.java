package com.yihu.ehr.users.controller;

import com.yihu.ehr.agModel.user.DoctorDetailModel;
import com.yihu.ehr.agModel.user.DoctorsModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.fileresource.service.FileResourceClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.user.MDoctor;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.users.service.DoctorClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeshijie on 2017/2/14.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "doctor", description = "医生管理接口", tags = {"基础信息 - 医生管理接口"})
public class DoctorController extends BaseController {

    @Autowired
    private DoctorClient doctorClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;
    @Autowired
    private FileResourceClient fileResourceClient;
    @Autowired
    private UserClient userClient;



    @RequestMapping(value = "/doctors", method = RequestMethod.GET)
    @ApiOperation(value = "获取医生列表", notes = "根据查询条件获取医生列表在前端表格展示")
    public Envelop searchUsers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ResponseEntity<List<MDoctor>> responseEntity = doctorClient.searchDoctors(fields, filters, sorts, size, page);
        List<MDoctor> mDoctors = responseEntity.getBody();
        List<DoctorDetailModel> doctorsModels = new ArrayList<>();
        for (MDoctor mDoctor : mDoctors) {
            DoctorDetailModel doctorsModel = convertToModel(mDoctor, DoctorDetailModel.class);
            doctorsModel.setInsertTime(mDoctor.getInsertTime() == null?"": DateTimeUtil.simpleDateTimeFormat(mDoctor.getInsertTime()));
            doctorsModel.setUpdateTime(mDoctor.getUpdateTime() == null?"": DateTimeUtil.simpleDateTimeFormat(mDoctor.getUpdateTime()));
            //性别
            if (StringUtils.isNotEmpty(mDoctor.getSex())) {
                MConventionalDict dict = conventionalDictClient.getGender(mDoctor.getSex());
                doctorsModel.setSex(dict == null ? "" : dict.getValue());
            }
            if (StringUtils.isNotEmpty(mDoctor.getRoleType())) {
                MConventionalDict dict = conventionalDictClient.getUserType(mDoctor.getRoleType());
                doctorsModel.setRoleType(dict == null ? "" : dict.getValue());
            }
            doctorsModels.add(doctorsModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

        Envelop envelop = getResult(doctorsModels, totalCount, page, size);
        return envelop;
    }

    @RequestMapping(value = "/doctor/existence", method = RequestMethod.GET)
    @ApiOperation(value = "医生属性唯一性验证", notes = "医生属性唯一性验证（用户名）")
    public Envelop existence(
            @ApiParam(name = "existenceType",value = "", defaultValue = "")
            @RequestParam(value = "existenceType") String existenceType,
            @ApiParam(name = "existenceNm",value = "", defaultValue = "")
            @RequestParam(value = "existenceNm") String existenceNm) {
        try {
            Envelop envelop = new Envelop();
            boolean bo = false;
            switch (existenceType) {
                case "code":
                    bo = doctorClient.isCodeExists(existenceNm);
                    break;
                case "idCardNo":
                    bo = doctorClient.isCardNoExists(existenceNm);
                    break;
                case "phone":
                   String ph="phone="+existenceNm;
                    bo = doctorClient.isExistence(ph);
                    boolean uf=userClient.isTelephoneExists(existenceNm);
                    if(bo==true || uf==true){
                        bo=true;
                    }
                    break;
                case "email":
                    String email="email="+existenceNm;
                    bo = doctorClient.isExistence(email);
                    boolean emailFlag=userClient.isEmailExists(existenceNm);
                    if(bo==true || emailFlag==true){
                        bo=true;
                    }
                    break;
            }
            envelop.setSuccessFlg(bo);

            return envelop;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "doctors/admin/{doctor_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取医生信息", notes = "医生信息")
    public Envelop getUser(
            @ApiParam(name = "doctor_id", value = "", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId) {
        try {
            MDoctor mDoctor = doctorClient.getDoctor(doctorId);
            if (mDoctor == null) {
                return failed("医生信息获取失败!");
            }

            DoctorDetailModel detailModel = convertToModel(mDoctor, DoctorDetailModel.class);
            detailModel.setInsertTime(mDoctor.getInsertTime() == null?"": DateTimeUtil.simpleDateTimeFormat(mDoctor.getInsertTime()));
            detailModel.setUpdateTime(mDoctor.getUpdateTime() == null?"": DateTimeUtil.simpleDateTimeFormat(mDoctor.getUpdateTime()));

            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/doctors/admin/{doctor_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除医生", notes = "根据医生id删除医生")
    public Envelop deleteDoctor(
            @ApiParam(name = "doctor_id", value = "医生编号", defaultValue = "")
            @PathVariable(value = "doctor_id") String doctorId) {
        try {
            boolean result = doctorClient.deleteDoctor(doctorId);
            if (!result) {
                return failed("删除失败!");
            }
            try{
                fileResourceClient.filesDelete(doctorId);
            }catch (Exception e){
                return success("数据删除成功！头像图片删除失败！");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/doctor", method = RequestMethod.POST)
    @ApiOperation(value = "创建医生", notes = "重新绑定医生信息")
    public Envelop createDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestParam(value = "doctor_json_data") String doctorJsonData,
            @ApiParam(name = "orgId", value = "", defaultValue = "")
            @RequestParam(value = "orgId") String orgId,
            @ApiParam(name = "deptId", value = "", defaultValue = "")
            @RequestParam(value = "deptId") String deptId) {
        try {
            DoctorDetailModel detailModel = objectMapper.readValue(doctorJsonData, DoctorDetailModel.class);

            String errorMsg = null;

            if (StringUtils.isEmpty(detailModel.getCode())) {
                errorMsg += "账户不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "姓名不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getSkill())) {
                errorMsg += "医生专长不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getEmail())) {
                errorMsg += "邮箱不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getPhone())) {
                errorMsg += "手机-主号码不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getOfficeTel())) {
                errorMsg += "办公电话不能为空!";
            }

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MDoctor mDoctor = convertToMDoctor(detailModel);
            mDoctor = doctorClient.createDoctor(objectMapper.writeValueAsString(mDoctor), orgId, deptId);
            if (mDoctor == null) {
                return failed("保存失败!");
            }

            detailModel = convertToModel(mDoctor, DoctorDetailModel.class);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/doctor", method = RequestMethod.PUT)
    @ApiOperation(value = "修改医生", notes = "重新绑定医生信息")
    public Envelop updateDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestParam(value = "doctor_json_data") String doctorJsonData) {
        try {
            DoctorDetailModel detailModel = toEntity(doctorJsonData, DoctorDetailModel.class);
            String errorMsg = null;
            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "姓名不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getSkill())) {
                errorMsg += "医生专长不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getEmail())) {
                errorMsg += "邮箱不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getPhone())) {
                errorMsg += "手机-主号码不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getOfficeTel())) {
                errorMsg += "办公电话不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MDoctor mDoctor = convertToMDoctor(detailModel);
            mDoctor = doctorClient.updateDoctor(objectMapper.writeValueAsString(mDoctor));
            if(mDoctor==null){
                return failed("保存失败!");
            }

            detailModel = convertToModel(mDoctor, DoctorDetailModel.class);
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/doctors/admin/{doctor_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "改变医生状态", notes = "根据医生状态改变当前医生状态")
    public boolean updDoctorStatus(
            @ApiParam(name = "doctor_id", value = "id", defaultValue = "")
            @PathVariable(value = "doctor_id") Long doctorId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status) {
        try {
            return doctorClient.updDoctorStatus(doctorId,status);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public MDoctor convertToMDoctor(DoctorDetailModel detailModel) throws ParseException {
        if(detailModel==null)
        {
            return null;
        }
        MDoctor mDoctor = convertToModel(detailModel,MDoctor.class);
        mDoctor.setUpdateTime(DateTimeUtil.simpleDateTimeParse(detailModel.getUpdateTime()));
        mDoctor.setInsertTime(DateTimeUtil.simpleDateTimeParse(detailModel.getInsertTime()));

        return mDoctor;
    }
    @RequestMapping(value = ServiceApi.Doctors.DoctorPhoneExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    public List idExistence(
            @ApiParam(name = "phones", value = "", defaultValue = "")
            @RequestParam("phones") String phones) throws Exception {

        List existPhones = doctorClient.idExistence(phones);
        return existPhones;
    }
    @RequestMapping(value = ServiceApi.Doctors.DoctoridCardNoExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在身份证号码")
    public List idCardNoExistence(
            @ApiParam(name = "idCardNos", value = "", defaultValue = "")
            @RequestParam("idCardNos") String idCardNos) throws Exception {

        List existPhones = doctorClient.idCardNoExistence(idCardNos);
        return existPhones;
    }



    @RequestMapping(value = ServiceApi.Doctors.DoctorBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入医生")
    public Envelop createMetadataPatch(
            @ApiParam(name = "doctors", value = "医生JSON", defaultValue = "")
            @RequestParam(value = "doctors") String doctors) throws Exception {


        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try{
            doctorClient.createDoctorsPatch(doctors);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("系统出错！");
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorOnePhoneExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public Envelop isExistence(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) {

        try {
            return success(doctorClient.isExistence(filters));
        }catch (Exception e){
            e.printStackTrace();
            return failed("查询出错！");
        }
    }

    @RequestMapping(value = ServiceApi.Doctors.DoctorEmailExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在邮箱")
    public List emailsExistence(
            @ApiParam(name = "emails", value = "", defaultValue = "")
            @RequestParam("emails") String emails) throws Exception {

        List existPhones = doctorClient.emailsExistence(emails);
        return existPhones;
    }
}
