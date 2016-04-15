package com.yihu.ehr.patient.controller;
import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.RestTemplates;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.encode.Base64;
import com.yihu.ehr.util.log.LogService;
import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResizableByteArrayOutputStream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                model.addAttribute("patientDialogType", patientDialogType);
                model.addAttribute("contentPage", "patient/patientInfoDialog");
                return "generalView";
            } else {
                url = "/populations/";
                //todo 该controller的download方法放后台处理
                resultStr = templates.doGet(comUrl + url + idCardNo);
                Envelop envelop = getEnvelop(resultStr);
                model.addAttribute("patientDialogType", patientDialogType);
                if (envelop.isSuccessFlg()) {
                    model.addAttribute("patientModel", resultStr);
                    if (patientDialogType.equals("updatePatient")) {
                        model.addAttribute("contentPage", "patient/patientInfoDialog");
                        return "generalView";
                    } else if (patientDialogType.equals("patientInfoMessage")) {
                        model.addAttribute("contentPage", "patient/patientBasicInfoDialog");
                        return "simpleView";
                    }
                } else {
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
        params.put("home_province", province);
        params.put("home_city", city);
        params.put("home_district", district);
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
        String url = "/populations/" + idCardNo;
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
        String url = "/populations/is_exist/" + searchNm;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id_card_no", searchNm);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping(value = "updatePatient")
    @ResponseBody
    //注册或更新病人信息Header("Content-type: text/html; charset=UTF-8")
    public Object updatePatient(String patientJsonData, String patientDialogType,HttpServletRequest request, HttpServletResponse response) {

        try {

            String url = "/populations";
            String resultStr = "";
            Envelop result = new Envelop();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            String[] strings = URLDecoder.decode(patientJsonData, "UTF-8").split(";");
            PatientDetailModel patientDetailModel = toModel(strings[0], PatientDetailModel.class);
            RestTemplates templates = new RestTemplates();

            request.setCharacterEncoding("UTF-8");
            InputStream inputStream = request.getInputStream();
            String imageName = request.getParameter("name");

            //读取文件流，将文件输入流转成 byte
            int temp = 0;
            int bufferSize = 1024;
            byte tempBuffer[] = new byte[bufferSize];
            byte[] fileBuffer = new byte[0];
            while ((temp = inputStream.read(tempBuffer)) != -1) {
                fileBuffer = ArrayUtils.addAll(fileBuffer, ArrayUtils.subarray(tempBuffer, 0, temp));
            }
            inputStream.close();

            String restStream = Base64.encode(fileBuffer);
            String imageStream = URLEncoder.encode(restStream, "UTF-8");

            params.add("inputStream", imageStream);
            params.add("imageName", imageName);
            if (strings[1].equals("updatePatient")) {
                String idCardNo = patientDetailModel.getIdCardNo();
                resultStr = templates.doGet(comUrl + url + '/' + idCardNo);
                Envelop envelop = getEnvelop(resultStr);
                if (envelop.isSuccessFlg()) {
                    PatientDetailModel updatePatient = getEnvelopModel(envelop.getObj(), PatientDetailModel.class);
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
                    telphoneNo = toModel(updatePatient.getTelephoneNo(), Map.class);
                    if (telphoneNo != null) {
                        if (telphoneNo.containsKey(tag)) {
                            telphoneNo.remove(tag);
                        }
                    } else {
                        telphoneNo = new HashMap<String, String>();
                    }
                    telphoneNo.put(tag, patientDetailModel.getTelephoneNo());
                    updatePatient.setTelephoneNo(toJson(telphoneNo));
                    updatePatient.setEmail(patientDetailModel.getEmail());

                    params.add("patient_model_json_data", toJson(updatePatient));
                } else {
                    result.setSuccessFlg(false);
                    result.setErrorMsg(envelop.getErrorMsg());
                    return result;
                }
            } else if (strings[1].equals("addPatient")) {
                //联系电话
                Map<String, String> telphoneNo = new HashMap<String, String>();
                String tag = "联系电话";
                telphoneNo.put(tag, patientDetailModel.getTelephoneNo());
                patientDetailModel.setTelephoneNo(toJson(telphoneNo));
                params.add("patientModelJsonData", toJson(patientDetailModel));
            }
            try {

                if (strings[1].equals("updatePatient")) {
                    resultStr = templates.doPost(comUrl + "/population", params);
                } else if (strings[1].equals("addPatient")) {
                    resultStr = templates.doPost(comUrl + url, params);
                }
                result.setSuccessFlg(getEnvelop(resultStr).isSuccessFlg());
                return result;
            } catch (Exception e) {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.SystemError.toString());
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @RequestMapping("resetPass")
    @ResponseBody
    public Object resetPass(String idCardNo) {
        String url = "/populations/password/";
        String resultStr = "";
        Envelop result = new Envelop();
        try {
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doPut(comUrl + url + idCardNo, null);
            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    /**
     * 注：因直接访问文件路径，无法显示文件信息
     * 将文件路径解析成字节流，通过字节流的方式读取文件
     *
     * @param request
     * @param response
     * @param localImgPath 文件路径
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
