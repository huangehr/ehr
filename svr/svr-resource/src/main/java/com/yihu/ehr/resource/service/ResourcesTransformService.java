package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsAdapterMetadataDao;
import com.yihu.ehr.resource.dao.RsAdapterSchemeDao;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by hzp on 2016/4/13.
 */
@Service
public class ResourcesTransformService extends BaseJpaService {


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
    public List<Map<String,Object>> displayCodeConvert(List<Map<String, Object>> resource,String version, String dataset) {
        //返回资源
        List<Map<String, Object>> returnRs =  new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : resource) {
            returnRs.add(displayCodeConvert(map, version, dataset));
        }
        return returnRs;
    }

    /**
     * 资源数据显示代码转换
     * @param version String 适配版本
     * @return
     */
    public Map<String, Object> displayCodeConvert(Map<String, Object> resource, String version, String dataset) {
        //返回资源
        Map<String,Object> returnMap =  new HashMap<>();
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);
        if (resource.size() > 0 && schemeList.size() > 0) {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList;
            if (StringUtils.isBlank(dataset)) {
                metadataList = adapterMetadataDao.findByScheme(schemeList.get(0).getId());
            } else {
                metadataList = adapterMetadataDao.findBySchemeIdAndSrcDatasetCode(schemeList.get(0).getId(), dataset);
            }
            for (RsAdapterMetadata metadata : metadataList) {
                String srcMetadataCode = metadata.getSrcMetadataCode();
                String metadataId = metadata.getMetadataId();
                if (resource.containsKey(metadataId)) {
                    returnMap.put(srcMetadataCode, resource.get(metadataId));
                }
                //同时返回字典值
                if (resource.containsKey(metadataId + "_VALUE")) {
                    returnMap.put(srcMetadataCode + "_VALUE", resource.get(metadataId + "_VALUE"));
                }
            }
            //将rowkey等基础字段放入数据
            returnMap.put("patient_id",resource.get("patient_id"));
            returnMap.put("rowkey", resource.get("rowkey"));
            returnMap.put("org_code", resource.get("org_code"));
            returnMap.put("event_no", resource.get("event_no"));
            returnMap.put("event_date", resource.get("event_date"));
            returnMap.put("event_type",resource.get("event_type"));
        }
        return returnMap;
    }


    /**
     * 资源数据显示代码转换
     * @param resource Map<数据集编码,数据集数据>
     * @param version String 适配版本
     * @return
     */
    public Map<String, Object> displayCodeListConvert(Map<String, Object> resource, String version) {
        //返回资源
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> datas = null;//数据集数据列表
        Map<String,Object> data =  null;
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);
        if (resource.size() > 0 && schemeList.size() > 0) {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList;
            List<Map<String,Object>>    metadataResources;
            Set<String> dataSets = resource.keySet();
            for (String dataset : dataSets) {
                datas = new ArrayList<>();
                metadataResources = (List<Map<String,Object>>) resource.get(dataset);
                for (Map<String,Object> metadataResource : metadataResources){
                    data =  new HashMap<>();
                    if (StringUtils.isBlank(dataset)) {
                        metadataList = adapterMetadataDao.findByScheme(schemeList.get(0).getId());
                    } else {
                        metadataList = adapterMetadataDao.findBySchemeIdAndSrcDatasetCode(schemeList.get(0).getId(), dataset);
                    }
                    for (RsAdapterMetadata metadata : metadataList) {
                        String srcMetadataCode = metadata.getSrcMetadataCode();
                        String metadataId = metadata.getMetadataId();
                        if (metadataResource.containsKey(metadataId)) {
                            data.put(srcMetadataCode, metadataResource.get(metadataId));
                        }
                        //同时返回字典值
                        if (metadataResource.containsKey(metadataId + "_VALUE")) {
                            data.put(srcMetadataCode + "_VALUE", metadataResource.get(metadataId + "_VALUE"));
                        }
                    }
                    //将rowkey等基础字段放入数据
                    data.put("patient_id",metadataResource.get("patient_id"));
                    data.put("rowkey", metadataResource.get("rowkey"));
                    data.put("org_code", metadataResource.get("org_code"));
                    data.put("event_no", metadataResource.get("event_no"));
                    data.put("event_date", metadataResource.get("event_date"));
                    data.put("event_type",metadataResource.get("event_type"));
                    datas.add(data);
                }
                result.put(dataset,datas);
            }

        }

        return result;
    }

    /**
     * EHR主表数据分解
     */
    public Map<String, Object> stdMasterTransform(Map<String, Object> resource, String dataset, String version) {
        //返回资源
        Map<String, Object> returnRs =  new HashMap<>();
        //适配方案
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);
        if (resource.size() > 0 ||  schemeList.size() > 0) {
            //适配方案对应数据元
            List<RsAdapterMetadata> metadataList = adapterMetadataDao.findByDataset(schemeList.get(0).getId(), dataset);
            for (RsAdapterMetadata metadata : metadataList) {
                String srcMetadataCode = metadata.getSrcMetadataCode();
                String metadataId = metadata.getMetadataId();
                if (resource.containsKey(metadataId)) {
                    returnRs.put(srcMetadataCode, resource.get(metadataId));
                }
                //同时返回字典值
                if (resource.containsKey(metadataId + "_VALUE")) {
                    returnRs.put(srcMetadataCode + "_VALUE", resource.get(metadataId + "_VALUE"));
                }
            }
            //将rowkey放入数据
            returnRs.put("patient_id",resource.get("patient_id"));
            returnRs.put("rowkey", resource.get("rowkey"));
            returnRs.put("org_code", resource.get("org_code"));
            returnRs.put("event_no", resource.get("event_no"));
            returnRs.put("event_type", resource.get("event_type"));
            returnRs.put("event_date", resource.get("event_date"));
        }
        return returnRs;
    }
}
