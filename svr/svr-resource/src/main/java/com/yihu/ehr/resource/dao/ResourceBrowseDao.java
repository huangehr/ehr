package com.yihu.ehr.resource.dao;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.common.sqlparser.ParserFactory;
import com.yihu.ehr.query.common.sqlparser.ParserSql;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hzp on 2016/4/22.
 * 资源查询底层接口
 */
@Service
public class ResourceBrowseDao {

    private Integer defaultPage = 1;
    private Integer defaultSize = 1000;
    private String mainJoinCore = ResourceCore.MasterTable + "_shard1_replica1";
    private String subJoinCore = ResourceCore.SubTable + "_shard1_replica1";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HbaseQuery hbase;
    @Autowired
    private SolrQuery solr;

    /**
     * 获取Hbase主表
     * queryParams可为solr表达式，也可为json例：{"q":"*:*","saas":"*","join":"*:*","fl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}""}
     * 有join参数做join操作
     */
    public Page<Map<String, Object>> getEhrCenter(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.MasterTable;
        String q = "";
        String fq = "";
        String basicFl = "";
        String dFl = "";
        String sort = "";
        if (queryParams != null && queryParams.length() > 0) {
            if (queryParams.startsWith("{") && queryParams.endsWith("}")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if (obj.containsKey("q")) {
                    q = obj.get("q");
                    if (obj.containsKey("saas") && !obj.get("saas").equals("*")) {
                        q += " AND (" + obj.get("saas") + ")";
                    }
                }
                else{
                    if (obj.containsKey("saas") && !obj.get("saas").equals("*")) {
                        q = obj.get("saas");
                    }
                }
                if (obj.containsKey("basicFl")) {
                    basicFl = obj.get("basicFl");
                }
                if (obj.containsKey("dFl")) {
                    dFl = obj.get("dFl");
                }
                if (obj.containsKey("sort")) {
                    sort = obj.get("sort");
                }
                //join操作
                if (obj.containsKey("join")) {
                    String join = obj.get("join");
                    fq = q;
                    q = "{!join fromIndex=" + subJoinCore + " from=profile_id to=rowkey}" +join;
                }
            }
            else {
                q = queryParams;
            }
        }

        //默认第一页
        if (page == null) {
            page = defaultPage;
        }
        //默认行数
        if (size == null) {
            size = defaultSize;
        }
        return hbase.queryBySolr(core, q, sort, fq, basicFl, dFl, page, size);
    }

    /**
     * 获取Hbase细表
     * queryParams可为solr表达式，也可为json例：{"table":"HDSD00_08","q":"*:*","join":"*:*","saas":"*","fl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}""}
     * 有join参数做join操作
     */
    public Page<Map<String,Object>> getEhrCenterSub(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.SubTable;
        String q = "";
        String fq = "";
        String basicFl = "";
        String dFl = "";
        String sort = "";
        if (queryParams != null && queryParams.length() > 0) {
            if (queryParams.startsWith("{") && queryParams.endsWith("}")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if (obj.containsKey("q")) {
                    q = obj.get("q");
                }
                if (obj.containsKey("basicFl")) {
                    basicFl = obj.get("basicFl");
                }
                if (obj.containsKey("dFl")) {
                    dFl = obj.get("dFl");
                }
                if (obj.containsKey("sort")) {
                    sort = obj.get("sort");
                }

                //join操作
                if (obj.containsKey("join")) {
                    String join = obj.get("join");
                    fq = q;
                    q = "{!join fromIndex="+mainJoinCore+" from=rowkey to=profile_id}" +join;
                    if (obj.containsKey("table")) {
                        if (fq.length() > 0) {
                            fq = "("+fq+") AND rowkey:*" + obj.get("table") + "*";
                        } else {
                            fq = "rowkey:*" + obj.get("table") + "*";
                        }
                    }
                    if (obj.containsKey("saas")) {
                        if (fq.length() > 0) {
                            fq = "("+fq+") AND (" + obj.get("saas") + ")";
                        } else {
                            fq = obj.get("saas");
                        }
                    }
                } else if(obj.containsKey("table")){
                    if (q.length() > 0) {
                        q = "("+q+") AND rowkey:*" + obj.get("table") + "*";
                    } else {
                        q = "rowkey:*" + obj.get("table") + "*";
                    }
                }

            } else {
                q = queryParams;
            }
        }
        //默认第一页
        if (page == null) {
            page = defaultPage;
        }
        //默认行数
        if (size == null) {
            size = defaultSize;
        }
        return hbase.queryBySolr(core, q, sort, fq, basicFl, dFl, page, size);
    }

    /**
     * habse主表的solr分组统计
     * queryParams为json例：{"q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4","customGroup":[{"groupField":"lastUpdateTime","groupCondition":{"3Month":"last_update_time:[2016-02-16 TO *]","6Month":"last_update_time:[2015-11-10 TO *]"}}]}
     */
    public Page<Map<String,Object>> countEhrCenter(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.MasterTable;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> params = objectMapper.readValue(queryParams, Map.class);
        String q = "";
        String fq = "";
        String groupFields = "";
        String statsFields = "";
        List<SolrGroupEntity> customGroup = new ArrayList<>();
        if (params.containsKey("q")) {
            fq = params.get("q").toString();
            if (params.containsKey("saas") && !params.get("saas").equals("*")) {
                fq += " AND ("+params.get("saas")+")";
            }
        }
        else{
            if (params.containsKey("saas") && !params.get("saas").equals("*")) {
                fq = params.get("saas").toString();
            }
        }

        if (params.containsKey("groupFields")) {
            groupFields = params.get("groupFields").toString();
        }
        if (params.containsKey("statsFields")) {
            statsFields = params.get("statsFields").toString();
        }
        if (params.containsKey("customGroup")) {
            ArrayList listGroup = ((ArrayList) params.get("customGroup"));
            if (listGroup != null && listGroup.size() > 0) {
                for (int i = 0; i < listGroup.size(); i++) {
                    String groupField = ((LinkedHashMap) listGroup.get(i)).get("groupField").toString();
                    Map<String, String> groupCondition = (Map) ((LinkedHashMap) listGroup.get(i)).get("groupCondition");

                    customGroup.add(new SolrGroupEntity(groupField, groupCondition));
                }
            }
        }
        //join操作
        if (params.containsKey("join")) {
            String join = params.get("join").toString();
            q = "{!join fromIndex="+subJoinCore+" from=profile_id to=rowkey}" +join;
        }

        if (groupFields.length() == 0 && customGroup.size() == 0) {
            throw new Exception("缺少分组条件！");
        }
        //数值统计
        if (statsFields != null && statsFields.length() > 0) {
            return solr.getStats(core, groupFields, statsFields, q,fq, customGroup);
        }
        //总数统计
        else {
            if (customGroup.size() == 0) {
                //默认第一页
                if (page == null) {
                    page = defaultPage;
                }
                //默认行数
                if (size == null) {
                    size = defaultSize;
                }

                //多分组
                if (groupFields.contains(",")) {
                    return solr.getGroupMult(core, groupFields, q,fq, page, size);
                } else {
                    return solr.getGroupCount(core, groupFields, q,fq, page, size);
                }
            } else {
                return solr.getGroupMult(core, groupFields, customGroup, q,fq);
            }
        }

    }
    
    /**
     * habse细表的solr分组统计
     * queryParams为json例：{"table":"HDSD00_08","q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4",customGroup:""}
     */
    public Page<Map<String,Object>> countEhrCenterSub(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.SubTable;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> params = objectMapper.readValue(queryParams, Map.class);

        String q = "";
        String fq="";
        String groupFields = "";
        String statsFields = "";
        if (params.containsKey("q")) {
            fq = params.get("q");
        }
        if (params.containsKey("groupFields")) {
            groupFields = params.get("groupFields");
        } else {
            throw new Exception("缺少分组条件！");
        }
        if (params.containsKey("statsFields")) {
            statsFields = params.get("statsFields");
        }
        if (params.containsKey("table")) {
            if (fq.length() > 0) {
                fq += " AND rowkey:*" + params.get("table") + "*";
            } else {
                fq = "rowkey:*" + params.get("table") + "*";
            }
        }
        if (params.containsKey("saas")) {
            if (fq.length() > 0) {
                fq = "("+fq+") AND (" + params.get("saas") + ")";
            } else {
                fq = params.get("saas");
            }
        }
        //join操作
        if (params.containsKey("join")) {
            String join = params.get("join").toString();
            q = "{!join  fromIndex="+mainJoinCore+" from=rowkey to=profile_id}" +join;
        }

        //数值统计
        if (statsFields != null && statsFields.length() > 0) {
            return solr.getStats(core, groupFields, statsFields, q,fq);
        }
        //总数统计
        else {
            if (groupFields.contains(",")) //多分组
            {
                String[] groups = groupFields.split(",");
                return solr.getGroupMult(core, groupFields, null, q,fq); //自定义分组未完善
            } else { //单分组
                //默认第一页
                if (page == null) {
                    page = defaultPage;
                }
                //默认行数
                if (size == null) {
                    size = defaultSize;
                }
                return solr.getGroupCount(core, groupFields, q, fq, page, size);
            }
        }

    }

    /**
     * 获取Mysql配置库数据
     */
    public Page<Map<String,Object>> getMysqlData(String queryParams, Integer page, Integer size) throws Exception {
        String sql = queryParams;

        //判定是否完整sql语句
        if (sql.toLowerCase().indexOf(" from ") <= 0) {
            sql = "select * from " + queryParams;
        }

        //查询总条数
        ParserSql parser = ParserFactory.getParserSql();
        String sqlCount = parser.getCountSql(sql);
        long count = jdbcTemplate.queryForObject(sqlCount,Long.class);

        //默认第一页
        if (page == null) {
            page = defaultPage;
        }
        //默认行数
        if (size == null) {
            size = defaultSize;
        }

        //分页查询
        List<Map<String,Object>> list = new ArrayList<>();
        if(count>size)
        {
            String sqlList = parser.getPageSql(sql, page, size);
            list = jdbcTemplate.queryForList(sqlList);
        }
        else{
            list = jdbcTemplate.queryForList(sql);
        }
        return new PageImpl<Map<String, Object>>(list,new PageRequest(page-1, size),count);
    }

    /**
     * 获取非结构化档案
     * @return
     */
    public Page<Map<String,Object>> getRawFiles(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.FileTable;
        String q = "";
        String fq = "";
        String sort = "";
        if (queryParams != null && queryParams.length() > 0) {
            if (queryParams.startsWith("{") && queryParams.endsWith("}")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if (obj.containsKey("q")) {
                    q = obj.get("q");
                }
                if (obj.containsKey("sort")) {
                    sort = obj.get("sort");
                }

                //join操作
                if (obj.containsKey("join")) {
                    String join = obj.get("join");
                    fq = q;
                    q = "{!join fromIndex=" + mainJoinCore + " from=rowkey to=profile_id}" + join;
                }

            } else {
                q = queryParams;
            }
        }
        //默认第一页
        if (page == null) {
            page = defaultPage;
        }
        //默认行数
        if (size == null) {
            size = defaultSize;
        }
        return hbase.queryBySolr(core, q, sort, fq, page, size);
    }

}
