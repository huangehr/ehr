package com.yihu.ehr.resolve.service.resource.stage2;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resolve.dao.RsDictionaryEntryDao;
import com.yihu.ehr.resolve.model.stage1.RsDictionaryEntry;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

/**
 * @author zdm
 * @created 2018.07.17
 */
@Service
@Transactional
public class RsDictionaryEntryService extends BaseJpaService<RsDictionaryEntry, RsDictionaryEntryDao> {

    @Transactional(readOnly = true)
    public String getRsDictionaryEntryByDictCode(String dictCode, String code) {
        Session session = currentSession();
        String sql = "SELECT NAME FROM rs_dictionary_entry WHERE DICT_CODE=:dictCode AND CODE=:code";
        Query query = session.createSQLQuery(sql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setString("dictCode", dictCode);
        query.setString("code", code);
        return (null== query.uniqueResult()?"": query.uniqueResult().toString());
    }
}
