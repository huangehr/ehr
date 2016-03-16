package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class PatientController {
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
        Map<String, Object> params = new HashMap<>();
        params.put("idCardNo",idCardNo);
        try {
            if (patientDialogType.equals("addPatient")) {
                model.addAttribute("patientDialogType", "addPatient");
                model.addAttribute("contentPage", "patient/patientInfoDialog");
                return "generalView";
            }else{
                url = "/populations/";
                //todo 该controller的download方法放后台处理
                resultStr = HttpClientUtil.doGet(comUrl + url + idCardNo, params, username, password);
                ObjectMapper mapper = new ObjectMapper();
                Envelop envelop = mapper.readValue(resultStr,Envelop.class);
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
        Map<String, Object> params = new HashMap<>();
        params.put("id_card_no",idCardNo);
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        try {
//            demographicIndex.delete(new DemographicId(idCardNo));
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }
    }

//    @RequestMapping("getPatient")
//    @ResponseBody
    /* 获取病人信息 requestBody格式:
    * "idCardNo":""  //身份证号
    */
    //todo　暂时没用到
//    public String getPatient(String idCardNo) {
//        XDemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(idCardNo));
//        PatientModel patientModel = demographicIndex.getPatient(demographicInfo);
//        Map<String, PatientModel> data = new HashMap<>();
//        data.put("patientModel", patientModel);
//        Result result = new Result();
//        result.setObj(data);
//
//        return result.toJson();
//    }

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
//        XDemographicInfo demographicInfo = demographicIndex.getDemographicInfo(new DemographicId(searchNm));
//        if (demographicInfo == null) {
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        }
//        return getSuccessResult(false).toJson();

    }

    @RequestMapping(value="updatePatient")
    @ResponseBody
    //注册或更新病人信息Header("Content-type: text/html; charset=UTF-8")
    public Object updatePatient(String patientJsonData,HttpServletRequest request, HttpServletResponse response) throws IOException {
        //将文件保存至服务器，返回文件的path，
        //String picPath = webupload(request, response);//网关中处理webupload
        String url = "/populations";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("patient_model_json_data",patientJsonData);
        try {
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
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
        String url = "/patient/resetPass";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("idCardNo",idCardNo);
        try {
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
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
//        try {
//            demographicIndex.resetPass(new DemographicId(idCardNo));
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        } catch (Exception e) {
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }

    }

    @RequestMapping("getParent")
    @ResponseBody
    public Object getParent(Integer level) {
        String url = "/address/address/level";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("level",level);
        try {
            //todo 返回Map<Integer, String>
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            result.setObj(resultStr);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        XAddressDict[] addrArray = addressManager.getLevelToAddr(level);
//        Map<Integer, String> parentMap = new HashMap<>();
//        for (XAddressDict addr : addrArray) {
//            parentMap.put(addr.getId(), addr.getName());
//        }
//        Result result = new Result();
//        result.setObj(parentMap);
//
//        return result.toJson();
    }

    @RequestMapping("getChildByParent")
    @ResponseBody
    public Object getChildByParent(Integer pid) {
        String url = "/address/address/pid";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("pid",pid);
        try {
            //todo 返回Map<Integer, String>
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            result.setObj(resultStr);
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        XAddressDict[] addrArray = addressManager.getPidToAddr(pid);
//        Map<Integer, String> childMap = new HashMap<>();
//        for (XAddressDict addr : addrArray) {
//            childMap.put(addr.getId(), addr.getName());
//        }
//        Result result = new Result();
//        result.setObj(childMap);
//
//        return result.toJson();
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
