package com.yihu.ehr.ha.patient.controller;

import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.agModel.patient.PatientModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.geography.service.AddressClient;
import com.yihu.ehr.ha.patient.service.PatientClient;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
public class PatientController extends BaseController{

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private AddressClient addressClient;

    @RequestMapping(value = "/populations",method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    public Envelop searchPatient(
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
            @RequestParam(value = "rows") Integer rows,
            HttpServletResponse response) throws Exception{

        Envelop envelop = patientClient.searchPatient(name,idCardNo,province,city,district,page,rows);
        List<MDemographicInfo> demographicInfos = (List<MDemographicInfo>)envelop.getDetailModelList();
        List<PatientModel> patients = new ArrayList<>();
        for(MDemographicInfo patientInfo : demographicInfos)
        {
            PatientModel patient = convertToModel(patientInfo,PatientModel.class);
            //TODO:获取家庭地址信息
            String homeAddressId = "";//patientInfo.getHomeAddress()
            MGeography geography = addressClient.getAddressById(homeAddressId);
            String homeAddress = "";
            if(geography!=null)
            {
                if(StringUtils.isNotEmpty(geography.getProvince())) homeAddress+=geography.getProvince();
                if(StringUtils.isNotEmpty(geography.getCity()))homeAddress+=geography.getCity();
                if(StringUtils.isNotEmpty(geography.getDistrict()))homeAddress+=geography.getDistrict();
                if(StringUtils.isNotEmpty(geography.getTown()))homeAddress+=geography.getTown();
                if(StringUtils.isNotEmpty(geography.getStreet()))homeAddress+=geography.getStreet();
                if(StringUtils.isNotEmpty(geography.getExtra()))homeAddress+=geography.getExtra();
            }
            patient.setAddress(homeAddress);
            patients.add(patient);
        }

        envelop = getResult(patients,envelop.getTotalCount(),page,rows);
        return envelop;
    }


    /**
     * 根据身份证号删除人
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/{id_card_no}",method = RequestMethod.DELETE)
    @ApiOperation(value = "根据身份证号删除人")
    public Envelop deletePatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception{
        Envelop envelop = new Envelop();
        boolean result = patientClient.deletePatient(idCardNo);
        envelop.setSuccessFlg(result);
        if(!result)
        {
            envelop.setErrorMsg("删除失败!");
        }
        return envelop;
    }


    /**
     * 根据身份证号查找人
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/{id_card_no}",method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证号查找人")
    public MDemographicInfo getPatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception{
        return patientClient.getPatient(idCardNo);
    }


    /**
     * 根据前端传回来的json新增一个人口信息
     * @param patientModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.POST)
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    public boolean createPatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patient_model_json_data") String patientModelJsonData) throws Exception{
        return patientClient.createPatient(patientModelJsonData);
    }

    /**
     * 根据前端传回来的json修改人口信息
     * @param patientModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.PUT)
    @ApiOperation(value = "根据前端传回来的json修改人口信息")
    public boolean updatePatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patient_model_json_data") String patientModelJsonData) throws Exception{

        return patientClient.updatePatient(patientModelJsonData);
    }

    /**
     * 初始化密码
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/password/{id_card_no}",method = RequestMethod.PUT)
    @ApiOperation(value = "初始化密码",notes = "用户忘记密码时重置密码，初始密码为123456")
    public Object resetPass(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception{
        return patientClient.resetPass(idCardNo);
    }

    /**
     * 注：因直接访问文件路径，无法显示文件信息
     * 将文件路径解析成字节流，通过字节流的方式读取文件
     * @param localImgPath       文件路径
     * @throws Exception
     */
    @RequestMapping(value = "/populations/images/{local_img_path}",method = RequestMethod.PUT)
    @ApiOperation(value = "显示头像")
    public void showImage(
            @ApiParam(name = "local_img_path", value = "身份证号", defaultValue = "")
            @PathVariable(value = "local_img_path") String localImgPath) throws Exception{
        patientClient.showImage(localImgPath);
    }
}
