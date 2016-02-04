package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.Result;
import com.yihu.ehr.patient.service.card.*;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/8/20.
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/card")
@Api(protocols = "https", value = "card", description = "卡管理", tags = {"卡管理"})
public class CardController extends BaseRestController {
    @Autowired
    private CardManager cardManager;

    /**
     * 已绑定的卡
     * @param idCardNo
     * @param searchNm
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping("own_cards")
    public Object searchCard(String idCardNo,String searchNm, String cardType, int page, int rows) throws Exception{
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("cardType","bound_card");
        conditionMap.put("idCardNo",idCardNo);
        conditionMap.put("number",searchNm);
        conditionMap.put("type",cardType);
        conditionMap.put("page",page);
        conditionMap.put("rows",rows);

        List<AbstractCard> cardAbstractCardList = cardManager.searchAbstractCard(conditionMap);
        Integer totalCount = cardManager.searchCardInt(conditionMap, false);
        Result result = new Result();
        result.setObj(cardAbstractCardList);
        result.setTotalCount(totalCount);
        return getResult(cardAbstractCardList,totalCount,page,rows);
    }

    /**
     * 未绑定的卡
     * @param searchNm
     * @param searchType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping("un_bind_cards")
    @ResponseBody
    public Object searchNewCard(String searchNm,String searchType, int page, int rows) throws Exception{
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("cardType","not_bound_card");
        conditionMap.put("number",searchNm);
        conditionMap.put("type",searchType);
        conditionMap.put("page",page);
        conditionMap.put("rows",rows);
        List<AbstractCard> cardBrowseModelList = cardManager.searchAbstractCard(conditionMap);
        Integer totalCount = cardManager.searchCardInt(conditionMap, false);
        return getResult(cardBrowseModelList, totalCount, page, rows);
    }

    @RequestMapping("getCard")
    public Object getCard(String id,String type){
        AbstractCard card = cardManager.getCard(id, type);
        CardModel cardModel = cardManager.getCard(card);
        Map<String,CardModel> data = new HashMap<>();
        data.put("cardModel", cardModel);
        Result result = new Result();
        result.setObj(data);

        return result;
    }

    @RequestMapping("detachCard")
    public boolean detachCard(String id,String type){
        AbstractCard card = cardManager.getCard(id, type);
        return cardManager.detachCard(card);
    }

    @RequestMapping("attachCard")
    public Object attachCard(String idCardNo,String id,String type){
        AbstractCard card = cardManager.getCard(id, type);
        if(cardManager.attachCardWith(card, idCardNo)){
            return true;
        }else{
            return false;
        }
    }

}
