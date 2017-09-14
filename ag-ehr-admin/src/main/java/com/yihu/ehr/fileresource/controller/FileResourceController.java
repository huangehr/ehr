package com.yihu.ehr.fileresource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fileresource.service.FileResourceClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.11 11:32
 */
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "文件管理接口", description = "文件管理接口" ,tags = {"文件管理接口"})
public class FileResourceController {

    @Autowired
    FileResourceClient fileResourceClient;

    @RequestMapping(value = "/files", method = RequestMethod.POST)
    @ApiOperation(value = "资源文件上传")
    public String pictureUpload(
            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
            @RequestParam(name = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name",required = false) String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data",required = false) String jsonData,HttpServletRequest request) {
        return fileResourceClient.fileUpload(fileStr,fileName,jsonData);
    }

    @RequestMapping(value = "/filesReturnUrl", method = RequestMethod.POST)
    @ApiOperation(value = "资源文件上传")
    public String pictureUploadReturnUrl(
            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
            @RequestParam(name = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name",required = false) String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data",required = false) String jsonData,HttpServletRequest request) {
        LogService.getLogger(FileResourceController.class).info("zjj-ag-admin-pictureUploadReturnUrl");
        return fileResourceClient.fileUploadReturnUrl(fileStr,fileName,jsonData);
    }

    @RequestMapping(value = "/filesReturnHttpUrl", method = RequestMethod.POST)
    @ApiOperation(value = "资源文件上传返回完整的http Url")
    public String pictureUploadReturnHttpUrl(
            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
            @RequestParam(name = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name",required = false) String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data",required = false) String jsonData,HttpServletRequest request) {
        return fileResourceClient.fileUploadReturnHttpUrl(fileStr,fileName,jsonData);
    }

    @RequestMapping(value = "/files", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应当文件")
    public boolean filesDelete(
            @ApiParam(name = "object_id", value = "文件字符串")
            @RequestParam(value = "object_id") String objectId) throws Exception {
        return fileResourceClient.filesDelete(objectId);
    }

    @RequestMapping(value = "/image_delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应当文件")
    public boolean delete(
            @ApiParam(name = "storagePath", value = "文件路径")
            @RequestParam(value = "storagePath") String storagePath) throws Exception {
        return fileResourceClient.filesDeleteByPath(storagePath);
    }


    @RequestMapping(value = "/files", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public Envelop fileDownload(
            @ApiParam(name = "object_id", value = "文件字符串")
            @RequestParam(value = "object_id") String objectId,
            @ApiParam(name = "mime", value = "所有者")
            @RequestParam(value = "mime", required = false) String mime) throws Exception {
        List<String> filesStr = fileResourceClient.filesDownload(objectId, mime);
        Envelop envelop = new Envelop();
        envelop.setDetailModelList(filesStr);
        return envelop;
//        return filesStr;
    }
    @RequestMapping(value = "/image_view", method = RequestMethod.GET)
    @ApiOperation(value = "查看图片")
    public String imageView( @ApiParam(value = "查看图片") @RequestParam(value = "storagePath") String storagePath) throws Exception {
        String filesStr = fileResourceClient.imageView(storagePath);
        return filesStr;
    }

    @RequestMapping(value = "/files_path", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public Envelop filePath(
            @ApiParam(name = "object_id", value = "文件字符串")
            @RequestParam(value = "object_id") String objectId) throws Exception {
        List<String> filesStr = fileResourceClient.filesPath(objectId);
        Envelop envelop = new Envelop();
        envelop.setDetailModelList(filesStr);
        return envelop;
    }

    @ApiOperation(value = "根据文件ID，获取文件的真实访问路径")
    @RequestMapping(value = "/file/getRealPathById", method = RequestMethod.GET)
    public String getRealPathById(
            @ApiParam(value = "文件ID", required = true)
            @RequestParam(value = "fileId") String fileId) throws Exception {
        return fileResourceClient.getRealPathById(fileId);
    }

    @ApiOperation(value = "根据文件的存储路径，获取文件的真实访问路径")
    @RequestMapping(value = "/file/getRealPathByStoragePath", method = RequestMethod.GET)
    public String getRealPathByStoragePath(
            @ApiParam(value = "文件存储路径", required = true)
            @RequestParam(value = "storagePath") String storagePath) throws Exception {
        return fileResourceClient.getRealPathByStoragePath(storagePath);
    }

}