package com.yihu.ehr.dfs.controller;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.client.FastDFSClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * EndPoint - 文件服务
 * Created by progr1mmer on 2017/12/1.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "FastDFSController", description = "FastDFS服务", tags = {"分布式综合服务-FastDfs服务"})
public class FastDFSController extends EnvelopRestEndPoint {

    @Autowired
    private FastDFSClient fastDFSClient;

    /**
     * 文件上传 - 返回相关索引信息,以及HttpUrl下载连接
     * @param file
     * @param creator
     * @param objectId
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.Upload, method = RequestMethod.POST)
    @ApiOperation(value = "文件上传", notes = "返回相关索引信息,以及HttpUrl下载连接")
    public Envelop upload(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name = "creator", value = "创建者", required = true)
            @RequestParam(value = "creator") String creator,
            @ApiParam(name = "objectId", value = "创建者标识", required = true, defaultValue = "EHR")
            @RequestParam(value = "objectId") String objectId) {
        return fastDFSClient.upload(file, creator, objectId);
    }

    /**
     * 文件上传 - 返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.OldUpload, method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)")
    public Envelop upload(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestParam(value = "jsonData") String jsonData) {
        return fastDFSClient.upload(jsonData);
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DeleteById, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteById(
            @ApiParam(name = "id", value = "id", required = true)
            @RequestParam(value = "id") String id) {
        return fastDFSClient.deleteById(id);
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param path
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DeleteByPath, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByPath(
            @ApiParam(name = "path", value = "文件路径", required = true)
            @RequestParam(value = "path") String path) {
        return fastDFSClient.deleteByPath(path);
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DeleteByObjectId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByObjectId(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
        return fastDFSClient.deleteByObjectId(objectId);
    }

    /**
     * 修改文件 - 返回相关索引信息,以及HttpUrl下载连接
     * @param file
     * @param path
     * @param _id
     * @param modifier
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.Modify, method = RequestMethod.POST)
    @ApiOperation(value = "修改文件", notes = "返回相关索引信息,以及HttpUrl下载连接")
    public Envelop modify(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name = "path", value = "旧文件路径", required = true)
            @RequestParam(value = "path") String path,
            @ApiParam(name = "_id", value = "旧文件唯一索引ID", required = true)
            @RequestParam(value = "_id") String _id,
            @ApiParam(name = "modifier", value = "修改者", required = true)
            @RequestParam(value = "modifier") String modifier) {
        return fastDFSClient.modify(file, path, _id, modifier);
    }

    /**
     * 修改文件 - 返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.OldModify, method = RequestMethod.POST)
    @ApiOperation(value = "修改文件", notes = "返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)")
    public Envelop modify(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestParam(value = "jsonData") String jsonData) {
        return fastDFSClient.modify(jsonData);
    }

    /**
     * 获取文件信息
     * @param path
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.FileInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取文件信息")
    public Envelop getFileInfo(
            @ApiParam(name = "path", value = "路径", required = true)
            @RequestParam(value = "path") String path) {
        return fastDFSClient.getFileInfo(path);
    }

    /**
     * 下载文件
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadById, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byId)")
    public Envelop downloadById (
            @ApiParam(name = "id", value = "id", required = true)
            @RequestParam(value = "id") String id) {
        return fastDFSClient.downloadById(id);
    }

    /**
     * 下载文件
     * @param path
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadByPath, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath)")
    public Envelop downloadByPath(
            @ApiParam(name = "path", value = "文件路径", required = true)
            @RequestParam(value = "path") String path) {
       return fastDFSClient.downloadByPath(path);
    }

    /**
     * 下载文件
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadByObjectId, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byObjectId)")
    public Envelop downloadByObjectId(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
        return fastDFSClient.downloadByObjectId(objectId);
    }

    /**
     * 获取文件下载路径
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.GetFilePath, method = RequestMethod.GET)
    @ApiOperation(value = "获取文件路径(byObjectId)")
    public Envelop getFilePath(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
       return fastDFSClient.getFilePath(objectId);
    }

    /**
     * 获取分页
     * @param filter
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.Page, method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop page(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "page", value = "页码", required = true)
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", required = true)
            @RequestParam(value = "size") int size) {
        return fastDFSClient.page(filter, page, size);
    }

    /**
     * 获取服务器状态信息
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.Status, method = RequestMethod.GET)
    @ApiOperation(value = "获取服务器状态信息")
    public Envelop status() {
        return fastDFSClient.status();
    }

    /**
     * 获取外链地址
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.GetPublicUrl, method = RequestMethod.GET)
    @ApiOperation(value = "获取外链地址")
    public Envelop getPublicUrl() {
        return fastDFSClient.getPublicUrl();
    }

    /**
     * 设置外链地址
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.SetPublicUrl, method = RequestMethod.POST)
    @ApiOperation(value = "设置外链地址")
    public Envelop setPublicUrl(
            @ApiParam(name = "jsonData", value = "字典项JSON结构")
            @RequestParam(value = "jsonData") String jsonData) {
        return fastDFSClient.setPublicUrl(jsonData);
    }

}