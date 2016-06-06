package com.yihu.ehr.pack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.Channel;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import com.yihu.ehr.util.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.31 16:24
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "message_service", description = "消息服务")
public class MessageEndPoint extends EnvelopRestEndPoint {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PackageService packageService;

    @Autowired
    RedisTemplate redisTemplate;

    @ApiOperation(value = "发送档案包解析消息", notes = "发送档案包解析消息")
    @RequestMapping(value = ServiceApi.Packages.ResolveMessage, method = RequestMethod.PUT)
    public Map<String, String> sendResolveMessage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "receiveDate>2016-03-01,archiveStatus=Received,Acquired,Finished")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+receiveDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "count", value = "包数量", defaultValue = "500")
            @RequestParam(value = "count", required = false) int count,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException, JsonProcessingException {

        List<Package> packages = packageService.search(null, filters, sorts, 1, count);
        Collection<MPackage> mPackages = new HashSet<>(packages.size());
        mPackages = convertToModels(packages, mPackages, MPackage.class, "id,pwd,remotePath,clientId");

        Map<String, String> packs = new HashMap<>();
        for (MPackage pack : mPackages) {
            redisTemplate.convertAndSend(Channel.PackageResolve, objectMapper.writeValueAsString(pack));

            packs.put(pack.getId(), pack.getRemotePath());
        }

        pagedResponse(request, response, (long)mPackages.size(), 1, count);
        return packs;
    }

    /*@ApiOperation(value = "消息定时器", notes = "定时向档案解析服务发送解析消息。此方法用于兼容旧的无法使用事件驱动解析档案包。")
    @RequestMapping(value = ServiceApi.Packages.MessageTimer, method = RequestMethod.GET)
    public ResponseEntity<String> enablePackagePollsTimer(
            @ApiParam(name = "enable", value = "true:执行 , false: 暂停", defaultValue = "true")
            @RequestParam(value = "enable") boolean enable) {

        List<CronTask> cronTaskList = taskConfiguration.getTaskRegistrar().getCronTaskList();
        for (CronTask cronTask : cronTaskList) {
            System.out.println(cronTask.getExpression());
        }

        return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
    }

    @Autowired
    private TaskConfiguration taskConfiguration;*/
}
