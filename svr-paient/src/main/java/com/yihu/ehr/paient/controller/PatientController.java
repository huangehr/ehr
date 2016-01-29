package com.yihu.ehr.paient.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.paient.paientIdx.model.DemographicIndex;
import com.yihu.ehr.paient.service.demographic.DemographicId;
import com.yihu.ehr.paient.service.demographic.DemographicInfo;
import com.yihu.ehr.paient.service.demographic.PatientBrowseModel;
import com.yihu.ehr.paient.service.demographic.PatientModel;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/8/14.
 */
@RestController
@RequestMapping("/patient")
@EnableFeignClients
public class PatientController extends BaseRestController {


    @Autowired
    private DemographicIndex demographicIndex;

    public PatientController(){}



    @RequestMapping("/search")
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


    /* 删除病人信息 requestBody格式:
    * "idCardNo":""  //身份证号
    */
    @RequestMapping("deletePatient")
    @ResponseBody
    public String deletePatient(String idCardNo) {
        demographicIndex.delete(new DemographicId(idCardNo));
        return "seccuss";
    }


    /**
     * 获取病人信息
     * @param idCardNo
     * @return
     */
    @RequestMapping(value = "getPatient" ,method = RequestMethod.GET)
    public Object getPatient(String idCardNo) {
        DemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(idCardNo));
        PatientModel patientModel = demographicIndex.getPatient(demographicInfo);
        return patientModel;
    }

    /**
     * 检查身份证是否已经存在
     */
    @RequestMapping("/demographicInfo/idCardNo")
    @ResponseBody
    public String checkIdCardNo(String IdCardNo){
        DemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(IdCardNo));
        if(demographicInfo==null){
            return "success";
        }else{
            return "faild";
        }

    }

    @RequestMapping("updatePatient")
    //注册或更新病人信息
    public Object updatePatient(String patientJsonData,HttpServletRequest request, HttpServletResponse response) throws IOException {

        String patientData = URLDecoder.decode(patientJsonData,"UTF-8");

        //将文件保存至服务器，返回文件的path，
        String picPath = webupload(request, response);
        ObjectMapper objectMapper = new ObjectMapper();
        PatientModel patientModels = objectMapper.readValue(patientData, PatientModel.class);
        //将文件path保存至数据库
        patientModels.setPicPath(picPath);
        if(picPath != null){
            patientModels.setLocalPath("");
        }
        Map<String, PatientModel> data = new HashMap<>();
        Result result = null;
        if (demographicIndex.updatePatient(patientModels)) {
            result = getSuccessResult(true);
        } else {
            result = getSuccessResult(false);
        }
        result.setObj(data);
        return result.toJson();
    }

    @RequestMapping("resetPass")
    @ResponseBody
    public Object resetPass(String idCardNo) {
        demographicIndex.resetPass(new DemographicId(idCardNo));
        return "success";
    }


    @RequestMapping(value = "/webupload")
    public String webupload(HttpServletRequest request,HttpServletResponse respons) throws IOException {
//        try {
//            request.setCharacterEncoding("UTF-8");
//        } catch (UnsupportedEncodingException e1) {
//        }
//        InputStream inputStearm = request.getInputStream();
//        String fileName = (String) request.getParameter("name");
//        if(fileName == null){
//            return null;
//        }
//        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//        String description = null;
//        if ((fileName != null) && (fileName.length() > 0)) {
//            int dot = fileName.lastIndexOf('.');
//            if ((dot > -1) && (dot < (fileName.length()))) {
//                description = fileName.substring(0, dot);
//            }
//        }
//        ObjectNode objectNode = null;
//        String path = null;
//        try {
//            objectNode = FastDFSUtil.upload(inputStearm, fileExtension, description);
//            String groupName = objectNode.get("groupName").toString();
//            String remoteFileName = objectNode.get("remoteFileName").toString();
//            path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
//        } catch (Exception e) {
//            LogService.getLogger(DemographicInfo.class).error("人口头像图片上传失败；错误代码："+e);
//        }
//        //返回文件路径
//        return path;
        return null;
    }


}
