package com.yihu.ehr.dfs.es.service;

import com.yihu.ehr.dfs.es.dao.FastDFSIndexDao;
import com.yihu.ehr.dfs.es.entity.FastDFSIndex;
import org.elasticsearch.client.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 * IndexService - FastDFS索引
 * Created by progr1mmer on 2017/11/25.
 */
@Service
public class FastDFSIndexService {

    @Autowired
    private FastDFSIndexDao fastDFSIndexDao;

    public FastDFSIndex index(FastDFSIndex fastDFS){
        return fastDFSIndexDao.save(fastDFS);
    }

    public void delete(String sn) {
        fastDFSIndexDao.delete(sn);
    }

    public FastDFSIndex findBySn(String sn) {
        return fastDFSIndexDao.findBySn(sn);
    }

    public Page<FastDFSIndex> search(SearchQuery searchQuery) {
        return fastDFSIndexDao.search(searchQuery);
    }
}
