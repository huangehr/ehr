package com.yihu.ehr.resource.dao;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.services.DBQuery;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by hzp on 2016/4/22.
 */
@Service("resourcesQueryDao")
public class ResourcesQueryDao {

    @Autowired
    DBQuery query;
    @Autowired
    HbaseQuery hbase;
    @Autowired
    SolrQuery solr;

    private Integer defaultPage = 1;
    private Integer defaultSize = 50;


    /**
     * 获取Hbase主表
     * queryParams可为solr表达式，也可为json例：{"q":"*:*","fq":"","fl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}""}
     */
    public DataList getEhrCenter(String queryParams,Integer page,Integer size) throws Exception
    {
        String core = "EHR_CENTER";
        String q = "";
        String fq = "";
        String fl = "";
        String sort = "";
        if(queryParams!=null&&queryParams.length()>0)
        {
            if(queryParams.startsWith("{")&&queryParams.endsWith("}"))
            {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if(obj.containsKey("q"))
                {
                    q = obj.get("q");
                }
                if(obj.containsKey("fq"))
                {
                    fq = obj.get("fq");
                }
                if(obj.containsKey("fl"))
                {
                    fl = obj.get("fl");
                }
                if(obj.containsKey("sort"))
                {
                    sort = obj.get("sort");
                }
            }
            else
            {
                q = queryParams;
            }
        }

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
        return hbase.queryBySolr(core,q,sort,page,size,fq,fl);
    }

    /**
     * 获取Hbase细表
     * queryParams可为solr表达式，也可为json例：{"table":"HDSD00_08","q":"*:*","fq":"","fl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}""}
     */
    public DataList getEhrCenterSub(String queryParams,Integer page,Integer size) throws Exception
    {
        String core = "EHR_CENTER_SUB";
        String q = "";
        String fq = "";
        String fl = "";
        String sort = "";
        if(queryParams!=null&&queryParams.length()>0) {
            if (queryParams.startsWith("{") && queryParams.endsWith("}")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if (obj.containsKey("q")) {
                    q = obj.get("q");
                }
                if (obj.containsKey("fq")) {
                    fq = obj.get("fq");
                }
                if (obj.containsKey("fl")) {
                    fl = obj.get("fl");
                }
                if (obj.containsKey("sort")) {
                    sort = obj.get("sort");
                }
                if(obj.containsKey("table"))
                {
                    if(q.length()>0)
                    {
                        q+= " AND rowkey:*"+obj.get("table")+"*";
                    }
                    else {
                        q = "rowkey:*"+obj.get("table")+"*";
                    }
                }
            } else {
                q = queryParams;
            }
        }

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
        return hbase.queryBySolr(core,q,sort,page,size,fq,fl);
    }

    /**
     * habse主表的solr分组统计
     * queryParams为json例：{"q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4","customGroup":[{"groupField":"lastUpdateTime","groupCondition":{"3Month":"last_update_time:[2016-02-16 TO *]","6Month":"last_update_time:[2015-11-10 TO *]"}}]}
     */
    public DataList countEhrCenter(String queryParams,Integer page,Integer size) throws Exception
    {
        String core = "EHR_CENTER";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> params = objectMapper.readValue(queryParams, Map.class);

        String q = "";
        String groupFields="";
        String statsFields="";
        List<SolrGroupEntity> customGroup = new ArrayList<>();
        if(params.containsKey("q"))
        {
            q = params.get("q").toString();
        }
        if(params.containsKey("groupFields"))
        {
            groupFields = params.get("groupFields").toString();
        }
        if(params.containsKey("statsFields"))
        {
            statsFields = params.get("statsFields").toString();
        }
        if(params.containsKey("customGroup"))
        {
            ArrayList listGroup = ((ArrayList)params.get("customGroup"));
            if(listGroup!=null && listGroup.size()>0)
            {
                for(int i=0;i<listGroup.size();i++)
                {
                    String groupField = ((LinkedHashMap)listGroup.get(i)).get("groupField").toString();
                    Map<String,String> groupCondition = (Map)((LinkedHashMap)listGroup.get(i)).get("groupCondition");

                    customGroup.add(new SolrGroupEntity(groupField,groupCondition));
                }
            }
        }

        if(groupFields.length()==0 &&customGroup.size()==0){
            throw new Exception("缺少分组条件！");
        }
        //数值统计
        if(statsFields!=null && statsFields.length()>0)
        {
            return solr.getStats(core, groupFields, statsFields,q,customGroup);
        }
        //总数统计
        else{
            if(customGroup.size()==0)
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

                //多分组
                if(groupFields.contains(","))
                {
                    return solr.getGroupMult(core,groupFields,q,page,size);
                }
                else{
                    return solr.getGroupCount(core, groupFields, q, page, size);
                }
            }
            else
            {
                return solr.getGroupMult(core,groupFields,customGroup,q);
            }
        }

    }

    /**
     * habse细表的solr分组统计
     * queryParams为json例：{"table":"HDSD00_08","q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4",customGroup:""}
     */
    public DataList countEhrCenterSub(String queryParams,Integer page,Integer size) throws Exception
    {
        String core = "EHR_CENTER_SUB";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> params = objectMapper.readValue(queryParams, Map.class);

        String q = "";
        String groupFields="";
        String statsFields="";
        if(params.containsKey("q"))
        {
            q = params.get("q");
        }
        if(params.containsKey("groupFields"))
        {
            groupFields = params.get("groupFields");
        }
        else{
            throw new Exception("缺少分组条件！");
        }
        if(params.containsKey("statsFields"))
        {
            statsFields = params.get("statsFields");
        }
        if(params.containsKey("table"))
        {
            if(q.length()>0)
            {
                q+= " AND rowkey:*"+params.get("table")+"*";
            }
            else {
                q = "rowkey:*"+params.get("table")+"*";
            }
        }

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
                return solr.getGroupMult(core,groupFields,null,q); //自定义分组未完善
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
     * 获取Mysql配置库数据
     */
    public DataList getMysqlData(String queryParams,Integer page,Integer size) throws Exception
    {
        String sql = queryParams;

        //判定是否完整sql语句
        if(sql.toLowerCase().indexOf(" from ")<=0)
        {
            sql = "select * from "+queryParams;
        }

        if(page!=null && size !=null)
        {
            return query.queryBySql(sql,page,size);
        }
        else{
            return query.queryBySql(sql);
        }
    }


}
