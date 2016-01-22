package com.yihu.ehr.paient.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.paient.feignClient.address.AddressClient;
import com.yihu.ehr.paient.paientIdx.model.DemographicIndex;
import com.yihu.ehr.paient.service.demographic.DemographicId;
import com.yihu.ehr.paient.service.demographic.DemographicInfo;
import com.yihu.ehr.paient.service.demographic.PatientBrowseModel;
import com.yihu.ehr.paient.service.demographic.PatientModel;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encode.HashUtil;
import org.apache.tomcat.jni.Address;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/8/14.
 */
@RestController
@RequestMapping("/patient")
public class PatientController extends BaseRestController {


    @Autowired
    private DemographicIndex demographicIndex;

    public PatientController(){}



    @RequestMapping("searchPatient")
    @ResponseBody
    public Object searchPatient(String name,String idCardNo,String province, String city, String district, int page, int rows){
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("name", name);
        conditionMap.put("idCardNo", idCardNo);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        conditionMap.put("province", province);
        conditionMap.put("city", city);
        conditionMap.put("district", district);

        List<PatientBrowseModel> patientBrowseModel = demographicIndex.searchPatientBrowseModel(conditionMap);
        Integer totalCount = demographicIndex.searchPatientInt(conditionMap);
        Result result = new Result();
        result.setObj(patientBrowseModel);
        result.setTotalCount(totalCount);
        return result;
    }

    @RequestMapping("deletePatient")
    @ResponseBody
    /* 删除病人信息 requestBody格式:
    * "idCardNo":""  //身份证号
    */
    public String deletePatient(String idCardNo) {
        demographicIndex.delete(new DemographicId(idCardNo));
        return "seccuss";
    }

    @RequestMapping("getPatient")
    @ResponseBody
    /* 获取病人信息 requestBody格式:
    * "idCardNo":""  //身份证号
    */
    public String getPatient(String idCardNo) {
        DemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(idCardNo));
        PatientModel patientModel = demographicIndex.getPatient(demographicInfo);
        Map<String, PatientModel> data = new HashMap<>();
        data.put("patientModel", patientModel);
        Result result = new Result();
        result.setObj(data);

        return result.toJson();
    }

    /**
     * 检查身份证是否已经存在
     */
    @RequestMapping("checkIdCardNo")
    @ResponseBody
    public String checkIdCardNo(String searchNm){
        DemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(searchNm));
        if(demographicInfo==null){
            return "success";
        }else{
            return "faild";
        }

    }

    @RequestMapping("updatePatient")
    @ResponseBody
    //注册或更新病人信息
    public Object updatePatient(String patientJsonData,HttpServletRequest req,HttpServletResponse res) throws IOException {

        try {
            req.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
        }
//		文件保存目录路径
        String savePath = "F:/baiduyuan";
//		获取文件名
        String fileName = (String) req.getParameter("name");
//		获取文件后缀
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//		生成新的文件名
        String newFileName = new Date().getTime()+"."+fileExt;

        FileOutputStream out = null;
        try {
            File uploadedFile = new File(savePath, newFileName);
            out = new FileOutputStream(uploadedFile);
            IOUtils.copy(req.getInputStream(), out);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
//		生成文件地址,保存至数据库
        String path = savePath+"/"+newFileName;

        IOUtils.closeQuietly(out);

        PatientModel patientModels = new PatientModel();
        String patientPassword = HashUtil.hashStr(patientModels.getPassword());
        patientModels.setPassword(patientPassword);
        if (demographicIndex.updatePatient(patientModels)){
            return "success";
        }else {
            return "faild";
        }
    }

    @RequestMapping("resetPass")
    @ResponseBody
    public Object resetPass(String idCardNo) {
        demographicIndex.resetPass(new DemographicId(idCardNo));
        return "success";
    }


    @RequestMapping(value = "/webupload")
    public String webupload(String a,HttpServletRequest req,HttpServletResponse resp) throws IOException {
        try {
            req.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
        }
//		文件保存目录路径
        String savePath = "F:/baiduyuan";
//		获取文件名
        String fileName = (String) req.getParameter("name");
//		获取文件后缀
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//		生成新的文件名
        String newFileName = new Date().getTime()+"."+fileExt;

        FileOutputStream out = null;
        try {
            File uploadedFile = new File(savePath, newFileName);
            out = new FileOutputStream(uploadedFile);
            IOUtils.copy(req.getInputStream(), out);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
//		生成文件地址,保存至数据库
        String path = savePath+"/"+newFileName;

        IOUtils.closeQuietly(out);

        return path;
    }


}
