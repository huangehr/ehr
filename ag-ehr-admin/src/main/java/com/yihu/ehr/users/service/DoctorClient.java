package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MDoctor;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by AndyCai on 2016/1/29.
 */
@FeignClient(name= MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DoctorClient {

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生列表", notes = "根据查询条件获取医生列表在前端表格展示")
    ResponseEntity<List<MDoctor>> searchDoctors(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Doctors.DoctorsExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断code是否存在")
    boolean isCodeExists(@PathVariable(value = "doctor_code") String code);

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取医生信息", notes = "医生信息")
    MDoctor getDoctor(@PathVariable(value = "doctor_id") Long doctorId);

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建医生", notes = "重新绑定医生信息")
    MDoctor createDoctor(
            @ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
            @RequestBody String doctoJsonData);

    @RequestMapping(value = ServiceApi.Doctors.Doctors, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改医生", notes = "重新绑定医生信息")
    MDoctor updateDoctor(@ApiParam(name = "doctor_json_data", value = "", defaultValue = "")
                         @RequestBody String doctoJsonData);

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除医生", notes = "根据医生id删除医生")
    boolean deleteDoctor(@PathVariable(value = "doctor_id") String doctorId);

    @RequestMapping(value = ServiceApi.Doctors.DoctorAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变医生状态", notes = "根据医生状态改变当前医生状态")
    boolean updDoctorStatus(
            @PathVariable(value = "doctor_id") Long doctorId,
            @RequestParam(value = "status") String status);

//    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.PUT)
//    @ApiOperation(value = "改变医生状态", notes = "根据医生状态改变当前医生状态")
//    boolean activityUser(
//            @PathVariable(value = "user_id") String userId,
//            @RequestParam(value = "activity") boolean activity);
//
//    @RequestMapping(value = ServiceApi.Users.UserAdminPassword, method = RequestMethod.PUT)
//    @ApiOperation(value = "重设密码", notes = "医生忘记密码管理员帮助重新还原密码，初始密码123456")
//    boolean resetPass(
//            @ApiParam(name = "user_id", value = "id", defaultValue = "")
//            @PathVariable(value = "user_id") String userId);
//
//    @RequestMapping(value = ServiceApi.Users.UserAdminContact, method = RequestMethod.DELETE)
//    @ApiOperation(value = "取消关联绑定", notes = "取消相关信息绑定")
//    boolean unBinding(
//            @ApiParam(name = "user_id", value = "", defaultValue = "")
//            @PathVariable(value = "user_id") String userId,
//            @ApiParam(name = "type", value = "", defaultValue = "")
//            @RequestParam(value = "type") String type);
//
//    @RequestMapping(value = ServiceApi.Users.UserAdminKey, method = RequestMethod.PUT)
//    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
//    Map<String, String> distributeKey(@PathVariable(value = "user_id") String userId);
//
//    @RequestMapping(value = ServiceApi.Users.UserVerification, method = RequestMethod.GET)
//    @ApiOperation(value = "根据医生名及密码获取医生", notes = "根据医生名及密码获取医生")
//    MUser getUserByNameAndPassword(
//            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
//            @RequestParam(value = "user_name") String userName,
//            @ApiParam(name = "password", value = "密码", defaultValue = "")
//            @RequestParam(value = "password") String password);
//
//    @RequestMapping(value = ServiceApi.Users.User, method = RequestMethod.GET)
//    @ApiOperation(value = "根据医生名获取医生", notes = "根据医生名获取医生")
//    MUser getUserByUserName(@PathVariable(value = "user_name") String userName);
//
//    @RequestMapping(value = ServiceApi.Users.UserExistence, method = RequestMethod.GET)
//    @ApiOperation(value = "判断账户是否存在")
//    boolean isUserNameExists(@PathVariable(value = "user_name") String userName);
//
//    @RequestMapping(value = ServiceApi.Users.UserIdCardNoExistence, method = RequestMethod.GET)
//    @ApiOperation(value = "判断身份证是否存在")
//    boolean isIdCardExists(@RequestParam(value = "id_card_no") String idCardNo);
//
//    @RequestMapping(value = ServiceApi.Users.UserEmailNoExistence, method = RequestMethod.GET)
//    @ApiOperation(value = "判断医生邮件是否存在")
//    boolean isEmailExists(@RequestParam(value = "email") String email);
//
//    @RequestMapping(value = ServiceApi.Users.UserAdminPasswordReset, method = RequestMethod.PUT)
//    @ApiOperation(value = "修改密码")
//    boolean changePassWord(
//            @ApiParam(name = "user_id", value = "user_id", defaultValue = "")
//            @PathVariable(value = "user_id") String userId,
//            @ApiParam(name = "password", value = "密码", defaultValue = "")
//            @RequestParam(value = "password") String password);
//
//    @RequestMapping(value = "/user/picture",method = RequestMethod.POST)
//    @ApiOperation(value = "头像上传")
//    String uploadPicture(
//            @ApiParam(name = "jsonData", value = "头像信息", defaultValue = "")
//            @RequestBody String jsonData);
//
//    @RequestMapping(value = "/user/picture",method = RequestMethod.GET)
//    @ApiOperation(value = "头像下载")
//    String downloadPicture(
//            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
//            @RequestParam(value = "group_name") String groupName,
//            @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
//            @RequestParam(value = "remote_file_name") String remoteFileName);
//
//    @RequestMapping(value = ApiVersion.Version1_0 + "/files", method = RequestMethod.POST)
//    @ApiOperation(value = "file upload test")
//    public String pictureUpload(
//            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
//            @RequestParam(value = "file_str") String fileStr,
//            @ApiParam(name = "file_name", value = "文件名")
//            @RequestParam(value = "file_name") String fileName,
//            @ApiParam(name = "json_data", value = "文件资源属性")
//            @RequestParam(value = "json_data") String jsonData);

    @RequestMapping(value = ServiceApi.Doctors.DoctorPhoneExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    List idExistence(
            @RequestBody String phones);

    @RequestMapping(value = ServiceApi.Doctors.DoctorBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入医生")
    boolean createDoctorsPatch(
            @RequestBody String doctors);

    @RequestMapping(value = ServiceApi.Doctors.DoctorOnePhoneExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    boolean isExistence(
            @RequestParam(value="filters") String filters);

    @RequestMapping(value = ServiceApi.Doctors.DoctorEmailExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在邮箱")
    public List emailsExistence(
            @RequestBody String emails);


}
