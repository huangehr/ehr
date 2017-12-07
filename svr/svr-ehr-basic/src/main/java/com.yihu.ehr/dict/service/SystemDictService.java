package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.dao.SystemDictEntryRepository;
import com.yihu.ehr.dict.dao.SystemDictRepository;
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
    @Autowired
    private SystemDictEntryRepository entryRepo;

    public Page<SystemDict> getDictList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return dictRepo.findAll(pageable);
    }

    public SystemDict createDict(SystemDict dict) {
        dict.setCreateDate(new Date());
        dict.setName(dict.getName());
        dictRepo.save(dict);

        return dict;
    }

    public void updateDict(SystemDict dict) {
        dictRepo.save(dict);
    }

    public void deleteDict(long dictId) {
        entryRepo.deleteByDictId(dictId);
        dictRepo.delete(dictId);
    }

    public boolean isDictNameExists(String name) {
        SystemDict systemDict = dictRepo.findByName(name);

        return systemDict != null;
    }

    public Page<SystemDict> searchDict(String name, String phoneticCode, int page, int size) {
        return dictRepo.findByNameOrPhoneticCodeOrderByNameAsc(name, phoneticCode, new PageRequest(page, size));
    }

    public long getNextId() {
        long id = dictRepo.getNextId()+1;
        return id;
    }

    public SystemDict findByPhoneticCode(String code) {
        return dictRepo.findByPhoneticCode(code);
    }

}
