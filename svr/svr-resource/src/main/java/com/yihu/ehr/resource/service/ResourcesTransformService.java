package com.yihu.ehr.resource.service;


import com.yihu.ehr.resource.dao.RsAdapterMetadataDao;
import com.yihu.ehr.resource.dao.RsAdapterSchemeDao;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hzp on 2016/4/13.
 */
@Service
public class ResourcesTransformService {


    @Autowired
    private RsAdapterSchemeDao adapterSchemeDao;
    @Autowired
    private RsAdapterMetadataDao adapterMetadataDao;

    /**
     * 资源数据显示代码转换
     *
     * @param resource List<Map<String,Object>> 资源
     * @param version String 适配版本
     * @return
     */
    public List<Map<String,Object>> displayCodeConvert(List<Map<String,Object>> resource,String version, String dataset) {
        //返回资源
        List<Map<String,Object>> returnRs =  new ArrayList<Map<String, Object>>();
        if(resource != null && resource.size() > 0) {
            for(Map<String,Object> map : resource) {
                returnRs.add(displayCodeConvert(map, version, dataset));
            }
        }
        return returnRs;
    }

    /**
     * 资源数据显示代码转换
     * @param version String 适配版本
     * @return
     */
    public Map<String,Object> displayCodeConvert(Map<String,Object> resource, String version, String dataset) {
        //返回资源
        Map<String,Object> returnMap =  new HashMap<>();
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);
        if ((resource != null && resource.size() > 0) || (schemeList != null && schemeList.size() > 0)) {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList = new ArrayList<RsAdapterMetadata>();
            if(StringUtils.isBlank(dataset)) {
                metadataList = adapterMetadataDao.findByScheme(schemeList.get(0).getId());
            }
            else {
                metadataList = adapterMetadataDao.findBySchemeIdAndSrcDatasetCode(schemeList.get(0).getId(),dataset);
            }
            if(metadataList != null && metadataList.size() > 0) {
                //数据元Map,便于对应查找
                Map<String, List<String>> adapterMap = new HashMap<>();

                //数据元放入Map
                for (RsAdapterMetadata meta : metadataList) {
                    String key = meta.getMetadataId();
                    List<String> values = new ArrayList<>();
                    if(adapterMap.containsKey(key)) {
                        values = adapterMap.get(key);
                    }
                    values.add(meta.getSrcMetadataCode());
                    adapterMap.put(key, values);
                }

                //数据元代码转换
                for (String key : resource.keySet()) {
                    Object value = resource.get(key);
                    //字典数据
                    if(key.lastIndexOf("_VALUE") > 0) {
                        String srcKey = key.substring(0, key.indexOf("_VALUE"));
                        if (adapterMap.containsKey(srcKey)) {
                            List<String> adapterKeys = adapterMap.get(srcKey);
                            for(String adapterKey : adapterKeys) {
                                returnMap.put(adapterKey + "_VALUE", value);
                            }
                        }
                        else{
                            returnMap.put(key, value);
                        }
                    }
                    else{
                        if (adapterMap.containsKey(key)) {
                            List<String> adapterKeys = adapterMap.get(key);
                            for(String adapterKey : adapterKeys) {
                                returnMap.put(adapterKey, value);
                            }
                        }
                        else{
                            returnMap.put(key, value);
                        }
                    }

                }
            }
        }
        return returnMap;
    }

    /**
     * EHR主表数据分解
     */
    public Map<String,Object> stdMasterTransform(Map<String,Object> resource, String dataset, String version) {
        //返回资源
        Map<String,Object> returnRs =  new HashMap<>();
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);

        if ((resource != null && resource.size() > 0) || (schemeList != null && schemeList.size() > 0)) {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList = adapterMetadataDao.findByDataset(schemeList.get(0).getId(), dataset);
            for(RsAdapterMetadata metadata : metadataList) {
                String srcMetadataCode = metadata.getSrcMetadataCode();
                String metadataId = metadata.getMetadataId();
                if(resource.containsKey(metadataId)) {
                    returnRs.put(srcMetadataCode,resource.get(metadataId));
                }
                //同时返回字典值
                if(resource.containsKey(metadataId + "_VALUE")) {
                    returnRs.put(srcMetadataCode + "_VALUE", resource.get(metadataId + "_VALUE"));
                }
            }
        }
        return returnRs;
    }
}
