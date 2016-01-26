package com.yihu.ehr.standard.standardsource.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.01.19 13:44
 */
public interface XStandardSourceRepository extends PagingAndSortingRepository<StandardSource, String> {

    @Query("select src from StandardSource src where src.id in(:ids)")
    public List<StandardSource> findByIdIn(Collection<String> ids);

    @Modifying
    @Query("delete from StandardSource src where src.id in ?1 ")
    public int deleteByIdIn(Collection<String> ids);


}
