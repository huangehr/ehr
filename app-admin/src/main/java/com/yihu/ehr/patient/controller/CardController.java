package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import com.yihu.ehr.util.controller.BaseRestController;
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
public class CardController extends BaseRestController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");  //目前定义为rest
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

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
        String url = "/card/getCards";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("idCardNo", idCardNo);
        params.put("cardNo", searchNm);
        params.put("cardType", cardType);
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
//        conditionMap.put("idCardNo",encodeStr(idCardNo));
//        conditionMap.put("number",searchNm);
//        conditionMap.put("type",cardType);
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

    @RequestMapping("searchNewCard")
    @ResponseBody
    //搜索新卡
    public Object searchNewCard(String idCardNo,String searchNm,String searchType, int page, int rows){
        String url = "/card/newCards";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("idCardNo", idCardNo);
        params.put("cardNo", searchNm);
        params.put("cardType", searchType);
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
        String url = "/card/cardDetail";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("cardId",objectId);
        params.put("cardType",type);
        try{
            //todo 后台转换成model后传前台
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            result.setObj(resultStr);
            result.setSuccessFlg(true);
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
        String url = "/card/detachCard";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("cardId",objectId);
        params.put("cardType",type);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
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
        String url = "/card/attachCard";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("idCardNo",idCardNo);
        params.put("cardId",objectId);
        params.put("cardType",type);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
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
