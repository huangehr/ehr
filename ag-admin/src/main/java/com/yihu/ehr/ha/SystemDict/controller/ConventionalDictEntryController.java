package com.yihu.ehr.ha.SystemDict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by AndyCai on 2016/1/19.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/conDict")
@RestController
public class ConventionalDictEntryController extends BaseRestController {

    @Autowired
    private ConventionalDictEntryClient dictEntryClient;

    @RequestMapping(value = "/orgType" , method = RequestMethod.GET)
    public Object getOrgType(String type) throws Exception{

        MConventionalDict baseDict = dictEntryClient.getOrgType(type);
        return baseDict;
    }

    @RequestMapping(value = "/settledWay" , method = RequestMethod.GET)
    public Object getSettledWay(String type) throws Exception{

        MConventionalDict baseDict = dictEntryClient.getSettledWay(type);
        return baseDict;
    }

    @RequestMapping(value = "/appCatalog" , method = RequestMethod.GET)
    public Object getAppCatalog(String type) throws Exception{

        MConventionalDict baseDict = dictEntryClient.getAppCatalog(type);
        return baseDict;
    }

    @RequestMapping(value = "/appStatus" , method = RequestMethod.GET)
    public Object getAppStatus(String type) throws Exception{

        MConventionalDict baseDict = dictEntryClient.getAppStatus(type);
        return baseDict;
    }
}
