package com.yihu.ehr.basic.patient.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.util.json.JSONObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.entity.patient.DemographicId;
import com.yihu.ehr.basic.patient.service.DemographicService;
import com.yihu.ehr.basic.user.entity.Doctors;
import com.yihu.ehr.basic.user.entity.RoleUser;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.basic.user.service.DoctorService;
import com.yihu.ehr.basic.user.service.RoleUserService;
import com.yihu.ehr.basic.user.service.UserService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.patient.DemographicInfo;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.phonics.PinyinUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.csource.common.MyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
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
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private UserService userManager;
    @Autowired
    private DoctorService doctorService;

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
        DemographicInfo demographicInfo = demographicService.getDemographicInfo(idCardNo);
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
        String pwd = "12345678";
        if(!StringUtils.isEmpty(demographicInfo.getIdCardNo())&&demographicInfo.getIdCardNo().length()>9){
            pwd=demographicInfo.getIdCardNo().substring(demographicInfo.getIdCardNo().length()-8);
            demographicInfo.setPassword(DigestUtils.md5Hex(pwd));
        }else{
            demographicInfo.setPassword(DigestUtils.md5Hex(pwd));
        }
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
        DemographicInfo old = demographicService.getDemographicInfo(demographicInfo.getIdCardNo());
        if (old == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "该对象没找到");
        }
        BeanUtils.copyProperties(demographicInfo, old, "registerTime");
        demographicService.savePatient(old);
        //同时修改用户信息
        String telNo = demographicInfo.getTelephoneNo();
        JSONObject object = new JSONObject(telNo);
        String tel = object.getField("联系电话").toString();
        User user = userManager.getUserByIdCardNo(demographicInfo.getIdCardNo());
        if (!StringUtils.isEmpty(user)) {
            user.setRealName(demographicInfo.getName());
            user.setGender(demographicInfo.getGender());
            user.setTelephone(tel.substring(1,tel.length() - 1));
            user.setMartialStatus(demographicInfo.getMartialStatus());
            user.setBirthday(DateUtil.toString(demographicInfo.getBirthday()));
            userManager.save(user);
        }
        Doctors doctors = doctorService.getByIdCardNo(demographicInfo.getIdCardNo());
        if (!StringUtils.isEmpty(doctors)) {
            doctors.setName(demographicInfo.getName());
            doctors.setPyCode(PinyinUtil.getPinYinHeadChar(demographicInfo.getName(), false));
            doctors.setSex(demographicInfo.getGender());
            doctors.setPhone(tel.substring(1,tel.length() - 1));
            doctorService.save(doctors);
        }
        return convertToModel(demographicInfo,MDemographicInfo.class);
    }

    /**
     * 初始化密码
     * @param idCardNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/populations/password/{id_card_no}",method = RequestMethod.PUT)
    @ApiOperation(value = "初始化密码",notes = "用户忘记密码时重置密码，初始密码为12345678")
    public boolean resetPass(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo) throws Exception{
        demographicService.resetPass(idCardNo);
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

        return demographicService.getDemographicInfo(idCardNo) != null;
    }

    @RequestMapping(value = "/populations/telphoneNumberIs_exist/{telphone_number}",method = RequestMethod.GET)
    @ApiOperation(value = "判断电话号码是否存在")
    public boolean isExisttelphoneNumber(
            @ApiParam(name = "telphone_number", value = "电话号码", defaultValue = "")
            @PathVariable(value = "telphone_number") String telphoneNumber) throws Exception {

        return demographicService.getDemographicInfoBytelephoneNo(telphoneNumber) != null;
    }


            /**
             * 用户信息 查询（添加查询条件修改）
             * @param search
             * @param province
             * @param city
             * @param district
             * @param page
             * @param rows
             * @return
             * @throws Exception
             */
            @RequestMapping(value = "/populationsByParams",method = RequestMethod.GET)
            @ApiOperation(value = "用户信息 查询（添加查询条件修改）")
            public List<MDemographicInfo> searchPatientByParams(
                    @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
                    @RequestParam(value = "search",required = false) String search,
                    @ApiParam(name = "gender", value = "性别", defaultValue = "")
                    @RequestParam(value = "gender") String gender,
                    @ApiParam(name = "home_province", value = "省", defaultValue = "")
                    @RequestParam(value = "home_province",required = false) String province,
                    @ApiParam(name = "home_city", value = "市", defaultValue = "")
                    @RequestParam(value = "home_city",required = false) String city,
                    @ApiParam(name = "home_district", value = "县", defaultValue = "")
                    @RequestParam(value = "home_district",required = false) String district,
                    @ApiParam(name = "searchRegisterTimeStart", value = "注册开始时间", defaultValue = "")
                    @RequestParam(value = "searchRegisterTimeStart") String searchRegisterTimeStart,
                    @ApiParam(name = "searchRegisterTimeEnd", value = "注册结束时间", defaultValue = "")
                    @RequestParam(value = "searchRegisterTimeEnd") String searchRegisterTimeEnd,
                    @ApiParam(name = "page", value = "当前页", defaultValue = "")
                    @RequestParam(value = "page") Integer page,
                    @ApiParam(name = "rows", value = "行数", defaultValue = "")
                    @RequestParam(value = "rows") Integer rows,
            HttpServletRequest request,HttpServletResponse response) throws Exception{
                Map<String, Object> conditionMap = new HashMap<>();
                conditionMap.put("search", search);
                conditionMap.put("page", page);
                conditionMap.put("pageSize", rows);
                conditionMap.put("province", province);
                conditionMap.put("city", city);
                conditionMap.put("district", district);
                conditionMap.put("gender", gender);

                Date startDate = DateTimeUtil.simpleDateTimeParse(searchRegisterTimeStart);
                Date endDate = DateTimeUtil.simpleDateTimeParse(searchRegisterTimeEnd);
                if(null!=endDate){
                    Calendar calendar   =   new GregorianCalendar();
                    calendar.setTime(endDate);
                    calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
                    endDate=calendar.getTime();   //日期往后推一天
                }
                conditionMap.put("startDate", startDate);
                conditionMap.put("endDate", endDate);
        //        List<DemographicInfo> demographicInfos = demographicService.searchPatient(conditionMap);
        //        Integer totalCount = demographicService.searchPatientTotalCount(conditionMap);
        //        List<MDemographicInfo> mDemographicInfos = (List<MDemographicInfo>)convertToModels(demographicInfos,new ArrayList<MDemographicInfo>(demographicInfos.size()), MDemographicInfo.class, null);
        //        return getResult(mDemographicInfos,totalCount);
                List<DemographicInfo> demographicInfos = demographicService.searchPatientByParams(conditionMap);
                Long totalCount =Long.parseLong(demographicService.searchPatientByParamsTotalCount(conditionMap).toString());
                pagedResponse(request, response, totalCount, page, rows);
                return (List<MDemographicInfo>)convertToModels(demographicInfos,new ArrayList<MDemographicInfo>(demographicInfos.size()), MDemographicInfo.class, null);

    }

    @RequestMapping(value = "/populationsByParams2",method = RequestMethod.GET)
    @ApiOperation(value = "用户信息 查询（添加查询条件修改）")
    public List<MDemographicInfo> searchPatientByParams2(
            @ApiParam(name = "search", value = "搜索内容", defaultValue = "")
            @RequestParam(value = "search",required = false) String search,
            @ApiParam(name = "gender", value = "性别", defaultValue = "")
            @RequestParam(value = "gender") String gender,
            @ApiParam(name = "home_province", value = "省", defaultValue = "")
            @RequestParam(value = "home_province",required = false) String province,
            @ApiParam(name = "home_city", value = "市", defaultValue = "")
            @RequestParam(value = "home_city",required = false) String city,
            @ApiParam(name = "home_district", value = "县", defaultValue = "")
            @RequestParam(value = "home_district",required = false) String district,
            @ApiParam(name = "searchRegisterTimeStart", value = "注册开始时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeStart") String searchRegisterTimeStart,
            @ApiParam(name = "searchRegisterTimeEnd", value = "注册结束时间", defaultValue = "")
            @RequestParam(value = "searchRegisterTimeEnd") String searchRegisterTimeEnd,
            @ApiParam(name = "districtList", value = "区域", defaultValue = "")
            @RequestParam(value = "districtList") String districtList,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows,
            HttpServletRequest request,HttpServletResponse response) throws Exception{
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("search", search);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", rows);
        conditionMap.put("province", province);
        conditionMap.put("city", city);
        conditionMap.put("district", district);
        conditionMap.put("gender", gender);
        conditionMap.put("districtList", districtList);

        Date startDate = DateTimeUtil.simpleDateTimeParse(searchRegisterTimeStart);
        Date endDate = DateTimeUtil.simpleDateTimeParse(searchRegisterTimeEnd);
        if(null!=endDate){
            Calendar calendar   =   new GregorianCalendar();
            calendar.setTime(endDate);
            calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
            endDate=calendar.getTime();   //日期往后推一天
        }
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        //        List<DemographicInfo> demographicInfos = demographicService.searchPatient(conditionMap);
        //        Integer totalCount = demographicService.searchPatientTotalCount(conditionMap);
        //        List<MDemographicInfo> mDemographicInfos = (List<MDemographicInfo>)convertToModels(demographicInfos,new ArrayList<MDemographicInfo>(demographicInfos.size()), MDemographicInfo.class, null);
        //        return getResult(mDemographicInfos,totalCount);
        List<DemographicInfo> demographicInfos = demographicService.searchPatientByParams2(conditionMap);
        Long totalCount =Long.parseLong(demographicService.searchPatientByParamsTotalCount2(conditionMap).toString());
        pagedResponse(request, response, totalCount, page, rows);
        return (List<MDemographicInfo>)convertToModels(demographicInfos,new ArrayList<MDemographicInfo>(demographicInfos.size()), MDemographicInfo.class, null);

    }
    /**
     * 居民信息-角色授权-角色组保存
     * @return
     */
    @RequestMapping(value = "/appUserRolesSave", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "居民信息-角色授权-角色组保存")
    public String saveRoleUser(
            @ApiParam(name = "userId", value = "机构", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "jsonData", value = "json数据", defaultValue = "")
            @RequestBody String jsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
//        String[] jsonDatalist=jsonData.split("jsonData=");
        //将json串转换成对象，放进list里面
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, RoleUser.class);
        List<RoleUser> models = objectMapper.readValue(jsonData,javaType);
        return roleUserService.saveRoleUser(models,userId);

    }

}
