package com.yihu.ehr.dict.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 字典CRUD操作接口使用 PagingAndSortingRepository 中的方法，除非有特殊需要再添加自定义方法。
 *
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
interface XSystemDictRepository extends JpaRepository<SystemDict, Long> {
    SystemDict findByName(String name);

    Page<SystemDict> findByNameOrPhoneticCodeOrderByNameAsc(String name, String phoneticCode, Pageable pageable);

    @Query("select max(dict.id) from SystemDict dict where 1=1")
    long getNextId();
}

