package com.yihu.ehr.standard.cdatype.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XcdaTypeRepository extends PagingAndSortingRepository<CDAType, String> {

    @Query("select cdaType from CDAType cdaType  where cdaType.id in (:ids)")
    List<CDAType> findCDATypeByIds(@Param("ids") String[] ids);

}
