package com.yihu.ehr.redis;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Redis 内存服务器数据访问接口.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.07 9:51
 */
public interface XRedisClient{
    /**
     * 向redis里面添加key-value格式的数据
     *
     * @param key   key
     * @param value value
     */
    public void set(final String key, final Serializable value);

    /**
     * 根据key从redis里面取出value
     *
     * @param key key
     */
    public <T> T get(final String key);

    /**
     * 删除一个key.
     *
     * @param key
     */
    public void delete(final String key);

    /**
     * 删除指定的key.
     *
     * @param keys
     */
    public void delete(final Collection<String> keys);

    /**
     * 获取所有的Key列表.
     *
     * @param <T>
     * @return
     */
    public <T> Set<String> keys(String pattern);

    /**
     * 判断一个key是否存在.
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key);
}
