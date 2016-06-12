package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.resource.dao.ResourcesMetadataQueryDao;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemeDao;
import com.yihu.ehr.resource.dao.intf.ResourceDefaultParamDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.model.*;
import com.yihu.ehr.util.Envelop;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;


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
     * ��Դ������ʾ����ת��
     *
     * @param resource List<Map<String,Object>> ��Դ
     * @param version String ����汾
     * @return
     */
    public List<Map<String,Object>> displayCodeConvert(List<Map<String,Object>> resource,String version)
    {
        //������Դ
        List<Map<String,Object>> returnRs =  new ArrayList<Map<String, Object>>();
        //���䷽��
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);

        if ((resource != null && resource.size() > 0) || (schemeList != null && schemeList.size() > 0))
        {
            //���䷽����Ӧ����Ԫ
            List<RsAdapterMetadata> metadataList = adapterMetadataDao.findBySchema(schemeList.get(0).getId());

            if(metadataList != null && metadataList.size() > 0)
            {
                //����ԪMap,���ڶ�Ӧ����
                Map<String,String> adapterMap = new HashMap<String,String>();

                //����Ԫ����Map
                for(RsAdapterMetadata meta : metadataList)
                {
                    adapterMap.put(meta.getMetadataId(),meta.getSrcMetadataCode());
                }

                //����Ԫ����ת��
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
     * ��Դ������ʾ����ת��
     * @param version String ����汾
     * @return
     */
    public Map<String,Object> displayCodeConvert(Map<String,Object> resource,String version)
    {
        //������Դ
        Map<String,Object> returnRs =  new HashMap<>();
        //���䷽��
        List<RsAdapterScheme> schemeList = adapterSchemeDao.findByAdapterVersion(version);

        if ((resource != null && resource.size() > 0) || (schemeList != null && schemeList.size() > 0))
        {
            //���䷽����Ӧ����Ԫ
            List<RsAdapterMetadata> metadataList = adapterMetadataDao.findBySchema(schemeList.get(0).getId());

            if(metadataList != null && metadataList.size() > 0)
            {
                //����ԪMap,���ڶ�Ӧ����
                Map<String,String> adapterMap = new HashMap<String,String>();

                //����Ԫ����Map
                for(RsAdapterMetadata meta : metadataList)
                {
                    adapterMap.put(meta.getMetadataId(),meta.getSrcMetadataCode());
                }

                //����Ԫ����ת��
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
}
