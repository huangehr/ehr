package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.RestTemplates;
import com.yihu.ehr.util.controller.BaseUIController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        String url = "/cards/id_card_no";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id_card_no", idCardNo);
        params.put("number", searchNm);
        params.put("card_type", cardType);
        params.put("page", page);
        params.put("rows", rows);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
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
        String url = "/cards";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("idCardNo", idCardNo);
        params.put("number", searchNm);
        params.put("card_type", searchType);
        params.put("page", page);
        params.put("rows", rows);
        try {
            //todo 返回result.toJson()
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        Map<String, Object> conditionMap = new HashMap<>();
//        Map<String, Object> infoMap = null;
//        conditionMap.put("searchIdCardNo",idCardNo);
//        conditionMap.put("number",searchNm);
//        conditionMap.put("type",searchType);
//        conditionMap.put("page",page);
//        conditionMap.put("rows",rows);
//
//        List<CardBrowseModel> cardBrowseModelList = cardManager.searchCardBrowseModel(conditionMap);
//        Integer totalCount = cardManager.searchCardInt(conditionMap, false);
//
//        Result result = getResult(cardBrowseModelList, totalCount, page, rows);
//
//        return result.toJson();
    }

    @RequestMapping("getCard")
    @ResponseBody
    public Object getCard(String objectId,String type){
        String url = "/cards/id/card_type";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",objectId);
        params.put("card_type",type);
        try{
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doGet(comUrl+url,params);
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
//        XCard card = cardManager.getCard(objectId, absDictEManage.getCardType(type));
//        CardModel cardModel = cardManager.getCard(card);
//        Map<String,CardModel> data = new HashMap<>();
//        data.put("cardModel", cardModel);
//        Result result = new Result();
//        result.setObj(data);
//
//        return result.toJson();

    }

    @RequestMapping("detachCard")
    @ResponseBody
    public Object detachCard(String objectId,String type){
        String url = "/cards/id/";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id",objectId);
        params.put("card_type",type);
        try {
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doPut(comUrl + url + type, params);
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
//        XCard card = cardManager.getCard(objectId, absDictEManage.getCardType(type));
//        Result result = getSuccessResult(cardManager.detachCard(card));
//        return  result.toJson();
    }

    @RequestMapping("attachCard")
    @ResponseBody
    public Object attachCard(String idCardNo,String objectId,String type){
        String url = "/cards/attach/";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("id_card_no",idCardNo);
        params.put("card_type",type);
        try {
            RestTemplates templates = new RestTemplates();
            resultStr = templates.doPut(comUrl + url + objectId, params);
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
//        XCard card = cardManager.getCard(objectId, absDictEManage.getCardType(type));
//        Result result = getSuccessResult(cardManager.attachCardWith(card, new DemographicId(idCardNo)));
//        return  result.toJson();
    }

}
