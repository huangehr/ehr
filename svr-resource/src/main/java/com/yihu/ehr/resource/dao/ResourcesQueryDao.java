package com.yihu.ehr.resource.dao;


import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.common.model.QueryEntity;
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

    private Integer defaultPage = 1;
    private Integer defaultSize = 50;

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
        DataList groupList = solr.getGroupCount("EHR_CENTER","patient_id");//根据病人ID分组*/

        //默认第一页
        if(page == null)
        {
            page = defaultPage;
        }
        //默认50行
        if(size == null)
        {
            size = defaultSize;
        }

        QueryEntity qe= new QueryEntity(core,page,size);
        if(metadata!=null && metadata.length()>0)
        {
            qe.setFields(metadata);
        }
        if(queryParams!=null && queryParams.length()>0)
        {
            qe.addConditionByJson(queryParams);
        }
        else{
            return hbase.query(qe);
        }
        //判断是否有包含Join查询
        if(queryParams.indexOf("joinParams")>0)
        {

            return hbase.queryJoinJson(qe, "");
        }
        else {
            return hbase.query(qe);
        }
    }

    /**
     * habsee多core
     */
    public DataList getHbaseMultiCore(String core,String metadata,String queryParams,Integer page,Integer size) throws Exception
    {
        //默认第一页
        if(page == null)
        {
            page = defaultPage;
        }
        //默认50行
        if(size == null)
        {
            size = defaultSize;
        }

        QueryEntity qe= new QueryEntity(core,page,size);
        return hbase.query(qe);
    }

    /**
     * habse的solr分组统计
     */
    public DataList getHbaseSolr(String core,String groupFields,String statsFields,String queryParams,Integer page,Integer size) throws Exception
    {
        String q = hbase.jsonToCondition(queryParams);
        //数值统计
        if(statsFields!=null && statsFields.length()>0)
        {
            return solr.getStats(core, groupFields, statsFields,q);
        }
        //总数统计
        else{
            if(groupFields.contains(",")) //多分组
            {
                String[] groups = groupFields.split(",");
                return solr.getGroupMult(core,groups,null,q); //自定义分组未完善
            }
            else{ //单分组
                //默认第一页
                if(page == null)
                {
                    page = defaultPage;
                }
                //默认50行
                if(size == null)
                {
                    size = defaultSize;
                }
                return solr.getGroupCount(core,groupFields,q,page,size);
            }
        }

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
