package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsAdapterScheme;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by lyr on 2016/5/17.
 */
public interface RsAdapterSchemeDao extends PagingAndSortingRepository<RsAdapterScheme, String> {
    List<RsAdapterScheme> findByAdapterVersion(String version);
}
