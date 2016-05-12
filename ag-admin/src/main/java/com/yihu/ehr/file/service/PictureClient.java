package com.yihu.ehr.file.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient(name=MicroServices.Picture)
public interface PictureClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/pictures/upload", method = RequestMethod.POST)
    @ApiOperation(value = "上次图片")
    String pictureUpload(
            @RequestParam(value = "file_str") String fileStr,
            @RequestParam(value = "file_name") String fileName,
            @RequestParam(value = "json_data") String jsonData);

}
