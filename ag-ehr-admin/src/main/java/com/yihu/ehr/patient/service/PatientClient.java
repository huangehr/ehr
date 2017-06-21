package com.yihu.ehr.patient.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.patient.MDemographicInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by AndyCai on 2016/2/16.
 */
@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PatientClient {

    @RequestMapping(value = "/populations",method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    ResponseEntity<List<MDemographicInfo>> searchPatient(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search") String search,
            @ApiParam(name = "home_province", value = "省", defaultValue = "")
            @RequestParam(value = "home_province") String province,
            @ApiParam(name = "home_city", value = "市", defaultValue = "")
            @RequestParam(value = "home_city") String city,
            @ApiParam(name = "home_district", value = "县", defaultValue = "")
            @RequestParam(value = "home_district") String district,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) ;

    /**
     * 根据身份证号删除人
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/{id_card_no}",method = RequestMethod.DELETE)
    @ApiOperation(value = "根据身份证号删除人")
    boolean deletePatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) ;


    /**
     * 根据身份证号查找人
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/{id_card_no}",method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证号查找人")
    MDemographicInfo getPatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) ;


    /**
     * 根据前端传回来的json新增一个人口信息
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    MDemographicInfo createPatient(
            @ApiParam(name = "json_data", value = "病人信息", defaultValue = "")
            @RequestBody String jsonData) ;

    /**
     * 根据前端传回来的json修改人口信息
     * @param patientModelJsonData

     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据前端传回来的json修改人口信息")
    MDemographicInfo updatePatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestBody String patientModelJsonData);

    @RequestMapping(value = "/populations/password/{id_card_no}",method = RequestMethod.PUT)
    @ApiOperation(value = "初始化密码",notes = "用户忘记密码时重置密码，初始密码为123456")
    boolean resetPass(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo);

    @RequestMapping(value = "/populations/is_exist/{id_card_no}",method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证是否存在")
    boolean isExistIdCardNo(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo);

    @RequestMapping(value = "/populations/picture",method = RequestMethod.POST)
    @ApiOperation(value = "头像上传")
    String uploadPicture(
            @ApiParam(name = "jsonData", value = "头像信息", defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = "/populations/picture",method = RequestMethod.GET)
    @ApiOperation(value = "头像下载")
    String downloadPicture(
                           @ApiParam(name = "group_name", value = "分组", defaultValue = "")
                           @RequestParam(value = "group_name") String groupName,
                           @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
                           @RequestParam(value = "remote_file_name") String remoteFileName);


    //用户信息 查询（添加查询条件修改）
    @RequestMapping(value = "/populationsByParams", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    ResponseEntity<List<MDemographicInfo>> searchPatientByParams(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search") String search,
            @ApiParam(name = "gender", value = "性别", defaultValue = "")
            @RequestParam(value = "gender") String gender,
            @ApiParam(name = "home_province", value = "省", defaultValue = "")
            @RequestParam(value = "home_province") String province,
            @ApiParam(name = "home_city", value = "市", defaultValue = "")
            @RequestParam(value = "home_city") String city,
            @ApiParam(name = "home_district", value = "县", defaultValue = "")
            @RequestParam(value = "home_district") String district,
            @ApiParam(name = "searchRegisterTimeStart", value = "注册开始时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeStart") String searchRegisterTimeStart,
            @ApiParam(name = "searchRegisterTimeEnd", value = "注册结束时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeEnd") String searchRegisterTimeEnd,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows);

    /**
     *居民信息-角色授权-角色组保存
     * @return
     */

    @ApiOperation(value = "居民信息-角色授权-角色组保存")
    @RequestMapping(value = "/appUserRolesSave", method = RequestMethod.POST)
    String saveRoleUser(
            @ApiParam(name = "userId", value = "居民账户id", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "jsonData", value = "json数据", defaultValue = "")
            @RequestBody String jsonData);
}
