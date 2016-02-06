package com.yihu.ehr;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * 自定义实体库操作类。增加批量删除操作。
 *
 * Created by Sand Wen on 2016.2.5.
 */
public interface XCustomRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>{
    /**
     * 删除所有具有指定ID的实体。
     *
     * @param ids
     * @throws IllegalArgumentException in case the given {@link Iterable} is {@literal null}.
     */
    void deleteCollection(Iterable<ID> ids);
}
