package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsDictionaryEntryDao;
import com.yihu.ehr.resource.model.RsDictionaryEntry;
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
public class RsDictionaryEntryService extends BaseJpaService<RsDictionaryEntry, RsDictionaryEntryDao> {

    @Autowired
    private RsDictionaryEntryDao dictionaryEntryDao;

    public RsDictionaryEntry findById(int id) {
        return dictionaryEntryDao.findOne(id);
    }

    public List<RsDictionaryEntry> findByDictCode(String code) {
        return dictionaryEntryDao.findByDictCode(code);
    }

    public int countByDictId(int dictId) {

        return dictionaryEntryDao.countByDictId(dictId);
    }
}
