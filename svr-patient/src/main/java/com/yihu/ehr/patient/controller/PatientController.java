package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.patient.service.demographic.DemographicId;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import com.yihu.ehr.patient.service.demographic.DemographicService;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encode.HashUtil;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "patient", description = "人口管理", tags = {"人口管理"})
public class PatientController extends BaseRestController {

    @Autowired
    private DemographicService demographicService;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 根据条件查询人口信息
     * @param search
     * @param province
     * @param city
     * @param district
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.GET)
    @ApiOperation(value = "根据条件查询人")
    public List<MDemographicInfo> searchPatient(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search") String search,
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
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("search", search);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        conditionMap.put("province", province);
        conditionMap.put("city", city);
        conditionMap.put("district", district);
//        List<DemographicInfo> demographicInfos = demographicService.searchPatient(conditionMap);
//        Integer totalCount = demographicService.searchPatientTotalCount(conditionMap);
//
//        List<MDemographicInfo> mDemographicInfos = (List<MDemographicInfo>)convertToModels(demographicInfos,new ArrayList<MDemographicInfo>(demographicInfos.size()), MDemographicInfo.class, null);
//        return getResult(mDemographicInfos,totalCount);

        List<DemographicInfo> demographicInfos = demographicService.searchPatient(conditionMap);
        Long totalCount =Long.parseLong(demographicService.searchPatientTotalCount(conditionMap).toString());
        pagedResponse(request, response, totalCount, page, rows);
        return (List<MDemographicInfo>)convertToModels(demographicInfos,new ArrayList<MDemographicInfo>(demographicInfos.size()), MDemographicInfo.class, null);

    }


    /**
     * 根据身份证号删除人
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/{id_card_no}",method = RequestMethod.DELETE)
    @ApiOperation(value = "根据身份证号删除人")
    public boolean deletePatient(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception{
        demographicService.delete(new DemographicId(idCardNo));
        return true;
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
        DemographicInfo demographicInfo = demographicService.getDemographicInfo(new DemographicId(idCardNo));
        MDemographicInfo demographicModel = convertToModel(demographicInfo,MDemographicInfo.class);
        return demographicModel;
    }

    @RequestMapping(value = "/populations/{id_card_no}/register",method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证号判断病人是否注册")
    public boolean isRegistered(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception{
        MDemographicInfo demographicInfo = getPatient(idCardNo);
        return  demographicInfo!=null;
    }



    /**
     * 根据前端传回来的json新增一个人口信息
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.POST)
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    public MDemographicInfo createPatient(
            @ApiParam(name = "json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData/*,
            HttpServletRequest request*/) throws Exception{
        //将文件保存至服务器，返回文件的path，
        //String picPath = webupload(request);
        ObjectMapper objectMapper = new ObjectMapper();
        DemographicInfo demographicInfo = objectMapper.readValue(jsonData, DemographicInfo.class);
        //将文件path保存至数据库
//        demographicInfo.setPicPath(picPath);
//        if(!StringUtils.isEmpty(picPath)){
//            demographicInfo.setLocalPath("");
//        }
        String pwd = "123456";
        demographicInfo.setPassword(HashUtil.hashStr(pwd));
        demographicService.savePatient(demographicInfo);
        return convertToModel(demographicInfo,MDemographicInfo.class);
    }

    /**
     * 根据前端传回来的json修改人口信息
     * @param patientModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.PUT)
    @ApiOperation(value = "根据前端传回来的json修改人口信息")
    public MDemographicInfo updatePatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestParam(value = "patient_model_json_data") String patientModelJsonData/*,
            HttpServletRequest request*/) throws Exception{

        //将文件保存至服务器，返回文件的path，
        //String picPath = webupload(request);
        ObjectMapper objectMapper = new ObjectMapper();
        DemographicInfo demographicInfo = objectMapper.readValue(patientModelJsonData, DemographicInfo.class);
        //将文件path保存至数据库
//        demographicInfo.setPicPath(picPath);
//        if(picPath != null){
//            demographicInfo.setLocalPath("");
//        }
        demographicService.savePatient(demographicInfo);
        return convertToModel(demographicInfo,MDemographicInfo.class);
    }



    /**
     * 初始化密码
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/password/{id_card_no}",method = RequestMethod.PUT)
    @ApiOperation(value = "初始化密码",notes = "用户忘记密码时重置密码，初始密码为123456")
    public boolean resetPass(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception{
        demographicService.resetPass(new DemographicId(idCardNo));
        return true;
    }


    /**
     * 人口信息头像图片上传
     * @param request
     * @return
     * @throws IOException
     */
    public String webupload(HttpServletRequest request) throws IOException {
        if(request==null){
            return "";
        }else {
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
    }


    /**
     * 注：因直接访问文件路径，无法显示文件信息
     * 将文件路径解析成字节流，通过字节流的方式读取文件
     * @param request
     * @param response
     * @param localImgPath       文件路径
     * @throws Exception
     */
    @RequestMapping(value = "/populations/images/{local_img_path}",method = RequestMethod.PUT)
    @ApiOperation(value = "显示头像")
    public void showImage(
            @ApiParam(name = "local_img_path", value = "身份证号", defaultValue = "")
            @PathVariable(value = "local_img_path") String localImgPath,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
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

    @RequestMapping(value = "/populations/is_exist/{id_card_no}",method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证是否存在")
    public boolean isExistIdCardNo(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception {

        return demographicService.getDemographicInfo(new DemographicId(idCardNo)) != null;
    }

}
