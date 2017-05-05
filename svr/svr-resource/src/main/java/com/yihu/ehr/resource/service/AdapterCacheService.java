package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.schema.ResourceAdaptionKeySchema;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemeDao;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;

/**
 * Created by lyr on 2016/5/18.
 */
@Service
@Transactional
public class AdapterCacheService extends BaseJpaService<RsAdapterScheme,AdapterSchemeDao>{
    @Autowired
    private ResourceAdaptionKeySchema keySchema;

    @Autowired
    private AdapterSchemeDao schemaDao;

    @Autowired
    private AdapterMetadataDao metadataDao;

    /**
     * 缓存适配数据元
     *
     * @param schema_id String 方案ID
     */
    public void cacheData(String schema_id)
    {
        RsAdapterScheme schema = schemaDao.findOne(schema_id);
        Iterable<RsAdapterMetadata> metaIterable = metadataDao.findBySchemaId(schema_id);

        for(RsAdapterMetadata meta : metaIterable)
        {
            if (StringUtils.isEmpty(meta.getSrcDatasetCode())||StringUtils.isEmpty(meta.getMetadataId())) {
                continue;
            }

            keySchema.setMetaData(schema.getAdapterVersion(), meta.getSrcDatasetCode(), meta.getSrcMetadataCode(), meta.getMetadataId());
        }
    }

}
