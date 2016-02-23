package com.yihu.ehr.ha.patient.service;

import com.yihu.ehr.model.patient.MDemographicInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by AndyCai on 2016/2/16.
 */
@FeignClient("svr-patient")
public interface PatientClient {

    @RequestMapping(value = "/rest/v1.0/populations",method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    List<MDemographicInfo> searchPatient(
            @ApiParam(name = "name", value = "姓名", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @RequestParam(value = "id_card_no") String idCardNo,
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district") String district,
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
    @RequestMapping(value = "/rest/v1.0/populations/{id_card_no}",method = RequestMethod.DELETE)
    @ApiOperation(value = "根据身份证号删除人")
    Object deletePatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) ;


    /**
     * 根据身份证号查找人
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/populations/{id_card_no}",method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证号查找人")
    MDemographicInfo getPatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) ;


    /**
     * 根据前端传回来的json新增一个人口信息
     * @param patientModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/populations",method = RequestMethod.POST)
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    boolean createPatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patient_model_json_data") String patientModelJsonData) ;

    /**
     * 根据前端传回来的json修改人口信息
     * @param patientModelJsonData
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/populations",method = RequestMethod.PUT)
    @ApiOperation(value = "根据前端传回来的json修改人口信息")
    boolean updatePatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patient_model_json_data") String patientModelJsonData);

    @RequestMapping(value = "/rest/v1.0/populations/password/{id_card_no}",method = RequestMethod.PUT)
    @ApiOperation(value = "初始化密码",notes = "用户忘记密码时重置密码，初始密码为123456")
    Object resetPass(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo);
    /**
     * 注：因直接访问文件路径，无法显示文件信息
     * 将文件路径解析成字节流，通过字节流的方式读取文件
     * @param localImgPath       文件路径
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/populations/images/{local_img_path}",method = RequestMethod.PUT)
    @ApiOperation(value = "显示头像")
    void showImage(
            @ApiParam(name = "local_img_path", value = "身份证号", defaultValue = "")
            @PathVariable(value = "local_img_path") String localImgPath);
}
