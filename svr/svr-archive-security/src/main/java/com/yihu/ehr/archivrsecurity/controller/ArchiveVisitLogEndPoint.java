package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.ArchiveVisitLog;
import com.yihu.ehr.model.archivesecurity.MArchiveVisitLog;
import com.yihu.ehr.archivrsecurity.service.ArchiveVisitLogService;
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
@Api(value = "ArchiveVisitLog", description = "档案访问日志管理", tags = {"档案访问日志管理"})
public class ArchiveVisitLogEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ArchiveVisitLogService archiveVisitLogService;

    @ApiOperation(value = "档案访问日志列表查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveLogs, method = RequestMethod.GET)
    public Collection<MArchiveVisitLog> searchArchiveVisitLog(
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
        List<MArchiveVisitLog> archiveVisitLogList = archiveVisitLogService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response, archiveVisitLogService.getCount(filters), page, size);

        return convertToModels(archiveVisitLogList, new ArrayList<MArchiveVisitLog>(archiveVisitLogList.size()), MArchiveVisitLog.class, fields);
    }

    @ApiOperation(value = "档案访问日志新增")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.ArchiveLogs, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MArchiveVisitLog createArchiveVisitLog(
            @ApiParam(name = "json_data", value = "json对象")
            @RequestBody String jsonData) {
        ArchiveVisitLog archiveVisitLog = toEntity(jsonData, ArchiveVisitLog.class);
        archiveVisitLogService.save(archiveVisitLog);
        return convertToModel(archiveVisitLog, MArchiveVisitLog.class, null);
    }


}
