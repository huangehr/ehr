package com.yihu.ehr.fileresource.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.11 11:32
 */
@FeignClient(name=MicroServices.FileResource)
public interface FileResourceClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/files", method = RequestMethod.POST)
    @ApiOperation(value = "上次图片")
    String fileUpload(
            @RequestParam(value = "file_str") String fileStr,
            @RequestParam(value = "file_name") String fileName,
            @RequestParam(value = "json_data") String jsonData);

    @RequestMapping(value = ApiVersion.Version1_0 + "/files", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastdfs相对应当文件")
    boolean filesDelete(
            @RequestParam(value = "object_id") String objectId);

    @RequestMapping(value = ApiVersion.Version1_0 + "/files", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    List<String> filesDownload(
            @RequestParam(value = "object_id") String objectId);

}
