package com.yihu.ehr.api.rhip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.org.OrgModel;
import com.yihu.ehr.agModel.user.DoctorDetailModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.feign.DoctorClient;
import com.yihu.ehr.feign.FileResourceClient;
import com.yihu.ehr.feign.OrganizationClient;
import com.yihu.ehr.feign.UserClient;
import com.yihu.ehr.model.org.MOrgDept;
import com.yihu.ehr.model.org.MOrgDeptJson;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.user.MDoctor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/4/24.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/rhip")
@RestController
@Api(value = "doctor", description = "区域卫生信息平台-医生管理接口", tags = {"区域卫生信息平台-医生管理接口"})
public class RhipDoctorEndPoint extends BaseController {

    @Autowired
    private DoctorClient doctorClient;

    @Autowired
    private FileResourceClient fileResourceClient;
    @Autowired
    private OrganizationClient orgClient;
    @Autowired
    private UserClient userClient;

    private String regex = "^[A-Za-z0-9\\-]+$";
    private Pattern pattern = Pattern.compile(regex);



    @RequestMapping(value = "doctor/{doctor_id}", method = RequestMethod.GET)
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

    @RequestMapping(value = "/doctor/{doctor_id}", method = RequestMethod.DELETE)
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
            @ApiParam(name = "model", value = "医生所属机构、部门", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            DoctorDetailModel detailModel = objectMapper.readValue(doctorJsonData, DoctorDetailModel.class);
            String errorMsg = null;
            if (StringUtils.isEmpty(detailModel.getCode())) {
                errorMsg += "账户不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "姓名不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getIdCardNo()) ) {
                errorMsg += "身份证号不能为空!";
            }else if(!pattern.matcher(detailModel.getIdCardNo()).find()){
                errorMsg += "身份证号格式有误!";
            }else  if (null != doctorClient.idCardNoExistence( "["+objectMapper.writeValueAsString(detailModel.getIdCardNo()) +"]")&& userClient.isIdCardExists( detailModel.getIdCardNo())) {
                return failed("身份证号已存在!");
            }

            if (StringUtils.isEmpty(detailModel.getSkill())) {
                errorMsg += "医生专长不能为空!";
            }

            if (StringUtils.isEmpty(detailModel.getEmail())) {
                errorMsg += "邮箱不能为空!";
            }else if (null != doctorClient.emailsExistence( "["+objectMapper.writeValueAsString(detailModel.getEmail())+"]")
                    && userClient.isEmailExists( detailModel.getEmail())) {
                return failed("邮箱已存在!");
            }

            if (StringUtils.isEmpty(detailModel.getPhone())) {
                errorMsg += "手机-主号码不能为空!";
            }else if (null != doctorClient.idExistence( "["+objectMapper.writeValueAsString(detailModel.getPhone())+"]")
                    && userClient.isTelephoneExists( detailModel.getPhone())) {
                return failed("电话号码已存在!");
            }
            String mOrgDeptStr = "";
            String orgCode = "";
            String deptName = "";
            if(StringUtils.isEmpty(model)){
                errorMsg += "model为医生的所属机构及部门，不能为空（格式：{\"orgCode\":\"jkzl\",\"deptName\":\"未分配\"}）!";
            }else{
                Map<String,String> deptMap = objectMapper.readValue(model, new TypeReference<Map<String,String>>() {});
                orgCode=deptMap.get("orgCode");
                deptName=deptMap.get("deptName");
                if(StringUtils.isEmpty(orgCode)){
                    errorMsg += "机构代码不能为空!";
                }
                if(StringUtils.isEmpty(deptName)){
                    errorMsg += "部门名称不能为空!";
                }
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            //根据机构编码、部门名称判断机构与部门是否存在
            String filters ="";
            StringBuffer stringBuffer = new StringBuffer();
            if (!org.apache.commons.lang.StringUtils.isEmpty(orgCode)){
                stringBuffer.append("orgCode=" + orgCode+ ";");
            }
            filters=stringBuffer.toString();
            ResponseEntity<List<MOrganization>> responseEntity = orgClient.searchOrgs("", filters, "", 999, 1);
            List<MOrganization> organizations = responseEntity.getBody();
            Integer orgId=0;
            for (MOrganization mOrg : organizations) {
                orgId=Integer.valueOf(mOrg.getId().toString());
            }
            if(null == orgId){
                errorMsg += "机构代码不存在!";
                return failed(errorMsg);
            }
            //根据机构id和部门名称获取部门id
            MOrgDept mOrgDept = orgClient.getOrgDeptByDeptName(orgId, deptName);
            MOrgDeptJson mOrgDeptJson =new MOrgDeptJson();
            mOrgDeptJson.setOrgId(orgId.toString());
            if(null == mOrgDept){
                errorMsg += "部门名称不存在!";
                return failed(errorMsg);
            }
            mOrgDeptJson.setDeptIds(String.valueOf(mOrgDept.getId()));
            mOrgDeptStr = "["+ objectMapper.writeValueAsString(mOrgDeptJson) +"]";

            MDoctor mDoctor = convertToMDoctor(detailModel);
            mDoctor = doctorClient.createDoctor(objectMapper.writeValueAsString(mDoctor),mOrgDeptStr);
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
            @RequestParam(value = "doctor_json_data") String doctorJsonData,
            @ApiParam(name = "model", value = "医生所属机构、部门", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            DoctorDetailModel detailModel = toEntity(doctorJsonData, DoctorDetailModel.class);
            String errorMsg = null;
            if (StringUtils.isEmpty(detailModel.getCode())) {
                errorMsg += "账户不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "姓名不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getIdCardNo()) ) {
                errorMsg += "身份证号不能为空!";
            }else if(!pattern.matcher(detailModel.getIdCardNo()).find()){
                errorMsg += "身份证号格式有误!";
            }else  if (null == doctorClient.idCardNoExistence( "["+objectMapper.writeValueAsString(detailModel.getIdCardNo()) +"]")&& !userClient.isIdCardExists( detailModel.getIdCardNo())) {
                return failed("身份证号不存在!");
            }

            if (StringUtils.isEmpty(detailModel.getSkill())) {
                errorMsg += "医生专长不能为空!";
            }

            if (StringUtils.isEmpty(detailModel.getEmail())) {
                errorMsg += "邮箱不能为空!";
            }else if (null == doctorClient.emailsExistence( "["+objectMapper.writeValueAsString(detailModel.getEmail())+"]")
                    && !userClient.isEmailExists( detailModel.getEmail())) {
                return failed("邮箱不存在!");
            }

            if (StringUtils.isEmpty(detailModel.getPhone())) {
                errorMsg += "手机-主号码不能为空!";
            }else if (null == doctorClient.idExistence( "["+objectMapper.writeValueAsString(detailModel.getPhone())+"]")
                    && !userClient.isTelephoneExists( detailModel.getPhone())) {
                return failed("电话号码不存在!");
            }
            String mOrgDeptStr = "";
            String orgCode = "";
            String deptName = "";
            if(StringUtils.isEmpty(model)){
                errorMsg += "model为医生的所属机构及部门，不能为空（格式：{\"orgCode\":\"jkzl\",\"deptName\":\"未分配\"}）!";
            }else{
                Map<String,String> deptMap = objectMapper.readValue(model, new TypeReference<Map<String,String>>() {});
                orgCode=deptMap.get("orgCode");
                deptName=deptMap.get("deptName");
                if(StringUtils.isEmpty(orgCode)){
                    errorMsg += "机构代码不能为空!";
                }
                if(StringUtils.isEmpty(deptName)){
                    errorMsg += "部门名称不能为空!";
                }
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            //根据机构编码、部门名称判断机构与部门是否存在
            String filters ="";
            StringBuffer stringBuffer = new StringBuffer();
            if (!org.apache.commons.lang.StringUtils.isEmpty(orgCode)){
                stringBuffer.append("orgCode=" + orgCode+ ";");
            }
            filters=stringBuffer.toString();
            ResponseEntity<List<MOrganization>> responseEntity = orgClient.searchOrgs("", filters, "", 999, 1);
            List<MOrganization> organizations = responseEntity.getBody();
            Integer orgId=0;
            for (MOrganization mOrg : organizations) {
                orgId=Integer.valueOf(mOrg.getId().toString());
            }
            if(null == orgId){
                errorMsg += "机构代码不存在!";
                return failed(errorMsg);
            }
            //根据机构id和部门名称获取部门id
            MOrgDept mOrgDept = orgClient.getOrgDeptByDeptName(orgId, deptName);
            MOrgDeptJson mOrgDeptJson =new MOrgDeptJson();
            mOrgDeptJson.setOrgId(orgId.toString());
            if(null == mOrgDept){
                errorMsg += "部门名称不存在!";
                return failed(errorMsg);
            }
            mOrgDeptJson.setDeptIds(String.valueOf(mOrgDept.getId()));
            mOrgDeptStr = "["+ objectMapper.writeValueAsString(mOrgDeptJson) +"]";
            MDoctor mDoctor = convertToMDoctor(detailModel);
            mDoctor = doctorClient.updateDoctor(objectMapper.writeValueAsString(mDoctor),mOrgDeptStr);
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

}
