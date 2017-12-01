package com.yihu.ehr.dfs.es.dao;

import com.yihu.ehr.dfs.es.entity.FastDFSIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * IndexDao - FastDFS索引
 * Created by progr1mmer on 2017/11/25.
 */
@Repository
public interface FastDFSIndexDao extends ElasticsearchRepository<FastDFSIndex, String> {

    FastDFSIndex findBySn(String sn);

}
