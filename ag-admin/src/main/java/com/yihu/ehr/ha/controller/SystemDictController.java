package com.yihu.ehr.ha.controller;

import com.yihu.ehr.ha.service.SystemDictClient;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by AndyCai on 2016/1/19.
 */
public class SystemDictController extends BaseRestController {

    @Autowired
    private SystemDictClient systemDictClient;

    @RequestMapping(value = "/getOrgType" , method = RequestMethod.GET)
    public Object getOrgType(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getOrgType(type);
        return baseDict;
    }

    @RequestMapping(value = "/getSettledWay" , method = RequestMethod.GET)
    public Object getSettledWay(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getSettledWay(type);
        return baseDict;
    }

    @RequestMapping(value = "/getAppCatalog" , method = RequestMethod.GET)
    public Object getAppCatalog(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getAppCatalog(type);
        return baseDict;
    }

    @RequestMapping(value = "/getAppStatus" , method = RequestMethod.GET)
    public Object getAppStatus(String type) throws Exception{

        MBaseDict baseDict = systemDictClient.getAppStatus(type);
        return baseDict;
    }
}
