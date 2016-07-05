package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.AddressDictKeySchema;
import com.yihu.ehr.schema.HealthProblemDictKeySchema;
import com.yihu.ehr.specialdict.model.DrugDict;
import com.yihu.ehr.specialdict.model.HealthProblemDict;
import com.yihu.ehr.specialdict.model.Icd10Dict;
import com.yihu.ehr.specialdict.model.IndicatorsDict;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
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
public class HealthProblemDictService extends BaseJpaService<HealthProblemDict, XHealthProblemDictRepository> {

    @Autowired
    private XHealthProblemDictRepository hpDictRepo;
    @Autowired
    private XIcd10HpRelationRepository hpIcd10ReRepo;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private HealthProblemDictKeySchema healthProblemDictKeySchema;

    public Page<HealthProblemDict> getDictList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return hpDictRepo.findAll(pageable);
    }

    public boolean isCodeExist(String code){
        HealthProblemDict hpDict = hpDictRepo.findByCode(code);
        return hpDict != null;
    }

    public boolean isNameExist(String name){
        HealthProblemDict hpDict = hpDictRepo.findByName(name);
        return hpDict != null;
    }

    public HealthProblemDict createDict(HealthProblemDict dict) {
        dict.setName(dict.getName());
        hpDictRepo.save(dict);
        return dict;
    }

    public boolean isUsage(long id){
        boolean result = (hpIcd10ReRepo.findByHpId(id) != null);
        return result;
    }

    public HealthProblemDict findHpDictByCode(String code) {
        return hpDictRepo.findByCode(code);
    }

    //根据健康问题代码获取药品信息
    public List<DrugDict> getDrugDictListByHpCode(String code) throws Exception{
        Session session = currentSession();
        String sql = "select d.* from icd10_drug_relation idr,drug_dict d,\n" +
                "(select i.id as icd10_id\n" +
                "from hp_icd10_relation r,icd10_dict i,health_problem_dict h\n" +
                "where r.hp_id=h.id \n" +
                "and r.icd10_id=i.id\n" +
                "and h.code=:code) icd10\n" +
                "where idr.drug_id=d.id\n" +
                "and idr.icd10_id = icd10.icd10_id";
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("code", code);
        query.addEntity(DrugDict.class);
        List<DrugDict> list = query.list();

        return list;
    }

    //根据健康问题代码获取指标信息
    public List<IndicatorsDict> getIndicatorsByHpCode(String code) throws Exception{
        Session session = currentSession();
        String sql = "select d.* from icd10_indicator_relation iir,indicators_dict d,\n" +
                "(select i.id as icd10_id\n" +
                "from hp_icd10_relation r,icd10_dict i,health_problem_dict h\n" +
                "where r.hp_id=h.id \n" +
                "and r.icd10_id=i.id\n" +
                "and h.code=:code) icd10\n" +
                "where iir.indicator_id=d.id\n" +
                "and iir.icd10_id = icd10.icd10_id";
        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("code", code);
        query.addEntity(IndicatorsDict.class);
        List<IndicatorsDict> list = query.list();
        return list;
    }

    //根据健康问题代码获取ICD10代码列表
    public List<Icd10Dict> getIcd10ByHpCode(String code) throws Exception{
        Session session = currentSession();
        String sql = "select i.* \n" +
                "from hp_icd10_relation r,icd10_dict i,health_problem_dict h\n" +
                "where r.hp_id=h.id \n" +
                "and r.icd10_id=i.id\n" +
                "and h.code=:code";

        SQLQuery query = session.createSQLQuery(sql);
        query.setParameter("code", code);
        query.addEntity(Icd10Dict.class);
        List<Icd10Dict> list = query.list();

        return list;
    }

    public boolean CacheIcd10ByHpCode() {
        Session session = currentSession();
        String sql = "select i.* \n" +
                "from hp_icd10_relation r,icd10_dict i,health_problem_dict h\n" +
                "where r.hp_id=h.id \n" +
                "and r.icd10_id=i.id\n" ;

        SQLQuery query = session.createSQLQuery(sql);
        query.addEntity(Icd10Dict.class);
        List<Icd10Dict> list = query.list();
        for(Icd10Dict icd10Dict:list){
            String redisKey = healthProblemDictKeySchema.HpCodeToIcd10KeySchema(icd10Dict.getCode());
            redisClient.set(redisKey,icd10Dict.getCode());
        }
        return true;
    }
    public String GetIcd10ByHpCodeCache(String code) {

        return  redisClient.get(healthProblemDictKeySchema.HpCodeToIcd10KeySchema(code));
    }
}