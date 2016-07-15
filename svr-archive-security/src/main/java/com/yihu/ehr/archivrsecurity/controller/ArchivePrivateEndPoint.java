package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.ScArchivePrivate;
import com.yihu.ehr.archivrsecurity.service.ArchivePrivateService;
import com.yihu.ehr.model.archivesecurity.MScArchivePrivate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lyr on 2016/7/11.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value="archive_private",description = "档案公开状态设置")
public class ArchivePrivateEndPoint extends BaseRestEndPoint {

    @Autowired
    ArchivePrivateService archivePrivateService;

    @ApiOperation(value="档案状态新增")
    @RequestMapping(value= ServiceApi.ArchiveSecurity.ArchivePrivate,method = RequestMethod.POST)
    public Collection<MScArchivePrivate> saveArchivePrivate(
            @ApiParam(value = "账户标识")
            @PathVariable(value="userId")String userId,
            @ApiParam(value = "档案标识")
            @RequestParam(value="rowKey",required = true)String rowKey,
            @ApiParam(value = "公开隐藏状态")
            @RequestParam(value="status",required = true)int status) throws Exception
    {
        String[] rowkeys = rowKey.split(",");
        List<ScArchivePrivate> list = new ArrayList<ScArchivePrivate>();

        for(String key : rowkeys)
        {
            ScArchivePrivate archivePrivate = new ScArchivePrivate();

            archivePrivate.setRowKey(key);
            archivePrivate.setUserId(userId);
            archivePrivate.setStatus(status);

            list.add(archivePrivate);
        }

        archivePrivateService.saveArchivePrivate(list);

        return convertToModels(list, new ArrayList<MScArchivePrivate>(list.size()), MScArchivePrivate.class, "");
    }

    @ApiOperation(value="档案状态更新")
    @RequestMapping(value=ServiceApi.ArchiveSecurity.ArchivePrivate,method = RequestMethod.PUT)
    public Collection<MScArchivePrivate> updateArchivePrivate(
            @ApiParam(value = "账户标识")
            @PathVariable(value="userId")String userId,
            @ApiParam(value = "档案标识")
            @RequestParam(value="rowKey",required = true)String rowKey,
            @ApiParam(value = "公开隐藏状态")
            @RequestParam(value="status",required = true)int status) throws Exception
    {
        String[] rowkeys = rowKey.split(",");
        List<ScArchivePrivate> list = new ArrayList<ScArchivePrivate>();

        for(String key : rowkeys)
        {
            ScArchivePrivate archivePrivate = new ScArchivePrivate();

            archivePrivate.setRowKey(key);
            archivePrivate.setUserId(userId);
            archivePrivate.setStatus(status);

            list.add(archivePrivate);
        }

        return convertToModels(list, new ArrayList<MScArchivePrivate>(list.size()), MScArchivePrivate.class, "");
    }

    @ApiOperation(value="档案状态删除")
    @RequestMapping(value=ServiceApi.ArchiveSecurity.ArchivePrivate,method = RequestMethod.DELETE)
    public boolean deleteArchivePrivate(
            @ApiParam(value = "账户标识")
            @PathVariable(value="userId")String userId,
            @ApiParam(value = "档案标识")
            @RequestParam(value="rowKey",required = true)String rowKey) throws Exception
    {
        archivePrivateService.deleteArchivePrivate(userId,rowKey);
        return true;
    }

    @ApiOperation(value="档案状态查询")
    @RequestMapping(value=ServiceApi.ArchiveSecurity.ArchivePrivate,method = RequestMethod.GET)
    public Collection<MScArchivePrivate> searchArchivePrivate(
            @ApiParam(value = "账户标识")
            @PathVariable(value="userId")String userId,
            @ApiParam(value = "公开隐藏状态")
            @RequestParam(value="status",required = false)int status)
    {
        List<ScArchivePrivate> list = archivePrivateService.findByUserIdAndStatus(userId,status);

        return convertToModels(list, new ArrayList<MScArchivePrivate>(list.size()), MScArchivePrivate.class, "");
    }

    @ApiOperation(value="档案状态查询")
    @RequestMapping(value=ServiceApi.ArchiveSecurity.ArchivePrivateRowKey,method = RequestMethod.GET)
    public MScArchivePrivate searchArchivePrivate(
            @ApiParam(value = "账户标识")
            @PathVariable(value="userId")String userId,
            @ApiParam(value = "档案标识")
            @PathVariable(value="rowKey")String rowKey)
    {
        return convertToModel(archivePrivateService.findByUserIdAndRowKey(userId,rowKey),MScArchivePrivate.class);
    }

}
