package com.yihu.ehr.dfs.fastdfs.service;

import com.yihu.ehr.dfs.fastdfs.dao.SystemDictEntryRepository;
import com.yihu.ehr.dfs.fastdfs.dao.SystemDictRepository;
import com.yihu.ehr.entity.dict.SystemDict;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 系统字典管理器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 11:46
 */
@Service
@Transactional
public class SystemDictService extends BaseJpaService<SystemDict, SystemDictRepository> {

    @Autowired
    private SystemDictRepository dictRepo;

    public SystemDict findByPhoneticCode(String code) {
        return dictRepo.findByPhoneticCode(code);
    }

}
