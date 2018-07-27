package com.yihu.ehr.basic.dict.service;

import com.yihu.ehr.basic.dict.dao.SystemDictEntryRepository;
import com.yihu.ehr.entity.dict.DictEntryKey;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
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
     * 下一字典项排序号。
     *
     * @param dictId
     * @return
     */
    public int getNextSN(long dictId) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();
        Integer nextSort = repo.getNextEntrySN(dictId);
        return null == nextSort ? 1 : nextSort + 1;
    }

    /**
     * 获取所有字典项。对于大字典，若不分页效率可能会很低。
     *
     * @param dictId 字典ID
     * @param page   分页，-1 表示查找全部
     * @param size   页大小, page 为 -1 时忽略此参数
     * @return
     */
    public Page<SystemDictEntry> getDictEntries(long dictId, int page, int size) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        return repo.findByDictId(dictId, page == -1 ? null : new PageRequest(page, size));
    }

    /**
     * 获取简易字典项列表.
     *
     * @param dictId
     * @param codes  字典项代码列表,为空返回所有字典项. 但对于大字典效率会很低.
     * @return
     */
    public List<SystemDictEntry> getDictEntries(long dictId, String[] codes) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        if (codes == null) {
            return repo.findByDictId(dictId);
        } else {
            return repo.findByDictIdAndCodes(dictId, codes);
        }
    }

    /**
     * 获取字典项。
     *
     * @param dictId
     * @param code
     * @return
     */
    public SystemDictEntry getDictEntry(long dictId, String code) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        return repo.findOne(new DictEntryKey(code, dictId));
    }

    public SystemDictEntry getDictEntryByValueAndDictId(String value, long dictId) {
        List<SystemDictEntry> systemDictEntryList = systemDictEntryRepository.findByDictIdAndValue(dictId, value);
        if (null != systemDictEntryList && systemDictEntryList.size() > 0) {
            return systemDictEntryList.get(0);
        }
        return null;
    }

    /**
     * 按字典ID与字典项值查找字典项.
     *
     * @param dictId
     * @param value
     * @param page
     * @param size
     * @return
     */
    public Page<SystemDictEntry> findByDictIdAndValueLike(long dictId, String value, int page, int size) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        return repo.findByDictIdAndValueLike(dictId, value, new PageRequest(page, size));
    }

    /**
     * 按字典ID查找字典项.
     *
     * @param dictId
     * @param page
     * @param size
     * @return
     */
    public Page<SystemDictEntry> findByDictId(long dictId, int page, int size) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        return repo.findByDictId(dictId, new PageRequest(page, size));
    }

    public boolean isDictContainEntry(long dictId, String code) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        SystemDictEntry systemDictEntry = repo.findOne(new DictEntryKey(code, dictId));

        return systemDictEntry != null;
    }

    public SystemDictEntry saveDictEntry(SystemDictEntry systemDictEntry) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        return repo.save(systemDictEntry);
    }

    public void createDictEntry(SystemDictEntry systemDictEntry) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();

        repo.save(systemDictEntry);
    }

    public void deleteDictEntry(long dictId, String code) {
        SystemDictEntryRepository repo = (SystemDictEntryRepository) getJpaRepository();
        repo.delete(new DictEntryKey(code, dictId));
    }


    /**
     * 根据字典id获取字典项编码和值
     */
    public List getDictEntryCodeAndValueByDictId(String dict_id) {
        String sql = "SELECT code,value FROM system_dict_entries where dict_id=:dict_id";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameter("dict_id", dict_id);
        return sqlQuery.list();
    }
}
