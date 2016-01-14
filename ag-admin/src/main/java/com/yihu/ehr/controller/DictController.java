package com.yihu.ehr.controller;

import com.yihu.ehr.model.BaseDict;
import com.yihu.ehr.service.ConventionalDictClient;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Andy.Cai on 2015/1/12.
 */
@RequestMapping("/conDict")
@RestController
//@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class DictController extends BaseRestController {

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @RequestMapping(value = "/getOrgType" , method = RequestMethod.GET)
    public Object getOrgType(String type) throws Exception{

        BaseDict baseDict = conventionalDictClient.getOrgType(type);
        return baseDict;
    }

    @RequestMapping(value = "/getSettledWay" , method = RequestMethod.GET)
    public Object getSettledWay(String type) throws Exception{

        BaseDict baseDict = conventionalDictClient.getSettledWay(type);
        return baseDict;
    }

    @RequestMapping(value = "/getAppCatalog" , method = RequestMethod.GET)
    public Object getAppCatalog(String type) throws Exception{

        BaseDict baseDict = conventionalDictClient.getAppCatalog(type);
        return baseDict;
    }

    @RequestMapping(value = "/getAppStatus" , method = RequestMethod.GET)
    public Object getAppStatus(String type) throws Exception{

        BaseDict baseDict = conventionalDictClient.getAppStatus(type);
        return baseDict;
    }
}
