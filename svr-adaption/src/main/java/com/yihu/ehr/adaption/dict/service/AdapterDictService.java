package com.yihu.ehr.adaption.dict.service;


import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanService;
import com.yihu.ehr.model.adaption.MAdapterDictVo;
import com.yihu.ehr.model.adaption.MAdapterRelationship;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@Service
public class AdapterDictService extends BaseJpaService<AdapterDict, XAdapterDictRepository> {

    @Autowired
    OrgAdapterPlanService orgAdapterPlanManager;


    /**
     * 根据方案和字典获取字典项适配
     *
     */
    @Transactional(propagation = Propagation.NEVER)
    public List<MAdapterRelationship> searchAdapterDict(OrgAdapterPlan orgAdapterPlan, String code, String name, String orders, int page, int rows) {
        long planId = orgAdapterPlan.getId();
        String dictTableName = CDAVersionUtil.getDictTableName(orgAdapterPlan.getVersion());
        Session session = currentSession();

        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct  ds.id    ");
        sb.append("       ,ds.code  ");
        sb.append("       ,ds.name  ");
        sb.append("   from adapter_dict ad          ");
        sb.append("        left join " + dictTableName + " ds on ad.std_dict = ds.id  ");
        sb.append("  where ad.plan_id = " + planId + " and ds.id is not null ");

        if (!StringUtils.isEmpty(code)){
            if (!StringUtils.isEmpty(name))
                sb.append(" and (ds.code like :code or ds.name like :name) ");
            else
                sb.append(" and ds.code like :code ");
        }
        else if (!StringUtils.isEmpty(name))
            sb.append(" and ds.name like :name ");

        sb.append(makeOrder(orders, "ds"));
        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        page = page == 0 ? 1 : page;
        sqlQuery.setMaxResults(rows);
        sqlQuery.setFirstResult((page - 1) * rows);
        return sqlQuery
                .addScalar("id", StandardBasicTypes.LONG )
                .addScalar("code", StandardBasicTypes.STRING)
                .addScalar("name", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(MAdapterRelationship.class))
                .list();
    }

    private String makeOrder(String orders, String vo) {
        if(StringUtils.isEmpty(orders))
            return "";
        String sql = "";
        for (String order : orders.split(",")) {
            if (order.startsWith("+"))
                sql += "," + vo + "." + order.substring(1);
            else if (order.startsWith("-"))
                sql += "," + vo + "." + order.substring(1) + " desc";
        }
        return StringUtils.isEmpty(sql) ?
                "" :
                " order by " + sql.substring(1);
    }
    /**
     * 查询适配字典的总条数
     *
     * @return
     */
    public int searchDictInt(OrgAdapterPlan orgAdapterPlan, String code, String name) {
        long planId = orgAdapterPlan.getId();
        String dictTableName = CDAVersionUtil.getDictTableName(orgAdapterPlan.getVersion());
        Session session = currentSession();

        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*) from (");
        sb.append(" select distinct  ds.id    ");
        sb.append("       ,ds.code  ");
        sb.append("       ,ds.name  ");
        sb.append("   from adapter_dict ad          ");
        sb.append("        left join " + dictTableName + " ds on ad.std_dict = ds.id  ");
        sb.append("  where ad.plan_id = " + planId);

        if (!StringUtils.isEmpty(code)){
            if (!StringUtils.isEmpty(name))
                sb.append(" and (ds.code like :code or ds.name like :name) ");
            else
                sb.append(" and ds.code like :code ");
        }
        else if (!StringUtils.isEmpty(name))
            sb.append(" and ds.name like :name ");
        sb.append(" ) t");

        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        return ((BigInteger) sqlQuery.list().get(0)).intValue();
    }


    /**
     * 根据条件搜索字典项适配关系
     *
     */
    public List<MAdapterDictVo> searchAdapterDictEntry(OrgAdapterPlan orgAdapterPlan, long dictId, String code, String name, String orders, int page, int rows) {
        String orgCode = orgAdapterPlan.getOrg();
        String dictTableName = CDAVersionUtil.getDictTableName(orgAdapterPlan.getVersion());
        String deTableName = CDAVersionUtil.getDictEntryTableName(orgAdapterPlan.getVersion());

        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        sb.append(" select ad.id                       ");
        sb.append("       ,ad.plan_id as adapterPlanId         ");
        sb.append("       ,ds.id   as dictId ");
        sb.append("       ,ds.code as dictCode ");
        sb.append("       ,ds.name as dictName ");
        sb.append("       ,de.id  as dictEntryId    ");
        sb.append("       ,de.code as dictEntryCode   ");
        sb.append("       ,de.value as dictEntryName   ");
        sb.append("       ,orgDict.id as orgDictSeq  ");
        sb.append("       ,orgDict.code as orgDictCode");
        sb.append("       ,orgDict.name as orgDictName");
        sb.append("       ,orgDE.id as orgDictEntrySeq  ");
        sb.append("       ,orgDE.code as orgDictEntryCode");
        sb.append("       ,orgDE.name as orgDictEntryName");
        sb.append("   from adapter_dict ad ");
        sb.append("        left join " + dictTableName + " ds on ad.std_dict = ds.id  ");
        sb.append("        left join " + deTableName + " de on ad.std_dictentry = de.id ");
        sb.append("        left join org_std_dict orgDict on ( orgDict.sequence = ad.org_dict and orgDict.organization='" + orgCode + "' )   ");
        sb.append("        left join org_std_dictentry orgDE on ( orgDE.sequence = ad.org_dictentry and orgDE.organization='" + orgCode + "' ) ");
        sb.append("  where ad.plan_id = " + orgAdapterPlan.getId());
        sb.append("    and ad.std_dict = " + dictId);

        if (!StringUtils.isEmpty(code)){
            if (!StringUtils.isEmpty(name))
                sb.append("     and (de.code like :code or de.value like :name)");
            else
                sb.append("     and de.code like :code ");
        }else if (!StringUtils.isEmpty(name))
            sb.append("     and de.value like :name");

        sb.append(makeOrder(orders, "de"));
        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        page = page == 0 ? 1 : page;
        sqlQuery.setMaxResults(rows);
        sqlQuery.setFirstResult((page - 1) * rows);

        return sqlQuery
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("adapterPlanId", StandardBasicTypes.LONG)
                .addScalar("dictId", StandardBasicTypes.LONG)
                .addScalar("dictCode", StandardBasicTypes.STRING)
                .addScalar("dictName", StandardBasicTypes.STRING)
                .addScalar("dictEntryId", StandardBasicTypes.LONG)
                .addScalar("dictEntryCode", StandardBasicTypes.STRING)
                .addScalar("dictEntryName", StandardBasicTypes.STRING)
                .addScalar("orgDictSeq", StandardBasicTypes.LONG)
                .addScalar("orgDictCode", StandardBasicTypes.STRING)
                .addScalar("orgDictName", StandardBasicTypes.STRING)
                .addScalar("orgDictEntrySeq", StandardBasicTypes.LONG)
                .addScalar("orgDictEntryCode", StandardBasicTypes.STRING)
                .addScalar("orgDictEntryName", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(MAdapterDictVo.class))
                .list();
    }


    /**
     * 根据条件搜索标准字典项适配关系
     *
     */
    public List<MAdapterRelationship> searchStdDictEntry(
            OrgAdapterPlan orgAdapterPlan, long dictId, String seachName, String mode, String orders, int page, int rows) {

        String deTableName = CDAVersionUtil.getDictEntryTableName(orgAdapterPlan.getVersion());
        Session session = currentSession();
        String sql =
                "SELECT entry.id, entry.code, entry.value as name " +
                "FROM" +
                "   "+ deTableName +" entry " +
                "LEFT JOIN" +
                "   (SELECT * FROM adapter_dict ad where ad.plan_id = :planId) adapterDict " +
                "ON" +
                "   entry.id = adapterDict.std_dictentry " +
                "WHERE" +
                "   entry.dict_id = :dictId ";
        if("new".equals(mode))
            sql += " AND adapterDict.id is null ";
        if(!StringUtils.isEmpty(seachName))
            sql += " AND (entry.code like :seachName or entry.value like :seachName) ";

        sql += makeOrder(orders, "entry");

        SQLQuery sqlQuery = session.createSQLQuery(sql);
        if (!StringUtils.isEmpty(seachName))
            sqlQuery.setParameter("seachName", "%" + seachName + "%");
        sqlQuery.setParameter("dictId", dictId);
        sqlQuery.setParameter("planId", orgAdapterPlan.getId());
        page = page == 0 ? 1 : page;
        sqlQuery.setMaxResults(rows);
        sqlQuery.setFirstResult((page - 1) * rows);

        return sqlQuery
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("code", StandardBasicTypes.STRING)
                .addScalar("name", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(MAdapterRelationship.class))
                .list();
    }


    /**
     * 根据条件搜索标准字典项总数
     *
     */
    public int countStdDictEntry(
            OrgAdapterPlan orgAdapterPlan, long dictId, String seachName, String mode) {

        String deTableName = CDAVersionUtil.getDictEntryTableName(orgAdapterPlan.getVersion());
        Session session = currentSession();
        String sql =
                "SELECT count(*) " +
                "FROM" +
                "   "+ deTableName +" entry " +
                "LEFT JOIN" +
                "   (SELECT * FROM adapter_dict ad where ad.plan_id = :planId) adapterDict " +
                "ON" +
                "   entry.id = adapterDict.std_dictentry " +
                "WHERE" +
                "   entry.dict_id = :dictId ";
        if("new".equals(mode))
            sql += " AND adapterDict.id is null ";
        if(!StringUtils.isEmpty(seachName))
            sql += " AND (entry.code like :seachName or entry.value like :seachName) ";

        SQLQuery sqlQuery = session.createSQLQuery(sql);
        if (!StringUtils.isEmpty(seachName))
            sqlQuery.setParameter("seachName", "%" + seachName + "%");
        sqlQuery.setParameter("dictId", dictId);
        sqlQuery.setParameter("planId", orgAdapterPlan.getId());

        return ((BigInteger)sqlQuery.list().get(0)).intValue();
    }

    /**
     * 查询适配字典细项的总条数
     *
     * @return
     */
    public int searchDictEntryInt(OrgAdapterPlan orgAdapterPlan, long dictId, String code, String name) {
        Session session = currentSession();
        String deTableName = CDAVersionUtil.getDictEntryTableName(orgAdapterPlan.getVersion());

        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*)    ");
        sb.append("   from adapter_dict ad     ");
        sb.append("        left join " + deTableName + " de on ad.std_dictentry = de.id ");
        sb.append("  where ad.plan_id = " + orgAdapterPlan.getId());
        sb.append("    and ad.std_dict = " + dictId);
        if (!StringUtils.isEmpty(code))
            sb.append("     and de.code like :code");
        if (!StringUtils.isEmpty(name))
            sb.append("     and de.value like :name");
        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        return ((BigInteger)sqlQuery.list().get(0)).intValue();
    }

    /**
     * 新增适配明细，返回操作结果
     *
     * @param adapterDict
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public AdapterDict addAdapterDict(AdapterDict adapterDict) {
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
        q.setParameter("org_dictentry", adapterDict.getOrgDictEntrySeq());
        q.setParameter("description", adapterDict.getDescription());
        int rs = q.executeUpdate();
        return adapterDict;
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
            AdapterDict adapterDict = retrieve(ids[0]);
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

    /*
   * 获取字典、字典项映射信息
   * @param strPlanId 方案ID
   * @param versionCode 标准版本ID
   * @result Map
   * */
    public Map<String, Object> getDictMappingInfo(String strPlanId, String versionCode,String strOrgCode) {
        Map<String, Object> mapResult = new HashMap<>();

        Session session = currentSession();
        String dictTableName = CDAVersionUtil.getDictTableName(versionCode);

        StringBuffer sb = new StringBuffer();

        sb.append("SELECT ");
        sb.append("DISTINCT ");
        sb.append("a.std_dict, ");
        sb.append("b.`code` std_dict_code,  ");
        sb.append("c.id dict_id, ");
        sb.append("c.`code` org_dict_code,  ");
        sb.append("b.name std_dict_name,  ");
        sb.append("c.name org_dict_name,   ");
        sb.append("a.org_dict ");
        sb.append("from adapter_dict a  ");
        sb.append("left JOIN " + dictTableName + " b on a.std_dict = b.id  ");
        sb.append("left join org_std_dict c on a.org_dict = c.sequence and c.organization='"+strOrgCode+"' ");
        sb.append("where a.plan_id='" + strPlanId + "'");

        Query query = session.createSQLQuery(sb.toString());

        List<Object> records = query.list();

        List<DictMappingInfo> listMapping = new ArrayList<>();
        List<DictEntryMappingInfo> listEntryInfo = new ArrayList<>();

        for (int i = 0; i < records.size(); ++i) {
            System.err.print("dict:"+ i);
            DictMappingInfo info = new DictMappingInfo();

            Object[] record = (Object[]) records.get(i);

            info.setId(String.valueOf(i + 1));
            info.setStdDictId(record[0] == null ? "" : record[0].toString());
            info.setStdDictCode(record[1] == null ? "" : record[1].toString());
            info.setOrgDictId(record[2] == null ? "" : record[2].toString());
            info.setOrgDictCode(record[3] == null ? "" : record[3].toString());
            info.setStdDictName(record[4] == null ? "" : record[4].toString());
            info.setOrgDictName(record[5] == null ? "" : record[5].toString());
            String orgSequence = record[6] == null ? "" : record[6].toString();
            info.setPlanId(strPlanId);
            listMapping.add(info);

            //获取数据元映射关系
            Map<String, Object> mapKey = new HashMap<>();
            mapKey.put("strStdDictId", info.getStdDictId());
            mapKey.put("StrOrgDictId", orgSequence);
            mapKey.put("versionCode", versionCode);
            mapKey.put("strPlanId", info.getPlanId());
            mapKey.put("strMappingId", info.getId());
            mapKey.put("length",listEntryInfo.size());
            mapKey.put("orgCode",strOrgCode);
            List<DictEntryMappingInfo> listMetadata = getDictEntrymapping(mapKey);

            listEntryInfo.addAll(listMetadata);
        }

        mapResult.put("dictlist", listMapping);
        mapResult.put("dictentrylist", listEntryInfo);

        return mapResult;
    }


    /*
    * 根据平台字典D和机构字典ID 获取字典项映射关系
    * @param strStdDictId 平台数据集ID
    * @param StrOrgDictId 机构数据集ID
    * @param versionCode 版本号
    * @return List<DictEntryMappingInfo>映射关系
    * */
    public List<DictEntryMappingInfo> getDictEntrymapping(Map<String, Object> map) {
        List<DictEntryMappingInfo> listInfo = null;

        String strStdSetId = map.get("strStdDictId").toString();
        String StrOrgSetId = map.get("StrOrgDictId").toString();
        String versionCode = map.get("versionCode").toString();
        String strPlanId = map.get("strPlanId").toString();
        String strMappingId = map.get("strMappingId").toString();
        String strOrgCode = map.get("orgCode").toString();
        int iLength = Integer.parseInt(map.get("length").toString());

        Session session = currentSession();
        String entryTableName = CDAVersionUtil.getDictEntryTableName(versionCode);

        StringBuffer sb = new StringBuffer();

        sb.append("select ");
        sb.append("a.std_dictentry, ");
        sb.append("c.`code` std_dictentry_code, ");
        sb.append("c.`value` std_dictentry_value, ");
        sb.append("a.org_dictentry, ");
        sb.append("b.`code` org_dictentry_code, ");
        sb.append("b.`name` org_dictentry_value ");
        sb.append("from adapter_dict a ");
        sb.append("LEFT JOIN org_std_dictentry b on a.org_dictentry = b.sequence and b.organization='"+strOrgCode+"' ");
        sb.append("LEFT JOIN "+entryTableName+" c on c.id=a.std_dictentry ");

        sb.append("where a.plan_id='"+strPlanId+"' ");
        sb.append("and a.std_dict='" + strStdSetId + "' ");

        if (StrOrgSetId == null || StrOrgSetId=="")
            sb.append("and (a.org_dict is null or a.org_dict='')");
        else
            sb.append("and a.org_dict='" + StrOrgSetId + "' ");

        Query query = session.createSQLQuery(sb.toString());
        System.err.println("entity: qs");
        List<Object> records = query.list();
        System.err.println("entity: qe");
        listInfo = new ArrayList<>();

        for (int i = 0; i < records.size(); ++i) {
            iLength++;
            DictEntryMappingInfo info = new DictEntryMappingInfo();

            Object[] record = (Object[]) records.get(i);

            info.setId(String.valueOf(iLength));
            info.setStdDictEntryId(record[0] == null ? "" : record[0].toString());
            info.setStdDictEntryCode(record[1] == null ? "" : record[1].toString());
            info.setStdDictEntryValue(record[2] == null ? "" : record[2].toString());
            info.setOrgDictEntryId(record[3] == null ? "" : record[3].toString());
            info.setOrgDictEntryCode(record[4] == null ? "" : record[4].toString());
            info.setOrgDictEntryValue(record[5] == null ? "" : record[5].toString());
            info.setAdapterDictId(strMappingId);
            info.setPlanId(strPlanId);
            listInfo.add(info);
        }
        return listInfo;
    }

    /**
     * 根据字典新增适配明细，
     * create by lincl 2016-4-15
     * @param orgAdapterPlan 方案
     * @param dictIds 字典编号集
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public int batchAddAdapterDict(OrgAdapterPlan orgAdapterPlan, List dictIds) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getDictEntryTableName(orgAdapterPlan.getVersion());
        String sql = "insert into adapter_dict(plan_id, std_dict, std_dictentry) "+
                "select  :planId as plan_id, tb.dict_id, tb.id from " + strTableName + " tb where  tb.dict_id in(:dictIds)";
        Query query = session.createSQLQuery(sql);
        query.setParameterList("dictIds", dictIds);
        query.setLong("planId", orgAdapterPlan.getId());
        return query.executeUpdate();
    }
}