package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.patient.feign.AddressClient;
import com.yihu.ehr.patient.feign.ConventionalDictClient;
import com.yihu.ehr.patient.paientIdx.model.DemographicIndex;
import com.yihu.ehr.patient.service.demographic.DemographicId;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
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
import java.io.*;
import java.util.ArrayList;
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

    @Autowired
    private ConventionalDictClient conventionalDictClient;



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
        List<DemographicInfo> demographicInfos = demographicIndex.searchPatientBrowseModel(conditionMap);
        List<MDemographicInfo> demographicModels = new ArrayList<>();
        for(DemographicInfo demographicInfo:demographicInfos){
            MDemographicInfo demographicModel = convertToModel(demographicInfo,MDemographicInfo.class);
            demographicModel.setBirthPlace(addressClient.getAddressById(demographicInfo.getBirthPlace()));
            demographicModel.setNativePlace(addressClient.getAddressById(demographicInfo.getNativePlace()));
            demographicModel.setWorkAddress(addressClient.getAddressById(demographicInfo.getWorkAddress()));
            demographicModel.setHomeAddress(addressClient.getAddressById(demographicInfo.getHomeAddress()));
            demographicModel.setGender(conventionalDictClient.getGender(demographicInfo.getGender()));
            demographicModel.setMartialStatus(conventionalDictClient.getMartialStatus(demographicInfo.getMartialStatus()));
            demographicModel.setResidenceType(conventionalDictClient.getResidenceType(demographicInfo.getResidenceType()));
            demographicModels.add(demographicModel);
        }
        Integer totalCount = demographicIndex.searchPatientInt(conditionMap);
        return getResult(demographicModels,totalCount,page,rows);
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
    public Object checkIdCardNo(String IdCardNo){
        DemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(IdCardNo));
        return demographicInfo!=null;

    }

    @RequestMapping(value="updatePatient")
    @ResponseBody
    //注册或更新病人信息Header("Content-type: text/html; charset=UTF-8")
    public Object updatePatient(String patientJsonData,HttpServletRequest request, HttpServletResponse response) throws Exception {

        String IdCardNo = "";
        DemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(IdCardNo));

//        String patientData = URLDecoder.decode(patientJsonData,"UTF-8");
//
//        //将文件保存至服务器，返回文件的path，
        String picPath = webupload(request, response);
//        ObjectMapper objectMapper = new ObjectMapper();
//        PatientModel patientModels = objectMapper.readValue(patientData, PatientModel.class);
        //将文件path保存至数据库
        demographicInfo.setPicPath(picPath);
        if(picPath != null){
            demographicInfo.setLocalPath("");
        }
        if (demographicIndex.updatePatient(demographicInfo)) {
            return true;
        } else {
            return true;
        }
    }

    @RequestMapping("/resetPass")
    public Object resetPass(String idCardNo) {
        demographicIndex.resetPass(new DemographicId(idCardNo));
        return true;
    }


    /**
     * 人口信息头像图片上传
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    public String webupload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
        }
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
        ObjectNode objectNode = null;
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


    /**
     * 注：因直接访问文件路径，无法显示文件信息
     * 将文件路径解析成字节流，通过字节流的方式读取文件
     * @param request
     * @param response
     * @param localImgPath       文件路径
     * @throws Exception
     */
    @RequestMapping("showImage")
    @ResponseBody
    public void showImage(HttpServletRequest request, HttpServletResponse response, String localImgPath) throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        response.setContentType("image/jpeg");
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            File file = new File(localImgPath);
            if (!file.exists()) {
                LogService.getLogger(PatientController.class).error("人口头像不存在：" + localImgPath);
                return;
            }
            fis = new FileInputStream(localImgPath);
            os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 1024];
            while ((count = fis.read(buffer)) != -1)
                os.write(buffer, 0, count);
            os.flush();
        } catch (IOException e) {
            LogService.getLogger(PatientController.class).error(e.getMessage());
        } finally {
            if (os != null)
                os.close();
            if (fis != null)
                fis.close();
        }
    }


}
