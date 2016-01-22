package com.yihu.ehr.adaption.service;


import com.yihu.ehr.model.dict.MBaseDict;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

/**
 * @author AndyCai
 * @version 1.0
 * @created 26-十月-2015 17:12:12
 */
@Service
public class AdapterDataSetManager {

    @Autowired
    private OrgAdapterPlanManager orgAdapterPlanManager;

    @Autowired
    private AdapterDictManager adapterDictManager;

    @Autowired
    private DictManager dictManager;

    @Autowired
    private MetaDataManager metaDataManager;

    public AdapterDataSetManager() {

    }

    public void finalize() throws Throwable {

    }

    /**
     * 新增数据元映射关系
     *
     * @param adapterDataSet
     */
    public boolean addAdapterDataSet(AdapterDataSet adapterDataSet) {
        try {
            saveEntity(adapterDataSet);

            //新增需要适配的字典
            Long planId = adapterDataSet.getAdapterPlanId();
            OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
            CDAVersion cdaVersion = orgAdapterPlan.getVersion();
            DataSet dataSet = new DataSet();
            dataSet.setId(adapterDataSet.getDataSetId());
            dataSet.setInnerVersion(cdaVersion);
            MetaData metaData = metaDataManager.getMetaData(dataSet, adapterDataSet.getMetaDataId());
            if (metaData != null) {
                long dictId = metaData.getDictId();
                if (dictId != 0) {
                    adapterDataSet.setStdDict(dictId);
                    updateEntity(adapterDataSet);
                    MBaseDict dict = dictManager.getDict(dictId, cdaVersion);
                    if (!adapterDictManager.isExist(planId, dictId)) {
                        adapterDictManager.addAdapterDict(planId, dict);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
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
        //要先删除数据字典映射
        for (Long id : ids) {
            Long stdDictId = getAdapterMetaData(id).getStdDict();
            List<Long> adapterDicts = adapterDictManager.getAdapterDictIds(stdDictId);
            adapterDictManager.deleteAdapterDict(adapterDicts.toArray(new Long[adapterDicts.size()]));
        }
        List<Long> lst = new ArrayList<>();
        lst = Arrays.asList(ids);
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery("delete from AdapterDataSet where id in (:ids)");
        query.setParameterList("ids", lst);

        return query.executeUpdate();
    }

    /**
     * 删除
     *
     * @param adapterDataSet
     */
    public boolean deleteAdapterDataSet(XAdapterDataSet adapterDataSet) {
        try {
            //要先删除数据字典映射
            List<Long> adapterDicts = adapterDictManager.getAdapterDictIds(adapterDataSet.getStdDict());
            adapterDictManager.deleteAdapterDict(adapterDicts.toArray(new Long[adapterDicts.size()]));
            deleteEntity(adapterDataSet);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    /**
     * 根据方案ID获取数据元映射
     *
     * @param adapterPlanId
     */
    public List<XAdapterDataSet> getAdapterMetaDataByPlan(Long adapterPlanId) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery(" from AdapterDataSet where adapterPlanId = :adapterPlanId");
        query.setParameter("adapterPlanId", adapterPlanId);
        return query.list();
    }

    /**
     * 根据方案ID获取数据元映射关系ID
     *
     * @param adapterPlanId
     */
    public List<Long> getAdapterMetaDataIds(Long adapterPlanId) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery("select id from AdapterDataSet where adapterPlanId = :adapterPlanId");
        query.setParameter("adapterPlanId", adapterPlanId);
        return query.list();
    }

    /**
     * 根据ID获取数据元适配关系明细
     *
     * @param id
     */
    public XAdapterDataSet getAdapterMetaData(long id) {
        return (XAdapterDataSet) getEntity(AdapterDataSet.class, id);

    }

    /**
     * 根据方案ID及查询条件查询数据集适配关系
     *
     * @param planId
     * @param strKey
     */
    public List<DataSetModel> searchAdapterDataSet(long planId, String strKey, int page, int rows) {

        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
        XCDAVersion version = orgAdapterPlan.getVersion();
        String dsTableName = version.getDataSetTableName();

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
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
     * 根据datasetId搜索数据元适配关系
     *
     * @param planId
     * @param dataSetId
     * @param strKey
     */
    public List<AdapterDataSetModel> searchAdapterMetaData(long planId, long dataSetId, String strKey, int page, int rows) {

        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
        String orgCode = orgAdapterPlan.getOrg();
        XCDAVersion version = orgAdapterPlan.getVersion();
        String dsTableName = version.getDataSetTableName();
        String mdTableName = version.getMetaDataTableName();

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        StringBuilder sb = new StringBuilder();

        sb.append(" select ads.id                    ");
        sb.append("       ,ads.plan_id               ");
        sb.append("       ," + dsTableName + ".id   as dsid  ");
        sb.append("       ," + dsTableName + ".code  as dscode ");
        sb.append("       ," + dsTableName + ".name  as dsname ");
        sb.append("       ," + mdTableName + ".id    as mdid ");
        sb.append("       ," + mdTableName + ".code  as mdcode ");
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

    /**
     * 修改数据元映射关系
     *
     * @param adapterDataSet
     */
    public boolean updateAdapterDataSet(XAdapterDataSet adapterDataSet) {
        try {
            updateEntity(adapterDataSet);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 查询适配数据集的总条数
     *
     * @return
     */
    public int searchDataSetInt(long planId, String strKey) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        StringBuilder sb = new StringBuilder();
        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
        XCDAVersion version = orgAdapterPlan.getVersion();
        String dsTableName = version.getDataSetTableName();
        sb.append(" select count(*)  ");
        sb.append("   from adapter_dataset ads     ");
        sb.append("        left join " + dsTableName + " on ads.std_dataset = " + dsTableName + ".id  ");
        sb.append("  where ads.plan_id = " + planId);
        if (!(strKey == null || strKey.equals(""))) {
            sb.append(" and (" + dsTableName + ".code like '%" + strKey + "%' or " + dsTableName + ".name like '%" + strKey + "%')");
        }
        sb.insert(0, " select count(*) from (  ");
        sb.append("  ) a" );
        String sql = sb.toString();
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        BigInteger i=  (BigInteger)sqlQuery.list().get(0);
        return i.intValue();
    }

    @Override
    public int searchMetaDataInt(long planId, long dataSetId, String strKey) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        StringBuilder sb = new StringBuilder();
        XOrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.getOrgAdapterPlan(planId);
        XCDAVersion version = orgAdapterPlan.getVersion();
        String mdTableName = version.getMetaDataTableName();
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


    /*
    * 获取数据集映射信息
    * @param strPlanId 方案ID
    * @param versionCode 映射版本ID
    * @return List<DataSetMappingInfo> 数据集映射信息列表
    * */
    public Map getDataSetMappingInfo(String strPlanId, String versionCode,String strOrgCode) {

        Map<String, Object> mapResult = new HashMap<>();

        try {
            Session session = currentSession();
            String datasetTableName = CDAVersion.getDataSetTableName(versionCode);

            StringBuffer sb = new StringBuffer();

            sb.append("SELECT ");
            sb.append("DISTINCT ");
            sb.append("a.std_dataset, ");
            sb.append("b.`code` std_dataset_code, ");
            sb.append("a.org_dataset, ");
            sb.append("c.`code` org_dataset_code,  ");
            sb.append("b.name std_dataset_name, ");
            sb.append("c.name org_dataset_name  ");
            sb.append("from adapter_dataset a ");
            sb.append("left JOIN " + datasetTableName + " b on a.std_dataset = b.id ");
            sb.append("left join org_std_dataset c on a.org_dataset = c.sequence and c.organization='"+strOrgCode+"' ");
            sb.append("where a.plan_id='" + strPlanId + "' ");
           // sb.append("and a.org_dataset is not null ");
//            sb.append("UNION ALL ");
//            sb.append("SELECT ");
//            sb.append("DISTINCT ");
//            sb.append("a.std_dataset, ");
//            sb.append("b.`code` std_dataset_code, ");
//            sb.append("a.org_dataset, ");
//            sb.append("c.`code` org_dataset_code,  ");
//            sb.append("b.name std_dataset_name, ");
//            sb.append("c.name org_dataset_name  ");
//            sb.append("from adapter_dataset a ");
//            sb.append("left JOIN " + datasetTableName + " b on a.std_dataset = b.id ");
//            sb.append("left join org_std_dataset c on a.org_dataset = c.id ");
//            sb.append("where a.plan_id='" + strPlanId + "' and a.org_dataset is null ");
//            sb.append("and a.std_dataset not in ( ");
//            sb.append("SELECT ");
//            sb.append("DISTINCT ");
//            sb.append("a.std_dataset ");
//            sb.append("from adapter_dataset a ");
//            sb.append("left JOIN " + datasetTableName + " b on a.std_dataset = b.id ");
//            sb.append("left join org_std_dataset c on a.org_dataset = c.id ");
//            sb.append("where a.plan_id='" + strPlanId + "' and a.org_dataset is not null ");
//            sb.append(") ");
            Query query = session.createSQLQuery(sb.toString());

            List<Object> records = query.list();

            List<DataSetMappingInfo> listMapping = new ArrayList<>();
            List<MetadataMappingInfo> listMetadataInfo = new ArrayList<>();

            for (int i = 0; i < records.size(); ++i) {

                DataSetMappingInfo info = new DataSetMappingInfo();

                Object[] record = (Object[]) records.get(i);

                info.setId(String.valueOf(i + 1));
                info.setStdSetId(record[0].toString());
                info.setStdSetCode(record[1].toString());
                info.setOrgSetId(record[2] == null ? "" : record[2].toString());
                info.setOrgSetCode(record[3] == null ? "" : record[3].toString());
                info.setStdSetName(record[4] == null ? "" : record[4].toString());
                info.setOrgSetName(record[5] == null ? "" : record[5].toString());
                info.setPlanId(strPlanId);
                listMapping.add(info);

                //获取数据元映射关系
                Map<String,Object> mapKey = new HashMap<>();
                mapKey.put("strStdSetId",info.getStdSetId());
                mapKey.put("StrOrgSetId",info.getOrgSetId());
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

        } catch (Exception ex) {
            mapResult=null;
        }

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

        try {
            String strStdSetId = map.get("strStdSetId").toString();
            String StrOrgSetId= map.get("StrOrgSetId").toString();
            String versionCode= map.get("versionCode").toString();
            String strPlanId= map.get("strPlanId").toString();
            String strMappingId= map.get("strMappingId").toString();
            String strOrgCode = map.get("orgCode").toString();
            int ilength = Integer.parseInt(map.get("length").toString());

            Session session = currentSession();
            String elementTanleName = CDAVersion.getMetaDataTableName(versionCode);

            StringBuffer sb = new StringBuffer();

            sb.append("select ");
            sb.append("a.std_metadata, ");
            sb.append("c.`code` std_metadata_code, ");
            sb.append("  a.org_metadata, ");
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
                info.setStdMetadataCode(record[1].toString());
                info.setOrgMetadataId(record[2] == null ? "" : record[2].toString());
                info.setOrgMetadataCode(record[3] == null ? "" : record[3].toString());
                info.setOrgDictDataType(record[4] == null ? "0" : record[4].toString());
                info.setStdMetadataName(record[5]==null?"":record[5].toString());
                info.setOrgMetadataName(record[6]==null?"":record[6].toString());
                info.setAdapterDataSetId(strMappingId);
                info.setPlanId(strPlanId);
                listInfo.add(info);
            }
        } catch (Exception e) {
            listInfo=null;
        }
        return listInfo;
    }

}