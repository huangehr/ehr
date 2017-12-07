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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
     * 上传文件，返回索引ID
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnId", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "返回索引id")
    public Envelop fileUploadReturnId(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestParam(value = "jsonData") String jsonData) {
        return fastDFSClient.fileUploadReturnId(jsonData);
    }

    /**
     * 上传文件，返回URL
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnUrl", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "返回url")
    public Envelop fileUploadReturnUrl(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestParam(value = "jsonData") String jsonData) {
        return fastDFSClient.fileUploadReturnUrl(jsonData);
    }

    /**
     * 上传文件，返回整个HttpUrl
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnHttpUrl", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "返回httpUrl")
    public Envelop fileUploadReturnHttpUrl(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestParam(value = "jsonData") String jsonData) {
        return fastDFSClient.fileUploadReturnHttpUrl(jsonData);
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/deleteById", method = RequestMethod.DELETE)
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
    @RequestMapping(value = "/fastDfs/deleteByPath", method = RequestMethod.DELETE)
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
    @RequestMapping(value = "/fastDfs/deleteByObjectId", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByObjectId(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
        return fastDFSClient.deleteByObjectId(objectId);
    }

    /**
     * 修改文件信息
     * @param jsonData
     * @return
     * @throws Exception
    @RequestMapping(value = "/fastDfs/modify", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "上传文件返回url")
    public Envelop modify(
            @ApiParam(name = "jsonData", value = "文件资源")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData.substring(9), Map.class);
        //FastDFS fileResource = toEntity(jsonData, FastDFS.class);
        //fileResource.setId(getObjectId(BizObject.FileResource));
        int size = fastDFSService.modify(paramMap);
        envelop.setSuccessFlg(true);
        envelop.setObj(size);
        return envelop;
    }
     */

    /**
     * 修改文件信息
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/modify", method = RequestMethod.POST)
    @ApiOperation(value = "修改文件")
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
    @RequestMapping(value = "/fastDfs/fileInfo", method = RequestMethod.GET)
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
    @RequestMapping(value = "/fastDfs/downloadById", method = RequestMethod.GET)
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
    @RequestMapping(value = "/fastDfs/downloadByPath", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath)")
    public Envelop downLoadByPath(
            @ApiParam(name = "path", value = "文件路径", required = true)
            @RequestParam(value = "path") String path) {
       return fastDFSClient.downLoadByPath(path);
    }

    /**
     * 下载文件
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/downloadByObjectId", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byObjectId)")
    public Envelop downLoadByObjectId(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
        return fastDFSClient.downLoadByObjectId(objectId);
    }

    /**
     * 获取文件下载路径
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/getFilePath", method = RequestMethod.GET)
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
    @RequestMapping(value = "/fastDfs/page", method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop page(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter") String filter,
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
    @RequestMapping(value = "/fastDfs/status", method = RequestMethod.GET)
    @ApiOperation(value = "获取服务器状态信息")
    public Envelop status() {
        return fastDFSClient.status();
    }

    /**
     * 获取外链地址
     * @return
     */
    @RequestMapping(value = "/fastDfs/getPublicUrl", method = RequestMethod.GET)
    @ApiOperation(value = "获取外链地址")
    public Envelop getPublicUrl() {
        return fastDFSClient.getPublicUrl();
    }

    /**
     * 设置外链地址
     * @return
     */
    @RequestMapping(value = "/fastDfs/setPublicUrl", method = RequestMethod.POST)
    @ApiOperation(value = "设置外链地址")
    public Envelop setPublicUrl(
            @ApiParam(name = "jsonData", value = "字典项JSON结构")
            @RequestParam(value = "jsonData") String jsonData) {
        return fastDFSClient.setPublicUrl(jsonData);
    }

}