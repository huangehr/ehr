package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsDictionaryQueryDao;
import com.yihu.ehr.resource.dao.intf.RsDictionaryDao;
import com.yihu.ehr.resource.model.RsDictionary;
import com.yihu.ehr.resource.service.intf.IRsDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsDictionaryService extends BaseJpaService<RsDictionary, RsDictionaryDao> implements IRsDictionaryService {

    @Autowired
    private RsDictionaryDao dictionaryDao;

    @Autowired
    private RsDictionaryQueryDao dictionaryQueryDao;

    public RsDictionary findById(String id) {
        return dictionaryDao.findOne(id);
    }

    public void batchInsertDictionaries(List<RsDictionary> dictionaries){
        dictionaryQueryDao.batchInsertDictionaries(dictionaries);
    }






}
