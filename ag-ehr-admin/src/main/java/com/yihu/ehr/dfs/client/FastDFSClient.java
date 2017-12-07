package com.yihu.ehr.dfs.client;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - 文件服务
 * Created by progr1mmer on 2017/12/1.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Dfs)
@RequestMapping(ApiVersion.Version1_0)
public interface FastDFSClient {

    @RequestMapping(value = "/fastDfs/uploadReturnId", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "上传文件", notes = "返回索引id")
    Envelop fileUploadReturnId(
            @RequestBody String jsonData);

    @RequestMapping(value = "/fastDfs/uploadReturnUrl", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "上传文件", notes = "返回url")
    Envelop fileUploadReturnUrl(
            @RequestBody String jsonData);

    @RequestMapping(value = "/fastDfs/uploadReturnHttpUrl", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "上传文件", notes = "返回httpUrl")
    Envelop fileUploadReturnHttpUrl(
            @RequestBody String jsonData);

    @RequestMapping(value = "/fastDfs/deleteById", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    Envelop deleteById(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "/fastDfs/deleteByPath", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    Envelop deleteByPath(
            @RequestParam(value = "path") String path);

    @RequestMapping(value = "/fastDfs/deleteByObjectId", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    Envelop deleteByObjectId(
            @RequestParam(value = "objectId") String objectId);

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

    @RequestMapping(value = "/fastDfs/modify", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改文件")
    Envelop modify(
            @RequestBody String jsonData);
    /**
     * 获取文件信息
     * @param path
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/fileInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件信息")
    Envelop getFileInfo(
            @RequestParam(value = "path") String path);

    @RequestMapping(value = "/fastDfs/downloadById", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byId)")
    Envelop downloadById (
            @RequestParam(value = "id") String id);

    @RequestMapping(value = "/fastDfs/downloadByPath", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath)")
    Envelop downLoadByPath(
            @RequestParam(value = "path") String path);

    @RequestMapping(value = "/fastDfs/downloadByObjectId", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byObjectId)")
    Envelop downLoadByObjectId(
            @RequestParam(value = "objectId") String objectId);

    @RequestMapping(value = "/fastDfs/getFilePath", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件路径(byObjectId)")
    Envelop getFilePath(
            @RequestParam(value = "objectId") String objectId);
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
    Envelop page(
            @RequestParam(value = "filter") String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = "/fastDfs/status", method = RequestMethod.GET)
    @ApiOperation(value = "获取服务器状态信息")
    Envelop status();

    @RequestMapping(value = "/fastDfs/getPublicUrl", method = RequestMethod.GET)
    @ApiOperation(value = "获取外链地址")
    Envelop getPublicUrl();

    @RequestMapping(value = "/fastDfs/setPublicUrl", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "设置外链地址")
    Envelop setPublicUrl(
            @RequestBody String jsonData);

}