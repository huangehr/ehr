package com.yihu.ehr.patient.controller;

import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.patient.CardDetailModel;
import com.yihu.ehr.agModel.patient.CardModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.patient.service.CardClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.patient.MAbstractCard;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "card", description = "就诊卡管理（旧）", tags = {"就诊卡管理（旧）"})
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
    @RequestMapping(value = "/cards/binding", method = RequestMethod.GET)
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
            cardModel.setCreateDate(DateToString(info.getCreateDate(),AgAdminConstants.DateFormat));
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
    @RequestMapping(value = "/cards/un_binding", method = RequestMethod.GET)
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
            cardModel.setCreateDate(info.getCreateDate() == null?"": DateTimeUtil.simpleDateTimeFormat(info.getCreateDate()).substring(0, 10) );
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
    @RequestMapping(value = "/cards/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据卡号和卡类型查找卡")
    public Envelop getCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType) throws Exception {

        MAbstractCard cardInfo = cardClient.getCard(id, cardType);
        CardDetailModel detailModel = convertToModel(cardInfo,CardDetailModel.class);
        if(detailModel==null)
        {
            return failed("数据获取失败!");
        }
        detailModel.setCreateDate(DateToString(cardInfo.getCreateDate(), AgAdminConstants.DateFormat));
        detailModel.setReleaseDate(DateToString(cardInfo.getReleaseDate(),AgAdminConstants.DateTimeFormat));
        detailModel.setValidityDateBegin(DateToString(cardInfo.getValidityDateBegin(),AgAdminConstants.DateTimeFormat));
        detailModel.setValidityDateEnd(DateToString(cardInfo.getValidityDateEnd(),AgAdminConstants.DateTimeFormat));
        MConventionalDict dict=null;
        if (!StringUtils.isEmpty(detailModel.getCardType())) {
            dict = conventionalDictEntryClient.getCardType(detailModel.getCardType());
            detailModel.setTypeName(dict.getValue());
        }

        if (!StringUtils.isEmpty(detailModel.getStatus())) {
            dict = conventionalDictEntryClient.getCardStatus(detailModel.getStatus());
            detailModel.setStatusName(dict.getValue());
        }

        if (!StringUtils.isEmpty(detailModel.getReleaseOrg())){
            MOrganization organization = orgClient.getOrg(detailModel.getReleaseOrg());
            detailModel.setReleaseOrgName(organization.getFullName());
        }

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
    @RequestMapping(value = "/cards/detach/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡号和卡类型解绑卡")
    public boolean detachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
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
