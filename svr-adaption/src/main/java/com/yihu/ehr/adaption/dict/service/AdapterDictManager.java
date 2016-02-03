package com.yihu.ehr.adaption.dict.service;


import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.util.service.BaseManager;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@Service
public class AdapterDictManager extends BaseManager<AdapterDict, XAdapterDictRepository> {

    @Autowired
    OrgAdapterPlanManager orgAdapterPlanManager;


    /**
     * 根据方案和字典获取字典项适配
     *
     * @param planId
     * @param strKey
     */
    @Transactional(propagation = Propagation.NEVER)
    public List<AdapterDictModel> searchAdapterDict(long planId, String strKey, int page, int rows) {

        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
//        CDAVersion version = orgAdapterPlan.getVersion();
//        String dictTableName = version.getDictTableName();
        String dictTableName = "";

        Session session = currentSession();
        StringBuilder sb = new StringBuilder();

        sb.append(" select distinct  " + dictTableName + ".id    ");
        sb.append("       ," + dictTableName + ".code  ");
        sb.append("       ," + dictTableName + ".name  ");
        sb.append("   from adapter_dict ad          ");
        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
        sb.append("  where ad.plan_id = " + planId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append(" and " + dictTableName + ".code like '%" + strKey + "%' or " + dictTableName + ".name like '%" + strKey + "%'");
        }
        sb.append(" order by " + dictTableName + ".code");

        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);

        sqlQuery.setMaxResults(rows);
        sqlQuery.setFirstResult((page - 1) * rows);

        List<Object> records = sqlQuery.list();

        if (records.size() == 0) {
            return null;
        } else {
            List<AdapterDictModel> adapterDictModels = new ArrayList<>();
            for (int i = 0; i < records.size(); ++i) {
                Object[] record = (Object[]) records.get(i);
                AdapterDictModel adapterDictModel = new AdapterDictModel();
                adapterDictModel.setId(Long.parseLong(record[0].toString()));
                adapterDictModel.setDictCode((String) record[1]);
                adapterDictModel.setDictName((String) record[2]);
                adapterDictModels.add(adapterDictModel);
            }
            return adapterDictModels;
        }
    }

    /**
     * 查询适配字典的总条数
     *
     * @return
     */
    public int searchDictInt(long planId, String strKey) {
        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
//        CDAVersion version = orgAdapterPlan.getVersion();
//        String dictTableName = version.getDictTableName();
        String dictTableName = "";
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct  " + dictTableName + ".id    ");
        sb.append("       ," + dictTableName + ".code  ");
        sb.append("       ," + dictTableName + ".name  ");
        sb.append("   from adapter_dict ad          ");
        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
        sb.append("  where ad.plan_id = " + planId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append(" and " + dictTableName + ".code like '%" + strKey + "%' or " + dictTableName + ".name like '%" + strKey + "%'");
        }
        sb.append(" order by " + dictTableName + ".code");
        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        int i=  sqlQuery.list().size();
        return i;
    }


    /**
     * 根据条件搜索字典项适配关系
     *
     * @param planId
     * @param dictId
     */
    public List<AdapterDictModel> searchAdapterDictEntry(long planId, long dictId, String strKey, int page, int rows) {
        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
        String orgCode = orgAdapterPlan.getOrg();
//        CDAVersion version = orgAdapterPlan.getVersion();
//        String dictTableName = version.getDictTableName();
//        String deTableName = version.getDictEntryTableName();
        String dictTableName = "";
        String deTableName = "";

        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        sb.append(" select ad.id                       ");
        sb.append("       ,ad.plan_id                  ");
        sb.append("       ," + dictTableName + ".id   as dictId ");
        sb.append("       ," + dictTableName + ".code as dictCode ");
        sb.append("       ," + dictTableName + ".name as dictName ");
        sb.append("       ," + deTableName + ".id  as dictEntryId    ");
        sb.append("       ," + deTableName + ".code as DictEntrycode   ");
        sb.append("       ," + deTableName + ".value as DictEntryName   ");
        sb.append("       ,  orgDict.id as orgDictId  ");
        sb.append("       ,  orgDict.code as orgDictCode");
        sb.append("       ,  orgDict.name as orgDictNanme");
        sb.append("       ,  orgDE.id as orgDictEntryId  ");
        sb.append("       ,  orgDE.code as orgDictEntryCode");
        sb.append("       ,  orgDE.name as orgDictEntryName");
        sb.append("   from adapter_dict ad ");
        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
        sb.append("        left join " + deTableName + " on ad.std_dictentry = " + deTableName + ".id ");
        sb.append("        left join org_std_dict orgDict on ( orgDict.sequence = ad.org_dict and orgDict.organization='" + orgCode + "' )   ");
        sb.append("        left join org_std_dictentry orgDE on ( orgDE.sequence = ad.org_dictentry and orgDE.organization='" + orgCode + "' ) ");
        sb.append("  where ad.plan_id = " + planId);
        sb.append("    and ad.std_dict = " + dictId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append(" and " + deTableName + ".code like '%" + strKey + "%' or " + deTableName + ".value like '%" + strKey + "%'");
        }
        sb.append(" order by " + deTableName + ".code");



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
            List<AdapterDictModel> adapterDictModels = new ArrayList<>();
            for (int i = 0; i < records.size(); ++i) {
                Object[] record = (Object[]) records.get(i);
                AdapterDictModel adapterDictModel = new AdapterDictModel();

                adapterDictModel.setId(Long.parseLong(record[0].toString()));
                adapterDictModel.setAdapterPlanId(Long.parseLong(record[1].toString()));

                adapterDictModel.setDictId(Long.parseLong(record[2].toString()));
                adapterDictModel.setDictCode((String) record[3]);
                adapterDictModel.setDictName((String) record[4]);

                adapterDictModel.setDictEntryId(Long.parseLong(record[5].toString()));
                adapterDictModel.setDictEntryCode((String) record[6]);
                adapterDictModel.setDictEntryName((String) record[7]);

                if (record[8] != null) {
                    adapterDictModel.setOrgDictSeq(Long.parseLong(record[8].toString()));
                }
                if (record[9] != null) {
                    adapterDictModel.setOrgDictCode((String) record[9]);
                }
                if (record[10] != null) {
                    adapterDictModel.setOrgDictName((String) record[10]);
                }
                if (record[11] != null) {
                    adapterDictModel.setOrgDictEntrySeq(Long.parseLong(record[11].toString()));
                }
                if (record[12] != null) {
                    adapterDictModel.setOrgDictEntryCode((String) record[12]);
                }
                if (record[13] != null) {
                    adapterDictModel.setOrgDictEntryName((String) record[13]);
                }

                adapterDictModels.add(adapterDictModel);
            }

            return adapterDictModels;
        }
    }

    /**
     * 查询适配字典细项的总条数
     *
     * @return
     */
    public int searchDictEntryInt(long planId, long dictId, String strKey) {
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(planId);
//        CDAVersion version = orgAdapterPlan.getVersion();
//        String deTableName = version.getDictEntryTableName();
        String deTableName ="";
        sb.append(" select count(*)    ");
        sb.append("   from adapter_dict ad     ");
        sb.append("        left join " + deTableName + " on ad.std_dictentry = " + deTableName + ".id ");
        sb.append("  where ad.plan_id = " + planId);
        sb.append("    and ad.std_dict = " + dictId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append(" and " + deTableName + ".code like '%" + strKey + "%' or " + deTableName + ".value like '%" + strKey + "%'");
        }
        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        BigInteger i=  (BigInteger)sqlQuery.list().get(0);
        return i.intValue();
    }

    /**
     * 新增适配明细，返回操作结果
     *
     * @param adapterDict
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public void addAdapterDict(AdapterDict adapterDict) {
        Session s = currentSession();
        String sql =
                " insert into adapter_dict " +
                        " (plan_id, std_dict, std_dictentry, org_dict, org_dictentry, description) " +
                        " values " +
                        " (:plan_id, :std_dict, :std_dictentry, :org_dict, :org_dictentry, :description) ";
        Query q = s.createSQLQuery(sql);
        q.setParameter("plan_id", adapterDict.getAdapterPlanId());
        q.setParameter("std_dict", adapterDict.getDictId());
        q.setParameter("std_dictentry", adapterDict.getDictEntryId());
        q.setParameter("org_dict", adapterDict.getOrgDictSeq());
        q.setParameter("org_dictentry", adapterDict.getOrgDictItemSeq());
        q.setParameter("description", adapterDict.getDescription());
        int rs = q.executeUpdate();
    }

    /**
     * 批量删除字典项适配关系,还剩余多少
     *
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteAdapterDictRemain(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }else{
            AdapterDict adapterDict = findOne(ids[0]);
            List<Long> lst = new ArrayList<>();
            lst = Arrays.asList(ids);
            Session session = currentSession();
            Query query = session.createQuery("select count(*) from AdapterDict where adapterPlanId=:planId and dictId=:stdDict and id not in (:ids)");
            query.setParameter("planId",adapterDict.getAdapterPlanId());
            query.setParameter("stdDict",adapterDict.getDictId());
            query.setParameterList("ids", lst);
            return Integer.parseInt(query.list().get(0).toString());
        }
    }

    /**
     * 批量删除字典项适配关系
     *
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteAdapterDict(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        Session session = currentSession();
        Query query = session.createQuery("delete from AdapterDict where id in (:ids)");
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }

    /**
     * 批量删除字典项适配关系
     *
     * @param planId
     * @param stdDictId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteAdapterDict(Long planId, Long stdDictId) {
        if(stdDictId==null)return 0;
        Session session = currentSession();
        Query query = session.createQuery("delete from AdapterDict where adapterPlanId=:planId and dictId = :stdDictId");
        query.setParameter("planId", planId);
        query.setParameter("stdDictId", stdDictId);
        return query.executeUpdate();
    }

    /**
     * 判断需适配的字典是否已经存在
     *
     * @param planId
     * @param dictId
     */
    public boolean isExist(Long planId, Long dictId) {
        Session session = currentSession();
        Query query = session.createQuery("select count(*) from AdapterDict where adapterPlanId=:planId and dictId=:dictId ");
        query.setParameter("planId", planId);
        query.setParameter("dictId", dictId);

        return (Long) query.list().get(0) > 0;
    }

    /**
     * 根据字典新增适配明细，返回操作结果
     * @param planId
     * @param dictId
     * @param vesion
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public int addAdapterDict(Long planId, Object dictId, String vesion) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getDictEntryTableName(vesion);
        String sql = "insert into adapter_dict(plan_id, std_dict, std_dictentry) "+
                "select  :planId as plan_id, tb.dict_id, tb.id from " + strTableName + " tb where  tb.dict_id =:dictId";
        Query query = session.createSQLQuery(sql);
        query.setParameter("dictId", dictId);
        query.setLong("planId", planId);
        return query.executeUpdate();
    }
}