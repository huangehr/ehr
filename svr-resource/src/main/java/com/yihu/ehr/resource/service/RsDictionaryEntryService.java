package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsDictionartyEntryDao;
import com.yihu.ehr.resource.model.RsDictionaryEntry;
import com.yihu.ehr.resource.service.intf.IRsDictionaryEntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsDictionaryEntryService extends BaseJpaService<RsDictionaryEntry, RsDictionartyEntryDao> implements IRsDictionaryEntryService {

}
