package com.yihu.ehr.resource.service;

import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemaDao;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterSchema;
import com.yihu.ehr.resource.service.intf.IAdapterCacheService;
import com.yihu.ehr.resource.service.intf.IAdapterMetadataService;
import com.yihu.ehr.schema.ResourceAdaptionKeySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by lyr on 2016/5/18.
 */
@Service
@Transactional
public class AdapterCacheService implements IAdapterCacheService{
    @Autowired
    private ResourceAdaptionKeySchema keySchema;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private AdapterSchemaDao schemaDao;

    @Autowired
    private AdapterMetadataDao metadataDao;

    /**
     * 缓存适配数据元
     *
     */
    public void cacheData()
    {
        Iterable<RsAdapterSchema>  schemaList = schemaDao.findAll();

        for(RsAdapterSchema schema : schemaList)
        {
            Iterable<RsAdapterMetadata> metaList = metadataDao.findAll();

            for(RsAdapterMetadata meta : metaList)
            {
                String redisKey = keySchema.metaData(schema.getAdapterVersion(),meta.getSrcDatasetCode(),meta.getSrcMetadataCode());

                redisClient.set(redisKey,meta.getMetadataId());
            }
        }
    }

    /**
     * 获取缓存
     *
     * @param key String 缓存KEY
     * @return
     */
    public String getCache(String key)
    {
        return redisClient.get(key);
    }
}
