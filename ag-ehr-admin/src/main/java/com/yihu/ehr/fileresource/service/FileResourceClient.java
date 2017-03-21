package com.yihu.ehr.fileresource.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.11 11:32
 */
@FeignClient(name=MicroServices.FileResource)
public interface FileResourceClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/files_upload", method = RequestMethod.POST)
    @ApiOperation(value = "上传图片")
    String fileUpload(
            @RequestBody String fileStr,
            @RequestParam(value = "file_name") String fileName,
            @RequestParam(value = "json_data") String jsonData);

    @RequestMapping(value = ApiVersion.Version1_0 + "/files_upload_returnUrl", method = RequestMethod.POST)
    @ApiOperation(value = "上传图片")
    String fileUploadReturnUrl(
            @RequestBody String fileStr,
            @RequestParam(value = "file_name") String fileName,
            @RequestParam(value = "json_data") String jsonData);

    @RequestMapping(value = ApiVersion.Version1_0 + "/files", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应当文件")
    boolean filesDelete(
            @RequestParam(value = "object_id") String objectId);

    @RequestMapping(value = ApiVersion.Version1_0 + "/files_download", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    List<String> filesDownload(
            @RequestParam(value = "object_id") String objectId,
            @RequestParam(value = "mime", required = false) String mime);

    @RequestMapping(value = ApiVersion.Version1_0 + "/image_view/{storagePath}", method = RequestMethod.GET)
    @ApiOperation(value = "查看图片")
    String imageView(@PathVariable(value = "storagePath") String storagePath);

    @RequestMapping(value = ApiVersion.Version1_0 + "/files_path", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件路径")
    List<String> filesPath(
            @RequestParam(value = "object_id") String objectId);

    @RequestMapping(value = ApiVersion.Version1_0 + "/image_delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应当文件")
    boolean filesDeleteByPath(
            @RequestParam(value = "storagePath") String storagePath);



}
