package com.yihu.ehr.basic.fzopen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.fzopen.service.OpenService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 转发福州总部内网、开放平台接口
 *
 * @author 张进军
 * @date 2018/4/12 18:42
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "转发福州总部内网、开放平台接口", tags = {"转发福州总部内网、开放平台接口"})
public class OpenEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OpenService openService;

    @ApiOperation("转发福州总部开放平台接口")
    @RequestMapping(value = ServiceApi.Fz.OpenApi, method = RequestMethod.POST)
    public Envelop fzOpenApi(
            @ApiParam(name = "apiUrl", value = "相对接口路径，不用\"/\"开头", required = true)
            @RequestParam(value = "apiUrl") String apiUrl,
            @ApiParam(name = "paramsJson", value = "参数JSON字符串，timestamp 不用传，后台添加", required = true)
            @RequestParam(value = "paramsJson") String paramsJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> params = objectMapper.readValue(paramsJson, Map.class);
            Map<String, Object> result = objectMapper.readValue(openService.callFzOpenApi(apiUrl, params), Map.class);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("转发福州总部内网接口")
    @RequestMapping(value = ServiceApi.Fz.InnerApi, method = RequestMethod.POST)
    public Envelop fzInnerApi(
            @ApiParam(name = "api", value = "API 名称，格式为 a.b.c", required = true)
            @RequestParam(value = "api") String api,
            @ApiParam(name = "paramsJson", value = "参数JSON字符串", required = true)
            @RequestParam(value = "paramsJson") String paramsJson,
            @ApiParam(name = "apiVersion", value = "API 版本号，版本号为整型，从数字 1 开始递增", required = true)
            @RequestParam(value = "apiVersion") int apiVersion) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> params = objectMapper.readValue(paramsJson, Map.class);
            String result = openService.callFzInnerApi(api, params, apiVersion);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
