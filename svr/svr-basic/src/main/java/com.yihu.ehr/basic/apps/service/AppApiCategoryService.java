package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.AppApiCategoryDao;
import com.yihu.ehr.entity.api.AppApiCategory;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service - 接口分类
 * Created by progr1mmer on 2018/3/14.
 */
@Service
@Transactional
public class AppApiCategoryService extends BaseJpaService<AppApiCategory, AppApiCategoryDao> {

    @Autowired
    private AppApiCategoryDao appApiCategoryDao;

    public AppApiCategory findOne(Integer id) {
        return appApiCategoryDao.findOne(id);
    }

}
