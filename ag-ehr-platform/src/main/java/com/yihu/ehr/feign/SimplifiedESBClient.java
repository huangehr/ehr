package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by chenweida on 2016/3/7.
 */
@FeignClient(name = MicroServices.ESB)
@ApiIgnore
public interface SimplifiedESBClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/esb/getUploadFlag", method = RequestMethod.GET)
    boolean getUploadFlag(@RequestParam(value = "orgCode", required = true) String orgCode,
                                 @RequestParam(value = "systemCode", required = true) String systemCode);

    @RequestMapping(value = ApiVersion.Version1_0+"/esb/uploadLog", method = RequestMethod.POST)
    boolean uploadLog(@RequestParam(value = "orgCode", required = true) String orgCode,
                             @RequestParam(value = "ip", required = false) String ip,
                             @RequestParam(value = "file", required = true) String file);


    @RequestMapping(value = ApiVersion.Version1_0+"/esb/getUpdateFlag", method = RequestMethod.GET)
    String getUpdateFlag(@RequestParam(value = "versionCode", required = true) String versionCode,
                                @RequestParam(value = "systemCode", required = true) String systemCode,
                                @RequestParam(value = "orgCode", required = true) String orgCode);


    @RequestMapping(value = ApiVersion.Version1_0+"/esb/downUpdateWar", method = RequestMethod.POST)
    String downUpdateWar(@RequestParam(value = "systemCode", required = true) String systemCode,
                                @RequestParam(value = "orgCode", required = true) String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0+"/esb/uploadResult", method = RequestMethod.POST)
    String uploadResult(@RequestParam(value = "systemCode", required = true) String systemCode,
                               @RequestParam(value = "orgCode", required = true) String orgCode,
                               @RequestParam(value = "versionCode", required = true) String versionCode,
                               @RequestParam(value = "versionName", required = true) String versionName,
                               @RequestParam(value = "updateDate", required = true) String updateDate,
                               @RequestParam(value = "message", required = true) String message);

    @RequestMapping(value = ApiVersion.Version1_0+"/esb/fillMining", method = RequestMethod.POST)
    String fillMining(
            @RequestParam(value = "systemCode", required = true) String systemCode,
            @RequestParam(value = "orgCode", required = true) String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0+"/esb/changeFillMiningStatus", method = RequestMethod.POST)
    String changeFillMiningStatus(
            @RequestParam(value = "message", required = true) String message,
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "status", required = true) String status);

    @RequestMapping(value = ApiVersion.Version1_0+"/esb/hisPenetration", method = RequestMethod.POST)
    String hisPenetration(@RequestParam(value = "systemCode", required = true) String systemCode,
                                 @RequestParam(value = "orgCode", required = true) String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0+"/esb/changeHisPenetrationStatus", method = RequestMethod.POST)
    String changeHisPenetrationStatus(@RequestParam(value = "result", required = true) String result,
                                             @RequestParam(value = "status", required = true) String status,
                                             @RequestParam(value = "id", required = true) String id,
                                             @RequestParam(value = "message", required = true) String message);

}
