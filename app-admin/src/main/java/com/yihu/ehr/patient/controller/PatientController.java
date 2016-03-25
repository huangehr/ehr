package com.yihu.ehr.patient.controller;

import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.RestTemplates;
import com.yihu.ehr.util.controller.BaseUIController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqb on 2015/8/14.
 */
@Controller
@RequestMapping("/patient")
public class PatientController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    public PatientController() {
    }

    @RequestMapping("initial")
    public String patientInitial(Model model) {
        model.addAttribute("contentPage", "/patient/patient");
        return "pageView";
    }

    @RequestMapping("patientDialogType")
    public Object patientDialogType(String idCardNo, String patientDialogType, Model model) throws IOException {
        String url = "";
        String resultStr = "";
        Envelop result = new Envelop();
        RestTemplates templates = new RestTemplates();
        try {
            if (patientDialogType.equals("addPatient")) {
                PatientDetailModel patientDetailModel = new PatientDetailModel();
                result.setObj(patientDetailModel);
                model.addAttribute("patientModel", toJson(result));
                model.addAttribute("patientDialogType", "addPatient");
                model.addAttribute("contentPage", "patient/patientInfoDialog");
                return "generalView";
            }else{
                url = "/populations/";
                //todo 该controller的download方法放后台处理
                resultStr = templates.doGet(comUrl+url+idCardNo);
                Envelop envelop = getEnvelop(resultStr);
                if (envelop.isSuccessFlg()){
                    model.addAttribute("patientModel", resultStr);
                    if (patientDialogType.equals("updatePatient")) {
                        model.addAttribute("contentPage", "patient/patientInfoDialog");
                        return "generalView";
                    }else if (patientDialogType.equals("patientInfoMessage")) {
                        model.addAttribute("contentPage", "patient/patientBasicInfoDialog");
                        return "simpleView";
                    }
                }else{
                    return envelop.getErrorMsg();
                }
                return "";
            }

        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("searchPatient")
    @ResponseBody
    public Object searchPatient(String searchNm, String province, String city, String district, int page, int rows) {
        String url = "/populations";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("search", searchNm);
        params.put("page", page);
        params.put("rows", rows);
        params.put("province", province);
        params.put("city", city);
        params.put("district", district);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("deletePatient")
    @ResponseBody
    /* 删除病人信息 requestBody格式:
    * "idCardNo":""  //身份证号
    */
    public Object deletePatient(String idCardNo) {
        String url = "/populations/"+idCardNo;
        String resultStr = "";
        Envelop result = new Envelop();
        try {
            RestTemplates restTemplates = new RestTemplates();
            resultStr = restTemplates.doDelete(comUrl + url);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 检查身份证是否已经存在
     */
    @RequestMapping("checkIdCardNo")
    @ResponseBody
    public Object checkIdCardNo(String searchNm) {
        String url = "/populations/is_exist/"+searchNm;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id_card_no",searchNm);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping(value="updatePatient")
    @ResponseBody
    //注册或更新病人信息Header("Content-type: text/html; charset=UTF-8")
    public Object updatePatient(String patientJsonData,String patientDialogType,HttpServletRequest request, HttpServletResponse response) throws IOException {
        //将文件保存至服务器，返回文件的path，
        //String picPath = webupload(request, response);//网关中处理webupload
        String url = "/populations";
        String resultStr = "";
        Envelop result = new Envelop();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        PatientDetailModel patientDetailModel = toModel(patientJsonData, PatientDetailModel.class);
        RestTemplates templates = new RestTemplates();
        if (patientDialogType.equals("addPatient")) {
            //联系电话
            Map<String, String> telphoneNo = new HashMap<String, String>();
            String tag="联系电话";
            telphoneNo.put(tag,patientDetailModel.getTelephoneNo());
            patientDetailModel.setTelephoneNo(toJson(telphoneNo));
            params.add("patientModelJsonData", toJson(patientDetailModel));
        }else{
            String idCardNo = patientDetailModel.getIdCardNo();
            resultStr = templates.doGet(comUrl+url+'/'+idCardNo);
            Envelop envelop = getEnvelop(resultStr);
            if (envelop.isSuccessFlg()){
                PatientDetailModel updatePatient = getEnvelopModel(envelop.getObj(),PatientDetailModel.class);
                //todo:姓名、身份证号能否修改
                updatePatient.setName(patientDetailModel.getName());
                updatePatient.setIdCardNo(patientDetailModel.getIdCardNo());
                updatePatient.setGender(patientDetailModel.getGender());
                updatePatient.setNation(patientDetailModel.getNation());
                updatePatient.setNativePlace(patientDetailModel.getNativePlace());
                updatePatient.setMartialStatus(patientDetailModel.getMartialStatus());
                updatePatient.setBirthday(patientDetailModel.getBirthday());
                updatePatient.setBirthPlaceInfo(patientDetailModel.getBirthPlaceInfo());
                updatePatient.setHomeAddressInfo(patientDetailModel.getHomeAddressInfo());
                updatePatient.setWorkAddressInfo(patientDetailModel.getWorkAddressInfo());
                updatePatient.setResidenceType(patientDetailModel.getResidenceType());
                //联系电话
                Map<String, String> telphoneNo = null;
                String tag = "联系电话";
                telphoneNo = toModel(updatePatient.getTelephoneNo(),Map.class);
                if (telphoneNo!=null){
                    if (telphoneNo.containsKey(tag)) {
                        telphoneNo.remove(tag);
                    }
                }else{
                    telphoneNo = new HashMap<String, String>();
                }
                telphoneNo.put(tag, patientDetailModel.getTelephoneNo());
                updatePatient.setTelephoneNo(toJson(telphoneNo));
                updatePatient.setEmail(patientDetailModel.getEmail());

                params.add("patient_model_json_data", toJson(updatePatient));
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg(envelop.getErrorMsg());
                return result;
            }
        }
        try {
            if (patientDialogType.equals("addPatient")) {
                resultStr = templates.doPost(comUrl + url, params);
            }else {
                resultStr = templates.doPut(comUrl + url, params);
            }
            result.setSuccessFlg(getEnvelop(resultStr).isSuccessFlg());
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        //String patientData = request.getParameter("patientJsonData");
//        String patientData = URLDecoder.decode(patientJsonData,"UTF-8");
//
//        //将文件保存至服务器，返回文件的path，
//        String picPath = webupload(request, response);
//        ObjectMapper objectMapper = new ObjectMapper();
//        PatientModel patientModels = objectMapper.readValue(patientData, PatientModel.class);
///*        String patientPassword = HashUtil.hashStr(patientModels.getPassword());
//        patientModels.setPassword(patientPassword);*/
//        //将文件path保存至数据库
//        patientModels.setPicPath(picPath);
//        if(picPath != null){
//            patientModels.setLocalPath("");
//        }
//        Map<String, PatientModel> data = new HashMap<>();
//        Result result = null;
//        if (demographicIndex.updatePatient(patientModels)) {
//            result = getSuccessResult(true);
//        } else {
//            result = getSuccessResult(false);
//        }
//        result.setObj(data);
//        return result.toJson();
    }

    @RequestMapping("resetPass")
    @ResponseBody
    public Object resetPass(String idCardNo) {
        String url = "/populations/password/";
        String resultStr = "";
        Envelop result = new Envelop();;
        try {
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doPut(comUrl+url+idCardNo,null);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

//    /**
//     * 人口信息头像图片上传
//     * @param request
//     * @param response
//     * @return
//     * @throws IOException
//     */
//    public String webupload(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
//        FastDFSUtil dfsUtil = new FastDFSUtil();
//        String path = null;
//        try {
//            objectNode = dfsUtil.upload(inputStearm, fileExtension, description);
//            String groupName = objectNode.get("groupName").toString();
//            String remoteFileName = objectNode.get("remoteFileName").toString();
//            path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
//        } catch (Exception e) {
//           //LogService.getLogger(DemographicInfo.class).error("人口头像图片上传失败；错误代码："+e);
//        }
//        //返回文件路径
//        return path;
//    }

//    /**
//     * 人口信息头像图片下载
//     * @param patientModel
//     * @return
//     * @throws IOException
//     * @throws MyException
//     */
//    //todo 放后台处理
//    public String download(PatientModel patientModel) throws IOException, MyException {
//        if(patientModel.getPicPath() == null||patientModel.getPicPath().equals("")){
//            return null;
//        }
//        Object obj = JSONObject.toBean(JSONObject.fromObject(patientModel.getPicPath()), HashMap.class);
//        String groupName = ((HashMap<String, String>) obj).get("groupName");
//        String remoteFileName = ((HashMap<String, String>) obj).get("remoteFileName");
//        String splitMark = System.getProperty("file.separator");
//        String strPath = System.getProperty("java.io.tmpdir");
//        strPath += splitMark + "patientImages" + splitMark + remoteFileName;
//        File file = new File(strPath);
//        String path = String.valueOf(file.getParentFile());
//        if (!file.getParentFile().exists()) {
//            file.getParentFile().mkdirs();
//        }
//        if (patientModel.getLocalPath() != null) {
//            File fileName = new File(patientModel.getLocalPath());
//            if (fileName.exists()) {
//                return patientModel.getLocalPath();
//            }
//        }
//
//        //调用图片下载方法，返回文件的储存位置localPath，将localPath保存至人口信息表
//        String localPath = null;
//        try {
//            localPath = FastDFSUtil.download(groupName, remoteFileName, path);
//            patientModel.setLocalPath(localPath);
//            demographicIndex.updatePatient(patientModel);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (MyException e) {
//            LogService.getLogger(DemographicInfo.class).error("人口头像图片下载失败；错误代码：" + e);
//        }
//
//        return localPath;
//    }

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
    //todo 不用调整
    public void showImage(HttpServletRequest request, HttpServletResponse response, String localImgPath) throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        response.setContentType("image/jpeg");
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            File file = new File(localImgPath);
            if (!file.exists()) {
//                LogService.getLogger(PatientController.class).error("人口头像不存在：" + localImgPath);
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
//            LogService.getLogger(PatientController.class).error(e.getMessage());
        } finally {
            if (os != null)
                os.close();
            if (fis != null)
                fis.close();
        }
    }

}
