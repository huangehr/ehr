package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.RestTemplates;
import com.yihu.ehr.util.URLQueryBuilder;
import com.yihu.ehr.util.controller.BaseUIController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqb on 2015/8/20.
 */
@Controller
@RequestMapping("/card")
public class CardController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("initial")
    public String cardInitial(Model model) {
        return "/card/card";
    }

    @RequestMapping("searchInitial")
    public String searchCardInitial(Model model) {
        return "/card/searchCard";
    }
    @RequestMapping("/addCardInfoDialog")
    public String addCardInfoDialog(String idCardNo,Model model){
        model.addAttribute("idCardNo",idCardNo);
        model.addAttribute("contentPage", "patient/addCardInfoDialog");
        return  "generalView";
    }

    @RequestMapping("searchCard")
    @ResponseBody
    public Object searchCard(String idCardNo,String searchNm, String cardType, int page, int rows){
        String url = "/cards/binding";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id_card_no", idCardNo);
        params.put("number", searchNm);
        params.put("card_type", cardType);
        params.put("page", page);
        params.put("rows", rows);
        try {
            URLQueryBuilder builder = new URLQueryBuilder();
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doGet(comUrl+url+"?"+builder.toString(params));
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("searchNewCard")
    @ResponseBody
    //搜索新卡
    public Object searchNewCard(String idCardNo,String searchNm,String searchType, int page, int rows){
        String url = "/cards/un_binding";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("idCardNo", idCardNo);
        params.put("number", searchNm);
        params.put("card_type", searchType);
        params.put("page", page);
        params.put("rows", rows);
        try {
            URLQueryBuilder builder = new URLQueryBuilder();
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doGet(comUrl+url+"?"+builder.toString(params));
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("getCard")
    @ResponseBody
    public Object getCard(String id,String cardType){
        String url = "/cards/";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String,Object> params = new HashMap<>();
        params.put("card_type", cardType);
        try{
            URLQueryBuilder builder = new URLQueryBuilder();
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doGet(comUrl+url+id+'?'+builder.toString(params));
            Envelop envelop = getEnvelop(resultStr);
            if (envelop.isSuccessFlg()){
                result.setObj(envelop.getObj());
                result.setSuccessFlg(true);
            }else {
                result.setSuccessFlg(false);
                result.setErrorMsg(envelop.getErrorMsg());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("detachCard")
    @ResponseBody
    public Object detachCard(String id,String cardType){
        String url = "/cards/detach/";
        String resultStr = "";
        Envelop result = new Envelop();
        MultiValueMap<String,String> conditionMap = new LinkedMultiValueMap<>();
        conditionMap.add("card_type",cardType);;
        try {
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doPut(comUrl + url + id, conditionMap);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("attachCard")
    @ResponseBody
    public Object attachCard(String idCardNo,String id,String cardType){
        String url = "/cards/attach/";
        String resultStr = "";
        Envelop result = new Envelop();
        MultiValueMap<String,String> conditionMap = new LinkedMultiValueMap<>();
        conditionMap.add("id_card_no",idCardNo);
        conditionMap.add("card_type",cardType);
        try {
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doPut(comUrl + url + id, conditionMap);
            if(Boolean.parseBoolean(resultStr)){
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

}
