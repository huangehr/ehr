package com.yihu.ehr.geography.dao;

import com.yihu.ehr.geography.service.GeographyDict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XGeographyDictRepository extends PagingAndSortingRepository<GeographyDict, Integer> {


    @Query("select dict from GeographyDict dict where dict.id = :id")
    GeographyDict getAddressDictById(@Param("id") String id);

    @Query("select dict from GeographyDict dict where dict.level = :level")
    List<GeographyDict> getAddrDictByLevel(@Param("level") Integer level);

    @Query("select dict from GeographyDict dict where dict.pid = :pid")
    List<GeographyDict> getAddrDictByPid(@Param("pid") Integer pid);


}
