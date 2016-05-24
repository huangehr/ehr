package com.yihu.ehr.fileresource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fileresource.service.FileResourceClient;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.11 11:32
 */
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "图片上传管理接口", description = "图片上传管理接口")
public class FileResourceController {

    @Autowired
    FileResourceClient fileResourceClient;

    @RequestMapping(value = "/files", method = RequestMethod.POST)
    @ApiOperation(value = "资源文件上传")
    public String pictureUpload(
            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data") String jsonData) {
        return fileResourceClient.fileUpload(fileStr,fileName,jsonData);
    }

    @RequestMapping(value = "/files", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应当文件")
    public boolean filesDelete(
            @ApiParam(name = "object_id", value = "文件字符串")
            @RequestParam(value = "object_id") String objectId) throws Exception {
        return fileResourceClient.filesDelete(objectId);
    }


    @RequestMapping(value = "/files", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public Envelop fileDownload(
            @ApiParam(name = "object_id", value = "文件字符串")
            @RequestParam(value = "object_id") String objectId) throws Exception {
        List<String> filesStr = fileResourceClient.filesDownload(objectId);

        Envelop envelop = new Envelop();

        envelop.setDetailModelList(filesStr);
        return envelop;
//        return filesStr;
    }

}