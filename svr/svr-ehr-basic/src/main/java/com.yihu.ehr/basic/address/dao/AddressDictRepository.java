package com.yihu.ehr.basic.address.dao;

import com.yihu.ehr.entity.address.AddressDict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface AddressDictRepository extends PagingAndSortingRepository<AddressDict, Integer> {


    @Query("select dict from AddressDict dict where dict.id = :id")
    AddressDict getAddressDictById(@Param("id") String id);

    @Query("select dict from AddressDict dict where dict.level = :level")
    List<AddressDict> getAddrDictByLevel(@Param("level") Integer level);

    @Query("select dict from AddressDict dict where dict.pid = :pid")
    List<AddressDict> getAddrDictByPid(@Param("pid") Integer pid);

    @Query("select dict from AddressDict dict where dict.name = :name")
    List<AddressDict> findByName(@Param("name") String pid);

    @Query("select dict from AddressDict dict where 1=1")
    List<AddressDict> getAll();

    @Query("select dict from AddressDict dict where dict.name like %:name% and dict.level > 0")
    List<AddressDict> getAddrDictByname(@Param("name") String name);
}
