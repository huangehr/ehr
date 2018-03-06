package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.util.LatitudeUtils;
import com.yihu.quota.vo.CheckInfoModel;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.PersonalInfoModel;
import com.yihu.quota.vo.SaveModel;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
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
	private SolrUtil solrUtil;
	@Autowired
	private ExtractUtil extractUtil;
	@Autowired
	private ElasticSearchUtil elasticSearchUtil;
	@Autowired
	private ElasticSearchClient elasticSearchClient;
	@Autowired
	private HBaseDao hbaseDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ObjectMapper objectMapper;


	/**
	 * 首先要有一个初始化过程
	 * 每天2点 执行一次
	 * @throws Exception
	 */
	@Scheduled(cron = "0 36 14 * * ?")
	public void validatorIdentityScheduler() throws Exception{

		String q =  "health_problem:HP0047"; // 查询条件  HP0047 为糖尿病
		String fq = ""; // 过滤条件
		String dictSql = "";
		String keyEventDate = "event_date";
		String keyOrgArea = "org_area";
		String keyOrgName = "org_name";
		String keyPatientName = "patient_name";
		String keyDemographicId = "demographic_id";//身份证
		String keyCardId = "card_id	";
		String keyHealthProblem = "health_problem	";
		String keySex = "EHR_000019";
		String keySexValue = "EHR_000019_VALUE";
		String keyAge = "EHR_000007";
		String keyAddress = "EHR_001211"; //地址
		String keyDiseaseType = "EHR_003810";//疾病类型
		String keyDiseaseSymptom = "EHR_000112";//并发症
		String keyFastingBloodGlucose = "EHR_002724";//空腹血糖
		String keysugarToleranceName = "EHR_000392";//糖耐量检测名称
		String keysugarToleranceVal = "EHR_000387";//糖耐量值
		String keyWestMedicine= "EHR_000100";  //西药
		String keyChineseMedicine= "EHR_000131 ";//中药
		List<PersonalInfoModel> personalInfoList = new ArrayList<>();
		List<CheckInfoModel> checkInfoList = new ArrayList<>();

		BasesicUtil basesicUtil = new BasesicUtil();
		String initializeDate = "2018-03-09";
		Date now = new Date();
		String nowDate = DateUtil.formatDate(now,DateUtil.DEFAULT_DATE_YMD_FORMAT);
		if(basesicUtil.compareDate(initializeDate,nowDate) == -1){//  当前时间大于初始化时间，就所有数据初始化，当前时间小于于初始时间每天抽取
			Date yesterdayDate = DateUtils.addDays(now,-1);
			String yesterday = DateUtil.formatDate(yesterdayDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
			fq = "create_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
		}else{
			fq = "create_date:[* TO *]";
			fq = "create_date:[2018-01-30T00:00:00Z TO 2018-01-30T23:59:59Z]";

		}

//		//找出糖尿病的就诊档案
		long count = solrUtil.count(ResourceCore.MasterTable, q,fq);
		List<String> rowKeyList = selectSubRowKey(ResourceCore.MasterTable, q, fq, count);
		if(rowKeyList != null && rowKeyList.size() > 0){
			List<Map<String,Object>> hbaseDataList = selectHbaseData(rowKeyList);

//			Map<String,Object> testMap = new HashMap<>();
//			testMap.put(keyEventDate,"2017-11-20 12:12:10");
//			testMap.put(keyOrgArea,"10001");
//			testMap.put(keyOrgName,"婺源县");
//			testMap.put(keyPatientName,"花灯节");
//			testMap.put(keyDemographicId,"456048111111");
//			testMap.put(keyCardId,"5641245");
//			testMap.put(keySex,"1");
//			testMap.put(keySexValue,"男");
//			testMap.put(keyAge,"1990-02-21");
//			testMap.put(keyAddress,"厦门市湖里区海天路");
//			testMap.put(keyHealthProblem,"HP0047");
//
//			testMap.put(keyDiseaseSymptom,"心脏");
//			testMap.put(keyFastingBloodGlucose,"7.9");
//			testMap.put(keysugarToleranceVal,"5.2");
//			testMap.put(keysugarToleranceName,"糖耐量");
//			testMap.put(keyWestMedicine,"阿司匹林");
//			testMap.put(keyChineseMedicine,"车前草");
//			hbaseDataList.add(testMap);


			if( hbaseDataList != null && hbaseDataList.size() > 0 ){
				for(Map<String,Object> map : hbaseDataList){
					//个人信息 > 姓名，身份证，就诊卡号，性别，出生日期，出生年份，区县，常住地址，常住地址经纬度，疾病名称，疾病code
					PersonalInfoModel personalInfo = new PersonalInfoModel();
					CheckInfoModel baseCheckInfo = new CheckInfoModel();
					personalInfo.setCreateTime(new Date());
					if(map.get(keyEventDate) != null){
						personalInfo.setEventDate(DateUtil.formatCharDateYMDHMS(map.get(keyEventDate).toString()));					}
					if(map.get(keyOrgArea) != null){
						personalInfo.setTown(map.get(keyOrgArea).toString());
						personalInfo.setTownName(map.get(keyOrgName).toString());
					}
					if(map.get(keyPatientName) != null){
						personalInfo.setName(map.get(keyPatientName).toString());
						baseCheckInfo.setName(map.get(keyPatientName).toString());
					}
					if(map.get(keyDemographicId) != null){
						personalInfo.setDemographicId(map.get(keyDemographicId).toString());
						baseCheckInfo.setDemographicId(map.get(keyDemographicId).toString());
					}
					if(map.get(keyCardId) != null){
						personalInfo.setCardId(map.get(keyCardId).toString());
						baseCheckInfo.setCardId(map.get(keyCardId).toString());
					}
					if(map.get(keySex) != null){
						personalInfo.setSex(Integer.valueOf(map.get(keySex).toString()));
						personalInfo.setSexName(map.get(keySexValue).toString());
						baseCheckInfo.setSex(Integer.valueOf(map.get(keySex).toString()));
						baseCheckInfo.setSexName(map.get(keySexValue).toString());
					}
					if(map.get(keyAge) != null){
						personalInfo.setBirthday( map.get(keyAge).toString().substring(0, 10));
						personalInfo.setBirthYear(Integer.valueOf(map.get(keyAge).toString().substring(0, 4)));
					}
					if(map.get(keyAddress) != null){
						String address = map.get(keyAddress).toString();
						Map<String, String> json = LatitudeUtils.getGeocoderLatitude(address);
						personalInfo.setAddress(address);
						personalInfo.setAddressLngLat(json.get("lng")+";" + json.get("lat"));
					}
					if(map.get(keyHealthProblem) != null){
						String val = map.get(keyHealthProblem).toString();
						personalInfo.setDisease(val);
						//去查询疾病编码的疾病名称
						dictSql = "select code,name from health_problem_dict where code='" + val +"'";
						Map<String, String> dictMap = getdimensionDicMap(dictSql);
						if(dictMap != null && dictMap.size() >0){
							for(String key:dictMap.keySet()){
								personalInfo.setDiseaseName(key);
								personalInfo.setDisease(dictMap.get(key).toString());
								break;
							}
						}
					}
					personalInfoList.add(personalInfo);


					//检查信息 姓名,身份证，就诊卡号,并发症，空腹血糖值，葡萄糖耐量值，用药名称，检查信息code （CH001 并发症,CH002 空腹血糖,CH003 葡萄糖耐量,CH004 用药名称）
					if(map.get(keyDiseaseSymptom) != null){
						CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
						checkInfo.setCheckCode("CH001");
						checkInfo.setSymptomName(map.get(keyDiseaseSymptom).toString());
						checkInfoList.add(checkInfo);
					}
					if(map.get(keyFastingBloodGlucose) != null){
						//7.8mmol/l 以下 2：7.8-11.1mmol/l  3:11.1 以上
						String fastname = "";
						String fastcode = "";
						double val = Double.valueOf(map.get(keyFastingBloodGlucose).toString());
						if(val < 7.8){
							fastname = "7.8mmol/l 以下";
							fastcode = "1";
						}
						if(val >= 7.8 && val < 11.1){
							fastname = "7.8-11.1mmol/l";
							fastcode = "2";
						}
						if( val > 11.1){
							fastname = "11.1mmol/l 上";
							fastcode = "3";
						}
						CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
						checkInfo.setFastingBloodGlucoseName(fastname);
						checkInfo.setFastingBloodGlucoseCode(fastcode);
						checkInfo.setCheckCode("CH002");
						checkInfoList.add(checkInfo);
					}

					if(map.get(keyWestMedicine) != null){
						CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
						checkInfo.setCheckCode("CH003");
						checkInfo.setMedicineName(map.get(keyWestMedicine).toString());
						checkInfoList.add(checkInfo);
					}
					if(map.get(keyChineseMedicine) != null) {
						CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
						checkInfo.setCheckCode("CH003");
						checkInfo.setMedicineName(map.get(keyChineseMedicine).toString());
						checkInfoList.add(checkInfo);
					}
					//葡萄糖（口服75 g葡萄糖后2 h)
					if(map.get(keysugarToleranceName) != null && map.get(keysugarToleranceName).equals("14995-5")){
						//7.8mmol/l 以下 2：7.8-11.1mmol/l  3:11.1 以上
						String sugarTolename = "";
						String sugarToleCode = "";
						double val = Double.valueOf(map.get(keysugarToleranceVal).toString());
						if(val >= 4.4 && val < 6.1){
							sugarTolename = "4.4-6.1mmol/l";
							sugarToleCode = "1";
						}
						if(val >= 6.1 && val < 7.0){
							sugarTolename = "6.1-7.0mmol/l";
							sugarToleCode = "2";
						}
						if( val > 7.0){
							sugarTolename = "大于7.0mmol/l 上";
							sugarToleCode = "3";
						}
						CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
						checkInfo.setSugarToleranceName(sugarTolename);
						checkInfo.setSugarToleranceCode(sugarToleCode);
						checkInfo.setCheckCode("CH004");
						checkInfoList.add(checkInfo);
					}


				}
				objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
				//保存到ES库
				//个人信息保存 去重处理，已保存的不在保存  身份证和就诊卡ID
				String index = "";
				String type = "";
				for(PersonalInfoModel personalInfo : personalInfoList){
					index = "singleDiseasePersonal";
					type = "personal_info";
					Map<String, Object> source = new HashMap<>();
					String jsonPer = objectMapper.writeValueAsString(personalInfo);
					source = objectMapper.readValue(jsonPer, Map.class);
					if(personalInfo.getDemographicId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index, type, "demographicId", personalInfo.getDemographicId());
						if(relist== null || relist.size() ==0){
							elasticSearchClient.index(index,type, source);
						}
					}else if(personalInfo.getCardId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "cardId",personalInfo.getCardId());
						if(relist== null || relist.size() ==0){
							elasticSearchClient.index(index,type, source);
						}
					}else {
						elasticSearchClient.index(index,type, source);
					}
				}
				//检查信息保存  并发症 去重处理，已保存的不在保存  身份证和就诊卡ID
				for(CheckInfoModel checkInfo : checkInfoList){
					index = "singleDiseaseCheck";
					type = "check_info";
					Map<String, Object> source = new HashMap<>();
					String jsonCheck = objectMapper.writeValueAsString(checkInfo);
					source = objectMapper.readValue(jsonCheck,Map.class);
					elasticSearchClient.index(index,type, source);
					if(checkInfo.getCheckCode().equals("CH001") && checkInfo.getDemographicId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "demographicId",checkInfo.getDemographicId());
						if(relist== null || relist.size() ==0){
							elasticSearchClient.index(index,type, source);
						}
					}else if(checkInfo.getCheckCode().equals("CH001") && checkInfo.getCardId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "cardId",checkInfo.getCardId());
						if(relist== null || relist.size() ==0){
							elasticSearchClient.index(index,type, source);
						}
					}else {
						elasticSearchClient.index(index,type, source);
					}
				}

			}
		}

	}

	public  CheckInfoModel setCheckInfoModel(CheckInfoModel baseCheckInfo ){
		CheckInfoModel checkInfo = new CheckInfoModel();
		checkInfo.setSexName(baseCheckInfo.getSexName());
		checkInfo.setSex(baseCheckInfo.getSex());
		checkInfo.setDemographicId(baseCheckInfo.getDemographicId());
		checkInfo.setCardId(baseCheckInfo.getCardId());
		checkInfo.setName(baseCheckInfo.getName());
		checkInfo.setCreateTime(new Date());
		return  checkInfo;
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
				data.add(rowkey);
			}
		}
		return  data;
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


}
