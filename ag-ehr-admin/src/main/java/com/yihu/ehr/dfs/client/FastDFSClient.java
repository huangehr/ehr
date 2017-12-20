package com.yihu.ehr.dfs.client;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - 文件服务
 * Created by progr1mmer on 2017/12/1.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Dfs)
@RequestMapping(ApiVersion.Version1_0)
public interface FastDFSClient {

    @RequestMapping(value = ServiceApi.FastDFS.Upload, method = RequestMethod.POST)
    @ApiOperation(value = "文件上传", notes = "返回相关索引信息,以及HttpUrl下载连接")
    Envelop upload(
            @RequestPart(value = "file") MultipartFile file,
            @RequestParam(value = "creator") String creator,
            @RequestParam(value = "objectId") String objectId);

    @RequestMapping(value = ServiceApi.FastDFS.OldUpload, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "文件上传", notes = "返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)")
    Envelop upload(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.FastDFS.DeleteById, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    Envelop deleteById(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.FastDFS.DeleteByPath, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    Envelop deleteByPath(
            @RequestParam(value = "path") String path);

    @RequestMapping(value = ServiceApi.FastDFS.DeleteByObjectId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    Envelop deleteByObjectId(
            @RequestParam(value = "objectId") String objectId);

    @RequestMapping(value = ServiceApi.FastDFS.Modify, method = RequestMethod.POST)
    @ApiOperation(value = "修改文件", notes = "返回相关索引信息,以及HttpUrl下载连接")
    Envelop modify(
            @RequestPart(value = "file") MultipartFile file,
            @RequestParam(value = "path") String path,
            @RequestParam(value = "_id") String _id,
            @RequestParam(value = "modifier") String modifier);

    @RequestMapping(value = ServiceApi.FastDFS.OldModify, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改文件", notes = "返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)")
    Envelop modify(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.FastDFS.FileInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取文件信息")
    Envelop getFileInfo(
            @RequestParam(value = "path") String path);

    @RequestMapping(value = ServiceApi.FastDFS.DownloadById, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byId)")
    Envelop downloadById (
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.FastDFS.DeleteByPath, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath)")
    Envelop downloadByPath(
            @RequestParam(value = "path") String path);

    @RequestMapping(value = ServiceApi.FastDFS.DownloadByObjectId, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byObjectId)")
    Envelop downloadByObjectId(
            @RequestParam(value = "objectId") String objectId);

    @RequestMapping(value = ServiceApi.FastDFS.GetFilePath, method = RequestMethod.GET)
    @ApiOperation(value = "获取文件路径(byObjectId)")
    Envelop getFilePath(
            @RequestParam(value = "objectId") String objectId);

    @RequestMapping(value = ServiceApi.FastDFS.Page, method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    Envelop page(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = "/fastDfs/status", method = RequestMethod.GET)
    @ApiOperation(value = "获取服务器状态信息")
    Envelop status();

    @RequestMapping(value = ServiceApi.FastDFS.GetPublicUrl, method = RequestMethod.GET)
    @ApiOperation(value = "获取外链地址")
    Envelop getPublicUrl();

    @RequestMapping(value = ServiceApi.FastDFS.SetPublicUrl, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "设置外链地址")
    Envelop setPublicUrl(
            @RequestBody String jsonData);

}