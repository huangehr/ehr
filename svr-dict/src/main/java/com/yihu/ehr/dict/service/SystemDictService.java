package com.yihu.ehr.dict.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 系统字典管理器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 11:46
 */
@Service
@Transactional
public class SystemDictService {
    @Autowired
    private XSystemDictRepository systemDictRepository;

    @Autowired
    private XSystemDictEntryRepository systemDictEntryRepository;

    public SystemDict getDict(long dictId) {
        return systemDictRepository.findOne(dictId);
    }

    public List<SystemDictEntry> getDictEntries(long dictId, String name) {
        if (null == name){
            return systemDictEntryRepository.getByDictId(dictId);
        } else {
            return systemDictEntryRepository.getEntryList(dictId, name);
        }
    }

    public int getNextSort(long dictId) {
        Integer nextSort = systemDictEntryRepository.getNextEntrySort(dictId);
        if (null == nextSort) return 1;

        return nextSort + 1;
    }

    public boolean isDictExists(String name) {
        SystemDict systemDict = systemDictRepository.getDictByName(name);

        return systemDict != null;
    }

    public SystemDict createDict(String name, String reference, String author) {
        SystemDict systemDict = new SystemDict();
        systemDict.setName(name);
        systemDict.setReference(reference);
        systemDict.setAuthorId(author);
        systemDictRepository.save(systemDict);

        return systemDict;
    }

    public void updateDict(SystemDict dict) {
        systemDictRepository.save(dict);
    }

    public void deleteDict(long dictId) {
        systemDictEntryRepository.deleteByDictId(dictId);
        systemDictRepository.delete(dictId);
    }

    public List<SystemDict> getDictionaries(Map<String, Object> args) {
        String name = (String) args.get("name");
        String phoneticCode = (String) args.get("phoneticCode");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");

        return systemDictRepository.findAll(name, phoneticCode, new PageRequest(page, pageSize));
    }

    public SystemDictEntry getDictEntry(long dictId, String code){
        return systemDictEntryRepository.findOne(new DictEntryKey(code, dictId));
    }

    /**
     * 获取符合特定名称的字典数量。
     *
     * @param args
     * @return
     */
    public Integer dictCount(Map<String, Object> args) {
        String name = (String) args.get("name");
        String phoneticCode = (String) args.get("phoneticCode");

        return systemDictRepository.countByNameOrPhoneticCode(name, phoneticCode);
    }

    public List<SystemDictEntry> searchEntryList(Map<String, Object> args) {
        Long dictId = (Long) args.get("dictId");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");

        return systemDictEntryRepository.findAll(dictId, new PageRequest(page, pageSize));
    }


    public SystemDictEntry saveSystemDictEntry(SystemDictEntry systemDictEntry) {
        return systemDictEntryRepository.save(systemDictEntry);
    }

    public boolean isDictContainEntry(long dictId, String code) {
        SystemDictEntry systemDictEntry = systemDictEntryRepository.findOne(new DictEntryKey(code, dictId));

        return systemDictEntry != null;
    }

    public void createDictEntry(SystemDictEntry systemDictEntry) {
        systemDictEntryRepository.save(systemDictEntry);
    }

    public void deleteDictEntry(long dictId, String code) {
        systemDictEntryRepository.delete(new DictEntryKey(code, dictId));
    }

    public SystemDictEntry getConvertionalDict(long dictId, String entryCode){
        return systemDictEntryRepository.getConvertionalDict(dictId, entryCode);
    }

    public List<SystemDictEntry> getConvertionalDicts(long dictId, String[] codes) {
        return systemDictEntryRepository.getConventionalDictList(dictId, codes);
    }
}
