package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.ArchiveSecuritySetting;
import com.yihu.ehr.model.archivesecurity.MArchiveSecuritySetting;
import com.yihu.ehr.archivrsecurity.service.ArchiveSecuritySettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ArchiveSecuritySetting", description = "档案安全设置", tags = {"档案安全设置"})
public class ArchiveSecuritySettingEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ArchiveSecuritySettingService archiveSecuritySettingService;

    @ApiOperation(value = "档案安全设置列表查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveSecuritySetting, method = RequestMethod.GET)
    public Collection<MArchiveSecuritySetting> searchArchiveSecuritySetting(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<ArchiveSecuritySetting> archiveSecuritySettingList = archiveSecuritySettingService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response, archiveSecuritySettingService.getCount(filters), page, size);

        return convertToModels(archiveSecuritySettingList, new ArrayList<MArchiveSecuritySetting>(archiveSecuritySettingList.size()), MArchiveSecuritySetting.class, fields);
    }

    @ApiOperation(value = "档案安全设置新增")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveSecuritySetting, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MArchiveSecuritySetting createArchiveSecuritySetting(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) {
        ArchiveSecuritySetting archiveSecuritySetting = toEntity(jsonData, ArchiveSecuritySetting.class);
        archiveSecuritySettingService.save(archiveSecuritySetting);
        return convertToModel(archiveSecuritySetting, MArchiveSecuritySetting.class, null);
    }

    @ApiOperation(value = "档案安全设置查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveSecuritySettingUser, method = RequestMethod.GET)
    public MArchiveSecuritySetting getArchiveSecuritySetting(
            @ApiParam(name = "user_id", value = "user_id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        ArchiveSecuritySetting archiveSecuritySetting = archiveSecuritySettingService.findByUserId(userId);
        if (archiveSecuritySetting == null) {
            return null;
        }else {
            return convertToModel(archiveSecuritySetting, MArchiveSecuritySetting.class);
        }

    }

    @ApiOperation(value = "档案安全设置修改")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveSecuritySetting, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MArchiveSecuritySetting updateArchiveSecuritySetting(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) throws Exception {
        ArchiveSecuritySetting archiveSecuritySetting = toEntity(jsonData, ArchiveSecuritySetting.class);
        archiveSecuritySettingService.save(archiveSecuritySetting);
        return convertToModel(archiveSecuritySetting, MArchiveSecuritySetting.class, null);
    }

    @ApiOperation(value = "档案安全设置删除")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveSecuritySettingUser, method = RequestMethod.DELETE)
    public boolean deleteArchiveSecuritySetting(
            @ApiParam(name = "user_id", value = "user_id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception{
        archiveSecuritySettingService.deleteByUserId(userId);
        return true;
    }


    @ApiOperation(value = "档案安全密码验证")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveSecuritySettingKeyAuthen, method = RequestMethod.POST)
    public boolean ArchiveSecuritySettingAuthentication(
            @ApiParam(name = "user_id", value = "user_id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "security_key", value = "security_key", defaultValue = "")
            @RequestParam(value = "security_key") String securityKey) throws Exception{
        return archiveSecuritySettingService.ArchiveSecuritySettingAuthentication(userId,securityKey);
    }


}
