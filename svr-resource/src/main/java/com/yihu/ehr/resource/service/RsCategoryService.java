package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsCategoryDao;
import com.yihu.ehr.resource.model.RsCategory;
import com.yihu.ehr.resource.service.intf.IRsCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsCategoryService extends BaseJpaService<RsCategory, RsCategoryDao> implements IRsCategoryService {

}
