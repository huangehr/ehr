package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResources;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface ResourcesDao extends PagingAndSortingRepository<RsResources,String> {

    List<RsResources> findByType(String type);

    List<RsResources> findByCode(String code);
 }
