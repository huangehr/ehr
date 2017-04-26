package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.patient.MDemographicInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name= MicroServices.Patient)
@ApiIgnore
public interface PatientClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/populations",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void registerPatient(@RequestBody String jsonData);


    @RequestMapping(value = ApiVersion.Version1_0+"/populations/{id_card_no}/register",method = RequestMethod.GET)
    boolean isRegistered(@PathVariable(value = "id_card_no") String idCardNo);


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

}
