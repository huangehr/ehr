package com.yihu.ehr.ha.patient.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.patient.service.CardClient;
import com.yihu.ehr.model.patient.MAbstractCard;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 )
@RestController
public class CardController {

    @Autowired
    private CardClient cardClient;

    @RequestMapping(value = "/cards/bound_card",method = RequestMethod.GET)
    @ApiOperation(value = "获取已绑定的卡列表")
    public Envelop searchCard(
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
        return cardClient.searchCard(idCardNo,number,cardType,page,rows);
    }

    /**
     * 未绑定的卡
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/not_bound_card",method = RequestMethod.GET)
    @ApiOperation(value = "未绑定的卡")
    public Envelop searchNewCard(
            @ApiParam(name = "number", value = "卡号", defaultValue = "")
            @RequestParam(value = "number") String number,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) throws Exception{
        return cardClient.searchNewCard(number,cardType,page,rows);
    }

    /**
     * 根据卡号和卡类型查找卡
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/{id}/{card_type}",method = RequestMethod.GET)
    @ApiOperation(value = "根据卡号和卡类型查找卡")
    public MAbstractCard getCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) throws Exception{
        return cardClient.getCard(id,cardType);
    }

    /**
     * 根据卡编号和卡类型解绑卡
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/detach/{id}/{card_type}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡号和卡类型解绑卡")
    public boolean detachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) throws Exception{
        return cardClient.detachCard(id,cardType);
    }

    /**
     * 根据卡编号，身份证号，卡类型绑定卡
     * @param id
     * @param idCardNo
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cards/attach/{id}/{id_card_no}/{card_type}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡编号(卡主键，卡的唯一标识)，身份证号，卡类型绑定卡")
    public boolean attachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) throws Exception{
        return cardClient.attachCard(id,idCardNo,cardType);
    }
}
