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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 糖尿病单病种 分析 数据统计
 */
@Component
public class DiabetesScheduler {

	private static final Logger log = LoggerFactory.getLogger(DiabetesScheduler.class);

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
		String ageSql = "SELECT t.`code` ,t.`value` as name  from system_dict_entries t  WHERE dict_id = 89";
		String q = "EHR_000112:*糖尿病*"; // 查询条件
		String fq = null; // 过滤条件
		String dictSql = "";


		//找出糖尿病的就诊档案
		long count = solrUtil.count(ResourceCore.SubTable, q,fq);
		List<String> rowKeyList = selectSubRowKey(ResourceCore.SubTable, q, fq, count);

		List<String> orgAreaList = new ArrayList<>();
		String keyOrgArea = "org_area";
		String keyOrgName = "org_name";
		List<String> sexList = new ArrayList<>();
		String keySex = "EHR_000019";
		String keySexValue = "EHR_000019_VALUE";
		String keyAge = "EHR_000007";
		List<String> ageList = new ArrayList<>();
		String keyAddress = "EHR_001211";
		String keyDiseaseType = "EHR_003810";
		String keyDiseaseSymptom = "EHR_000112";
		List<String> diseaseSymptomList = new ArrayList<>();
		String keyFastingBloodGlucose = "EHR_002724";
		List<String> fastingBloodGlucoseList = new ArrayList<>();

		String keyWestMedicine= "EHR_000100";
		String keyChineseMedicine= "EHR_000131 ";
		List<String> medicineList = new ArrayList<>();



		for(String rowKey:rowKeyList){
			List<Map<String,Object>> hbaseDataList = selectHbaseData(rowKeyList);
			if( hbaseDataList != null && hbaseDataList.size() > 0 ){
				for(Map<String,Object> map : hbaseDataList){
					//指标1 各个区县患病数量
					if(map.get(keyOrgArea) != null){
						orgAreaList.add(map.get(keyOrgArea).toString() + "-" + map.get(keyOrgName).toString());
					}
					//指标2 性别患病数量
					if(map.get(keySex) != null){
						sexList.add(map.get(keySex).toString() + "-" + map.get(keySexValue).toString());
					}
					//指标3 年龄段患病数量 "EHR_000007": "1930-03-20T00:00:00Z",
					if(map.get(keyAge) != null){
						String birthDate = map.get(keyAge).toString().substring(0,9);
						int  age = getAge(parse(birthDate));
						Map<String, String> ageDicMap = getdimensionDicMap(ageSql);
						for(String key : ageDicMap.keySet()){
							if(ageDicMap.get(key).contains("-")){
								int f = Integer.valueOf(ageDicMap.get(key).split("-")[0]);
								int h = Integer.valueOf(ageDicMap.get(key).split("-")[0]);
								if(age > f && age < h ){
									ageList.add(key + "-" + ageDicMap.get(key));
								}
							}
						}
						for(String key : ageDicMap.keySet()) {
							int n = Integer.valueOf(ageDicMap.get(key).substring(ageDicMap.get(key).indexOf(">")));
							if (ageDicMap.get(key).contains(">")) {
								if(age > n ){
									diseaseSymptomList.add(key + "-" + ageDicMap.get(key));
								}
							}
						}
					}
					//指标4 患病拌并发症
					if(map.get(keyDiseaseSymptom) != null && keyDiseaseSymptom.contains("并发症")){
						orgAreaList.add(map.get(keyDiseaseSymptom).toString() + "-" + map.get(keyDiseaseSymptom).toString());
					}
					//指标5 空腹血糖
					if(map.get(keyFastingBloodGlucose) != null ){
						int val = Integer.valueOf(map.get(keyFastingBloodGlucose).toString());
						fastingBloodGlucoseList.add(val + "-" + val);
					}
					//指标6 用药人数
					if(map.get(keyWestMedicine) != null || map.get(keyChineseMedicine) != null ){
						String westVal = map.get(keyWestMedicine).toString();
						medicineList.add(westVal + "-" + westVal);
						String chineseVal = map.get(keyChineseMedicine).toString();
						medicineList.add(chineseVal + "-" + chineseVal);
					}


				}
			}
		}


		//新增患病数-各区县  HC_05_0001

		//第一步 统计糖尿病的具体数据
		//EHR_001225  地址

		//EHR_003810 诊断编码 患病类型
		String groupField = "EHR_003810";
		Map<String, Long> map = solrUtil.groupCount(ResourceCore.MasterTable, q, fq, groupField, 0, -1);
		Map<String, String> dimensionDicMap = getdimensionDicMap(diseaseTypeSql);

	}

	//获取维度的字典项
	private Map<String, String> getdimensionDicMap(String dictSql){
		BasesicUtil baseUtil = new BasesicUtil();
		Map<String, String> dimensionDicMap = new HashMap<>();
		List<DictModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(DictModel.class));
		for (DictModel dictModel : dictDatas) {
			String name = baseUtil.getFieldValueByName("name", dictModel);
			String val = baseUtil.getFieldValueByName("code", dictModel).toLowerCase();
			dimensionDicMap.put(val, name);
		}
		return  dimensionDicMap;
	}

	//获取查询结果中的rowKey
	private List<String> selectSubRowKey(String core ,String q,String fq,long count) throws Exception {
		List<String> data = new ArrayList<>();
		/***** Solr查询 ********/
		SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 1,count);
		if(solrList!=null && solrList.getNumFound()>0){
			for (SolrDocument doc : solrList){
				String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
				//49236052X_17021811_1502640127000$HDSD00_75$75
				data.add(rowkey.substring(0,rowkey.indexOf("$")));
			}
		}
		return  data;
	}

	//统计不同维度的数量
	private Map<String,Integer> keyCountList(List<String> dataList){
		Map<String, Integer> map = new HashMap<String, Integer>();
		Set<String> set = new HashSet<String>(dataList);
		for (String str : set) {
			for (String lstr : dataList) {
				if (str.equals(lstr)) {
					if (map.containsKey(str)) {
						Integer count = map.get(str);
						count++;
						map.put(str, count);
					} else {
						map.put(str, 1);
					}
				}
			}
		}
		return  map;
	}

	//查询habase里面数据
	private List<Map<String,Object>> selectHbaseData(List<String> list) throws Exception {
		List<Map<String,Object>> data = new ArrayList<>();
		/***** Hbase查询 ********/
		Result[] resultList = hbaseDao.getResultList(ResourceCore.MasterTable,list, "", ""); //hbase结果集
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

	//出生日期字符串转化成Date对象
	public  Date parse(String strDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(strDate);
	}

	//由出生日期获得年龄
	public  int getAge(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();
		if (cal.before(birthDay)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) age--;
			}else{
				age--;
			}
		}
		return age;
	}


}
