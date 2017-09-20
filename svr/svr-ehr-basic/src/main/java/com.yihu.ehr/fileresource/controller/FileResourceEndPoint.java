package com.yihu.ehr.fileresource.controller;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.fileresource.service.FileResource;
import com.yihu.ehr.fileresource.service.FileResourceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "files", description = "文件管理接口", tags = {"文件管理接口"})
public class FileResourceEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private FileResourceManager fileResourceManager;
    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;

    @RequestMapping(value = "/files_upload", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件")
    public String fileUpload(
            @ApiParam(name = "file_str", value = "文件字符串", required = true)
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名", required = true)
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性", required = true)
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        FileResource fileResource = toEntity(jsonData, FileResource.class);
        fileResource.setId(getObjectId(BizObject.FileResource));
        return fileResourceManager.saveFileResource(fileStr, fileName, fileResource);
    }

    @RequestMapping(value = "/files_upload_returnUrl", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件，并返回存储相对路径")
    public String fileUploadReturnUrl(
            @ApiParam(name = "file_str", value = "文件字符串", required = true)
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名", required = true)
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性", required = true)
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        FileResource fileResource = toEntity(jsonData, FileResource.class);
        fileResource.setId(getObjectId(BizObject.FileResource));
        return fileResourceManager.saveFileResourceReturnUrl(fileStr, fileName, fileResource);
    }

    @RequestMapping(value = "/files_upload_returnHttpUrl", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件，并返回存储绝对路径")
    public String fileUploadReturnHttpUrl(
            @ApiParam(name = "file_str", value = "文件字符串", required = true)
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名", required = true)
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性", required = true)
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        FileResource fileResource = toEntity(jsonData, FileResource.class);
        fileResource.setId(getObjectId(BizObject.FileResource));
        return fileResourceManager.saveFileResourceReturnHttpUrl(fileStr, fileName, fileResource);
    }

    @RequestMapping(value = "/files", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应文件")
    public boolean filesDelete(
            @ApiParam(name = "object_id", value = "文件字符串", required = true)
            @RequestParam(value = "object_id") String objectId) throws Exception {
        List<FileResource> fileResources = fileResourceManager.findByObjectId(objectId);
        return fileResourceManager.deleteFileResource(fileResources);
    }

    @RequestMapping(value = "/image_delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应文件")
    public boolean filesDeleteByPath(
            @ApiParam(name = "storagePath", value = "文件路径", required = true)
            @RequestParam(value = "storagePath") String storagePath) throws Exception {
        String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        List<FileResource> fileResources = fileResourceManager.findByStoragePath(s);
        return fileResourceManager.deleteFileResource(fileResources);
    }

    @RequestMapping(value = "/files_download", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public List<String> filesDownload(
            @ApiParam(name = "object_id", value = "文件字符串", required = true)
            @RequestParam(value = "object_id") String objectId,
            @ApiParam(name = "mime", value = "所有者", required = true)
            @RequestParam(value = "mime", required = false) String mime) throws Exception {
        List<FileResource> fileResources;
        if (StringUtils.isEmpty(mime))
            fileResources = fileResourceManager.findByObjectId(objectId);
        else
            fileResources = fileResourceManager.findByObjectIdAndMime(objectId, mime);

        List<String> filesStrs = new ArrayList<>();
        for (FileResource fileResource : fileResources) {
            String storagePath = fileResource.getStoragePath();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);

            String fileStream = new String(Base64.getEncoder().encode(bytes));
            filesStrs.add(fileStream);
        }
        return filesStrs;
    }

    @RequestMapping(value = "/files_path", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件路径")
    public List<String> getFilePath(
            @ApiParam(name = "object_id", value = "文件字符串", required = true)
            @RequestParam(value = "object_id") String objectId) throws Exception {
        List<FileResource> fileResources = fileResourceManager.findByObjectId(objectId);
        List<String> filesStrs = new ArrayList<>();
        for (FileResource fileResource : fileResources) {
            String storagePath = fileResource.getStoragePath();
            storagePath = URLEncoder.encode(storagePath, "ISO8859-1");
            filesStrs.add(storagePath);
        }
        return filesStrs;
    }

    @RequestMapping(value = "/image_view", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public String imageView(
            @ApiParam(name = "storagePath", value = "文件路径", required = true)
            @RequestParam(value = "storagePath") String storagePath) throws Exception {
        String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        String groupName = s.split(":")[0];
        String remoteFileName = s.split(":")[1];
        byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        return fileStream;
    }

    @ApiOperation(value = "根据文件ID，获取文件的真实访问路径")
    @RequestMapping(value = "/file/getRealPathById", method = RequestMethod.GET)
    public String getRealPathById(
            @ApiParam(name = "fileId", value = "文件ID", required = true)
            @RequestParam(value = "fileId") String fileId) throws Exception {
        String s = java.net.URLDecoder.decode(fileId, "UTF-8");
        String path = fileResourceManager.getStoragePathById(s);
        path = path.replace(":", "/");
        path = fastDfsPublicServers + "/" + path;
        return path;
    }

    @ApiOperation(value = "根据文件的存储路径，获取文件的真实访问路径")
    @RequestMapping(value = "/file/getRealPathByStoragePath", method = RequestMethod.GET)
    public String getRealPathByStoragePath(
            @ApiParam(name = "storagePath", value = "文件存储路径", required = true)
            @RequestParam(value = "storagePath") String storagePath) throws Exception {
        String realPath = fastDfsPublicServers + "/" + storagePath.replace(":", "/");
        return realPath;
    }

}