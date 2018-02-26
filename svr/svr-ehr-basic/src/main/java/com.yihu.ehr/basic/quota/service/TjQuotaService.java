package com.yihu.ehr.basic.quota.service;

import com.yihu.ehr.basic.quota.dao.XTjQuotaRepository;
import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.entity.quota.TjQuotaDataSave;
import com.yihu.ehr.entity.quota.TjQuotaDataSource;
import com.yihu.ehr.model.tj.MQuotaConfigModel;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

    public List<MQuotaConfigModel> getQuotaConfig(String quotaNameOrCode, Integer page, Integer pageSize) {
        Session session = entityManager.unwrap(Session.class);
        String sql = "SELECT h.name as quotaTypeName,tj.name as quotaName,tj.code as quotaCode,tj.id as quotaId from tj_quota tj left join tj_quota_category h on tj.quota_type = h.id where 1 = 1";
        if (!StringUtils.isEmpty(quotaNameOrCode)) {
            sql += " AND tj.name LIKE :quotaName";
            sql += " OR tj.code = :quotaCode";
        }
        Query query = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(MQuotaConfigModel.class));
        if (!StringUtils.isEmpty(quotaNameOrCode)) {
            query.setParameter("quotaName", "%" + quotaNameOrCode + "%");
            query.setParameter("quotaCode", quotaNameOrCode);
        }
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        List<MQuotaConfigModel> quotaConfigList = query.list();
        return quotaConfigList;
    }

    public int getCountInfo(String quotaNameOrCode) {
        Session session = entityManager.unwrap(Session.class);
        String sql = "SELECT count(*) from tj_quota tj left join tj_quota_category h on tj.quota_type = h.id where 1 = 1";
        if (!StringUtils.isEmpty(quotaNameOrCode)) {
            sql += " AND tj.name LIKE :quotaName";
            sql += " OR tj.code = :quotaCode";
        }
        Query query = session.createSQLQuery(sql);
        if (!StringUtils.isEmpty(quotaNameOrCode)) {
            query.setParameter("quotaName", "%" + quotaNameOrCode + "%");
            query.setParameter("quotaCode", quotaNameOrCode);
        }
        Object ob  = (query.list().get(0));
        int count = Integer.parseInt(ob.toString());
        return count;
    }


    /**
     * 查询指标编码/指标名称是否已存在， 返回已存在数据
     */
    public List tjQuotaTypeIsExist(String type,String[] values)
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

    /**
     * 导入指标tj_quota-TjQuota、数据源tj_quota_data_source-TjQuotaDataSource、数据存储tj_quota_data_save-TjQuotaDataSave
     * @param models
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void tjQuotaBatch(List<Map<String, Object>> models) throws SQLException, InstantiationException, IllegalAccessException {
        for(Map<String, Object> map: models) {
            addTjQuota(map);
            //数据源
            TjQuotaDataSource dataSource = new TjQuotaDataSource();
            dataSource.setQuotaCode(String.valueOf(map.get("code")));
            dataSource.setSourceCode(String.valueOf(map.get("quotaDataSource")));
            dataSource.setConfigJson(String.valueOf(map.get("quotaDataSourceConfigJson")));
            tjQuotaDataSourceService.save(dataSource);
            //数据存储
            TjQuotaDataSave dataSave = new TjQuotaDataSave();
            dataSave.setQuotaCode(String.valueOf(map.get("code")));
            dataSave.setSaveCode(String.valueOf(map.get("quotaDataSave")));
            dataSave.setConfigJson(String.valueOf(map.get("quotaDataSaveConfigJson")));
            tjQuotaDataSaveService.save(dataSave);
        }
    }

    /**
     * 存储数据源tj_quota_data_source-TjQuotaDataSource
     * @param quotaSet
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public long addTjQuota(Map<String, Object> quotaSet) throws IllegalAccessException, InstantiationException {
        TjQuota data = new TjQuota();
       try {
           data.setCode(String.valueOf(quotaSet.get("code")));
           data.setName(String.valueOf(quotaSet.get("name")));
           data.setCron(String.valueOf(quotaSet.get("cron")));
           data.setExecType(String.valueOf(quotaSet.get("execType")));
           data.setQuotaType(Integer.valueOf(String.valueOf(quotaSet.get("quotaType"))));
           data.setJobClazz(String.valueOf(quotaSet.get("jobClazz")));
           data.setCreateTime(DateUtil.strToDate(DateUtil.getNowDateTime()));
           data.setCreateUser(String.valueOf(quotaSet.get("createUser")));
           data.setCreateUserName(String.valueOf(quotaSet.get("createUserName")));
           data.setStatus(Integer.valueOf(String.valueOf(quotaSet.get("status"))));
           data.setDataLevel(Integer.valueOf(String.valueOf(quotaSet.get("dataLevel"))));
           data.setRemark(String.valueOf(quotaSet.get("remark")));
           data.setIsInitExec("0");
           int maxId = getMaxIdNumber();
           maxId = maxId + 1;
           String newId = "" + maxId;
           for (int i = newId.length(); i < 6; i++) {
               newId = "0" + newId;
           }
           String metadataCode = "EHR_" + newId;
           metadataSave(String.valueOf(quotaSet.get("name")), metadataCode);
           data.setMetadataCode(metadataCode);
           save(data);
       }catch (Exception e){
           e.printStackTrace();
       }
        return data.getId();
    }

    /**
     * 获取资源数据元最大编码
     * @return
     */
    public int getMaxIdNumber() {
        String sql = "SELECT MAX(CONVERT(case when ID is not null  then substring(ID,5) else '0' end ,SIGNED)) from rs_metadata";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        List list = sqlQuery.list();
        if(list != null && list.size() > 0){
            return Integer.valueOf(list.get(0).toString());
        }else{
            return 0;
        }
    }

    /**
     * 保存数据元
     * @param quotaName
     * @param metadataCode
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void metadataSave(String quotaName,String metadataCode) throws SQLException, InstantiationException, IllegalAccessException {
        String sql = "INSERT INTO rs_metadata " +
                "(ID, DOMAIN, NAME, STD_CODE, COLUMN_TYPE, NULL_ABLE,DESCRIPTION, VALID,data_source) VALUES ";
        StringBuilder sb = new StringBuilder(sql);
        sb.append("('"+ metadataCode +"',");
        sb.append("'"+ "04" +"',");
        sb.append("'"+quotaName +"',");
        sb.append("'"+metadataCode +"',");
        sb.append("'"+ "VARCHAR" +"',");
        sb.append("'"+"1" +"',");
        sb.append("'"+ "统计指标:" + quotaName+"',");
        sb.append("'"+ "1" +"',");
        sb.append("'"+ 2 +"')");
        currentSession().createSQLQuery(sb.toString()).executeUpdate();
    }
}
