//package com.yihu.ehr.adaption.service;
//
//
//import com.yihu.ha.constrant.Services;
//import com.yihu.ha.data.sql.SQLGeneralDAO;
//import com.yihu.ha.std.model.*;
//import org.hibernate.Query;
//import org.hibernate.SQLQuery;
//import org.hibernate.Session;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import javax.transaction.Transactional;
//import java.math.BigInteger;
//import java.util.*;
//
///**
// * @author AndyCai
// * @version 1.0
// * @created 26-十月-2015 17:12:12
// */
//@Service(Services.AdapterDictManager)
//public class AdapterDictManager extends SQLGeneralDAO implements XAdapterDictManager {
//
//    @Resource(name = Services.OrgAdapterPlanManager)
//    XOrgAdapterPlanManager orgAdapterPlanManager;
//
//    @Resource(name = Services.DictManager)
//    private XDictManager dictManager;
//
//    public AdapterDictManager() {
//
//    }
//
//    public void finalize() throws Throwable {
//
//    }
//
//    /**
//     * 判断需适配的字典是否已经存在
//     *
//     * @param planId
//     * @param dictId
//     */
//    public boolean isExist(Long planId, Long dictId) {
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("select count(*) from AdapterDict where adapterPlanId=:planId and dictId=:dictId ");
//        query.setParameter("planId", planId);
//        query.setParameter("dictId", dictId);
//
//        return (Long) query.list().get(0) > 0;
//    }
//
//    /**
//     * 根据字典新增适配明细，返回操作结果
//     *
//     * @param planId
//     * @param dict
//     */
//    public boolean addAdapterDict(Long planId, XDict dict) {
//        try {
//            XDictEntry[] dictEntries = dictManager.getDictEntries(dict);
//            for (XDictEntry dictEntry : dictEntries) {
//                AdapterDict adapterDict = new AdapterDict();
//                adapterDict.setAdapterPlanId(planId);
//                adapterDict.setDictId(dictEntry.getDictId());
//                adapterDict.setDictEntryId(dictEntry.getId());
//                addAdapterDict(adapterDict);
//            }
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * 新增适配明细，返回操作结果
//     *
//     * @param adapterDict
//     */
//    public boolean addAdapterDict(XAdapterDict adapterDict) {
//        try {
//            saveEntity(adapterDict);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    /**
//     * 根据方案ID获取所有字典适
//     *
//     * @param adapterPlanId
//     */
//    public List<XAdapterDict> getAdapterDictByPlan(Long adapterPlanId) {
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery(" from AdapterDict where adapterPlanId = :adapterPlanId");
//        query.setParameter("adapterPlanId", adapterPlanId);
//        return query.list();
//    }
//
//    /**
//     * 根据字典ID获取所有字典适配的ID
//     *
//     * @param dictId
//     */
//    public List<Long> getAdapterDictIds(Long dictId) {
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("select id from AdapterDict where dictId = :dictId");
//        query.setParameter("dictId", dictId);
//        return query.list();
//    }
//
//    /**
//     * 批量删除字典项适配关系
//     *
//     * @param ids
//     */
//    public int deleteAdapterDict(Long[] ids) {
//        if (ids == null || ids.length == 0) {
//            return 0;
//        }
//        List<Long> lst = new ArrayList<>();
//        lst = Arrays.asList(ids);
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Query query = session.createQuery("delete from AdapterDict where id in (:ids)");
//        query.setParameterList("ids", lst);
//        return query.executeUpdate();
//    }
//
//    /**
//     * 根据适配明细ID获取明细信息
//     *
//     * @param id
//     */
//    public XAdapterDict getAdapterDict(long id) {
//        return (XAdapterDict) getEntity(AdapterDict.class, id);
//    }
//
//    /**
//     * 根据方案和字典获取字典项适配
//     *
//     * @param planId
//     * @param strKey
//     */
//    @Transactional(Transactional.TxType.NEVER)
//    public List<AdapterDictModel> searchAdapterDict(long planId, String strKey, int page, int rows) {
//
//        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
//        XCDAVersion version = orgAdapterPlan.getVersion();
//        String dictTableName = version.getDictTableName();
//
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(" select distinct  " + dictTableName + ".id    ");
//        sb.append("       ," + dictTableName + ".code  ");
//        sb.append("       ," + dictTableName + ".name  ");
//        sb.append("   from adapter_dict ad          ");
//        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
//        sb.append("  where ad.plan_id = " + planId);
//        if (!(strKey == null || strKey.equals(""))) {
//            sb.append(" and " + dictTableName + ".code like '%" + strKey + "%' or " + dictTableName + ".name like '%" + strKey + "%'");
//        }
//        sb.append(" order by " + dictTableName + ".code");
//
//        String sql = sb.toString();
//        SQLQuery sqlQuery = session.createSQLQuery(sql);
//
//        sqlQuery.setMaxResults(rows);
//        sqlQuery.setFirstResult((page - 1) * rows);
//
//        List<Object> records = sqlQuery.list();
//
//        if (records.size() == 0) {
//            return null;
//        } else {
//            List<AdapterDictModel> adapterDictModels = new ArrayList<>();
//            for (int i = 0; i < records.size(); ++i) {
//                Object[] record = (Object[]) records.get(i);
//                AdapterDictModel adapterDictModel = new AdapterDictModel();
//                adapterDictModel.setId(Long.parseLong(record[0].toString()));
//                adapterDictModel.setDictCode((String) record[1]);
//                adapterDictModel.setDictName((String) record[2]);
//                adapterDictModels.add(adapterDictModel);
//            }
//            return adapterDictModels;
//        }
//    }
//
//    /**
//     * 根据条件搜索字典项适配关系
//     *
//     * @param planId
//     * @param dictId
//     */
//    public List<AdapterDictModel> searchAdapterDictEntry(long planId, long dictId, String strKey, int page, int rows) {
//        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
//        String orgCode = orgAdapterPlan.getOrg();
//        XCDAVersion version = orgAdapterPlan.getVersion();
//        String dictTableName = version.getDictTableName();
//        String deTableName = version.getDictEntryTableName();
//
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(" select ad.id                       ");
//        sb.append("       ,ad.plan_id                  ");
//        sb.append("       ," + dictTableName + ".id   as dictId ");
//        sb.append("       ," + dictTableName + ".code as dictCode ");
//        sb.append("       ," + dictTableName + ".name as dictName ");
//        sb.append("       ," + deTableName + ".id  as dictEntryId    ");
//        sb.append("       ," + deTableName + ".code as DictEntrycode   ");
//        sb.append("       ," + deTableName + ".value as DictEntryName   ");
//        sb.append("       ,  orgDict.id as orgDictId  ");
//        sb.append("       ,  orgDict.code as orgDictCode");
//        sb.append("       ,  orgDict.name as orgDictNanme");
//        sb.append("       ,  orgDE.id as orgDictEntryId  ");
//        sb.append("       ,  orgDE.code as orgDictEntryCode");
//        sb.append("       ,  orgDE.name as orgDictEntryName");
//        sb.append("   from adapter_dict ad ");
//        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
//        sb.append("        left join " + deTableName + " on ad.std_dictentry = " + deTableName + ".id ");
//        sb.append("        left join org_std_dict orgDict on ( orgDict.sequence = ad.org_dict and orgDict.organization='" + orgCode + "' )   ");
//        sb.append("        left join org_std_dictentry orgDE on ( orgDE.sequence = ad.org_dictentry and orgDE.organization='" + orgCode + "' ) ");
//        sb.append("  where ad.plan_id = " + planId);
//        sb.append("    and ad.std_dict = " + dictId);
//        if (!(strKey == null || strKey.equals(""))) {
//            sb.append(" and " + deTableName + ".code like '%" + strKey + "%' or " + deTableName + ".value like '%" + strKey + "%'");
//        }
//        sb.append(" order by " + deTableName + ".code");
//
//        String sql = sb.toString();
//        SQLQuery sqlQuery = session.createSQLQuery(sql);
//
//        if(rows>0){
//            sqlQuery.setMaxResults(rows);
//            sqlQuery.setFirstResult((page - 1) * rows);
//        }
//
//        List<Object> records = sqlQuery.list();
//
//        if (records.size() == 0) {
//            return null;
//
//        } else {
//            List<AdapterDictModel> adapterDictModels = new ArrayList<>();
//            for (int i = 0; i < records.size(); ++i) {
//                Object[] record = (Object[]) records.get(i);
//                AdapterDictModel adapterDictModel = new AdapterDictModel();
//
//                adapterDictModel.setId(Long.parseLong(record[0].toString()));
//                adapterDictModel.setAdapterPlanId(Long.parseLong(record[1].toString()));
//
//                adapterDictModel.setDictId(Long.parseLong(record[2].toString()));
//                adapterDictModel.setDictCode((String) record[3]);
//                adapterDictModel.setDictName((String) record[4]);
//
//                adapterDictModel.setDictEntryId(Long.parseLong(record[5].toString()));
//                adapterDictModel.setDictEntryCode((String) record[6]);
//                adapterDictModel.setDictEntryName((String) record[7]);
//
//                if (record[8] != null) {
//                    adapterDictModel.setOrgDictSeq(Long.parseLong(record[8].toString()));
//                }
//                if (record[9] != null) {
//                    adapterDictModel.setOrgDictCode((String) record[9]);
//                }
//                if (record[10] != null) {
//                    adapterDictModel.setOrgDictName((String) record[10]);
//                }
//                if (record[11] != null) {
//                    adapterDictModel.setOrgDictEntrySeq(Long.parseLong(record[11].toString()));
//                }
//                if (record[12] != null) {
//                    adapterDictModel.setOrgDictEntryCode((String) record[12]);
//                }
//                if (record[13] != null) {
//                    adapterDictModel.setOrgDictEntryName((String) record[13]);
//                }
//
//                adapterDictModels.add(adapterDictModel);
//            }
//
//            return adapterDictModels;
//        }
//    }
//
//
//    /**
//     * 查询适配字典的总条数
//     *
//     * @return
//     */
//    public int searchDictInt(long planId, String strKey) {
//        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
//        XCDAVersion version = orgAdapterPlan.getVersion();
//        String dictTableName = version.getDictTableName();
//
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        StringBuilder sb = new StringBuilder();
//        sb.append(" select count(*)   ");
//        sb.append("   from adapter_dict ad          ");
//        sb.append("        left join " + dictTableName + " on ad.std_dict = " + dictTableName + ".id  ");
//        sb.append("  where ad.plan_id = " + planId);
//        if (!(strKey == null || strKey.equals(""))) {
//            sb.append(" and " + dictTableName + ".code like '%" + strKey + "%' or " + dictTableName + ".name like '%" + strKey + "%'");
//        }
//        String sql = sb.toString();
//        SQLQuery sqlQuery = session.createSQLQuery(sql);
//        BigInteger i=  (BigInteger)sqlQuery.list().get(0);
//        return i.intValue();
//    }
//
//    /**
//     * 查询适配字典细项的总条数
//     *
//     * @return
//     */
//    public int searchDictEntryInt(long planId, long dictId, String strKey) {
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        StringBuilder sb = new StringBuilder();
//        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
//        XCDAVersion version = orgAdapterPlan.getVersion();
//        String deTableName = version.getDictEntryTableName();
//        sb.append(" select count(*)    ");
//        sb.append("   from adapter_dict ad     ");
//        sb.append("        left join " + deTableName + " on ad.std_dictentry = " + deTableName + ".id ");
//        sb.append("  where ad.plan_id = " + planId);
//        sb.append("    and ad.std_dict = " + dictId);
//        if (!(strKey == null || strKey.equals(""))) {
//            sb.append(" and " + deTableName + ".code like '%" + strKey + "%' or " + deTableName + ".value like '%" + strKey + "%'");
//        }
//        String sql = sb.toString();
//        SQLQuery sqlQuery = session.createSQLQuery(sql);
//        BigInteger i=  (BigInteger)sqlQuery.list().get(0);
//        return i.intValue();
//    }
//
//    /**
//     * 修改适配字典明细，返回操作结果
//     *
//     * @param adapterDict
//     */
//    public boolean updateAdapterDict(XAdapterDict adapterDict) {
//        try {
//            updateEntity(adapterDict);
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    /*
//    * 获取字典、字典项映射信息
//    * @param strPlanId 方案ID
//    * @param versionCode 标准版本ID
//    * @result Map
//    * */
//    public Map<String, Object> getDictMappingInfo(String strPlanId, String versionCode,String strOrgCode) {
//        Map<String, Object> mapResult = new HashMap<>();
//
//        try {
//            Session session = currentSession();
//            String dictTableName = CDAVersion.getDictTableName(versionCode);
//
//            StringBuffer sb = new StringBuffer();
//
//            sb.append("SELECT ");
//            sb.append("DISTINCT ");
//            sb.append("a.std_dict, ");
//            sb.append("b.`code` std_dict_code,  ");
//            sb.append("a.org_dict, ");
//            sb.append("c.`code` org_dict_code,  ");
//            sb.append("b.name std_dict_name,  ");
//            sb.append("c.name org_dict_name   ");
//            sb.append("from adapter_dict a  ");
//            sb.append("left JOIN " + dictTableName + " b on a.std_dict = b.id  ");
//            sb.append("left join org_std_dict c on a.org_dict = c.sequence and c.organization='"+strOrgCode+"' ");
//            sb.append("where a.plan_id='" + strPlanId + "'");
////            sb.append("and a.org_dict is not null ");
////            sb.append("UNION ALL ");
////            sb.append("SELECT ");
////            sb.append("DISTINCT ");
////            sb.append("a.std_dict, ");
////            sb.append("b.`code` std_dict_code, ");
////            sb.append("a.org_dict, ");
////            sb.append("c.`code` org_dict_code,  ");
////            sb.append("b.name std_dict_name,  ");
////            sb.append("c.name org_dict_name   ");
////            sb.append("from adapter_dict a  ");
////            sb.append("left JOIN " + dictTableName + " b on a.std_dict = b.id ");
////            sb.append("left join org_std_dict c on a.org_dict = c.id  ");
////            sb.append("where a.plan_id='" + strPlanId + "' and a.org_dict is null  ");
////            sb.append("and a.std_dict not in (  ");
////            sb.append("SELECT  ");
////            sb.append("DISTINCT  ");
////            sb.append("a.std_dict ");
////            sb.append("from adapter_dict a  ");
////            sb.append("left JOIN " + dictTableName + " b on a.std_dict = b.id  ");
////            sb.append("left join org_std_dict c on a.org_dict = c.id  ");
////            sb.append("where a.plan_id='" + strPlanId + "' and a.org_dict is not null  ");
////            sb.append(") ");
//            Query query = session.createSQLQuery(sb.toString());
//
//            List<Object> records = query.list();
//
//            List<DictMappingInfo> listMapping = new ArrayList<>();
//            List<DictEntryMappingInfo> listEntryInfo = new ArrayList<>();
//
//            for (int i = 0; i < records.size(); ++i) {
//
//                DictMappingInfo info = new DictMappingInfo();
//
//                Object[] record = (Object[]) records.get(i);
//
//                info.setId(String.valueOf(i + 1));
//                info.setStdDictId(record[0].toString());
//                info.setStdDictCode(record[1].toString());
//                info.setOrgDictId(record[2] == null ? "" : record[2].toString());
//                info.setOrgDictCode(record[3] == null ? "" : record[3].toString());
//                info.setStdDictName(record[4] == null ? "" : record[4].toString());
//                info.setOrgDictName(record[5] == null ? "" : record[5].toString());
//                info.setPlanId(strPlanId);
//                listMapping.add(info);
//
//                //获取数据元映射关系
//                Map<String, Object> mapKey = new HashMap<>();
//                mapKey.put("strStdDictId", info.getStdDictId());
//                mapKey.put("StrOrgDictId", info.getOrgDictId());
//                mapKey.put("versionCode", versionCode);
//                mapKey.put("strPlanId", info.getPlanId());
//                mapKey.put("strMappingId", info.getId());
//                mapKey.put("length",listEntryInfo.size());
//                mapKey.put("orgCode",strOrgCode);
//                List<DictEntryMappingInfo> listMetadata = getDictEntrymapping(mapKey);
//
//                listEntryInfo.addAll(listMetadata);
//            }
//
//            mapResult.put("dictlist", listMapping);
//            mapResult.put("dictentrylist", listEntryInfo);
//
//        } catch (Exception ex) {
//            mapResult=null;
//        }
//
//        return mapResult;
//    }
//
//    /*
//    * 根据平台字典D和机构字典ID 获取字典项映射关系
//    * @param strStdDictId 平台数据集ID
//    * @param StrOrgDictId 机构数据集ID
//    * @param versionCode 版本号
//    * @return List<DictEntryMappingInfo>映射关系
//    * */
//    public List<DictEntryMappingInfo> getDictEntrymapping(Map<String, Object> map) {
//        List<DictEntryMappingInfo> listInfo = null;
//
//        try {
//            String strStdSetId = map.get("strStdDictId").toString();
//            String StrOrgSetId = map.get("StrOrgDictId").toString();
//            String versionCode = map.get("versionCode").toString();
//            String strPlanId = map.get("strPlanId").toString();
//            String strMappingId = map.get("strMappingId").toString();
//            String strOrgCode = map.get("orgCode").toString();
//            int iLength = Integer.parseInt(map.get("length").toString());
//
//            Session session = currentSession();
//            String entryTableName = CDAVersion.getDictEntryTableName(versionCode);
//
//            StringBuffer sb = new StringBuffer();
//
//            sb.append("select ");
//            sb.append("a.std_dictentry, ");
//            sb.append("c.`code` std_dictentry_code, ");
//            sb.append("c.`value` std_dictentry_value, ");
//            sb.append("a.org_dictentry, ");
//            sb.append("b.`code` org_dictentry_code, ");
//            sb.append("b.`name` org_dictentry_value ");
//            sb.append("from adapter_dict a ");
//            sb.append("LEFT JOIN org_std_dictentry b on a.org_dictentry = b.sequence and b.organization='"+strOrgCode+"' ");
//            sb.append("LEFT JOIN "+entryTableName+" c on c.id=a.std_dictentry ");
//
//            sb.append("where a.plan_id='"+strPlanId+"' ");
//            sb.append("and a.std_dict='" + strStdSetId + "' ");
//
//            if (StrOrgSetId == null || StrOrgSetId=="")
//                sb.append("and (a.org_dict is null or a.org_dict='')");
//            else
//                sb.append("and a.org_dict='" + StrOrgSetId + "' ");
//
//            Query query = session.createSQLQuery(sb.toString());
//
//            List<Object> records = query.list();
//
//            listInfo = new ArrayList<>();
//
//            for (int i = 0; i < records.size(); ++i) {
//                iLength++;
//                DictEntryMappingInfo info = new DictEntryMappingInfo();
//
//                Object[] record = (Object[]) records.get(i);
//
//                info.setId(String.valueOf(iLength));
//                info.setStdDictEntryId(record[0].toString());
//                info.setStdDictEntryCode(record[1].toString());
//                info.setStdDictEntryValue(record[2].toString());
//                info.setOrgDictEntryId(record[3] == null ? "" : record[3].toString());
//                info.setOrgDictEntryCode(record[4] == null ? "" : record[4].toString());
//                info.setOrgDictEntryValue(record[5] == null ? "" : record[5].toString());
//                info.setAdapterDictId(strMappingId);
//                info.setPlanId(strPlanId);
//                listInfo.add(info);
//            }
//        } catch (Exception e) {
//            listInfo=null;
//        }
//        return listInfo;
//    }
//}