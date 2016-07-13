package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.ScArchivePrivate;
import com.yihu.ehr.archivrsecurity.service.ArchivePrivateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @RequestMapping(value="/archiveprivate/{userId}",method = RequestMethod.POST)
    public List<ScArchivePrivate> saveArchivePrivate(
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

        return archivePrivateService.saveArchivePrivate(list);
    }

    @ApiOperation(value="档案状态更新")
    @RequestMapping(value="/archiveprivate/{userId}",method = RequestMethod.PUT)
    public List<ScArchivePrivate> updateArchivePrivate(
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

        return archivePrivateService.updateArchivePrivate(list);
    }

    @ApiOperation(value="档案状态删除")
    @RequestMapping(value="/archiveprivate/{userId}",method = RequestMethod.DELETE)
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
    @RequestMapping(value="/archiveprivate/{userId}",method = RequestMethod.GET)
    public List<ScArchivePrivate> searchArchivePrivate(
            @ApiParam(value = "账户标识")
            @PathVariable(value="userId")String userId,
            @ApiParam(value = "公开隐藏状态")
            @RequestParam(value="status")int status)
    {
        return archivePrivateService.findByUserIdAndStatus(userId,status);
    }

    @ApiOperation(value="档案状态查询")
    @RequestMapping(value="/archiveprivate/{userId}/{rowKey}",method = RequestMethod.GET)
    public ScArchivePrivate searchArchivePrivate(
            @ApiParam(value = "账户标识")
            @PathVariable(value="userId")String userId,
            @ApiParam(value = "公开隐藏状态")
            @PathVariable(value="rowKey")String rowKey)
    {
        return archivePrivateService.findByUserIdAndRowKey(userId,rowKey);
    }

}
