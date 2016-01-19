package com.yihu.ehr.ha.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.service.SystemDictClient;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by AndyCai on 2016/1/19.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/sysDict")
@RestController
public class SystemDictController extends BaseRestController {

    @Autowired
    private SystemDictClient systemDictClient;

    @RequestMapping(value = "/orgType" , method = RequestMethod.GET)
    public Object getOrgType(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getOrgType(type);
        return baseDict;
    }

    @RequestMapping(value = "/settledWay" , method = RequestMethod.GET)
    public Object getSettledWay(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getSettledWay(type);
        return baseDict;
    }

    @RequestMapping(value = "/appCatalog" , method = RequestMethod.GET)
    public Object getAppCatalog(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getAppCatalog(type);
        return baseDict;
    }

    @RequestMapping(value = "/appStatus" , method = RequestMethod.GET)
    public Object getAppStatus(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getAppStatus(type);
        return baseDict;
    }
}
