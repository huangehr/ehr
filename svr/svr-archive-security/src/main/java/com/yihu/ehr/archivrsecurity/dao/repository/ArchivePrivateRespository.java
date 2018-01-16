package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.ScArchivePrivate;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by LYR-WORK on 2016/7/11.
 */
public interface ArchivePrivateRespository extends PagingAndSortingRepository<ScArchivePrivate,String> {

    ScArchivePrivate findByUserIdAndRowKey(String userId, String rowKey);

    void deleteByUserIdAndRowKey(String userId, String rowKey);

    List<ScArchivePrivate> findByUserId(String userId);

    List<ScArchivePrivate> findByUserIdAndStatus(String userId, int status);

}
