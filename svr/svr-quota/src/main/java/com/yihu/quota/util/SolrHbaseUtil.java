package com.yihu.quota.util;

import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.vo.DictModel;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by janseny on 2018/5/16.
 */
@Component("solrHbaseUtil")
public class SolrHbaseUtil {

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private HBaseDao hbaseDao;

    //获取查询结果中的单个索引值列表 FieldValue
    public List<String> selectFieldValue(String core ,String q,String fq,long count,String field) throws Exception {
        List<String> data = new ArrayList<>();
        /***** Solr查询 ********/
        SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 0,count);
        if(solrList != null && solrList.getNumFound() > 0 ){
            for (SolrDocument doc : solrList){
                String fieldValue = String.valueOf(doc.getFieldValue(field));
                data.add(fieldValue);
            }
        }
        return  data;
    }

    //获取查询结果集
    public List<Map<String,Object>> selectFieldValueList(String core ,String q,String fq,long count) throws Exception {
        List<Map<String,Object>> data = new ArrayList<>();
        /***** Solr查询 ********/
        SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 0,count);
        if(solrList != null && solrList.getNumFound() > 0){
            for (SolrDocument doc : solrList){
                if(doc.getFieldValueMap() != null){
                    data.add(doc.getFieldValueMap());
                }
            }
        }
        return  data;
    }

    //查询habase里面数据
    public List<Map<String,Object>> selectHbaseData(String table, List<String> list) throws Exception {
        List<Map<String,Object>> data = new ArrayList<>();
        /***** Hbase查询 ********/
        Result[] resultList = hbaseDao.getResultList(table,list, "", ""); //hbase结果集
        if(resultList != null && resultList.length > 0){
            for (Result result :resultList) {
                Map<String,Object> obj = resultToMap(result, "");
                if(obj!=null) {
                    data.add(obj);
                }
            }
        }
        return  data;
    }

    //查询habase里面数据 单条
    private Map<String,Object> selectSingleHbaseData(String table, String rowKey) throws Exception {
        return hbaseDao.getResultMap(table,rowKey);
    }

    /**
     * Result 转 JSON
     * @return
     */
    private Map<String,Object> resultToMap(Result result,String fl){
        String rowkey = Bytes.toString(result.getRow());
        if(rowkey!=null && rowkey.length()>0){
            Map<String,Object> obj = new HashMap<>();
            obj.put("rowkey", rowkey);
            for  (Cell cell : result.rawCells()) {
                String fieldName = Bytes.toString(CellUtil.cloneQualifier(cell));
                String fieldValue = Bytes.toString(CellUtil. cloneValue(cell));
                if(fl!=null&&!fl.equals("")&&!fl.equals("*")){
                    String[] fileds = fl.split(",");
                    for(String filed : fileds){
                        if(filed.equals(fieldName)){
                            obj.put(fieldName, fieldValue);
                            continue;
                        }
                    }
                }else{
                    obj.put(fieldName, fieldValue);
                }
            }
            return obj;
        }else{
            return null;
        }
    }


}
