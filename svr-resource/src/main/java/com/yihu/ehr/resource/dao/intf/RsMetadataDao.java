package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsMetadata;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 数据元DAO
 *
 * Created by lyr on 2016/5/16.
 */
public interface RsMetadataDao extends PagingAndSortingRepository<RsMetadata,String> {

    @Query("select a from RsMetadata a where a.dictCode <> null and a.dictCode <> '' ")
    List<RsMetadata> findMetadataExistDictCode();
}
