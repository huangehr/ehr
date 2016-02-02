package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.patient.feignClient.AddressClient;
import com.yihu.ehr.patient.paientIdx.model.DemographicIndex;
import com.yihu.ehr.patient.service.demographic.DemographicId;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import com.yihu.ehr.patient.service.demographic.PatientBrowseModel;
import com.yihu.ehr.patient.service.demographic.PatientModel;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private AddressClient addressClient;

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
        return getResult(patientBrowseModel,totalCount,page,rows);
    }


    /* 删除病人信息 requestBody格式:
    * "idCardNo":""  //身份证号
    */
    @RequestMapping("deletePatient")
    @ResponseBody
    public Object deletePatient(String idCardNo) {
        demographicIndex.delete(new DemographicId(idCardNo));
        return true;
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
    public Object checkIdCardNo(String IdCardNo){
        DemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(IdCardNo));
        if(demographicInfo==null){
            return true;
        }else{
            return false;
        }

    }

    @RequestMapping("updatePatient")
    //注册或更新病人信息
    public Object updatePatient(String patientJsonData,HttpServletRequest request, HttpServletResponse response) throws Exception {

        String idCardNo = "350425198506080016";
        DemographicInfo demographicInfoIn = demographicIndex.getDemographicInfo(new DemographicId(idCardNo));
        demographicInfoIn.setEmail("10086@qq.com");

        ObjectMapper objectMapper = new ObjectMapper();
        String patientJsonDataIn = objectMapper.writeValueAsString(demographicInfoIn);
        patientJsonDataIn = URLDecoder.decode(patientJsonDataIn,"UTF-8");
        DemographicInfo demographicInfoOut = objectMapper.readValue(patientJsonDataIn, DemographicInfo.class);

        PatientModel patientModel = new PatientModel();
        patientModel.setIdCardNo(demographicInfoOut.getIdCardNo());
        patientModel.setEmail(demographicInfoOut.getEmail());
        patientModel.setBirthPlace(addressClient.getAddressById(demographicInfoOut.getBirthPlace()));
        patientModel.setHomeAddress(addressClient.getAddressById(demographicInfoOut.getHomeAddress()));
        patientModel.setWorkAddress(addressClient.getAddressById(demographicInfoOut.getWorkAddress()));
        patientModel.setPicPath(demographicInfoOut.getPicPath());

        String patientModelIn = objectMapper.writeValueAsString(patientModel);
        patientModelIn = URLDecoder.decode(patientModelIn,"UTF-8");

        PatientModel patientModels = objectMapper.readValue(patientModelIn, PatientModel.class);

        //将文件保存至服务器，返回文件的path，
        String picPath = webupload(request, response);

        //将文件path保存至数据库
        patientModels.setPicPath(picPath);
        if(picPath != null){
            patientModels.setLocalPath("");
        }
        demographicIndex.updatePatient(patientModels);
        return patientModels;
    }

    @RequestMapping("/resetPass")
    public Object resetPass(String idCardNo) {
        demographicIndex.resetPass(new DemographicId(idCardNo));
        return true;
    }


    @RequestMapping(value = "/webupload")
    public String webupload(HttpServletRequest request,HttpServletResponse respons) throws Exception {

        request.setCharacterEncoding("UTF-8");
        InputStream inputStearm = request.getInputStream();
        String fileName = (String) request.getParameter("name");
        if(fileName == null){
            return null;
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String description = null;
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                description = fileName.substring(0, dot);
            }
        }
        ObjectNode objectNode;
        String path = null;
        try {
            objectNode = fastDFSUtil.upload(inputStearm, fileExtension, description);
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
        } catch (Exception e) {
            LogService.getLogger(DemographicInfo.class).error("人口头像图片上传失败；错误代码："+e);
        }
        //返回文件路径
        return path;
    }


}
