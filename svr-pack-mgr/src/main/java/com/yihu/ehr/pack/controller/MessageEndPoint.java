package com.yihu.ehr.pack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.Channel;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
    ObjectMapper objectMapper;

    @Autowired
    PackageService packageService;

    @Autowired
    RedisTemplate redisTemplate;

    @ApiOperation(value = "发送档案包解析消息", notes = "发送档案包解析消息")
    @RequestMapping(value = RestApi.Packages.ResolveMessage, method = RequestMethod.PUT)
    public void sendResolveMessage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "receiveDate>2016-03-01,archiveStatus=Received,Acquired,Finished")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+receiveDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "count", value = "包数量", defaultValue = "500")
            @RequestParam(value = "count", required = false) int count) throws ParseException, JsonProcessingException {
        List<Package> packages = packageService.search(null, filters, sorts, 1, count);
        Collection<MPackage> mPackages = new HashSet<>(packages.size());
        mPackages = convertToModels(packages, mPackages, MPackage.class, "id,pwd,remotePath");

        for (MPackage pack : mPackages) {
            redisTemplate.convertAndSend(Channel.PackageResolve, objectMapper.writeValueAsString(pack));
        }
    }
}
