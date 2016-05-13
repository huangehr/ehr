package com.yihu.ehr.controller;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.service.FileResource;
import com.yihu.ehr.service.FileResourceManager;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "files", description = "文件管理管理接口")
public class FileResourceController extends BaseRestController {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private FileResourceManager fileResourceManager;


    /**
     * 上传文件
     *
     * @param fileStr
     * @param fileName
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "图片上传")
    public String fileUpload(
            @ApiParam(name = "file_str", value = "文件字符串")
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        FileResource fileResource = toEntity(jsonData, FileResource.class);
        return fileResourceManager.saveFileResource(fileStr, fileName, fileResource);

    }

    /**
     * 删除资源表对应关系，并且删除fastdfs相对应当文件
     *
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/files", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应文件")
    public boolean filesDelete(
            @ApiParam(name = "object_id", value = "文件字符串")
            @RequestParam(value = "object_id") String objectId) throws Exception {
        List<FileResource> fileResources = fileResourceManager.findByObjectId(objectId);
        return fileResourceManager.deleteFileResource(fileResources);
    }


    /**
     * 下载文件
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/files", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public List<String> filesDownload(
            @ApiParam(name = "object_id", value = "文件字符串")
            @RequestParam(value = "object_id") String objectId) throws Exception {
        List<FileResource> fileResources = fileResourceManager.findByObjectId(objectId);
        List<String> filesStrs = new ArrayList<>();
        for (FileResource fileResource : fileResources) {
            String storagePath = fileResource.getStoragePath();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);
            String fileStream = new String(bytes);
            filesStrs.add(fileStream);
        }
        return filesStrs;
    }
}