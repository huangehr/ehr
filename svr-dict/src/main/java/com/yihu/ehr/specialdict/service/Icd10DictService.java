package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.specialdict.model.Icd10Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 用户公私钥管理
 *
 * @author CWS
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class Icd10DictService extends BaseJpaService<Icd10Dict, XIcd10DictRepository> {

    @Autowired
    private XIcd10DictRepository icd10DictRepo;
    @Autowired
    private XIcd10HpRelationRepository hpIcd10ReRepo;
    @Autowired
    private XIcd10DrugRelationRepository icd10DrugReRepo;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorReRepo;

    public boolean isCodeExist(String code){
        Icd10Dict icd10Dict = icd10DictRepo.findByCode(code);
        return icd10Dict != null;
    }

    public boolean isNameExist(String name){
        Icd10Dict icd10Dict = icd10DictRepo.findByName(name);
        return icd10Dict != null;
    }

    public Icd10Dict createDict(Icd10Dict dict) {
        dict.setName(dict.getName());
        icd10DictRepo.save(dict);
        return dict;
    }

    public Page<Icd10Dict> getDictList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return icd10DictRepo.findAll(pageable);
    }

    public boolean isUsage(long id){
        boolean result = ((hpIcd10ReRepo.findByIcd10Id(id) != null)||(icd10DrugReRepo.findByIcd10Id(id) != null)||(icd10IndicatorReRepo.findByIcd10Id(id) != null));
        return result;
    }

    public List<Icd10Dict> findByIds(long[] ids) {
        return  icd10DictRepo.findByIds(ids);
    }
}