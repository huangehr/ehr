package com.yihu.ehr.dfs.fastdfs.service;

import com.yihu.ehr.dfs.fastdfs.dao.SystemDictDao;
import com.yihu.ehr.entity.dict.SystemDict;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统字典管理器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 11:46
 */
@Service
@Transactional
public class SystemDictService extends BaseJpaService<SystemDict, SystemDictDao> {

    @Autowired
    private SystemDictDao systemDictDao;

    public SystemDict findByPhoneticCode(String code) {
        return systemDictDao.findByPhoneticCode(code);
    }

}
