package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsDictionaryQueryDao;
import com.yihu.ehr.resource.dao.intf.RsDictionaryDao;
import com.yihu.ehr.resource.model.RsDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsDictionaryService extends BaseJpaService<RsDictionary, RsDictionaryDao>  {

    @Autowired
    private RsDictionaryDao dictionaryDao;

    @Autowired
    private RsDictionaryQueryDao dictionaryQueryDao;

    public RsDictionary findById(String id) {
        return dictionaryDao.findOne(id);
    }

    public void batchInsertDictionaries(RsDictionary[] dictionaries){
        dictionaryQueryDao.batchInsertDictionaries(dictionaries);
    }






}
