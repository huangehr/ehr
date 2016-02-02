package com.yihu.ehr.dict.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 字典CRUD操作接口使用 PagingAndSortingRepository 中的方法，除非有特殊需要再添加自定义方法。
 *
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
interface XSystemDictRepository extends PagingAndSortingRepository<SystemDict, Long> {
    @Query("select dict from SystemDict dict where dict.name = :name")
    SystemDict getDictByName(@Param("name") String name);

    @Query(value = "select dict from SystemDict dict where dict.name like '%:name%' or dict.phoneticCode like '%:phoneticCode%' order by dict.name asc",
            countQuery = "SELECT COUNT(dict) FROM SystemDict dict where dict.name like '%:name%' or dict.phoneticCode like '%:phoneticCode%'")
    List<SystemDict> findAll(@Param("name") String name, @Param("phoneticCode") String phoneticCode, Pageable pageable);

    @Query("select count(*) from SystemDict dict where dict.name like '%:name%' or dict.phoneticCode like '%:phoneticCode%'")
    Integer countByNameOrPhoneticCode(@Param("name") String name, @Param("phoneticCode") String phoneticCode);
}

