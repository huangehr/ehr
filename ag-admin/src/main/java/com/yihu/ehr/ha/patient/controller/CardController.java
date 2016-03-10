package com.yihu.ehr.ha.patient.controller;

import com.yihu.ehr.agModel.patient.CardDetailModel;
import com.yihu.ehr.agModel.patient.CardModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.ha.organization.service.OrganizationClient;
import com.yihu.ehr.ha.patient.service.CardClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.patient.MAbstractCard;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
public class CardController extends BaseController {

    @Autowired
    private CardClient cardClient;

    @Autowired
    private ConventionalDictEntryClient conventionalDictEntryClient;

    @Autowired
    private OrganizationClient orgClient;
    /**
     * 根据身份证好查询相对应的卡列表
     *
     * @param idCardNo
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/id_card_no", method = RequestMethod.GET)
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
            @RequestParam(value = "rows") Integer rows) throws Exception {
        ResponseEntity<List<MAbstractCard>> responseEntity = cardClient.searchCardBinding(idCardNo, number, cardType, page, rows);
        List<MAbstractCard> mAbstractCards=responseEntity.getBody();
        List<CardModel> cardModels = new ArrayList<>();

        for (MAbstractCard info : mAbstractCards) {

            CardModel cardModel = convertToModel(info, CardModel.class);

            MConventionalDict dict =null;
            if(!StringUtils.isEmpty(cardModel.getCardType())) {
                dict = conventionalDictEntryClient.getCardType(cardModel.getCardType());
                cardModel.setTypeName(dict.getValue());
            }
            if (!StringUtils.isEmpty(cardModel.getStatus())) {
                dict = conventionalDictEntryClient.getCardStatus(cardModel.getStatus());
                cardModel.setStatusName(dict.getValue());
            }
            if(!StringUtils.isEmpty(cardModel.getReleaseOrg())) {
                MOrganization organization = orgClient.getOrg(cardModel.getReleaseOrg());
                cardModel.setReleaseOrgName(organization.getFullName());
            }

            cardModels.add(cardModel);
        }
        Envelop envelop = getResult(cardModels, getTotalCount(responseEntity), page, rows);
        return envelop;
    }

    /**
     * 查询未绑定的卡列表
     *
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards", method = RequestMethod.GET)
    @ApiOperation(value = "查询未绑定的卡列表")
    public Envelop searchCardUnBinding(
            @ApiParam(name = "number", value = "卡号", defaultValue = "")
            @RequestParam(value = "number") String number,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) throws Exception {

        ResponseEntity<List<MAbstractCard>> responseEntity  = cardClient.searchCardUnBinding(number, cardType, page, rows);
        List<MAbstractCard> mAbstractCards =responseEntity.getBody();
        List<CardModel> cardModels = new ArrayList<>();
        for (MAbstractCard info : mAbstractCards){

            CardModel cardModel = convertToModel(info, CardModel.class);

            MConventionalDict dict =null;
            if(!StringUtils.isEmpty(cardModel.getCardType())) {
                dict = conventionalDictEntryClient.getCardType(cardModel.getCardType());
                cardModel.setTypeName(dict.getValue());
            }
            if (!StringUtils.isEmpty(cardModel.getStatus())) {
                dict = conventionalDictEntryClient.getCardStatus(cardModel.getStatus());
                cardModel.setStatusName(dict.getValue());
            }
            if(!StringUtils.isEmpty(cardModel.getReleaseOrg())) {
                MOrganization organization = orgClient.getOrg(cardModel.getReleaseOrg());
                cardModel.setReleaseOrgName(organization.getFullName());
            }
            cardModels.add(cardModel);
        }
        Envelop envelop = getResult(cardModels, getTotalCount(responseEntity), page, rows);
        return envelop;
    }

    /**
     * 根据卡号和卡类型查找卡
     *
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/id/card_type", method = RequestMethod.GET)
    @ApiOperation(value = "根据卡号和卡类型查找卡")
    public Envelop getCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType) throws Exception {

        MAbstractCard cardInfo = cardClient.getCard(id, cardType);
        CardDetailModel detailModel = convertToModel(cardInfo,CardDetailModel.class);
        if(detailModel!=null)
        {
            return failed("数据获取失败!");
        }
        MConventionalDict dict = conventionalDictEntryClient.getCardType(detailModel.getCardType());
        detailModel.setTypeName(dict.getValue());

        dict = conventionalDictEntryClient.getCardStatus(detailModel.getStatus());
        detailModel.setStatusName(dict.getValue());

        MOrganization organization = orgClient.getOrg(detailModel.getReleaseOrg());
        detailModel.setReleaseOrgName(organization.getFullName());

        return success(detailModel);
    }

    /**
     * 根据卡编号和卡类型解绑卡
     *
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/id/{card_type}", method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡号和卡类型解绑卡")
    public boolean detachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType) throws Exception {
        return cardClient.detachCard(id, cardType);
    }

    /**
     * 根据卡编号，身份证号，卡类型绑定卡
     *
     * @param id
     * @param idCardNo
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/attach/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡编号(卡主键，卡的唯一标识)，身份证号，卡类型绑定卡")
    public boolean attachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @RequestParam(value = "id_card_no") String idCardNo,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType) throws Exception {
        return cardClient.attachCard(id, idCardNo, cardType);
    }
}
