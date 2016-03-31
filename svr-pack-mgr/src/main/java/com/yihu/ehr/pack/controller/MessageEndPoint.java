package com.yihu.ehr.pack.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.Channel;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 16:24
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "message_service", description = "消息服务")
public class MessageEndPoint extends BaseRestController {
    @Autowired
    PackageService packageService;

    @Autowired
    RedisTemplate redisTemplate;

    @ApiOperation(value = "发送档案包解析消息", notes = "发送档案包解析消息")
    @RequestMapping(value = RestApi.Packages.ResolveMessage, method = RequestMethod.PUT)
    public void sendResolveMessage(
            @RequestParam(value = "id", defaultValue = "0dae000555fe19561d32324680720bc8") String id){
        Package pack = packageService.getPackage(id);

        redisTemplate.convertAndSend(Channel.PackageResolve, pack.getId());
    }
}
