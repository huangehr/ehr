package com.yihu.ehr.dfs.fastdfs.dao;

import com.yihu.ehr.entity.dict.SystemDict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 字典CRUD操作接口使用 PagingAndSortingRepository 中的方法，除非有特殊需要再添加自定义方法。
 *
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface SystemDictRepository extends JpaRepository<SystemDict, Long> {

    SystemDict findByName(String name);

    Page<SystemDict> findByNameOrPhoneticCodeOrderByNameAsc(String name, String phoneticCode, Pageable pageable);

    @Query("select max(dict.id) from SystemDict dict where 1=1")
    long getNextId();

    SystemDict findByPhoneticCode(String phoneticCode);
}

