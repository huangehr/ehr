package com.yihu.ehr.address.service;

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


    @Query("select address from AddressDict address where 1 = 1")
    List<GeographyDict> findAddressDictList();

    @Query("delete from AddressDict address where address.id = :id")
    void delAddressDict(@Param("id") int id);

    @Query("select address from AddressDict address where address.id = :id")
    GeographyDict getAddressDictById(@Param("id") String id);

    @Query("select address from AddressDict address where 1 = 1")
    //@Query("select * from AddressDict a where a.id not in (select a.id from AddressDict b where a.id=b.pid)")
    List<GeographyDict> getAddressDictNotImport();

    @Query("select addressDict from AddressDict addressDict where addressDict.level = :level")
    List<GeographyDict> getAddrDictByLevel(@Param("level") Integer level);

    @Query("select a from AddressDict a where a.pid = :pid")
    List<GeographyDict> getAddrDictByPid(@Param("pid") Integer pid);


}
