package com.yihu.quota.service.quota;

import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jansney
 */
@Service
public class HbaseService {
    @Autowired
    HBaseDao hbaseDao;
    @Autowired
    SolrUtil solrUtil;


    public void selectData(Integer id) throws Exception {

        List<Map<String,Object>> data = new ArrayList<>();
        /***** Hbase查询 ********/
//       int count = hbaseDao.count(ResourceCore.MasterTable);

        /***** Solr查询 ********/
        SolrDocumentList solrList = solrUtil.query("HealthProfile", null, null, null, 1,15);

        /***** Hbase查询 ********/
        List<String> list = new ArrayList<String>();
        if(solrList!=null && solrList.getNumFound()>0){
            long num = solrList.getNumFound();
            for (SolrDocument doc : solrList){
                String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
                list.add(rowkey);
            }
        }

        /***** Hbase查询 ********/
        Result[] resultList = hbaseDao.getResultList(ResourceCore.MasterTable,list); //hbase结果集
        if(resultList!=null&&resultList.length>0){
            for (Result result :resultList) {
                Map<String,Object> obj = resultToMap(result, "");
                if(obj!=null) {
                    data.add(obj);
                }
            }
        }

    }

    /**
     * Result 转 JSON
     * @return
     */
    private Map<String,Object> resultToMap(Result result,String fl){

        String rowkey = Bytes.toString(result.getRow());
        if(rowkey!=null && rowkey.length()>0)
        {

            Map<String,Object> obj = new HashMap<>();
            obj.put("rowkey", rowkey);
            for  (Cell cell : result.rawCells()) {
                String fieldName = Bytes.toString(CellUtil.cloneQualifier(cell));
                String fieldValue = Bytes.toString(CellUtil. cloneValue(cell));
                if(fl!=null&&!fl.equals("")&&!fl.equals("*"))
                {
                    String[] fileds = fl.split(",");
                    for(String filed : fileds){
                        if(filed.equals(fieldName))
                        {
                            obj.put(fieldName, fieldValue);
                            continue;
                        }
                    }
                }
                else
                {
                    obj.put(fieldName, fieldValue);
                }

            }
            return obj;
        }
        else
            return null;

    }


}
