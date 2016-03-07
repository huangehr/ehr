package com.yihu.ehr.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.esb.MHosEsbMiniRelease;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by chenweida on 2016/3/7.
 */
@FeignClient(MicroServices.Simplifiedesb)
@ApiIgnore
public interface SimplifiedESBClient {
    @RequestMapping(value = "/uploadLog", method = RequestMethod.POST)
    public boolean uploadLog(String orgCode, HttpServletRequest request);


    @RequestMapping(value = "/getUpdateFlag", method = RequestMethod.POST)
    public MHosEsbMiniRelease getUpdateFlag(String versionCode, String systemCode, String orgCode);


    @RequestMapping(value = "/downUpdateWar", method = RequestMethod.POST)
    public void downUpdateWar(String systemCode, String orgCode, HttpServletResponse response);

    @RequestMapping(value = "/uploadResult", method = RequestMethod.POST)
    public String uploadResult(String systemCode, String orgCode, String versionCode, String versionName, String updateDate);

    @RequestMapping(value = "/fillMining", method = RequestMethod.POST)
    public String fillMining(String systemCode, String orgCode);

    @RequestMapping(value = "/changeFillMiningStatus", method = RequestMethod.POST)
    public void changeFillMiningStatus(String result, String message, String id, String status);

    @RequestMapping(value = "/hisPenetration", method = RequestMethod.POST)
    public String hisPenetration(String systemCode, String orgCode);

    @RequestMapping(value = "/changeHisPenetrationStatus", method = RequestMethod.POST)
    public void changeHisPenetrationStatus(String result, String status, String id);
}
