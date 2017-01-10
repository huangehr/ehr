package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsCategoryDao;
import com.yihu.ehr.resource.model.RsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsCategoryService extends BaseJpaService<RsCategory, RsCategoryDao> {

    @Autowired
    private  RsCategoryDao categoryDao;

    public RsCategory FindById(String id) {
        return categoryDao.findOne(id);
    }
}
