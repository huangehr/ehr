package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.AdapterDictionaryQueryDao;
import com.yihu.ehr.resource.dao.intf.RsAdapterDictionaryDao;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsAdapterDictionaryService extends BaseJpaService<RsAdapterDictionary, RsAdapterDictionaryDao>  {

    @Autowired
    private RsAdapterDictionaryDao adapterDictionaryDao;

    @Autowired
    private AdapterDictionaryQueryDao adapterDictionaryQueryDao;

    public RsAdapterDictionary findById(String id) {
        return adapterDictionaryDao.findOne(id);
    }

    public void batchInsertAdapterDictionaries(RsAdapterDictionary[] AdapterDictionaries) throws SQLException {
        adapterDictionaryQueryDao.batchInsertAdapterDictionaries(AdapterDictionaries);
    }
}
