package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjDataSave;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjDataSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/6/9.
 */
@Service
@Transactional
public class TjDataSaveService extends BaseJpaService<TjDataSave,XTjDataSaveRepository> {
    @Autowired
    XTjDataSaveRepository tjDataSaveRepository;

    public TjDataSave getById(Long id) {
        TjDataSave tjDataSave = tjDataSaveRepository.findOne(id);
        return tjDataSave;
    }

    public TjDataSave getByCode(String code) {
        return tjDataSaveRepository.findByCode(code);
    }

}
