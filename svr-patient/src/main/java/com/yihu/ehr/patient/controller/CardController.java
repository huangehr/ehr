package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.patient.MAbstractCard;
import com.yihu.ehr.patient.service.card.AbstractCard;
import com.yihu.ehr.patient.service.card.CardManager;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/8/20.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "cards", description = "卡管理", tags = {"卡管理"})
public class CardController extends BaseRestController {
    @Autowired
    private CardManager cardManager;

    /**
     * 根据身份证好查询相对应的卡列表
     * @param idCardNo
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/id_card_no",method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证好查询相对应的卡列表")
    public Envelop searchCardBinding(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @RequestParam(value = "id_card_no") String idCardNo,
            @ApiParam(name = "number", value = "卡号", defaultValue = "")
            @RequestParam(value = "number") String number,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) throws Exception{
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("type","bind_card");
        conditionMap.put("idCardNo",idCardNo);
        conditionMap.put("number",number);
        conditionMap.put("cardType",cardType);
        conditionMap.put("page",page);
        conditionMap.put("rows",rows);
        List<AbstractCard> cardAbstractCardList = cardManager.searchAbstractCard(conditionMap);
        Integer totalCount = cardManager.searchCardInt(conditionMap, false);

        List<MAbstractCard> mAbstractCards = (List<MAbstractCard>)convertToModels(cardAbstractCardList,new ArrayList<MAbstractCard>(cardAbstractCardList.size()), MAbstractCard.class, null);
        return getResult(mAbstractCards,totalCount);
    }

    /**
     * 查询未绑定的卡列表
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards",method = RequestMethod.GET)
    @ApiOperation(value = "查询未绑定的卡列表")
    public Envelop searchCardUnBinding(
            @ApiParam(name = "number", value = "卡号", defaultValue = "")
            @RequestParam(value = "number") String number,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) throws Exception{
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("type","not_bound_card");
        conditionMap.put("number",number);
        conditionMap.put("cardType",cardType);
        conditionMap.put("page",page);
        conditionMap.put("rows",rows);
        List<AbstractCard> cardBrowseModelList = cardManager.searchAbstractCard(conditionMap);
        List<MAbstractCard> mAbstractCards = (List<MAbstractCard>)convertToModels(cardBrowseModelList,new ArrayList<MAbstractCard>(cardBrowseModelList.size()), MAbstractCard.class, null);
        Integer totalCount = cardManager.searchCardInt(conditionMap, false);
        return getResult(mAbstractCards, totalCount);
    }

    /**
     * 根据卡号和卡类型查找卡
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/id/card_type",method = RequestMethod.GET)
    @ApiOperation(value = "根据卡号和卡类型查找卡")
    public MAbstractCard getCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType) throws Exception{
        AbstractCard card = cardManager.getCard(id, cardType);
        return convertToModel(card,MAbstractCard.class);
    }

    /**
     * 根据卡编号和卡类型解绑卡
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/id/card_type}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡号和卡类型解绑卡")
    public boolean detachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType) throws Exception{
        AbstractCard card = cardManager.getCard(id, cardType);
        return cardManager.detachCard(card);
    }

    /**
     * 根据卡编号，身份证号，卡类型绑定卡
     * @param id
     * @param idCardNo
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/attach/{id}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡编号(卡主键，卡的唯一标识)，身份证号，卡类型绑定卡")
    public boolean attachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @RequestParam(value = "id_card_no") String idCardNo,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType) throws Exception{
        AbstractCard card = cardManager.getCard(id, cardType);
        return cardManager.attachCardWith(card, idCardNo);
    }

}
