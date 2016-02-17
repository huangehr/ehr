package com.yihu.ehr.ha.patient.service;

import com.yihu.ehr.model.patient.MAbstractCard;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/2/16.
 */
@FeignClient("svr-patient")
public interface CardClient {

    @RequestMapping(value = "/rest/v1.0/cards/bound_card",method = RequestMethod.GET)
    @ApiOperation(value = "获取已绑定的卡列表")
    Envelop searchCard(
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @RequestParam(value = "id_card_no") String idCardNo,
            @ApiParam(name = "number", value = "卡号", defaultValue = "")
            @RequestParam(value = "number") String number,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) ;

    /**
     * 未绑定的卡
     * @param number
     * @param cardType
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/cards/not_bound_card",method = RequestMethod.GET)
    @ApiOperation(value = "未绑定的卡")
    Envelop searchNewCard(
            @ApiParam(name = "number", value = "卡号", defaultValue = "")
            @RequestParam(value = "number") String number,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @RequestParam(value = "card_type") String cardType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) ;

    /**
     * 根据卡号和卡类型查找卡
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/cards/{id}/{card_type}",method = RequestMethod.GET)
    @ApiOperation(value = "根据卡号和卡类型查找卡")
    MAbstractCard getCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType);

    /**
     * 根据卡编号和卡类型解绑卡
     * @param id
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/cards/detach/{id}/{card_type}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡号和卡类型解绑卡")
    boolean detachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) ;

    /**
     * 根据卡编号，身份证号，卡类型绑定卡
     * @param id
     * @param idCardNo
     * @param cardType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/rest/v1.0/cards/attach/{id}/{id_card_no}/{card_type}",method = RequestMethod.PUT)
    @ApiOperation(value = "根据卡编号(卡主键，卡的唯一标识)，身份证号，卡类型绑定卡")
    boolean attachCard(
            @ApiParam(name = "id", value = "卡号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "id_card_no", value = "身份证号", defaultValue = "")
            @PathVariable(value = "id_card_no") String idCardNo,
            @ApiParam(name = "card_type", value = "卡类别", defaultValue = "")
            @PathVariable(value = "card_type") String cardType) ;
}
