package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
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
public class DrugDictService extends BaseJpaService<DrugDict, XDrugDictRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XDrugDictRepository drugDictRepo;
    @Autowired
    private XIcd10DrugRelationRepository icd10DrugReRepo;

    public Page<DrugDict> getDictList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return drugDictRepo.findAll(pageable);
    }

    public boolean isCodeExist(String code){
        DrugDict drugDict = drugDictRepo.findByCode(code);
        return drugDict != null;
    }

    public boolean isNameExist(String name){
        DrugDict drugDict = drugDictRepo.findByName(name);
        return drugDict != null;
    }

    public DrugDict createDict(DrugDict dict) {
        dict.setName(dict.getName());
        drugDictRepo.save(dict);
        return dict;
    }

    public boolean isUsage(String id){
        boolean result = (icd10DrugReRepo.findByDrugId(id) != null);
        return result;
    }
}