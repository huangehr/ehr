package com.yihu.ehr.solr;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Solr底层操作类
 *
 * @author hzp
 * @version 1.0
 * @created 2017.05.06
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SolrAdmin {
    @Autowired
    SolrPool pool;


    /************************* 基础操作 **************************************************/
    /**
     * 新建单条索引
     */
    public Boolean create(String core,Map<String, Object> map) throws Exception {
        SolrClient client = pool.getConnection(core);
        SolrInputDocument doc = new SolrInputDocument();
        //注意date的格式，要进行适当的转化
        for(String key:map.keySet())
        {
            doc.addField(key, map.get(key));
        }
        UpdateResponse re = client.add(doc);
        client.commit();

        pool.close(core); //释放连接

        if(re.getStatus()!=0) {
            System.out.print("create index cost " + re.getQTime());
            return true;
        }
        else{
            System.out.print("create index faild!");
            return false;
        }
    }

    /**
     * 修改单条索引单字段
     */
    public Boolean update(String core,String uniqueKey,String uniqueKeyValue,String key,Object value) throws Exception {
        Map<String,Object> map = new HashedMap();
        map.put(key,value);
        return update(core,uniqueKey+":"+uniqueKeyValue,map);
    }

    /**
     * 修改索引多字段
     */
    public Boolean update(String core,String keyQuery,Map<String, Object> map) throws Exception {
        SolrClient client = pool.getConnection(core);
        QueryResponse qr = client.query(new SolrQuery(keyQuery));
        SolrDocumentList docs = qr.getResults();

        if(docs!=null && docs.size()>0)
        {
            List<SolrInputDocument> solrList = new ArrayList<>();
            for(int i=0;i<docs.size();i++)
            {
                SolrDocument doc = docs.get(i);
                SolrInputDocument newItem = new SolrInputDocument();
                newItem.addField("rowkey",doc.get("rowkey"));
                for(String key :map.keySet())
                {
                    newItem.addField(key,map.get(key));
                }

                solrList.add(newItem);
            }

            UpdateResponse re = client.add(solrList);
            client.commit();
            pool.close(core); //释放连接

            if(re.getStatus()!=0) {
                System.out.print("update index cost " + re.getQTime());
                return true;
            }
            else{
                System.out.print("update index faild!");
                return false;
            }
        }
        else{
            System.out.print("Null result!");
        }

        return true;
    }


    /**
     * 删除单条索引
     */
    public Boolean delete(String core,String keyQuery) throws Exception {
        SolrClient client = pool.getConnection(core);
        UpdateResponse de = client.deleteByQuery(keyQuery);
        client.commit();
        pool.close(core); //释放连接

        if(de.getStatus()!=0) {
            System.out.print("delete index cost " + de.getQTime());
            return true;
        }
        else{
            System.out.print("delete index faild!");
            return false;
        }
    }
}
