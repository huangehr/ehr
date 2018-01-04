package com.yihu.ehr.redisMemory.cache;

import com.yihu.ehr.redisMemory.cache.entity.RedisCacheKeyMemory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

/**
 * @author 张进军
 * @date 2017/12/6 15:11
 */
public class RedisCacheKeyMemorySetMapper implements FieldSetMapper<RedisCacheKeyMemory> {

    public RedisCacheKeyMemory mapFieldSet(FieldSet fieldSet) {
        RedisCacheKeyMemory keyMemory = new RedisCacheKeyMemory();

        keyMemory.setDatabaseNo(fieldSet.readInt(0));
        keyMemory.setType(fieldSet.readString(1));
        keyMemory.setCacheKey(fieldSet.readString(2));
        keyMemory.setSizeInBytes(fieldSet.readLong(3));
        keyMemory.setEncoding(fieldSet.readString(4));
        keyMemory.setNumElements(fieldSet.readInt(5));
        keyMemory.setLenLargestElement(fieldSet.readInt(6));

        return keyMemory;
    }

}
