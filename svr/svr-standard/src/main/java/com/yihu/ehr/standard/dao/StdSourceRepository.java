package com.yihu.ehr.standard.dao;

import com.yihu.ehr.standard.model.StandardSource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.01.19 13:44
 */
public interface StdSourceRepository extends PagingAndSortingRepository<StandardSource, String> {

    @Modifying
    @Query("delete from StandardSource src where src.id in ?1 ")
    public int deleteByIdIn(Collection<String> ids);


}
