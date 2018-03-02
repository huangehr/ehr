package com.yihu.ehr.dfs.fastdfs.dao;

import com.yihu.ehr.entity.dict.SystemDict;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 字典CRUD操作接口使用 PagingAndSortingRepository 中的方法，除非有特殊需要再添加自定义方法。
 *
 * @author CWS
 * @version 1.0
 * @created 2015.07.30 14:43
 */
public interface SystemDictDao extends PagingAndSortingRepository<SystemDict, Long> {

    SystemDict findByPhoneticCode(String phoneticCode);
}

