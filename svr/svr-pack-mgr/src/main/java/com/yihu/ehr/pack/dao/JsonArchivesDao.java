package com.yihu.ehr.pack.dao;

import com.yihu.ehr.pack.entity.JsonArchives;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 临时数据处理
 * Created by progr1mmer on 2018/4/23.
 */
public interface JsonArchivesDao extends PagingAndSortingRepository<JsonArchives, String> {

}
