package com.yihu.ehr.dfs.fastdfs.service;

import com.yihu.ehr.dfs.fastdfs.dao.SystemDictEntryRepository;
import com.yihu.ehr.entity.dict.DictEntryKey;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典项服务。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.14 14:49
 */
@Service
@Transactional
public class SystemDictEntryService extends BaseJpaService<SystemDictEntry, SystemDictEntryRepository> {

    @Autowired
    private SystemDictEntryRepository systemDictEntryRepository;

    /**
     * 按字典ID查找字典项.
     *
     * @param dictId
     * @param page
     * @param size
     * @return
     */
    public Page<SystemDictEntry> findByDictId(long dictId, int page, int size) {
        return systemDictEntryRepository.findByDictId(dictId, new PageRequest(page, size));
    }

    public void createDictEntry(SystemDictEntry systemDictEntry) {
        systemDictEntryRepository.save(systemDictEntry);
    }

}
