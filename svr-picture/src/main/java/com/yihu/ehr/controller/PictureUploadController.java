package com.yihu.ehr.controller;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.service.PictureResource;
import com.yihu.ehr.service.PictureResourceManager;
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
import java.util.Base64;
import java.util.Date;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "picture", description = "图片管理接口")
public class PictureUploadController extends BaseRestController {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private PictureResourceManager pictureResourceManager;


    @RequestMapping(value = "/pictures/upload", method = RequestMethod.POST)
    @ApiOperation(value = "图片上传", notes = "图片上传")
    public String fileUpload(
            @ApiParam(name = "file_str", value = "文件字符串")
            @RequestParam(value = "file_str") String fileStr,
            @ApiParam(name = "file_name", value = "文件名")
            @RequestParam(value = "file_name") String fileName,
            @ApiParam(name = "json_data", value = "文件资源属性")
            @RequestParam(value = "json_data") String jsonData) throws Exception {

        PictureResource pictureResource = new PictureResource();
        pictureResource.setId(getObjectId(BizObject.PictureResource));
        pictureResource.setMime("");                //冗余字段，后继图片管理用
        pictureResource.setObjectId("341321234");   //郸城县人民医院二院
        pictureResource.setUsage("org");            //机构 用途
        pictureResource.setCreateUser("0dae0003561cc415c72d9111e8cb88aa");            //admin
        pictureResource.setCreateDate(new Date());

        jsonData = toJson(pictureResource);
        PictureResource pr = toEntity(jsonData, PictureResource.class);

       return pictureResourceManager.saveResource(fileStr,fileName,pr);


    }



    /**
     * 用户头像图片下载
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/picture",method = RequestMethod.GET)
    @ApiOperation(value = "下载头像")
    public String downloadPicture(
            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
            @RequestParam(value = "group_name") String groupName,
            @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
            @RequestParam(value = "remote_file_name") String remoteFileName) throws Exception {
        String imageStream = null;
        byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);

        String fileStream = new String(Base64.getEncoder().encode(bytes));
        imageStream = URLEncoder.encode(fileStream, "UTF-8");

        return imageStream;
    }
}