package com.yihu.ehr.redisMemory.cache.entity;

import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 缓存Key值占用内存分析 entity
 *
 * @author 张进军
 * @date 2017/12/6 14:58
 */
@Entity
@Table(name = "redis_cache_key_memory")
public class RedisCacheKeyMemory extends BaseAssignedEntity {

    private int databaseNo; // Redis数据库编号
    private String type; // 数据类型
    private String cacheKey; // Redis Key编码
    private long sizeInBytes; // Key值内存字节大小
    private String encoding; // RDB编码类型
    private int numElements;
    private int lenLargestElement;

    @Column(name = "database_no", nullable = false)
    public int getDatabaseNo() {
        return databaseNo;
    }

    public void setDatabaseNo(int databaseNo) {
        this.databaseNo = databaseNo;
    }

    @Column(name = "type", nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "cache_key", nullable = false)
    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    @Column(name = "size_in_bytes", nullable = false)
    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    @Column(name = "encoding", nullable = false)
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Column(name = "num_elements", nullable = false)
    public int getNumElements() {
        return numElements;
    }

    public void setNumElements(int numElements) {
        this.numElements = numElements;
    }

    @Column(name = "len_largest_element", nullable = false)
    public int getLenLargestElement() {
        return lenLargestElement;
    }

    public void setLenLargestElement(int lenLargestElement) {
        this.lenLargestElement = lenLargestElement;
    }
}
