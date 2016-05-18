package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsSystemDictionaryDao;
import com.yihu.ehr.resource.model.RsSystemDictionary;
import com.yihu.ehr.resource.service.intf.IRsSystemDictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsSystemDictionaryService extends BaseJpaService<RsSystemDictionary, RsSystemDictionaryDao> implements IRsSystemDictionaryService {

}
