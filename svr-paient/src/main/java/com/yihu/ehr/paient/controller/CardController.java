package com.yihu.ehr.paient.controller;

import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.paient.feignClient.dict.ConventionalDictClient;
import com.yihu.ehr.paient.service.card.CardBrowseModel;
import com.yihu.ehr.paient.service.card.CardManager;
import com.yihu.ehr.paient.service.card.CardModel;
import com.yihu.ehr.paient.service.demographic.DemographicId;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.smartcardio.Card;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/8/20.
 */
@RestController
@RequestMapping("/card")
public class CardController extends BaseRestController {
    @Autowired
    private CardManager cardManager;

    public CardController(){}

    @Autowired
    private ConventionalDictClient conventionalDictClient;



    @RequestMapping("searchCard")
    public Object searchCard(String idCardNo,String searchNm, int page, int rows){
        Map<String, Object> conditionMap = new HashMap<>();
        Map<String, Object> infoMap = null;
        //conditionMap.put("idCardNo",encodeStr(idCardNo));
        conditionMap.put("number",searchNm);

        List<CardBrowseModel> cardBrowseModelList = cardManager.searchCardBrowseModel(conditionMap);
        Integer totalCount = cardManager.searchCardInt(conditionMap);
        Result result = new Result();
        result.setObj(cardBrowseModelList);
        result.setTotalCount(totalCount);
        return result;
    }

    @RequestMapping("searchNewCard")
    //搜索新卡
    public Object searchNewCard(String idCardNo,String searchNm,String searchType, int page, int rows){
        Map<String, Object> conditionMap = new HashMap<>();
        Map<String, Object> infoMap = null;
        conditionMap.put("searchIdCardNo",idCardNo);
        conditionMap.put("number",searchNm);
        conditionMap.put("type",searchType);

        List<CardBrowseModel> cardBrowseModelList = cardManager.searchCardBrowseModel(conditionMap);
        Integer totalCount = cardManager.searchCardInt(conditionMap);

        Result result =  new Result();
        result.setObj(cardBrowseModelList);
        result.setTotalCount(totalCount);

        return result;
    }

    @RequestMapping("getCard")
    public Object getCard(String objectId,String type){
        Card card = cardManager.getCard(objectId, type);
        CardModel cardModel = cardManager.getCard(card);
        Map<String,CardModel> data = new HashMap<>();
        data.put("cardModel", cardModel);
        Result result = new Result();
        result.setObj(data);

        return result;
    }

    @RequestMapping("detachCard")
    public Object detachCard(String objectId,String type){
        Card card = cardManager.getCard(objectId, type);
        if(cardManager.detachCard(card)){
            return "success";
        }else{
            return "faild";
        }
    }

    @RequestMapping("attachCard")
    public Object attachCard(String idCardNo,String objectId,String type){
        Card card = cardManager.getCard(objectId, type);
        if(cardManager.attachCardWith(card, new DemographicId(idCardNo))){
            return "success";
        }else{
            return "faild";
        }
    }

}
