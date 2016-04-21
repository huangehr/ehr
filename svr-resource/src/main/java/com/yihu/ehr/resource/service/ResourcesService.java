package com.yihu.ehr.resource.service;


import com.yihu.ehr.resource.dao.ResourcesDao;
import com.yihu.ehr.resource.service.intf.IResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by hzp on 2016/4/13.
 */
@Service("resourcesService")
public class ResourcesService implements IResourcesService {
    @Autowired
    private ResourcesDao resourceDao;

    public String getDataset(String datasetCode, String orgCode) throws Exception {

        return "";
    }

}
