package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.entity.quota.TjQuotaDataSave;
import com.yihu.ehr.entity.quota.TjQuotaDataSource;
import com.yihu.ehr.model.tj.MQuotaConfigModel;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XTjQuotaRepository;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
@Service
@Transactional
public class TjQuotaService extends BaseJpaService<TjQuota, XTjQuotaRepository> {
    @Autowired
    XTjQuotaRepository tjQuotaRepository;
    @Autowired
    TjQuotaDataSourceService tjQuotaDataSourceService;
    @Autowired
    TjQuotaDataSaveService tjQuotaDataSaveService;

    public TjQuota saves(TjQuota quota, TjQuotaDataSource dataSource, TjQuotaDataSave dataSave) {
        if(dataSource != null){
            tjQuotaDataSourceService.deleteByQuotaCode(dataSource.getQuotaCode());
            tjQuotaDataSourceService.save(dataSource);
        }
        if(dataSave != null){
            tjQuotaDataSaveService.deleteByQuotaCode(dataSave.getQuotaCode());
            tjQuotaDataSaveService.save(dataSave);
        }
        quota.setName(quota.getName().trim());
        quota.setCode(quota.getCode().trim());
        quota = save(quota);
        return quota;
    }

    public TjQuota getById(Long id) {
        TjQuota tjQuota = tjQuotaRepository.findOne(id);
        return tjQuota;
    }

    public TjQuota findByCode(String code) {
        TjQuota tjQuota = tjQuotaRepository.findByCode(code);
        return tjQuota;
    }

    public List<MQuotaConfigModel> getQuotaConfig(String quotaName, Integer page, Integer pageSize) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String sql = "SELECT h.name as quotaTypeName,tj.name as quotaName,tj.code as quotaCode,tj.id as quotaId from tj_quota tj left join tj_quota_category h on tj.quota_type = h.id where 1 = 1";
        if (!StringUtils.isEmpty(quotaName)) {
            sql += " AND tj.name LIKE :quotaName";
        }
        Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(MQuotaConfigModel.class));
        if (!StringUtils.isEmpty(quotaName)) {
            query.setParameter("quotaName", "%" + quotaName + "%");
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<MQuotaConfigModel> quotaConfigList = query.list();
        return quotaConfigList;
    }

    public int getCountInfo(String quotaName) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String sql = "SELECT count(*) from tj_quota tj left join tj_quota_category h on tj.quota_type = h.id where 1 = 1";
        if (!StringUtils.isEmpty(quotaName)) {
            sql += " AND tj.name LIKE :quotaName";
        }
        Query query = session.createSQLQuery(sql);
        if (!StringUtils.isEmpty(quotaName)) {
            query.setParameter("quotaName", "%" + quotaName + "%");
        }
        Object ob  = (query.list().get(0));
        int count = Integer.parseInt(ob.toString());
        return count;
    }


    /**
     * 查询指标编码/指标名称是否已存在， 返回已存在数据
     */
    public List isExist(String type,String[] values)
    {
        String sql ="";
        if("code".equals(type)){
            sql = "SELECT code FROM tj_quota WHERE code in(:values)";

        }else{
            sql = "SELECT name FROM tj_quota WHERE name in(:values)";
        }
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("values", values);
        return sqlQuery.list();
    }
}
