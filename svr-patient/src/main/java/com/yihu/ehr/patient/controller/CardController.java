package com.yihu.ehr.patient.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.patient.feign.GeographyClient;
import com.yihu.ehr.patient.feign.ConventionalDictClient;
import com.yihu.ehr.patient.feign.OrgClient;
import com.yihu.ehr.patient.service.card.*;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zqb on 2015/8/20.
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "cards", description = "卡管理", tags = {"卡管理"})
public class CardController extends BaseRestController {
    @Autowired
    private CardManager cardManager;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @Autowired
    private GeographyClient addressClient;

    @Autowired
    private OrgClient orgClient;

    /**
     * 已绑定的卡
     * @param apiVersion
     * @param idCardNo
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/bound_card",method = RequestMethod.GET)
    @ApiOperation(value = "获取已绑定的卡列表")
    public Object searchCard(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
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
        conditionMap.put("cardType","bound_card");
        conditionMap.put("idCardNo",idCardNo);
        conditionMap.put("number",number);
        conditionMap.put("type",cardType);
        conditionMap.put("page",page);
        conditionMap.put("rows",rows);
        List<AbstractCard> cardAbstractCardList = cardManager.searchAbstractCard(conditionMap);
        Integer totalCount = cardManager.searchCardInt(conditionMap, false);
        return getResult(cardAbstractCardList,totalCount,page,rows);
    }

    /**
     * 未绑定的卡
     * @param apiVersion
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/not_bound_card",method = RequestMethod.GET)
    @ApiOperation(value = "未绑定的卡")
    public Object searchNewCard(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "number", value = "卡号", defaultValue = "")
            @RequestParam(value = "number") String number,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) throws Exception{
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("cardType","not_bound_card");
        conditionMap.put("number",number);
        conditionMap.put("type",cardType);
        conditionMap.put("page",page);
        conditionMap.put("rows",rows);
        List<AbstractCard> cardBrowseModelList = cardManager.searchAbstractCard(conditionMap);
        Integer totalCount = cardManager.searchCardInt(conditionMap, false);
        return getResult(cardBrowseModelList, totalCount, page, rows);
    }

    /**
     * 根据卡号和卡类型查找卡
     * @param apiVersion
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/{id}/{card_type}",method = RequestMethod.GET)
    @ApiOperation(value = "根据卡号和卡类型查找卡")
    public MAbstractCard getCard(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) throws Exception{
        AbstractCard card = cardManager.getCard(apiVersion,id, cardType);
        MAbstractCard abstractCardModel = convertToModel(card,MAbstractCard.class);
        abstractCardModel.setStatus(conventionalDictClient.getCardStatus(apiVersion,card.getStatus()));
        abstractCardModel.setType(conventionalDictClient.getCardType(apiVersion,card.getType()));
        abstractCardModel.setLocal(addressClient.getAddressById(apiVersion,card.getLocal()));
        abstractCardModel.setReleaseOrg(orgClient.getOrg(apiVersion,card.getReleaseOrg()));
        return abstractCardModel;
    }

    /**
     * 根据卡编号和卡类型解绑卡
     * @param apiVersion
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/detach/{id}/{card_type}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡号和卡类型解绑卡")
    public boolean detachCard(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) throws Exception{
        AbstractCard card = cardManager.getCard(apiVersion,id, cardType);
        return cardManager.detachCard(apiVersion,card);
    }

    /**
     * 根据卡编号，身份证号，卡类型绑定卡
     * @param apiVersion
     * @param id
     * @param idCardNo
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/attach/{id}/{id_card_no}/{card_type}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡编号(卡主键，卡的唯一标识)，身份证号，卡类型绑定卡")
    public boolean attachCard(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) throws Exception{
        AbstractCard card = cardManager.getCard(apiVersion,id, cardType);
        return cardManager.attachCardWith(apiVersion,card, idCardNo);
    }

}
