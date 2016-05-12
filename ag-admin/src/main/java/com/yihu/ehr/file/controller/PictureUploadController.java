package com.yihu.ehr.file.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.file.service.PictureClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linaz
 * @created 2016.05.11 11:32
 */
@RequestMapping(ApiVersion.Version1_0)
@RestController
@Api(value = "图片上传管理接口", description = "图片上传管理接口")
public class PictureUploadController {

    @Autowired
    PictureClient pictureClient;

    @RequestMapping(value = "/pictures/upload", method = RequestMethod.POST)
    @ApiOperation(value = "file upload test")
    public String pictureUpload(
            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data") String jsonData) {

        return pictureClient.pictureUpload(fileStr,fileName,jsonData);
    }
}