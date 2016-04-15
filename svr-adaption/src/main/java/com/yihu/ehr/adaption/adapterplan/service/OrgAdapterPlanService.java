package com.yihu.ehr.adaption.adapterplan.service;


import com.yihu.ehr.adaption.adapterorg.service.AdapterOrgService;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetService;
import com.yihu.ehr.adaption.dict.service.AdapterDictService;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Service
public class OrgAdapterPlanService extends BaseJpaService<OrgAdapterPlan, XOrgAdapterPlanRepository> {

    @Autowired
    AdapterDataSetService adapterDataSetService;
    @Autowired
    AdapterOrgService adapterOrgService;
    @Autowired
    AdapterDictService adapterDictService;

    /**
     * 新增方案信息
     * 2015-12-31  新增速度优化以及添加事务控制
     *
     * @param orgAdapterPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrgAdapterPlan addOrgAdapterPlan(OrgAdapterPlan orgAdapterPlan, String isCover) {
        Session s = currentSession();
        String org = orgAdapterPlan.getOrg();
        if (orgAdapterPlan.getVersion() == null) {
            throw new IllegalArgumentException("版本号不能为空");
        }
        if (orgAdapterPlan.getCode() == null) {
            throw new IllegalArgumentException("代码不能为空");
        }
        if (orgAdapterPlan.getType() == null) {
            throw new IllegalArgumentException("类型不能为空");
        }
        if (org == null) {
            throw new IllegalArgumentException("映射机构不能为空");
        }

        save(orgAdapterPlan);
        //拷贝父级方案映射
        Long parentId = orgAdapterPlan.getParentId();
        if (parentId != null) {
            OrgAdapterPlan parentAdapterPlan = retrieve(parentId);
            if (parentAdapterPlan != null) {
                String parentOrg = parentAdapterPlan.getOrg();
                //是否拷贝采集标准
                if (!org.equals(parentOrg) && "true".equals(isCover)) {
                    if (adapterOrgService.isExistData(org)) {
                        adapterOrgService.deleteData(org);//清除数据
                    }
                    adapterOrgService.copy(org, parentOrg);
                }
                //拷贝映射方案
                copyPlan(orgAdapterPlan.getId(), parentId, isCover);
            }
        }
        ;
        s.flush();
        return orgAdapterPlan;
    }

    /**
     * 根据类型获取方案信息
     *
     * @param type
     */
    public List<OrgAdapterPlan> findList(String type, String version) {
        Session session = currentSession();
        String hql = "from OrgAdapterPlan where version =:version";
        Query query = null;
        if ("2".equals(type)) {
            //医院，父级方案没有限制
            query = session.createQuery(hql);
        } else if ("3".equals(type)) {
            //区域,父级方案只能选择厂商或区域选择
            query = session.createQuery(hql + " and (type=:factory or type=:area)");
            query.setParameter("factory", "1");
            query.setParameter("area", "3");
        } else {
            //厂商 只能继承厂商
            query = session.createQuery(hql + " and type=:factory");
            query.setParameter("factory", "1");
        }
        query.setString("version", version);
        return query.list();
    }


    /**
     * 标准定制
     * 2015-12-31  定制速度优化以及添加事务控制
     *
     * @param planId
     * @param adapterCustomizes
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void adapterDataSet(Long planId, List<AdapterCustomize> adapterCustomizes) {
        //删除取消的 数据元、字典
        int rs = unselectAdapterDataSet(planId, adapterCustomizes);

        Session session = currentSession();
        OrgAdapterPlan orgAdapterPlan = retrieve(planId);
        List<AdapterDataSet> adapterMetaDataList = adapterDataSetService.getAdapterMetaData(planId);
        List<Long> lst = new ArrayList<>();
        Long metaDataId;
        boolean adapterFlag = true;
        //先增加
        for (AdapterCustomize adapter : adapterCustomizes) {
            if (adapter.getPid().equals("0") || adapter.getPid().equals("-1")) {
                //没有数据元的数据集
                continue;
            }
            lst.add(Long.valueOf(adapter.getId()));//不删除的数据元ID
            for (AdapterDataSet adapterDataSet : adapterMetaDataList) {
                //数据元，若存在不定制，退出循环
                adapterFlag = !adapter.getId().equals(adapterDataSet.getMetaDataId().toString());
                if (!adapterFlag) {
                    break;
                }
            }

            if (adapterFlag) {
                AdapterDataSet dataSet = new AdapterDataSet();
                dataSet.setAdapterPlanId(planId);
                dataSet.setDataSetId(Long.valueOf(adapter.getPid()));
                metaDataId = Long.valueOf(adapter.getId());
                if (metaDataId != null) {
                    dataSet.setMetaDataId(metaDataId);
                }
                adapterDataSetService.addAdapterDataSet(dataSet, orgAdapterPlan);
            }
            adapterFlag = true;
        }
    }

    /**
     * 删除取消的 数据元、字典
     *
     * @param planId
     * @param adapterCustomizes
     * @return
     */
    private int unselectAdapterDataSet(Long planId, List<AdapterCustomize> adapterCustomizes) {
        List<Long> addIds = new ArrayList<>();
        for (AdapterCustomize adapter : adapterCustomizes) {
            if (adapter.getPid().equals("0") || adapter.getPid().equals("-1")) {
                //没有数据元的数据集
                continue;
            }
            addIds.add(Long.valueOf(adapter.getId()));//不删除的数据元ID
        }
        Session session = currentSession().getSessionFactory().openSession();
        int row = 0;
        try {
            String searchSql = "SELECT DISTINCT std_dict FROM adapter_dataset where plan_id=:planId and (std_dict is not null or std_dict<>'') ";
            if (addIds.size() > 0) {
                searchSql +=
                        "and std_metadata not in (:ids)" +
                                "and " +
                                "std_dict not in(" +
                                "	SELECT d.std_dict from adapter_dataset d where d.plan_id=:planId and (d.std_dict is not null or d.std_dict<>'') and d.std_metadata in(:ids)" +
                                ")";
            }
            Query sq = session.createSQLQuery(searchSql);
            sq.setLong("planId", planId);
            if (addIds.size() > 0) {
                sq.setParameterList("ids", addIds);
            }
            List<BigInteger> ls = sq.list();
            List parms = new ArrayList();
            for (BigInteger b : ls) {
                parms.add(b.longValue());
            }
            if (ls.size() > 0) {
                Query delQuery = session.createSQLQuery("delete from adapter_dict where plan_id = :planId and std_dict in (:ids)");
                delQuery.setLong("planId", planId);
                delQuery.setParameterList("ids", parms);
                row = delQuery.executeUpdate();
            }

            String hql = "delete from adapter_dataset where plan_id = :planId ";
            if (addIds.size() > 0) {
                hql += " and std_metadata not in (:ids)";
            }
            Query query = session.createSQLQuery(hql);
            query.setLong("planId", planId);
            if (addIds.size() > 0) {
                query.setParameterList("ids", addIds);
            }
            row = query.executeUpdate();
            session.close();
            return row;
        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    /**
     * 删除方案信息
     *
     * @param orgAdapterPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteOrgAdapterPlan(OrgAdapterPlan orgAdapterPlan) {
        try {
            //要先删除数据集映射
            List<Long> adapterMetaDatas = adapterDataSetService.getAdapterMetaDataIds(orgAdapterPlan.getId());
            adapterDataSetService.deleteAdapterDataSet(adapterMetaDatas.toArray(new Long[adapterMetaDatas.size()]));
            delete(orgAdapterPlan);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 批量删除方案信息
     *
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgAdapterPlan(Object[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }

        Session session = currentSession();
        Query query = session.createQuery("delete from AdapterDataSet where adapterPlanId in(:planIds)");
        query.setParameterList("planIds", ids);
        int rows = query.executeUpdate();

        query = session.createQuery("delete from AdapterDict where adapterPlanId in(:planIds)");
        query.setParameterList("planIds", ids);
        rows = query.executeUpdate();

        query = session.createQuery("delete from OrgAdapterPlan where id in (:ids)");
        query.setParameterList("ids", ids);
        rows = query.executeUpdate();
        return rows;
    }

    /**
     * 判断适配代码是否重复
     *
     * @param code
     * @return
     */
    public boolean isAdapterCodeExist(String code) {
        Session session = currentSession();

        Query query = session.createQuery("select 1 from OrgAdapterPlan where code = :code");
        query.setString("code", code);

        return query.list().size() > 0;
    }


    /**
     * 方案拷贝
     *
     * @param planId
     * @param parentPlanId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean copyPlan(Long planId, Long parentPlanId, String isCover) {

        Session session = currentSession();
        //数据集映射拷贝
        String sql = "insert into adapter_dataset(" +
                "plan_id,std_dataset,std_metadata,data_type,org_dataset,org_metadata,description,std_dict) " +
                "  select :planId as plan_id,t.std_dataset,t.std_metadata,";
        if ("true".equals(isCover))
            sql += "t.data_type,t.org_dataset,t.org_metadata,t.description,";
        else
            sql += "NULL as data_type, NULL as org_dataset, NULL as org_metadata, NULL as description,";
        sql += "t.std_dict from adapter_dataset t where t.plan_id=:parentPlanId ";

        Query sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter("planId", planId);
        sqlQuery.setParameter("parentPlanId", parentPlanId);
        int rows = sqlQuery.executeUpdate();

        //字典映射拷贝
        sql = "insert into adapter_dict(" +
                "plan_id,std_dict,std_dictentry,org_dict,org_dictentry,description) " +
                "  select :planId as plan_id,t.std_dict,t.std_dictentry,";
        if ("true".equals(isCover))
            sql += "t.org_dict,t.org_dictentry,t.description ";
        else
            sql += "NULL as org_dict, NULL as org_dictentry, NULL as description";
        sql += " from adapter_dict t where t.plan_id=:parentPlanId ";

        sqlQuery = session.createSQLQuery(sql);
        sqlQuery.setParameter("planId", planId);
        sqlQuery.setParameter("parentPlanId", parentPlanId);
        rows = sqlQuery.executeUpdate();
        return true;
    }

    public List<OrgAdapterPlan> getOrgAdapterPlanByOrgCode(Map<String, Object> args) {

        List<OrgAdapterPlan> orgAdapterPlans = new ArrayList<OrgAdapterPlan>();
        try {
            Session session = currentSession();
            String orgCode = (String) args.get("orgcode");

            Query query = session.createQuery("from OrgAdapterPlan where org = :org and status=1 order by version desc");
            query.setString("org", orgCode);
            orgAdapterPlans = query.list();
        } catch (Exception ex) {
        }

        return orgAdapterPlans;
    }


    /**
     * 标准定制，
     * 2016-4-14  定制速度优化
     *
     * @param planId
     * @param adapterCustomizes
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void adapterDataSet(Long planId, AdapterCustomize[] adapterCustomizes) {
        List<String> metaIds = new ArrayList<>();
        for(AdapterCustomize adapter : adapterCustomizes){
            if (adapter.getPid().equals("0") || adapter.getPid().equals("-1")) {
                //没有数据元的数据集
                continue;
            }
            metaIds.add(adapter.getId());
        }
        //删除取消的 数据元、字典
        int rs = cancelSelectAdapterDataSet(planId, metaIds);

        if(metaIds.size()==0)
            return;

        OrgAdapterPlan orgAdapterPlan = retrieve(planId);
        //获取定制字典
        List<Integer> ls = findAddAdapter(orgAdapterPlan, metaIds);
        if(ls.size()>0)
            adapterDictService.batchAddAdapterDict(orgAdapterPlan, ls);//新增定制字典项
        adapterDataSetService.copyAdapterDataSet(orgAdapterPlan, metaIds);//新增定制数据元
    }

    /**
     * 删除取消的 数据元、字典
     * create by lincl 2016-04-14
     * @param planId 方案编号
     * @param adapterMetaIds 适配数据元
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private int cancelSelectAdapterDataSet(Long planId, List<String> adapterMetaIds) {
        Session session = currentSession().getSessionFactory().openSession();
        int row = 0;
        try {
            String searchSql = "SELECT DISTINCT std_dict FROM adapter_dataset where plan_id=:planId and (std_dict is not null or std_dict<>'') ";
            if (adapterMetaIds.size() > 0) {
                searchSql +=
                        "and std_metadata not in (:ids)" +
                                "and " +
                                "std_dict not in(" +
                                "	SELECT d.std_dict from adapter_dataset d where d.plan_id=:planId and (d.std_dict is not null or d.std_dict<>'') and d.std_metadata in(:ids)" +
                                ")";
            }
            Query sq = session.createSQLQuery(searchSql);
            sq.setLong("planId", planId);
            if (adapterMetaIds.size() > 0) {
                sq.setParameterList("ids", adapterMetaIds);
            }
            List<BigInteger> ls = sq.list();
            List parms = new ArrayList();
            for (BigInteger b : ls) {
                parms.add(b.longValue());
            }
            if (ls.size() > 0) {
                Query delQuery = session.createSQLQuery("delete from adapter_dict where plan_id = :planId and std_dict in (:ids)");
                delQuery.setLong("planId", planId);
                delQuery.setParameterList("ids", parms);
                row = delQuery.executeUpdate();
            }

            String hql = "delete from adapter_dataset where plan_id = :planId ";
            if (adapterMetaIds.size() > 0) {
                hql += " and std_metadata not in (:ids)";
            }
            Query query = session.createSQLQuery(hql);
            query.setLong("planId", planId);
            if (adapterMetaIds.size() > 0) {
                query.setParameterList("ids", adapterMetaIds);
            }
            row = query.executeUpdate();
            session.close();
            return row;
        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    /**
     * 获取适配的字典
     * create by lincl 2016-04-14
     * @param orgAdapterPlan 方案
     * @param metaIds 适配数据元
     * @return
     */
    private List<Integer> findAddAdapter(OrgAdapterPlan orgAdapterPlan, List metaIds){
        String metaTable = CDAVersionUtil.getMetaDataTableName(orgAdapterPlan.getVersion());
        String sql =
                "SELECT  " +
                "   meta.dict_id as stdDict " +
                "FROM" +
                "   "+metaTable+" meta " +
                "LEFT JOIN" +
                "   (SELECT * FROM adapter_dataset WHERE plan_id=:planId) adt " +
                "ON" +
                "   meta.id = adt.std_metadata " +
                "WHERE" +
                "   adt.id IS NULL AND meta.dict_id<>0 AND meta.id in(:metaIds) AND NOT EXISTS(SELECT 1 FROM adapter_dict WHERE std_dict=meta.dict_id and plan_id=:planId) " +
                "GROUP BY meta.dict_id";
        Session session = currentSession();
        Query query = session.createSQLQuery(sql);
        query.setParameter("planId", orgAdapterPlan.getId());
        query.setParameterList("metaIds", metaIds);
        return query.list();
    }
}