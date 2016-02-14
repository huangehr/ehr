package com.yihu.ehr.adaption.dict.service;


import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.model.adaption.MAdapterDict;
import com.yihu.ehr.util.parm.PageModel;
import com.yihu.ehr.util.service.BaseManager;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
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
     */
    @Transactional(propagation = Propagation.NEVER)
    public List<AdapterDictModel> searchAdapterDict(OrgAdapterPlan orgAdapterPlan, PageModel pageModel) {
        long planId = orgAdapterPlan.getId();
        String dictTableName = CDAVersionUtil.getDictTableName(orgAdapterPlan.getVersion());

        Session session = currentSession();
        StringBuilder sb = new StringBuilder();

        sb.append(" select distinct  " + dictTableName + ".id    ");
        sb.append("       ," + dictTableName + ".code  ");
        sb.append("       ," + dictTableName + ".name  ");
        sb.append("   from adapter_dict ad          ");
        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
        sb.append("  where ad.plan_id = " + planId);
        sb.append(pageModel.formatSqlWithOrder(dictTableName));

        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        setQueryVal(sqlQuery, pageModel);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(MAdapterDict.class));
        return sqlQuery.list();
    }

    /**
     * 查询适配字典的总条数
     *
     * @return
     */
    public int searchDictInt(OrgAdapterPlan orgAdapterPlan, PageModel pageModel) {
        long planId = orgAdapterPlan.getId();
        String dictTableName = CDAVersionUtil.getDictTableName(orgAdapterPlan.getVersion());

        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*) from (");
        sb.append(" select distinct  " + dictTableName + ".id    ");
        sb.append("       ," + dictTableName + ".code  ");
        sb.append("       ," + dictTableName + ".name  ");
        sb.append("   from adapter_dict ad          ");
        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
        sb.append("  where ad.plan_id = " + planId);
        sb.append(pageModel.formatSql(dictTableName));
        sb.append(" ) t");

        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        setQueryVal(sqlQuery, pageModel);
        return ((BigInteger) sqlQuery.list().get(0)).intValue();
    }


    /**
     * 根据条件搜索字典项适配关系
     *
     * @param orgAdapterPlan
     * @param dictId
     * @param pageModel
     */
    public List<AdapterDictModel> searchAdapterDictEntry(OrgAdapterPlan orgAdapterPlan, long dictId, PageModel pageModel) {
        String orgCode = orgAdapterPlan.getOrg();
        String dictTableName = CDAVersionUtil.getDictTableName(orgAdapterPlan.getVersion());
        String deTableName = CDAVersionUtil.getDictEntryTableName(orgAdapterPlan.getVersion());

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
        sb.append("  where ad.plan_id = " + orgAdapterPlan.getId());
        sb.append("    and ad.std_dict = " + dictId);
        sb.append(pageModel.formatSqlWithOrder(dictTableName));

        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        setQueryVal(sqlQuery, pageModel);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(MAdapterDict.class));
        return sqlQuery.list();
    }

    /**
     * 查询适配字典细项的总条数
     *
     * @return
     */
    public int searchDictEntryInt(OrgAdapterPlan orgAdapterPlan, long dictId, PageModel pageModel) {
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        String deTableName = CDAVersionUtil.getDictEntryTableName(orgAdapterPlan.getVersion());

        sb.append(" select count(*)    ");
        sb.append("   from adapter_dict ad     ");
        sb.append("        left join " + deTableName + " on ad.std_dictentry = " + deTableName + ".id ");
        sb.append("  where ad.plan_id = " + orgAdapterPlan.getId());
        sb.append("    and ad.std_dict = " + dictId);
        sb.append(pageModel.formatSql(deTableName));

        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        return ((BigInteger)sqlQuery.list().get(0)).intValue();
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