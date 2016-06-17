package com.yihu.ehr.resource.service;


import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemeDao;
import com.yihu.ehr.resource.dao.intf.ResourceDefaultParamDao;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hzp on 2016/4/13.
 */
@Service("resourcesTransformService")
public class ResourcesTransformService {


    @Autowired
    ResourceDefaultParamDao resourceDefaultParamDao;

    @Autowired
    AdapterSchemeDao adapterSchemeDao;

    @Autowired
    AdapterMetadataDao adapterMetadataDao;

    /**
     * 资源数据显示代码转换
     *
     * @param resource List<Map<String,Object>> 资源
     * @param version String 适配版本
     * @return
     */
    public List<Map<String,Object>> displayCodeConvert(List<Map<String,Object>> resource,String version)
    {
        //返回资源
        List<Map<String,Object>> returnRs =  new ArrayList<Map<String, Object>>();
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);

        if ((resource != null && resource.size() > 0) || (schemeList != null && schemeList.size() > 0))
        {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList = adapterMetadataDao.findBySchema(schemeList.get(0).getId());

            if(metadataList != null && metadataList.size() > 0)
            {
                //数据元Map,便于对应查找
                Map<String,String> adapterMap = new HashMap<String,String>();

                //数据元放入Map
                for(RsAdapterMetadata meta : metadataList)
                {
                    adapterMap.put(meta.getMetadataId(),meta.getSrcMetadataCode());
                }

                //数据元代码转换
                for(Map<String,Object> rs : resource)
                {
                    Map<String,Object> convertedMap = new HashMap<String,Object>();

                    for(String key : rs.keySet())
                    {
                        if(adapterMap.containsKey(key))
                        {
                            convertedMap.put(adapterMap.get(key),rs.get(key));
                        }
                        else
                        {
                            convertedMap.put(key,rs.get(key));
                        }
                    }

                    returnRs.add(convertedMap);
                }
            }
        }

        return returnRs;
    }

    /**
     * 资源数据显示代码转换
     * @param version String 适配版本
     * @return
     */
    public Map<String,Object> displayCodeConvert(Map<String,Object> resource,String version)
    {
        //返回资源
        Map<String,Object> returnRs =  new HashMap<>();
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);

        if ((resource != null && resource.size() > 0) || (schemeList != null && schemeList.size() > 0))
        {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList = adapterMetadataDao.findBySchema(schemeList.get(0).getId());

            if(metadataList != null && metadataList.size() > 0)
            {
                //数据元Map,便于对应查找
                Map<String,String> adapterMap = new HashMap<String,String>();

                //数据元放入Map
                for(RsAdapterMetadata meta : metadataList)
                {
                    adapterMap.put(meta.getMetadataId(),meta.getSrcMetadataCode());
                }

                //数据元代码转换
                for(String key : resource.keySet())
                {
                    if(adapterMap.containsKey(key))
                    {
                        returnRs.put(adapterMap.get(key),resource.get(key));
                    }
                    else
                    {
                        returnRs.put(key,resource.get(key));
                    }
                }
            }
        }

        return returnRs;
    }

    /**
     * EHR主表数据分解（未完成）
     */
    public Map<String,Object> stdMasterTransform(Map<String,Object> resource,String dataset,String version)
    {
        //返回资源
        Map<String,Object> returnRs =  new HashMap<>();
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);

        if ((resource != null && resource.size() > 0) || (schemeList != null && schemeList.size() > 0))
        {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList = adapterMetadataDao.findByDataset(schemeList.get(0).getId(), dataset);
            for(RsAdapterMetadata metadata : metadataList)
            {
                String srcMetadataCode = metadata.getSrcMetadataCode();
                String metadataId = metadata.getMetadataId();

                if(resource.containsKey(metadataId))
                {
                    returnRs.put(srcMetadataCode,resource.get(metadataId));
                }
            }
        }

        return returnRs;
    }
}
