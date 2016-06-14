package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.patient.service.demographic.DemographicId;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import com.yihu.ehr.patient.service.demographic.DemographicService;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.encode.HashUtil;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.csource.common.MyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by zqb on 2015/8/14.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "patient", description = "人口管理", tags = {"人口管理"})
public class PatientEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private DemographicService demographicService;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    ObjectMapper objectMapper;
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
            @RequestParam(value = "search",required = false) String search,
            @ApiParam(name = "home_province", value = "省", defaultValue = "")
            @RequestParam(value = "home_province",required = false) String province,
            @ApiParam(name = "home_city", value = "市", defaultValue = "")
            @RequestParam(value = "home_city",required = false) String city,
            @ApiParam(name = "home_district", value = "县", defaultValue = "")
            @RequestParam(value = "home_district",required = false) String district,
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
    @RequestMapping(value = "/populations",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    public MDemographicInfo createPatient(
            @ApiParam(name = "json_data", value = "身份证号", defaultValue = "")
            @RequestBody String jsonData) throws Exception{
        DemographicInfo demographicInfo = toEntity(jsonData, DemographicInfo.class);
        String pwd = "123456";
        demographicInfo.setPassword(HashUtil.hashStr(pwd));
        demographicInfo.setRegisterTime(new Date());
        demographicService.savePatient(demographicInfo);
        return convertToModel(demographicInfo,MDemographicInfo.class);
    }

    /**
     * 根据前端传回来的json修改人口信息
     * @param patientModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations",method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据前端传回来的json修改人口信息")
    public MDemographicInfo updatePatient(
            @ApiParam(name = "patient_model_json_data", value = "身份证号", defaultValue = "")
            @RequestBody String patientModelJsonData) throws Exception{
        DemographicInfo demographicInfo = toEntity(patientModelJsonData, DemographicInfo.class);
        DemographicInfo old = demographicService.getDemographicInfo(new DemographicId(demographicInfo.getIdCardNo()));
        if(old==null)
            throw new ApiException(HttpStatus.NOT_FOUND, "该对象没找到");
        BeanUtils.copyProperties(demographicInfo, old, "registerTime");
        demographicService.savePatient(old);
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
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/populations/picture",method = RequestMethod.POST)
    @ApiOperation(value = "上传头像,把图片转成流的方式发送")
    public String uploadPicture(
            @ApiParam(name = "jsonData", value = "头像转化后的输入流")
            @RequestBody String jsonData ) throws IOException {
        if(jsonData == null){
            return null;
        }
        String date = URLDecoder.decode(jsonData,"UTF-8");

        String[] fileStreams = date.split(",");
        String is = URLDecoder.decode(fileStreams[0],"UTF-8").replace(" ","+");
        byte[] in = Base64.getDecoder().decode(is);

        String pictureName = fileStreams[1].substring(0,fileStreams[1].length()-1);
        String fileExtension = pictureName.substring(pictureName.lastIndexOf(".") + 1).toLowerCase();
        String description = null;
        if ((pictureName != null) && (pictureName.length() > 0)) {
            int dot = pictureName.lastIndexOf('.');
            if ((dot > -1) && (dot < (pictureName.length()))) {
                description = pictureName.substring(0, dot);
            }
        }
        String path = null;
        try {

//            FileOutputStream fileOutputStream = new FileOutputStream(new File("F:\\m\\"+pictureName));
//            fileOutputStream.write(in);
//            fileOutputStream.flush();
//            fileOutputStream.close();

            InputStream inputStream = new ByteArrayInputStream(in);
            ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, description);
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
//            path = "{\"groupName\":" + groupName + ",\"remoteFileName\":" + remoteFileName + "}";
            path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);

        } catch (Exception e) {
            LogService.getLogger(DemographicInfo.class).error("人口头像图片上传失败；错误代码："+e);
        }
        //返回文件路径
        return path;
    }


    /**
     * 人口信息头像图片下载
     * @return
     * @throws IOException
     * @throws MyException
     */
    @RequestMapping(value = "/populations/picture",method = RequestMethod.GET)
    @ApiOperation(value = "下载头像")
    public String downloadPicture(
            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
            @RequestParam(value = "group_name") String groupName,
            @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
            @RequestParam(value = "remote_file_name") String remoteFileName) throws Exception {
        String imageStream = null;
        try {

            byte[] bytes = fastDFSUtil.download(groupName,remoteFileName);

            String fileStream = Base64.getEncoder().encodeToString(bytes);
            imageStream = URLEncoder.encode(fileStream,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            LogService.getLogger(DemographicInfo.class).error("人口头像图片下载失败；错误代码：" + e);
        }
        return imageStream;
    }

    @RequestMapping(value = "/populations/is_exist/{id_card_no}",method = RequestMethod.GET)
    @ApiOperation(value = "判断身份证是否存在")
    public boolean isExistIdCardNo(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception {

        return demographicService.getDemographicInfo(new DemographicId(idCardNo)) != null;
    }





}
