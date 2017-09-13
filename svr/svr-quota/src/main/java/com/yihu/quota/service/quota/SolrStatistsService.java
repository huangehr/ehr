package com.yihu.quota.service.quota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/7/7.
 */
@Service
public class SolrStatistsService {

    private String mainJoinCore = ResourceCore.MasterTable + "_shard1_replica1";
    private String subJoinCore = ResourceCore.SubTable + "_shard1_replica1";
    private static String core = "HealthProfile";
    private Integer defaultPage = 1;
    private Integer defaultSize = 1000;

    @Autowired
    SolrUtil solrUtil;
    @Autowired
    SolrQuery solrQuery;
    @Autowired
    HbaseQuery hbase;
    @Autowired
    ObjectMapper objectMapper;


    /**
     * 获取Hbase细表
     * queryParams可为solr表达式，也可为json例：{"table":"HDSD00_08","q":"*:*","join":"*:*","saas":"*","fl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}""}
     * 有join参数做join操作
     */
    public Page<Map<String,Object>> getEhrCenterSub(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.SubTable;
        String q = "";
        String fq = "";
        String fl = "";
        String sort = "";
        if (queryParams != null && queryParams.length() > 0) {
            if (queryParams.startsWith("{") && queryParams.endsWith("}")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if (obj.containsKey("q")) {
                    q = obj.get("q");
                }
                if (obj.containsKey("fl")) {
                    fl = obj.get("fl");
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
        return hbase.queryBySolr(core, q, sort, fq, "", fl, page, size);
    }

    /**
     * habse主表的solr分组统计
     * queryParams为json
     * 例：{"q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4","customGroup":[{"groupField":"lastUpdateTime","groupCondition":{"3Month":"last_update_time:[2016-02-16 TO *]","6Month":"last_update_time:[2015-11-10 TO *]"}}]}
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
            return solrQuery.getStats(core, groupFields, statsFields, q,fq, customGroup);
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
                    return solrQuery.getGroupMult(core, groupFields, q,fq, page, size);
                } else {
                    return solrQuery.getGroupCount(core, groupFields, q,fq, page, size);
                }
            } else {
                return solrQuery.getGroupMult(core, groupFields, customGroup, q,fq);
            }
        }

    }

}
