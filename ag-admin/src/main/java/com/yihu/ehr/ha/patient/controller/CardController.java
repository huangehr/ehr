package com.yihu.ehr.ha.patient.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.Version1_0 + "/card")
@RestController
public class CardController extends BaseRestController {

    @RequestMapping(value = "/getCards", method = RequestMethod.GET)
    public Object getCards(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                           @PathVariable(value = "api_version") String apiVersion,
                           @ApiParam(name = "idCardNo", value = "身份证号")
                           @RequestParam(value = "idCardNo") String idCardNo,
                           @ApiParam(name = "cardNo", value = "卡号")
                           @RequestParam(value = "cardNo") String cardNo,
                           @ApiParam(name = "cardType", value = "卡类别")
                           @RequestParam(value = "cardType") String cardType,
                           @ApiParam(name = "page", value = "当前页数", defaultValue = "0")
                           @RequestParam(value = "page") int page,
                           @ApiParam(name = "rows", value = "每页显示数", defaultValue = "15")
                           @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/newCards", method = RequestMethod.GET)
    public Object getNewCard(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "idCardNo", value = "身份证号")
                             @RequestParam(value = "idCardNo") String idCardNo,
                             @ApiParam(name = "cardNo", value = "卡号")
                             @RequestParam(value = "cardNo") String cardNo,
                             @ApiParam(name = "cardType", value = "卡类别")
                             @RequestParam(value = "cardType") String cardType,
                             @ApiParam(name = "page", value = "当前页数", defaultValue = "0")
                             @RequestParam(value = "page") int page,
                             @ApiParam(name = "rows", value = "每页显示数", defaultValue = "15")
                             @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/cardDetail", method = RequestMethod.GET)
    public Object getCardDetailById(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "api_version") String apiVersion,
                                    @ApiParam(name = "cardId", value = "卡ID")
                                    @RequestParam(value = "cardId") String cardId,
                                    @ApiParam(name = "cardType", value = "卡类别")
                                    @RequestParam(value = "cardType") String cardType) {
        //TODO:根据卡ID获取卡信息
        return null;
    }

    @RequestMapping(value = "/detachCard", method = RequestMethod.GET)
    public Object detachCard(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "cardId", value = "卡ID")
                             @RequestParam(value = "cardId") String cardId,
                             @ApiParam(name = "cardType", value = "卡类别")
                             @RequestParam(value = "cardType") String cardType) {
        //TODO：卡解除绑定
        return null;
    }

    @RequestMapping(value = "/attachCard", method = RequestMethod.GET)
    public Object attachCard(@ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "api_version") String apiVersion,
                             @ApiParam(name = "idCardNo", value = "身份证号")
                             @RequestParam(value = "idCardNo") String idCardNo,
                             @ApiParam(name = "cardId", value = "卡ID")
                             @RequestParam(value = "cardId") String cardId,
                             @ApiParam(name = "cardType", value = "卡类别")
                             @RequestParam(value = "cardType") String cardType) {
        //TODO:绑定卡信息
        return null;
    }
}
