package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsAdapterSchema;
import com.yihu.ehr.schema.ResourceAdaptionKeySchema;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lyr on 2016/5/18.
 */
public interface IAdapterCacheService {
    /**
     * 缓存适配数据元
     *
     */
    void cacheData(String schema_id);

    /**
     * 获取缓存
     *
     * @param key String 缓存KEY
     * @return
     */
    String getCache(String key);
}
