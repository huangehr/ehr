package com.yihu.ehr.resource.dao;


import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.jdbc.DBHelper;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * Created by hzp on 2016/4/22.
 */
@Service("resourcesQueryDao")
public class ResourcesQueryDao extends BaseDao {

    @Autowired
    DBHelper db;
    @Autowired
    HbaseQuery hbase;
    @Autowired
    SolrQuery solr;

    /**
     * habse单core
     */
    public DataList getHbaseSingleCore(String core,String metadata,String queryParams,Integer page,Integer size) throws Exception
    {
        /*DataList list= hbase.queryBySolr("EHR_CENTER", "patient_id:10293555", null, 1, 50);
        DataList list2= solr.getGroupCount("EHR_CENTER","patient_id");
        QueryEntity qe = new QueryEntity("EHR_CENTER",1,50);
        List<QueryCondition> cl = new ArrayList<>();
        cl.add(new QueryCondition("archive_id","41872607-9_10289499_000618952_1443715200000"));
        List<SolrJoinEntity> joinList =  new ArrayList<>();
        joinList.add(new SolrJoinEntity("EHR_CENTER_SUB_shard1_replica1","main_rowkey","rowkey",cl));
        DataList list3= hbase.query(qe,joinList);
        List list4 = db.query("select * from RS_CATEGORY");
        List<RsCategory> list5 = db.query(RsCategory.class,"select * from RS_CATEGORY");
        long count = solr.count("EHR_CENTER","patient_id:10293555");
        DataList groupList = solr.getGroupCount("EHR_CENTER","patient_id");//根据病人ID分组
        DataList list6 = hbase.queryBySolr("EHR_CENTER", "patient_id:10293555", null, 1, 50);
        DataList list7 = hbase.queryBySolr("EHR_CENTER_SUB", "patient_id:10293555", null, 1, 50);*/

        return null;
    }

    /**
     * habsee多core
     */
    public DataList getHbaseMultiCore(String core,String metadata,String queryParams,Integer page,Integer size) throws Exception
    {

        return null;
    }

    /**
     * habse的solr分组统计
     */
    public DataList getHbaseSolr(String core,String metadata,String queryParams,Integer page,Integer size) throws Exception
    {
        return null;
    }

    /**
     * ETL数据
     */
    public DataList getEtlData(String datasource,String table,String cols,String queryParams,Integer page,Integer size) throws Exception
    {
        return null;
    }

    /**
     * 配置数据
     */
    public DataList getConfigData(String datasource,String table,String cols,String queryParams,Integer page,Integer size) throws Exception
    {
        return null;
    }

    /**
     * 字典数据
     */
    public DataList getDictData(String dictName) throws Exception
    {
        return null;
    }
}
