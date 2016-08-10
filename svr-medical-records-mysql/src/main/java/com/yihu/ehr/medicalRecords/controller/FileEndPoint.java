package com.yihu.ehr.medicalRecords.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecords.model.Entity.MrIcd10DictEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntryEntity;
import com.yihu.ehr.medicalRecords.service.MaterialService;
import com.yihu.ehr.medicalRecords.service.MrSystemDictEntryService;
import com.yihu.ehr.medicalRecords.service.MrSystemDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/3
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0+"/admin")
@Api(value = "文件上传服务", description = "文件上传服务")
public class FileEndPoint extends BaseRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MaterialService materialService;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @ApiOperation("文件上传接口")
    @RequestMapping(value = ServiceApi.MedicalRecords.fileUpload, method = RequestMethod.POST)
    public void uploadImg(
            @ApiParam(name = "file_str", value = "文件流转化后的字符串")
            @RequestParam(name = "file_str") String fileStr,
            @ApiParam(name = "json_data", value = "对象实体集合֤", required = true)
            @RequestParam(value = "json_data",required = false) String jsonData,
            HttpServletRequest request) throws Exception{
            Map<String,Object> dataMap =  objectMapper.readValue(jsonData, Map.class);
            materialService.uploadImgMaterial(dataMap.get("doucumentName")+"",dataMap.get("creatorId")+"",dataMap.get("patient")+"",fileStr);
    }

    /**
     * 下载BASE64文件
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dossier/image_view", method = RequestMethod.GET)
    @ApiOperation(value = "下载BASE64文件")
    public String imageView(@ApiParam(name = "storagePath", value = "文件路径", defaultValue = "")
                            @RequestParam(value = "storagePath") String storagePath)throws Exception{
        String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        String groupName = s.split(":")[0];
        String remoteFileName = s.split(":")[1];
        byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        return fileStream;
    }
}

