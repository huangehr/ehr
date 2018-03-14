package com.yihu.quota.service.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.redis.schema.HealthArchiveSchema;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.util.ElasticsearchUtil;
import com.yihu.quota.vo.HealthArchiveInfoModel;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by wxw on 2018/3/14.
 */
@Service
public class HealthArchiveSchedulerService {

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ElasticSearchClient elasticSearchClient;
    @Autowired
    private HBaseDao hbaseDao;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private HealthArchiveSchema healthArchiveSchema;

    /**
     * ES保存
     * @param index
     * @param type
     * @param archiveInfo
     * @throws Exception
     */
    public void saveHealthArchiveInfo(String index, String type, HealthArchiveInfoModel archiveInfo) throws Exception {
        Map<String, Object> source = new HashMap<>();
        String jsonPer = objectMapper.writeValueAsString(archiveInfo);
        source = objectMapper.readValue(jsonPer, Map.class);
        if(null != archiveInfo.getDemographicId()) {
            List<Map<String, Object>> list = elasticSearchUtil.findByField(index, type, "demographicId", archiveInfo.getDemographicId());
            if(list == null || list.size() == 0) {
                if (!healthArchiveSchema.hasKey(archiveInfo.getDemographicId())) {
                    healthArchiveSchema.set(archiveInfo.getDemographicId(), archiveInfo.getDemographicId(), 86400);
                    elasticSearchClient.index(index, type, source);
                }
            }
        }else if(null != archiveInfo.getCardId()) {
            List<Map<String, Object>> list = elasticSearchUtil.findByField(index, type, "cardId",archiveInfo.getCardId());
            if(list == null || list.size() == 0) {
                if (!healthArchiveSchema.hasKey(archiveInfo.getCardId())) {
                    healthArchiveSchema.set(archiveInfo.getCardId(), archiveInfo.getCardId(), 86400);
                    elasticSearchClient.index(index, type, source);
                }
            }
        }else {
            elasticSearchClient.index(index, type, source);
        }
    }

    /**
     * 计算年龄
     * @param birthInfo
     * @return
     */
    public int getAgeByBirth(String birthInfo) {
        Date birthday = DateUtil.strToDate(birthInfo);
        int age = 0;
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());// 当前时间

        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);

        if (birth.after(now)) {
            // 如果传入的时间，在当前时间的后面，返回0岁
            age = 0;
        } else {
            age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            int system = now.get(Calendar.DAY_OF_YEAR);
            int user = birth.get(Calendar.DAY_OF_YEAR);
            if (system < user) {
                age -= 1;
            }
        }
        return age;
    }

    /**
     * 按年龄返回所在年龄段编码
     * @param age
     * @return
     */
    public int exchangeCodeByAge(int age) {
        int code = 1;
        if (age >= 7 && age < 18) {
            code = 2;
        } else if (age >= 18 && age < 41) {
            code = 3;
        } else if (age >= 41 && age < 66) {
            code = 4;
        } else if (age >= 66) {
            code = 5;
        }
        return code;
    }

    /**
     * 按年龄返回所在年龄段
     * @param age
     * @return
     */
    public String exchangeNameByAge(int age) {
        String name = "0-6岁";
        if (age >= 7 && age < 18) {
            name = "7-17岁";
        } else if (age >= 18 && age < 41) {
            name = "18-40岁";
        } else if (age >= 41 && age < 66) {
            name = "41-65岁";
        } else if (age >= 66) {
            name = "65岁以上";
        }
        return name;
    }

    // 获取查询结果中的rowKey
    public List<String> selectSubRowKey(String core , String q, String fq, long count) throws Exception {
        List<String> data = new ArrayList<>();
        /***** Solr查询 ********/
        SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 1,count);
        if(solrList!=null && solrList.getNumFound()>0){
            for (SolrDocument doc : solrList){
                String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
                data.add(rowkey);
            }
        }
        return  data;
    }

    //查询habase里面数据
    public List<Map<String,Object>> selectHbaseData(List<String> list) throws Exception {
        List<Map<String,Object>> data = new ArrayList<>();
        /***** Hbase查询 ********/
        Result[] resultList = hbaseDao.getResultList(ResourceCore.MasterTable,list, "", ""); // hbase结果集
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

    /**
     * Result 转 JSON
     * @return
     */
    public Map<String,Object> resultToMap(Result result,String fl){
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
