package com.yihu.ehr.profile.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.profile.service.Template;
import com.yihu.ehr.profile.service.ThridPrescriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Date;

/**
 * Created by cwd on 2016/6/14.
 */

@RestController
@RequestMapping(ApiVersion.Version1_0+"/thridPrescription")
@Api(value = "第三方处方调阅服务", description = "第三方处方调阅服务")
public class ThridPrescriptionEndPoint extends BaseRestEndPoint {
    @Autowired
    private ThridPrescriptionService thridPrescriptionService;

    @ApiOperation(value = "处方模板转图片")
    @RequestMapping(value = "/prescriptioToImage", method = RequestMethod.GET)
    public String prescriptioToImage(@ApiParam(value = "eventNo" ) @RequestParam String eventNo,
                                     @ApiParam(value = "orgId") @RequestParam(required=false) String orgId,
                                     @ApiParam(value = "cdaType") @RequestParam(required=false) String cdaType,
                                     @ApiParam(value = "version") @RequestParam(required=false) String version,
                                     @ApiParam(value = "height(默认600)") @RequestParam(required=false,defaultValue = "600") int height,
                                     @ApiParam(value = "width(默认400)") @RequestParam(required=false,defaultValue = "400") int width) {
        String filePath="";
        try{
             filePath= thridPrescriptionService.prescriptioToImage(eventNo,orgId,cdaType,version,height,width);
        }catch (Exception e){
            e.printStackTrace();
        }
        return filePath;
    }
}
