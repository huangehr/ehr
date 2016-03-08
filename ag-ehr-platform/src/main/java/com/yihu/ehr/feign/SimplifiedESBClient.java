package com.yihu.ehr.feign;

import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by chenweida on 2016/3/7.
 */
@FeignClient(MicroServices.Simplifiedesb)
@ApiIgnore
public interface SimplifiedESBClient {
    @RequestMapping(value = "/uploadLog", method = RequestMethod.POST)
    public boolean uploadLog(@RequestParam(value = "orgCode", required = true) String orgCode,
                             @RequestParam(value = "ip", required = true) String ip,
                             @RequestParam(value = "file", required = true) String file);


    @RequestMapping(value = "/getUpdateFlag", method = RequestMethod.POST)
    public String getUpdateFlag(@RequestParam(value = "versionCode", required = true) String versionCode,
                                @RequestParam(value = "systemCode", required = true) String systemCode,
                                @RequestParam(value = "orgCode", required = true) String orgCode);


    @RequestMapping(value = "/downUpdateWar", method = RequestMethod.POST)
    public String downUpdateWar(@RequestParam(value = "systemCode", required = true) String systemCode,
                                @RequestParam(value = "orgCode", required = true) String orgCode,
                                HttpServletResponse response);

    @RequestMapping(value = "/uploadResult", method = RequestMethod.POST)
    public String uploadResult(@RequestParam(value = "systemCode", required = true) String systemCode,
                               @RequestParam(value = "orgCode", required = true) String orgCode,
                               @RequestParam(value = "versionCode", required = true) String versionCode,
                               @RequestParam(value = "versionName", required = true) String versionName
            , @RequestParam(value = "updateDate", required = true) String updateDate);

    @RequestMapping(value = "/fillMining", method = RequestMethod.POST)
    public String fillMining(@RequestParam(value = "systemCode", required = true) String systemCode, @RequestParam(value = "orgCode", required = true) String orgCode);

    @RequestMapping(value = "/changeFillMiningStatus", method = RequestMethod.POST)
    public String changeFillMiningStatus(@RequestParam(value = "result", required = true) String result,
                                         @RequestParam(value = "message", required = true) String message,
                                         @RequestParam(value = "id", required = true) String id,
                                         @RequestParam(value = "status", required = true) String status);

    @RequestMapping(value = "/hisPenetration", method = RequestMethod.POST)
    public String hisPenetration(@RequestParam(value = "systemCode", required = true) String systemCode,
                                 @RequestParam(value = "orgCode", required = true) String orgCode);

    @RequestMapping(value = "/changeHisPenetrationStatus", method = RequestMethod.POST)
    public String changeHisPenetrationStatus(@RequestParam(value = "result", required = true) String result,
                                             @RequestParam(value = "status", required = true) String status,
                                             @RequestParam(value = "id", required = true) String id);
}
