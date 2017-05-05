package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.redis.schema.ResourceAdaptionDictSchema;
import com.yihu.ehr.resource.dao.intf.AdapterSchemeDao;
import com.yihu.ehr.resource.dao.intf.RsAdapterDictionaryDao;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsAdapterDictionaryService extends BaseJpaService<RsAdapterDictionary, RsAdapterDictionaryDao>  {

    @Autowired
    private ResourceAdaptionDictSchema keySchema;

    @Autowired
    private AdapterSchemeDao schemaDao;

    @Autowired
    private RsAdapterDictionaryDao adapterDictionaryDao;

    public RsAdapterDictionary findById(String id) {
        return adapterDictionaryDao.findOne(id);
    }


    public void adaterDictCache(String schemaId)
    {
        RsAdapterScheme schema = schemaDao.findOne(schemaId);
        List<RsAdapterDictionary> adapterDictList = adapterDictionaryDao.findBySchemeId(schemaId);

        if(adapterDictList != null)
        {
            for(RsAdapterDictionary dict : adapterDictList)
            {
                if(StringUtils.isEmpty(dict.getDictCode().trim()) || StringUtils.isEmpty(dict.getSrcDictEntryCode().trim()))
                {
                    continue;
                }

                keySchema.setMetaData(schema.getAdapterVersion(),dict.getDictCode(),dict.getSrcDictEntryCode(),dict.getDictEntryCode() + "&" + dict.getSrcDictEntryName());
            }
        }
    }
}
