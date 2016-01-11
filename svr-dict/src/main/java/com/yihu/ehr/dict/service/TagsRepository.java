package com.yihu.ehr.dict.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by zqb on 2015/12/21.
 */
public interface TagsRepository extends PagingAndSortingRepository<Tags, String> {
    @Query("select dict from Tags dict where dict.dictId = 17  order by dict.sort asc")
    List<Tags> getTagList();
}
