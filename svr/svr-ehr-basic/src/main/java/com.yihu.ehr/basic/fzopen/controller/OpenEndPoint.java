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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 转发福州总部开放平台接口
 *
 * @author 张进军
 * @date 2018/4/12 18:42
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "转发福州总部开放平台接口", tags = {"转发福州总部开放平台接口"})
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
            String result = openService.callFzOpenApi(apiUrl, params);
            if (!StringUtils.isEmpty(result)) {
                envelop.setObj(objectMapper.readValue(result, Map.class));
                envelop.setSuccessFlg(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
