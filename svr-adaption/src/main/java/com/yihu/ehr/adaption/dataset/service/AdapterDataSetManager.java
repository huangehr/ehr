package com.yihu.ehr.adaption.dataset.service;


import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.util.service.BaseManager;
import com.yihu.ehr.adaption.dict.service.AdapterDictManager;
import com.yihu.ehr.adaption.feignclient.DictClient;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@Service
public class AdapterDataSetManager extends BaseManager<AdapterDataSet, XAdapterDataSetRepository> {
    @Autowired
    OrgAdapterPlanManager orgAdapterPlanManager;
    @Autowired
    AdapterDictManager adapterDictManager;
    @Autowired
    DictClient dictClient;

    /**
     * 根据方案ID及查询条件查询数据集适配关系
     *
     * @param planId
     * @param strKey
     */
    public List<DataSetModel> searchAdapterDataSet(long planId, String strKey, int page, int rows) {

        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
        String dsTableName = CDAVersionUtil.getDataSetTableName(orgAdapterPlan.getVersion());
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();

        sb.append(" select distinct " + dsTableName + ".id    ");
        sb.append("       ," + dsTableName + ".code  ");
        sb.append("       ," + dsTableName + ".name  ");
        sb.append("   from adapter_dataset ads     ");
        sb.append("        left join " + dsTableName + " on ads.std_dataset = " + dsTableName + ".id  ");
        sb.append("  where ads.plan_id = " + planId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append(" and (" + dsTableName + ".code like '%" + strKey + "%' or " + dsTableName + ".name like '%" + strKey + "%')");
        }
        sb.append(" order by " + dsTableName + ".code");


        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);

        sqlQuery.setMaxResults(rows);
        sqlQuery.setFirstResult((page - 1) * rows);

        List<Object> records = sqlQuery.list();

        if (records.size() == 0) {
            return null;
        } else {
            List<DataSetModel> dataSetModels = new ArrayList<>();
            for (int i = 0; i < records.size(); ++i) {
                Object[] record = (Object[]) records.get(i);
                DataSetModel dataSet = new DataSetModel();
                dataSet.setId((Integer) record[0]);
                dataSet.setCode((String) record[1]);
                dataSet.setName((String) record[2]);
                dataSetModels.add(dataSet);
            }
            return dataSetModels;
        }
    }


    /**
     * 查询适配数据集的总条数
     *
     * @return
     */
    public int searchDataSetInt(long planId, String strKey) {
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
        String dsTableName = CDAVersionUtil.getDataSetTableName(orgAdapterPlan.getVersion());
        sb.append(" select distinct " + dsTableName + ".id    ");
        sb.append("       ," + dsTableName + ".code  ");
        sb.append("       ," + dsTableName + ".name  ");
        sb.append("   from adapter_dataset ads     ");
        sb.append("        left join " + dsTableName + " on ads.std_dataset = " + dsTableName + ".id  ");
        sb.append("  where ads.plan_id = " + planId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append(" and (" + dsTableName + ".code like '%" + strKey + "%' or " + dsTableName + ".name like '%" + strKey + "%')");
        }
        sb.append(" order by " + dsTableName + ".code");
        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        int i=  sqlQuery.list().size();
        return i;
    }


    /**
     * 根据datasetId搜索数据元适配关系
     *
     * @param planId
     * @param dataSetId
     * @param strKey
     */
    public List<AdapterDataSetModel> searchAdapterMetaData(long planId, long dataSetId, String strKey, int page, int rows) {

        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
        String orgCode = orgAdapterPlan.getOrg();
        String dsTableName = CDAVersionUtil.getDataSetTableName(orgAdapterPlan.getVersion());
        String mdTableName = CDAVersionUtil.getMetaDataTableName(orgAdapterPlan.getVersion());

        Session session = currentSession();
        StringBuilder sb = new StringBuilder();

        sb.append(" select ads.id                    ");
        sb.append("       ,ads.plan_id               ");
        sb.append("       ," + dsTableName + ".id   as dsid  ");
        sb.append("       ," + dsTableName + ".code  as dscode ");
        sb.append("       ," + dsTableName + ".name  as dsname ");
        sb.append("       ," + mdTableName + ".id    as mdid ");
        sb.append("       ," + mdTableName + ".inner_code  as mdcode ");
        sb.append("       ," + mdTableName + ".name   as mdname ");
        sb.append("       ," + mdTableName + ".column_type  as mdcolumn_type ");
        sb.append("       ,  orgDS.id   as orgDSid ");
        sb.append("       ,  orgDS.code as orgDScode ");
        sb.append("       ,  orgDS.name as orgDSname ");
        sb.append("       ,  orgMD.id as orgMDid  ");
        sb.append("       ,  orgMD.code as orgMDcode ");
        sb.append("       ,  orgMD.name as orgMDname ");
        sb.append("   from adapter_dataset ads ");
        sb.append("        left join " + dsTableName + " on ads.std_dataset = " + dsTableName + ".id  ");
        sb.append("        left join " + mdTableName + " on ads.std_metadata = " + mdTableName + ".id ");
        sb.append("        left join org_std_dataset orgDS on (orgDS.sequence = ads.org_dataset and orgDS.organization='" + orgCode + "')    ");
        sb.append("        left join org_std_metadata orgMD on (orgMD.sequence = ads.org_metadata and orgMD.organization='" + orgCode + "')  ");
        sb.append("  where ads.plan_id = " + planId);
        sb.append("    and ads.std_dataset = " + dataSetId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append("    and (" + mdTableName + ".code like '%" + strKey + "%' or " + mdTableName + ".name like '%" + strKey + "%')");
        }
        sb.append(" order by " + mdTableName + ".code");

        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);

        if(rows>0){
            sqlQuery.setMaxResults(rows);
            sqlQuery.setFirstResult((page - 1) * rows);
        }

        List<Object> records = sqlQuery.list();

        if (records.size() == 0) {
            return null;

        } else {

            List<AdapterDataSetModel> adapterDataSetModels = new ArrayList<>();
            for (int i = 0; i < records.size(); ++i) {
                Object[] record = (Object[]) records.get(i);
                AdapterDataSetModel adapterDataSetModel = new AdapterDataSetModel();

                adapterDataSetModel.setId(Long.parseLong(record[0].toString()));
                adapterDataSetModel.setAdapterPlanId(Long.parseLong(record[1].toString()));
                adapterDataSetModel.setDataSetId(Long.parseLong(record[2].toString()));
                adapterDataSetModel.setDataSetCode(record[3].toString());
                adapterDataSetModel.setDataSetName(record[4].toString());
                adapterDataSetModel.setMetaDataId(Long.parseLong(record[5].toString()));
                adapterDataSetModel.setMetaDataCode(record[6].toString());
                adapterDataSetModel.setMetaDataName(record[7].toString());
                if (record[8] != null) {
                    adapterDataSetModel.setDataTypeName(record[8].toString());
                }
                if (record[9] != null) {
                    adapterDataSetModel.setOrgDataSetSeq(Long.parseLong(record[9].toString()));
                }
                if (record[10] != null) {
                    adapterDataSetModel.setOrgDataSetCode(record[10].toString());
                }
                if (record[11] != null) {
                    adapterDataSetModel.setOrgDataSetName(record[11].toString());
                }
                if (record[12] != null) {
                    adapterDataSetModel.setOrgMetaDataSeq(Long.parseLong(record[12].toString()));
                }
                if (record[13] != null) {
                    adapterDataSetModel.setOrgMetaDataCode(record[13].toString());
                }
                if (record[14] != null) {
                    adapterDataSetModel.setOrgMetaDataName(record[14].toString());
                }

                adapterDataSetModels.add(adapterDataSetModel);
            }

            return adapterDataSetModels;
        }
    }


    public int searchMetaDataInt(long planId, long dataSetId, String strKey) {
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
        String mdTableName = CDAVersionUtil.getMetaDataTableName(orgAdapterPlan.getVersion());
        sb.append(" select count(*)    ");
        sb.append("   from adapter_dataset ads     ");
        sb.append("        left join " + mdTableName + " on ads.std_metadata = " + mdTableName + ".id ");
        sb.append("  where ads.plan_id = " + planId);
        sb.append("    and ads.std_dataset = " + dataSetId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append("    and (" + mdTableName + ".code like '%" + strKey + "%' or " + mdTableName + ".name like '%" + strKey + "%')");
        }
        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        BigInteger i=  (BigInteger)sqlQuery.list().get(0);
        return i.intValue();
    }



    /**
     * 新增数据元映射关系
     * @param adapterDataSet
     * @param orgAdapterPlan
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public void addAdapterDataSet(String apiVersion, AdapterDataSet adapterDataSet, OrgAdapterPlan orgAdapterPlan) {
        //新增需要适配的字典
        Long planId = adapterDataSet.getAdapterPlanId();
        String cdaVersion = orgAdapterPlan.getVersion();
        Map map = dictClient.getDataSetMapByIds(apiVersion, cdaVersion, adapterDataSet.getDataSetId(), adapterDataSet.getMetaDataId());
        if(map!=null){
            Long dictId = Long.parseLong((String) map.get("dictId"));
            adapterDataSet.setStdDict(dictId);
            if (!adapterDictManager.isExist(planId, dictId)) {
                adapterDictManager.addAdapterDict(planId, dictId, cdaVersion);
            }
        }
        saveAdapterDataSet(adapterDataSet);
    }


    @Transactional(propagation= Propagation.REQUIRED)
    public int saveAdapterDataSet(AdapterDataSet adapterDataSet){
        Session s = currentSession();
        String sql =
                " insert into healtharchive.adapter_dataset " +
                        " (plan_id, std_dataset, std_metadata, org_dataset, org_metadata, data_type, description, std_dict) " +
                        " values " +
                        " (:plan_id, :std_dataset, :std_metadata, :org_dataset, :org_metadata, :data_type, :description, :std_dict) ";
        Query q = s.createSQLQuery(sql);
        q.setParameter("plan_id", adapterDataSet.getAdapterPlanId());
        q.setParameter("std_dataset", adapterDataSet.getDataSetId());
        q.setParameter("std_metadata", adapterDataSet.getMetaDataId());
        q.setParameter("org_dataset", adapterDataSet.getOrgDataSetSeq());
        q.setParameter("org_metadata", adapterDataSet.getOrgMetaDataSeq());
        q.setParameter("data_type", adapterDataSet.getDataType());
        q.setParameter("description", adapterDataSet.getDescription());
        q.setParameter("std_dict", adapterDataSet.getStdDict());
        int rs = q.executeUpdate();
        return rs;
    }


    /**
     * 删除数据元适配关系
     *
     * @param ids return >0 success
     */
    public int deleteAdapterDataSet(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        Map<String,List<Long>> map = new HashMap();
        String key = "";
        List<Long> value = null;
        Long stdDictId, planId;
        //要先删除数据字典映射
        for (Long id : ids) {
            AdapterDataSet ds = findOne(id);
            stdDictId = ds.getStdDict();
            if(stdDictId!=null){
                planId = ds.getAdapterPlanId();
                key = planId +","+ stdDictId;
                if((value = map.get("key"))==null)
                    value = new ArrayList<Long>();
                value.add(ds.getId());
                map.put(key, value);
            }
        }
        for(String k : map.keySet()){
            planId = Long.parseLong(k.split(",")[0]);
            stdDictId = Long.parseLong(k.split(",")[1]);
            if(!isStdDictRelatedIgnore(map.get(k), planId, stdDictId))
                adapterDictManager.deleteAdapterDict(planId, stdDictId);
        }

        Session session = currentSession();
        Query query = session.createQuery("delete from AdapterDataSet where id in (:ids)");
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }


    public boolean isStdDictRelatedIgnore(List<Long> dataSetId, Long planId, Long stdDictId){
        Session s = currentSession();
        String sql = "select count(*) from adapter_dataset where " +
                "plan_id=:planId and std_dict=:stdDictId and id not in(:dataSetId)";
        Query q = s.createSQLQuery(sql);
        q.setLong("planId", planId);
        q.setLong("stdDictId", stdDictId);
        q.setParameterList("dataSetId", dataSetId);
        return ((BigInteger)q.list().get(0)).intValue() > 0;
    }


    /**
     * 根据方案ID获取数据元映射
     *
     * @param adapterPlanId
     */
    public List<AdapterDataSet> getAdapterMetaDataByPlan(Long adapterPlanId) {
        Session session = currentSession();
        Query query = session.createQuery(" from AdapterDataSet where adapterPlanId = :adapterPlanId");
        query.setParameter("adapterPlanId", adapterPlanId);
        return query.list();
    }

    /**
     * 获取已定制标准数据集
     *
     * @param planId
     */
    public List<Long> getAdapterDataSet(Long planId){
        Session session = currentSession();
        Query query = session.createQuery("select dataSetId from AdapterDataSet where adapterPlanId = :planId group by dataSetId");
        query.setParameter("planId", planId);
        return query.list();
    }

    /**
     * 根据方案ID获取数据元映射关系ID
     *
     * @param adapterPlanId
     */
    public List<Long> getAdapterMetaDataIds(Long adapterPlanId) {
        Session session = currentSession();
        Query query = session.createQuery("select id from AdapterDataSet where adapterPlanId = :adapterPlanId");
        query.setParameter("adapterPlanId", adapterPlanId);
        return query.list();
    }

    /**
     * 获取已定制标准数据元
     *
     * @param planId
     */
    public List<AdapterDataSet> getAdapterMetaData(Long planId){
        Session session = currentSession();
        Query query = session.createQuery("from AdapterDataSet where adapterPlanId = :planId");
        query.setParameter("planId", planId);
        return query.list();
    }

}