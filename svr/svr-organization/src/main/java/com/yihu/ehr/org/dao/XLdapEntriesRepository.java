package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.LdapEntries;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author add by HZY at 20170309
 */
public interface XLdapEntriesRepository extends PagingAndSortingRepository<LdapEntries, Long> {


    LdapEntries findByOcMapIdAndKeyval(Integer ocMapId,Long keyval) throws Exception;

    @Query(value = "select a.user_id from users a where a.id=?1 and a.activated='1'",nativeQuery = true)
    String getUserIdById(String id) throws Exception;
}
