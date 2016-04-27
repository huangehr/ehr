package com.yihu.ehr.resource.dao;


import com.yihu.ehr.resource.common.DataGridResult;
import com.yihu.ehr.solr.query.HbaseList;
import com.yihu.ehr.solr.query.HbaseQuery;
import com.yihu.ehr.solr.query.SolrQuery;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * Created by hzp on 2016/4/22.
 */
@Service("resourcesQueryDao")
public class ResourcesQueryDao extends BaseDao {
    @Autowired
    HbaseQuery hbase;

    @Autowired
    SolrQuery solr;
    /**
     * habse单core
     */
    public DataGridResult getHbaseSingleCore(String core,String metadata,String queryParams,Integer page,Integer size) throws Exception
    {
        long count = solr.count("HealthArchives","patient_id:10295435");
        HbaseList groupList = solr.getGroupCount("HealthArchives","patient_id");//根据病人ID分组
        JSONObject obj = hbase.queryByRowkey("HealthArchives", "41872607-9_10295435_000622450_1444060800000");
        HbaseList list = hbase.queryBySolr("HealthArchives", "patient_id:10295435", null, 1, 50);

        return null;
    }

    /**
     * habsee多core
     */
    public DataGridResult getHbaseMultiCore(String core,String metadata,String queryParams,Integer page,Integer size) throws Exception
    {
        return null;
    }

    /**
     * habse的solr分组统计
     */
    public DataGridResult getHbaseSolr(String core,String metadata,String queryParams,Integer page,Integer size) throws Exception
    {
        return null;
    }

    /**
     * ETL数据
     */
    public DataGridResult getEtlData(String datasource,String table,String cols,String queryParams,Integer page,Integer size) throws Exception
    {
        return null;
    }

    /**
     * 配置数据
     */
    public DataGridResult getConfigData(String datasource,String table,String cols,String queryParams,Integer page,Integer size) throws Exception
    {
        return null;
    }

    /**
     * 字典数据
     */
    public DataGridResult getDictData(String dictName) throws Exception
    {
        return null;
    }
}
