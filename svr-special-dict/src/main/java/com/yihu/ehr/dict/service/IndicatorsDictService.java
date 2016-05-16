package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 用户公私钥管理
 *
 * @author CWS
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class IndicatorsDictService extends BaseJpaService<IndicatorsDict, XIndicatorsDictRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XIndicatorsDictRepository indicatorsDictRepo;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorReRepo;

    public Page<IndicatorsDict> getDictList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return indicatorsDictRepo.findAll(pageable);
    }

    public boolean isCodeExist(String code){
        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode(code);
        return indicatorsDict != null;
    }

    public boolean isNameExist(String name){
        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByName(name);
        return indicatorsDict != null;
    }

    public boolean isUsage(String id){
        boolean result = (icd10IndicatorReRepo.findByIndicatorId(id) != null);
        return result;
    }

    public IndicatorsDict createDict(IndicatorsDict dict) {
        dict.setName(dict.getName());
        indicatorsDictRepo.save(dict);
        return dict;
    }
}