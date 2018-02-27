package com.yihu.quota.scheduler;

import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.DictModel;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 糖尿病单病种 分析 数据统计
 */
@Component
public class DiabetesScheduler {

	private static final Logger log = LoggerFactory.getLogger(DiabetesScheduler.class);
	private static final String core = "HealthProfile";
	private static final String coreSub = "HealthProfileSub";

	@Autowired
	SolrUtil solrUtil;
	@Autowired
	ExtractUtil extractUtil;
	@Autowired
	SolrQuery solrQuery;
	@Autowired
	HBaseDao hbaseDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 每天2点 执行一次
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 2 * * ?")
	public void validatorIdentityScheduler() throws Exception{
		List<Map<String, Object>> list = new ArrayList<>();
		String diseaseTypeSql = "SELECT * from  icd10_dict where  name  like '%糖尿病%'";
		String q = "EHR_000112:*糖尿病*"; // 查询条件
		String fq = null; // 过滤条件
		String dictSql = "";


		//第一步 找出糖尿病的就诊档案  患病数量
		long count = solrUtil.count(coreSub, q,fq);
		List<Map<String,Object>> dataList = selectData(coreSub, q, fq, count);
		//新增患病数-各区县  HC_05_0001

		//第一步 统计糖尿病的具体数据
		//EHR_001225  地址

		//EHR_003810 诊断编码 患病类型
		String groupField = "EHR_003810";
		Map<String, Long> map = solrUtil.groupCount(core, q, fq, groupField, 0, -1);
		dictSql = diseaseTypeSql;
		BasesicUtil baseUtil = new BasesicUtil();
		Map<String, String> dimensionDicMap = new HashMap<>();
		List<DictModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(DictModel.class));
		for (DictModel dictModel : dictDatas) {
			String name = baseUtil.getFieldValueByName("name", dictModel);
			String val = baseUtil.getFieldValueByName("code", dictModel).toLowerCase();
			dimensionDicMap.put(val, name);
		}

	}


	public List<Map<String,Object>> selectData(String core ,String q,String fq,long count) throws Exception {

		List<Map<String,Object>> data = new ArrayList<>();
		/***** Solr查询 ********/
		SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 1,count);

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
		Result[] resultList = hbaseDao.getResultList(ResourceCore.MasterTable,list, "", ""); //hbase结果集
		if(resultList!=null&&resultList.length>0){
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
