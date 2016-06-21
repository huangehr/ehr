package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsAdapterDictionaryDao;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsAdapterDictionaryService extends BaseJpaService<RsAdapterDictionary, RsAdapterDictionaryDao>  {

    @Autowired
    private RsAdapterDictionaryDao adapterDictionaryDao;

    public RsAdapterDictionary findById(String id) {
        return adapterDictionaryDao.findOne(id);
    }


}
