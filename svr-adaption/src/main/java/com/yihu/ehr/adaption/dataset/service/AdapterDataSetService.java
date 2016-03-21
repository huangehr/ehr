package com.yihu.ehr.adaption.dataset.service;


import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanService;
import com.yihu.ehr.adaption.dict.service.AdapterDictService;
import com.yihu.ehr.adaption.feignclient.DictClient;
import com.yihu.ehr.model.adaption.MAdapterDataVo;
import com.yihu.ehr.model.adaption.MAdapterRelationship;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@Service
public class AdapterDataSetService extends BaseJpaService<AdapterDataSet, XAdapterDataSetRepository> {
    @Autowired
    OrgAdapterPlanService orgAdapterPlanManager;
    @Autowired
    AdapterDictService adapterDictService;
    @Autowired
    DictClient dictClient;


    /**
     * 根据方案ID及查询条件查询数据集适配关系
     */
    public List<MAdapterRelationship> searchAdapterDataSet(OrgAdapterPlan plan, String code, String name, String orders, int page, int rows) {
        long planId = plan.getId();
        String version = plan.getVersion();
        String dsTableName = CDAVersionUtil.getDataSetTableName(version);
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();

        sb.append("  select distinct ds.id    ");
        sb.append("       ,ds.code  ");
        sb.append("       ,ds.name  ");
        sb.append("  from adapter_dataset ads     ");
        sb.append("        left join " + dsTableName + " ds on ads.std_dataset = ds.id  ");
        sb.append("  where ads.plan_id = " + planId);
        if (!StringUtils.isEmpty(code))
            sb.append("     and ds.code like :code");
        if (!StringUtils.isEmpty(name))
            sb.append("     and ds.name like :name");
        sb.append(makeOrder(orders));
        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        page = page == 0 ? 1 : page;
        sqlQuery.setMaxResults(rows);
        sqlQuery.setFirstResult((page - 1) * rows);
//        return sqlQuery.addEntity(MDataSet.class).list();
        return sqlQuery
                .addScalar("id", StandardBasicTypes.LONG )
                .addScalar("code", StandardBasicTypes.STRING)
                .addScalar("name", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(MAdapterRelationship.class))
                .list();
    }

    private String makeOrder(String orders) {
        if(StringUtils.isEmpty(orders))
            return "";
        String sql = "";
        for (String order : orders.split(",")) {
            if (order.startsWith("+"))
                sql += "," + order.substring(1);
            else if (order.startsWith("-"))
                sql += "," + order.substring(1) + " desc";
        }
        return StringUtils.isEmpty(sql) ?
                "" :
                " order by " + sql.substring(1);
    }

    /**
     * 查询适配数据集的总数
     *
     * @return
     */
    public int searchDataSetInt(OrgAdapterPlan orgAdapterPlan, String code, String name) {
        long planId = orgAdapterPlan.getId();
        String version = orgAdapterPlan.getVersion();
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        String dsTableName = CDAVersionUtil.getDataSetTableName(version);
        sb.append(" select count(*) from ");
        sb.append(" (select distinct ds.id ");
        sb.append("       ,ds.code  ");
        sb.append("       ,ds.name  ");
        sb.append("   from adapter_dataset ads     ");
        sb.append("        left join " + dsTableName + " ds on ads.std_dataset = ds.id  ");
        sb.append("  where ads.plan_id = " + planId);
        if (!StringUtils.isEmpty(code))
            sb.append("     and ds.code like :code");
        if (!StringUtils.isEmpty(name))
            sb.append("     and ds.name like :name");
        sb.append(") t");
        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        return ((BigInteger) sqlQuery.list().get(0)).intValue();
    }


    /**
     * 根据datasetId搜索数据元适配关系
     */
    public List<MAdapterDataVo> searchAdapterMetaData(OrgAdapterPlan orgAdapterPlan, long dataSetId, String code, String name, String orders, int page, int rows) {

        String orgCode = orgAdapterPlan.getOrg();
        String dsTableName = CDAVersionUtil.getDataSetTableName(orgAdapterPlan.getVersion());
        String mdTableName = CDAVersionUtil.getMetaDataTableName(orgAdapterPlan.getVersion());
        long planId = orgAdapterPlan.getId();
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();

        sb.append(" select ads.id                               ");
        sb.append("       ,ads.plan_id as adapterPlanId         ");
        sb.append("       ,ds.id   as dataSetId  ");
        sb.append("       ,ds.code as dataSetCode ");
        sb.append("       ,ds.name as dataSetName ");
        sb.append("       ,md.id   as metaDataId ");
        sb.append("       ,md.inner_code  as metaDataCode ");
        sb.append("       ,md.name as metaDataName ");
        sb.append("       ,md.column_type as dataTypeName ");
        sb.append("       ,orgDS.id   as orgDataSetSeq   ");
        sb.append("       ,orgDS.code as orgDataSetCode  ");
        sb.append("       ,orgDS.name as orgDataSetName  ");
        sb.append("       ,orgMD.id   as orgMetaDataSeq  ");
        sb.append("       ,orgMD.code as orgMetaDataCode ");
        sb.append("       ,orgMD.name as orgMetaDataName ");
        sb.append("   from adapter_dataset ads ");
        sb.append("        left join " + dsTableName + " ds on ads.std_dataset = ds.id  ");
        sb.append("        left join " + mdTableName + " md on ads.std_metadata = md.id ");
        sb.append("        left join org_std_dataset orgDS on (orgDS.sequence = ads.org_dataset and orgDS.organization='" + orgCode + "')    ");
        sb.append("        left join org_std_metadata orgMD on (orgMD.sequence = ads.org_metadata and orgMD.organization='" + orgCode + "')  ");
        sb.append("  where ads.plan_id = " + planId);
        sb.append("        and ads.std_dataset = " + dataSetId);
        if (!StringUtils.isEmpty(code)){
            if (!StringUtils.isEmpty(name)){
                sb.append("     and (md.inner_code like :code ");
                sb.append("         or md.name like :name)");
            }else
                sb.append("     and md.inner_code like :code ");
        }
        else if (!StringUtils.isEmpty(name)){
            sb.append("   and md.name like :name)");
        }

        sb.append(makeOrder(orders));
        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        page = page == 0 ? 1 : page;

        sqlQuery.setMaxResults(rows)
                .setFirstResult((page - 1) * rows);
        return sqlQuery
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("adapterPlanId", StandardBasicTypes.LONG)
                .addScalar("dataSetId", StandardBasicTypes.LONG)
                .addScalar("dataSetCode", StandardBasicTypes.STRING)
                .addScalar("dataSetName", StandardBasicTypes.STRING)
                .addScalar("metaDataId", StandardBasicTypes.LONG)
                .addScalar("metaDataCode", StandardBasicTypes.STRING)
                .addScalar("metaDataName", StandardBasicTypes.STRING)
                .addScalar("dataTypeName", StandardBasicTypes.STRING)
                .addScalar("orgDataSetSeq", StandardBasicTypes.LONG)
                .addScalar("orgDataSetCode", StandardBasicTypes.STRING)
                .addScalar("orgDataSetName", StandardBasicTypes.STRING)
                .addScalar("orgMetaDataSeq", StandardBasicTypes.LONG)
                .addScalar("orgMetaDataCode", StandardBasicTypes.STRING)
                .addScalar("orgMetaDataName", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(MAdapterDataVo.class))
                .list();
    }


    public int searchMetaDataInt(OrgAdapterPlan orgAdapterPlan, long dataSetId, String code, String name) {
        long planId = orgAdapterPlan.getId();
        Session session = currentSession();
        StringBuilder sb = new StringBuilder();
        String mdTableName = CDAVersionUtil.getMetaDataTableName(orgAdapterPlan.getVersion());
        sb.append(" select count(*)    ");
        sb.append("   from adapter_dataset ads     ");
        sb.append("        left join " + mdTableName + " md on ads.std_metadata = md.id ");
        sb.append("  where ads.plan_id = " + planId);
        sb.append("        and ads.std_dataset = " + dataSetId);
        if (!StringUtils.isEmpty(code))
            sb.append("     and md.inner_code like :code");
        if (!StringUtils.isEmpty(name))
            sb.append("     and md.name like :name");
        SQLQuery sqlQuery = session.createSQLQuery(sb.toString());
        if (!StringUtils.isEmpty(code))
            sqlQuery.setParameter("code", "%" + code + "%");
        if (!StringUtils.isEmpty(name))
            sqlQuery.setParameter("name", "%" + name + "%");
        return ((BigInteger) sqlQuery.list().get(0)).intValue();
    }


    /**
     * 新增数据元映射关系
     *
     * @param adapterDataSet
     * @param orgAdapterPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AdapterDataSet addAdapterDataSet(AdapterDataSet adapterDataSet, OrgAdapterPlan orgAdapterPlan) {
        //新增需要适配的字典
        Long planId = adapterDataSet.getAdapterPlanId();
        String cdaVersion = orgAdapterPlan.getVersion();
        Map map = dictClient.getDictMapByIds(cdaVersion, adapterDataSet.getDataSetId(), adapterDataSet.getMetaDataId());
        if (map != null) {
            Long dictId = Long.parseLong((String) map.get("dictId"));
            adapterDataSet.setStdDict(dictId);
            if (!adapterDictService.isExist(planId, dictId)) {
                adapterDictService.addAdapterDict(planId, dictId, cdaVersion);
            }
        }
        saveAdapterDataSet(adapterDataSet);
        return adapterDataSet;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public int saveAdapterDataSet(AdapterDataSet adapterDataSet) {
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
    public int deleteAdapterDataSet(Object[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        Map<String, List<Long>> map = new HashMap();
        String key = "";
        List<Long> value = null;
        Long stdDictId, planId;
        //要先删除数据字典映射
        for (Object id : ids) {
            AdapterDataSet ds = retrieve((Long) id);
            stdDictId = ds.getStdDict();
            if (stdDictId != null) {
                planId = ds.getAdapterPlanId();
                key = planId + "," + stdDictId;
                if ((value = map.get("key")) == null)
                    value = new ArrayList<>();
                value.add(ds.getId());
                map.put(key, value);
            }
        }
        for (String k : map.keySet()) {
            planId = Long.parseLong(k.split(",")[0]);
            stdDictId = Long.parseLong(k.split(",")[1]);
            if (!isStdDictRelatedIgnore(map.get(k), planId, stdDictId))
                adapterDictService.deleteAdapterDict(planId, stdDictId);
        }

        Session session = currentSession();
        Query query = session.createQuery("delete from AdapterDataSet where id in (:ids)");
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }


    public boolean isStdDictRelatedIgnore(List<Long> dataSetId, Long planId, Long stdDictId) {
        Session s = currentSession();
        String sql = "select count(*) from adapter_dataset where " +
                "plan_id=:planId and std_dict=:stdDictId and id not in(:dataSetId)";
        Query q = s.createSQLQuery(sql);
        q.setLong("planId", planId);
        q.setLong("stdDictId", stdDictId);
        q.setParameterList("dataSetId", dataSetId);
        return ((BigInteger) q.list().get(0)).intValue() > 0;
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
    public List<Long> getAdapterDataSet(Long planId) {
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
    public List<AdapterDataSet> getAdapterMetaData(Long planId) {
        Session session = currentSession();
        Query query = session.createQuery("from AdapterDataSet where adapterPlanId = :planId");
        query.setParameter("planId", planId);
        return query.list();
    }


    /*
    * 获取数据集映射信息
    * @param strPlanId 方案ID
    * @param versionCode 映射版本ID
    * @return List<DataSetMappingInfo> 数据集映射信息列表
    * */
    public Map getDataSetMappingInfo(String strPlanId, String versionCode,String strOrgCode) {

        Map<String, Object> mapResult = new HashMap<>();

        Session session = currentSession();
        String datasetTableName = CDAVersionUtil.getDataSetTableName(versionCode);

        StringBuffer sb = new StringBuffer();

        sb.append("SELECT ");
        sb.append("DISTINCT ");
        sb.append("a.std_dataset, ");
        sb.append("b.`code` std_dataset_code, ");
        sb.append("c.id, ");
        sb.append("c.`code` org_dataset_code,  ");
        sb.append("b.name std_dataset_name, ");
        sb.append("c.name org_dataset_name,  ");
        sb.append("a.org_dataset ");
        sb.append("from adapter_dataset a ");
        sb.append("left JOIN " + datasetTableName + " b on a.std_dataset = b.id ");
        sb.append("left join org_std_dataset c on a.org_dataset = c.sequence and c.organization='"+strOrgCode+"' ");
        sb.append("where a.plan_id='" + strPlanId + "' ");
        Query query = session.createSQLQuery(sb.toString());

        List<Object> records = query.list();

        List<DataSetMappingInfo> listMapping = new ArrayList<>();
        List<MetadataMappingInfo> listMetadataInfo = new ArrayList<>();

        for (int i = 0; i < records.size(); ++i) {

            DataSetMappingInfo info = new DataSetMappingInfo();

            Object[] record = (Object[]) records.get(i);

            info.setId(String.valueOf(i + 1));
            info.setStdSetId(String.valueOf(record[0]));
            info.setStdSetCode(record[1] == null ? "" : record[1].toString());
            info.setOrgSetId(record[2] == null ? "" : record[2].toString());
            info.setOrgSetCode(record[3] == null ? "" : record[3].toString());
            info.setStdSetName(record[4] == null ? "" : record[4].toString());
            info.setOrgSetName(record[5] == null ? "" : record[5].toString());
            String orgSequence = record[6] == null ? "" : record[6].toString();
            info.setPlanId(strPlanId);
            listMapping.add(info);

            //获取数据元映射关系
            Map<String,Object> mapKey = new HashMap<>();
            mapKey.put("strStdSetId",info.getStdSetId());
            mapKey.put("StrOrgSetId",orgSequence);
            mapKey.put("versionCode",versionCode);
            mapKey.put("strPlanId",info.getPlanId());
            mapKey.put("strMappingId",info.getId());
            mapKey.put("length",listMetadataInfo.size());
            mapKey.put("orgCode",strOrgCode);
            List<MetadataMappingInfo> listMetadata=getMetadatamapping(mapKey);

            listMetadataInfo.addAll(listMetadata);
        }

        mapResult.put("datasetlist", listMapping);
        mapResult.put("elementlist", listMetadataInfo);

        return mapResult;
    }


    /*
    * 根据平台数据集ID和机构数据ID 获取数据元映射关系
    * @param strStdSetId 平台数据集ID
    * @param StrOrgSetId 机构数据集ID
    * @param versionCode 版本号
    * @return List<MetadataMappingInfo>映射关系
    * */
    public List<MetadataMappingInfo> getMetadatamapping(Map<String,Object> map) {
        List<MetadataMappingInfo> listInfo = null;

        String strStdSetId = map.get("strStdSetId").toString();
        String StrOrgSetId= map.get("StrOrgSetId").toString();
        String versionCode= map.get("versionCode").toString();
        String strPlanId= map.get("strPlanId").toString();
        String strMappingId= map.get("strMappingId").toString();
        String strOrgCode = map.get("orgCode").toString();
        int ilength = Integer.parseInt(map.get("length").toString());

        Session session = currentSession();
        String elementTanleName = CDAVersionUtil.getMetaDataTableName(versionCode);

        StringBuffer sb = new StringBuffer();

        sb.append("select ");
        sb.append("a.std_metadata, ");
        sb.append("c.inner_code std_metadata_code, ");
        sb.append(" b.id, ");
        sb.append("b.`code` org_metadata_code, ");
        sb.append("a.data_type, ");
        sb.append("c.name std_metadata_name, ");
        sb.append("b.name org_metadata_name ");
        sb.append("from adapter_dataset a ");
        sb.append("LEFT JOIN org_std_metadata b on a.org_metadata =b.sequence and b.organization='"+strOrgCode+"' ");
        sb.append("LEFT JOIN "+elementTanleName+" c on c.id=a.std_metadata ");

        sb.append("where a.plan_id='"+strPlanId+"' ");
        sb.append("and a.std_dataset='"+strStdSetId+"' ");
        if(StrOrgSetId==null || StrOrgSetId=="")
            sb.append("and (a.org_dataset ='' or a.org_dataset is null)");
        else
            sb.append("and a.org_dataset='"+StrOrgSetId+"' ");

        Query query = session.createSQLQuery(sb.toString());

        List<Object> records = query.list();

        listInfo = new ArrayList<>();

        for (int i = 0; i < records.size(); ++i) {
            ilength++;
            MetadataMappingInfo info = new MetadataMappingInfo();

            Object[] record = (Object[]) records.get(i);

            info.setId(String.valueOf(ilength));
            info.setStdMetadataId(record[0].toString());
            info.setStdMetadataCode(record[1] == null ? "" : record[1].toString());
            info.setOrgMetadataId(record[2] == null ? "" : record[2].toString());
            info.setOrgMetadataCode(record[3] == null ? "" : record[3].toString());
            info.setOrgDictDataType(record[4] == null ? "0" : record[4].toString());
            info.setStdMetadataName(record[5]==null?"":record[5].toString());
            info.setOrgMetadataName(record[6]==null?"":record[6].toString());
            info.setAdapterDataSetId(strMappingId);
            info.setPlanId(strPlanId);
            listInfo.add(info);
        }
        return listInfo;
    }
}