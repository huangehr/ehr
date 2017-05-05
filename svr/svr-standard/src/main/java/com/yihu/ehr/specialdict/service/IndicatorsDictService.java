package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.redis.schema.IndicatorsDictKeySchema;
import com.yihu.ehr.specialdict.model.IndicatorsDict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
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
public class IndicatorsDictService extends BaseJpaService<IndicatorsDict, XIndicatorsDictRepository>{

    @Autowired
    private XIndicatorsDictRepository indicatorsDictRepo;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorReRepo;
    @Autowired
    private IndicatorsDictKeySchema indicatorsDictKeySchema;

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

    public boolean isUsage(long id){
        boolean result = (icd10IndicatorReRepo.findByIndicatorId(id) != null);
        return result;
    }

    public IndicatorsDict createDict(IndicatorsDict dict) {
        dict.setName(dict.getName());
        indicatorsDictRepo.save(dict);
        return dict;
    }

    public IndicatorsDict findByCode(String code) {
        return indicatorsDictRepo.findByCode(code);
    }

    public List<IndicatorsDict> getIndicatorsDictByIds(long[] ids) {
        return indicatorsDictRepo.findByIds(ids);
    }

    public List<IndicatorsDict> findAllIndicatorsDict(){
        return indicatorsDictRepo.findAllIndicatorsDict();
    }

    public void CacheIndicatorsDict() {
        try {
            for (IndicatorsDict m :findAllIndicatorsDict()) {
                HashMap<String,String> map=new HashMap<>();
                map.put("id",String.valueOf(m.getId()));
                map.put("code",m.getCode());
                map.put("name",m.getName());
                map.put("PhoneticCode",m.getPhoneticCode());
                map.put("type",m.getType());
                map.put("unit",m.getUnit());
                map.put("LowerLimit",m.getLowerLimit());
                map.put("UpperLimit",m.getUpperLimit());
                map.put("Description",m.getDescription());
                indicatorsDictKeySchema.set(m.getCode(), map);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public HashMap<String,String> getIndicatorsDictByCode(String code) {

        return  indicatorsDictKeySchema.get(code);
    }
}