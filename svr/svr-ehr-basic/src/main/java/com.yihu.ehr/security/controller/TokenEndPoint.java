package com.yihu.ehr.security.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.security.MToken;
import com.yihu.ehr.security.feign.AppClient;
import com.yihu.ehr.security.feign.UserClient;
import com.yihu.ehr.security.service.KeyManager;
import com.yihu.ehr.security.service.TokenManager;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.17 10:03
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "securities", description = "Token接口", tags = {"安全管理-Token接口"})
public class TokenEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private KeyManager keyManager;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AppClient appClient;

    @Autowired
    private UserClient userClient;

    /**
     * 获取用户临时Token，不存在则创建一个。用户验证由网关负责。
     * <p>
     * 参数内容：
     * scopes, array, 可选
     * note, string, 必选
     * fingerprint, string, 可选,为空则创建临时Token,即temp
     */
    @RequestMapping(value = ServiceApi.Securities.UserTokens, method = RequestMethod.PUT)
    @ApiOperation(value = "创建用户Token，存在直接返回", produces = "application/json", notes = "此Token用于第三方与平台之间的会话")
    public MToken createUserToken(
            @ApiParam(required = true, name = "user_id", value = "用户id")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(required = true, name = "param", value = "参数")
            @RequestParam(value = "param", required = true) String param) throws Exception {
        return null;
    }

    /**
     * 为客户端应用创建一个临时Token，不存在则创建一个。客户端验证由网关负责。
     * <p>
     * scopes, array
     * note, string 必填
     * note_url, string
     * client_id, string
     * client_secret, string
     * fingerprint, string, 可选,为空则创建临时Token,即temp
     *
     * @param clientId
     * @return
     */
    @RequestMapping(value = ServiceApi.Securities.ClientTokens, method = RequestMethod.PUT)
    @ApiOperation(value = "创建应用Token，存在直接返回", produces = "application/json", notes = "此Token用于第三方与平台之间的会话")
    public MToken createClientToken(@ApiParam(required = true, name = "client_id", value = "应用id")
                                    @PathVariable(value = "client_id") String clientId,
                                    @ApiParam(required = true, name = "param", value = "参数")
                                    @RequestParam(value = "param", required = true) String param) {
        return null;
    }
}
