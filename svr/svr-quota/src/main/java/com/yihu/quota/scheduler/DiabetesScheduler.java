package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.apache.commons.lang.StringUtils;
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
	@Scheduled(cron = "0 01 02 * * ?")
	public void validatorIdentityScheduler(){

		try {
//			String q =  null; // 查询条件 health_problem:HP0047  HP0047 为糖尿病
			String q2 = "EHR_000295:*糖尿病* OR EHR_000112:*糖尿病*";
			String keyDiseaseNameH = "EHR_000295";//诊断名字（住院） *糖尿病*
			String keyDiseaseNameZ = "EHR_000112";//诊断名字（门诊）*糖尿病*
			String fq = ""; // 过滤条件
			String keyEventDate = "event_date";
			String keyArea = "EHR_001225";
			String keyAreaName = "EHR_001225_VALUE";
			String keyPatientName = "patient_name";
			String keyDemographicId = "demographic_id";//身份证
			String keyCardId = "card_id	";
			String keyHealthProblem = "health_problem";
			String keySex = "EHR_000019";//性别
			String keySexValue = "EHR_000019_VALUE";
			String keyAge = "EHR_000007";//出生日期 年龄
			String keyAddress = "EHR_001211"; //地址
			String keyDiseaseType = "EHR_003810";//EHR_003810 诊断代码
			String keyDiseaseSymptom = "EHR_000112";//并发症  诊断名称(门诊)
			String keyDiseaseSymptom2 = "EHR_000295";//并发症  诊断名称（住院）
			String keysugarToleranceName = "EHR_000392";//  检验-项目结果 - 报告子项的LOINC编码  14995-5 糖耐量值  14771-0 空腹血糖
			String keysugarToleranceVal = "EHR_000387";//检验-项目结果 -  结果值  糖耐量值
			String keyWestMedicine= "EHR_000100";  //西药
			String keyChineseMedicine= "EHR_000131";//中药
			List<PersonalInfoModel> personalInfoList = new ArrayList<>();
			List<CheckInfoModel> checkInfoList = new ArrayList<>();
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			BasesicUtil basesicUtil = new BasesicUtil();
			String initializeDate = "2018-03-13";
			Date now = new Date();
			String nowDate = DateUtil.formatDate(now,DateUtil.DEFAULT_DATE_YMD_FORMAT);
			boolean flag = true;
			String startDate = "2015-01-01";
			String endDate = "2015-02-01";
			while(flag){
				//  当前时间大于初始化时间，就所有数据初始化，每个月递增查询，当前时间小于于初始时间每天抽取
				if(basesicUtil.compareDate(initializeDate,nowDate) == -1){
					Date yesterdayDate = DateUtils.addDays(now,-1);
					String yesterday = DateUtil.formatDate(yesterdayDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
					fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
					flag = false;
				}else{
					fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T00:00:00Z]";
					Date sDate = DateUtils.addMonths(DateUtil.parseDate(startDate,DateUtil.DEFAULT_DATE_YMD_FORMAT),1);
					startDate = DateUtil.formatDate(sDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
					Date eDate = DateUtils.addMonths(DateUtil.parseDate(startDate,DateUtil.DEFAULT_DATE_YMD_FORMAT),1);
					endDate = DateUtil.formatDate(eDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
					if(startDate.equals("2018-04-01")){
						flag = false;
					}
					System.out.println("startDate=" + startDate);
				}
				//找出糖尿病的就诊档案
				List<String> rowKeyList = new ArrayList<>() ;//子表rowkey
				List<String> subRrowKeyList = new ArrayList<>() ; //细表rowkey
				System.out.println("开始查询solr, fq = " + fq);
				subRrowKeyList = selectSubRowKey(ResourceCore.SubTable, q2, fq, 5000);
				System.out.println("查询结果条数："+subRrowKeyList.size());
				if(subRrowKeyList != null && subRrowKeyList.size() > 0){
					//糖尿病数据 Start
					for(String subRowkey:subRrowKeyList){//循环糖尿病 找到主表就诊人信息
						PersonalInfoModel personalInfo = new PersonalInfoModel();
						personalInfo.setCreateTime(DateUtils.addHours(new Date(),8));
						Map<String,Object> subMap = hbaseDao.getResultMap(ResourceCore.SubTable, subRowkey);
						String diseaseName = "";
						if(subMap.get(keyDiseaseSymptom) != null){
							diseaseName = subMap.get(keyDiseaseSymptom).toString();
						}else if(subMap.get(keyDiseaseSymptom2) != null ){
							diseaseName = subMap.get(keyDiseaseSymptom2).toString();
						}
						if(StringUtils.isNotEmpty(diseaseName)){
							if(diseaseName.contains("1型")){
								personalInfo.setDiseaseType("1");
								personalInfo.setDiseaseTypeName("I型糖尿病");
							}else if(diseaseName.contains("2型")){
								personalInfo.setDiseaseType("2");
								personalInfo.setDiseaseTypeName("II型糖尿病");
							}else if(diseaseName.contains("妊娠")){
								personalInfo.setDiseaseType("3");
								personalInfo.setDiseaseTypeName("妊娠糖尿病");
							}else{
								personalInfo.setDiseaseType("4");
								personalInfo.setDiseaseTypeName("其他糖尿病");
							}
						}
						String mainRowkey = subRowkey.substring(0, subRowkey.indexOf("$"));
						Map<String,Object> map = hbaseDao.getResultMap(ResourceCore.MasterTable, mainRowkey);
						//个人信息 > 姓名，身份证，就诊卡号，性别，出生日期，出生年份，区县，常住地址，常住地址经纬度，疾病名称，疾病code
						if(map.get(keyEventDate) != null){
							Date eventDate = DateUtil.formatCharDate(map.get(keyEventDate).toString(), DateUtil.DATE_WORLD_FORMAT);
							personalInfo.setEventDate(DateUtils.addHours(eventDate,8));					}
						if(map.get(keyArea) != null){
							personalInfo.setTown(map.get(keyArea).toString());
							personalInfo.setTownName(map.get(keyAreaName).toString());
						}
						if(map.get(keyPatientName) != null){
							personalInfo.setName(map.get(keyPatientName).toString());
						}
						if(map.get(keyDemographicId) != null){
							personalInfo.setDemographicId(map.get(keyDemographicId).toString());
						}
						if(map.get(keyCardId) != null){
							personalInfo.setCardId(map.get(keyCardId).toString());
						}
						if(map.get(keySex) != null){
							personalInfo.setSex(Integer.valueOf(map.get(keySex).toString()));
							if(map.get(keySex).toString().equals("0")){
								personalInfo.setSexName("未知");
							}else {
								personalInfo.setSexName("未知");
							}
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
						personalInfo.setDisease("HP0047");
						personalInfo.setDiseaseName("糖尿病");
						personalInfoList.add(personalInfo);
						//个人信息记录end
						savePersonal(personalInfo);

						//查询此次就诊记录的相关数据 保存到检测记录中
						String name = "";
						String demographicId = "";
						String cardId = "";
						Integer sex = 0;
						String sexName = "";
						if(map.get(keyDemographicId) != null){
							demographicId = map.get(keyDemographicId).toString();
						}
						if(map.get(keyCardId) != null){
							cardId = map.get(keyCardId).toString();
						}
						if(map.get(keySex) != null) {
							sex = Integer.valueOf(map.get(keySex).toString());
							sexName = map.get(keySexValue).toString();
						}
						if(map.get(keyPatientName) != null){
							name = map.get(keyPatientName).toString();
						}
						fq = "rowkey:"+ mainRowkey +"*";
						//查询主表对应的细表的数据 循环解析
						List<String> subRrowKeyList2 = selectSubRowKey(ResourceCore.SubTable, null, fq, 10000);
						//细表解析保存 start
						if(subRrowKeyList2!=null && subRrowKeyList2.size() > 0){
							List<Map<String,Object>> subhbaseDataList = selectHbaseData(ResourceCore.SubTable, subRrowKeyList2);
							if( subhbaseDataList != null && subhbaseDataList.size() > 0 ){
								for(Map<String,Object> submap : subhbaseDataList){
									CheckInfoModel baseCheckInfo = new CheckInfoModel();
									baseCheckInfo.setName(name);
									baseCheckInfo.setDemographicId(demographicId);
									baseCheckInfo.setCardId(cardId);
									baseCheckInfo.setSex(sex);
									baseCheckInfo.setSexName(sexName);
									//检查信息 姓名,身份证，就诊卡号,并发症，空腹血糖值，葡萄糖耐量值，用药名称，检查信息code （CH001 并发症,CH002 空腹血糖,CH003 葡萄糖耐量,CH004 用药名称）
									if(submap.get(keyDiseaseSymptom) != null && submap.get(keyDiseaseSymptom).toString().contains("并发症")){
										CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
										checkInfo.setCheckCode("CH001");
										checkInfo.setSymptomName(submap.get(keyDiseaseSymptom).toString());
										checkInfoList.add(checkInfo);
									}
									if(submap.get(keyDiseaseSymptom2) != null && submap.get(keyDiseaseSymptom2).toString().contains("并发症")){
										CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
										checkInfo.setCheckCode("CH001");
										checkInfo.setSymptomName(submap.get(keyDiseaseSymptom2).toString());
										checkInfoList.add(checkInfo);
									}
									if(submap.get(keysugarToleranceName) != null && submap.get(keysugarToleranceName).equals("14771-0")){
										//7.8mmol/l 以下 2：7.8-11.1mmol/l  3:11.1 以上
										String fastname = "";
										String fastcode = "";
										double val = Double.valueOf(submap.get(keysugarToleranceVal).toString());
										if(val >= 4.4 && val < 6.1){
											fastname = "4.4~6.1mmol/L";
											fastcode = "1";
										}
										if(val >= 6.1 && val < 7.0){
											fastname = "6.1~7mmol/L";
											fastcode = "2";
										}
										if( val > 7.0){
											fastname = "7.0mmol/L以上";
											fastcode = "3";
										}
										CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
										checkInfo.setFastingBloodGlucoseName(fastname);
										checkInfo.setFastingBloodGlucoseCode(fastcode);
										checkInfo.setCheckCode("CH002");
										checkInfoList.add(checkInfo);
									}
									//葡萄糖（口服75 g葡萄糖后2 h)
									if(submap.get(keysugarToleranceName) != null && submap.get(keysugarToleranceName).equals("14995-5")){
										//7.8mmol/l 以下 2：7.8-11.1mmol/l  3:11.1 以上
										String sugarTolename = "";
										String sugarToleCode = "";
										double val = Double.valueOf(submap.get(keysugarToleranceVal).toString());
										if(val < 7.8){
											sugarTolename = "7.8 mmol/L以下";
											sugarToleCode = "1";
										}
										if(val >= 7.8 && val < 11.1){
											sugarTolename = "7.8~11.1 mmol/L";
											sugarToleCode = "2";
										}
										if( val > 11.1){
											sugarTolename = "11.1 mmol/L以上";
											sugarToleCode = "3";
										}
										CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
										checkInfo.setSugarToleranceName(sugarTolename);
										checkInfo.setSugarToleranceCode(sugarToleCode);
										checkInfo.setCheckCode("CH003");
										checkInfoList.add(checkInfo);
									}
									if(submap.get(keyWestMedicine) != null){
										CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
										checkInfo.setCheckCode("CH004");
										checkInfo.setMedicineName(submap.get(keyWestMedicine).toString());
										checkInfoList.add(checkInfo);
									}
									if(submap.get(keyChineseMedicine) != null) {
										CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
										checkInfo.setCheckCode("CH004");
										checkInfo.setMedicineName(submap.get(keyChineseMedicine).toString());
										checkInfoList.add(checkInfo);
									}
								}
								//保存到ES库
								//检查信息保存  并发症 去重处理，已保存的不在保存  身份证和就诊卡ID
								for(CheckInfoModel checkInfo : checkInfoList){
									saveCheckInfo(checkInfo);
								}
							}
						}
						//细表解析保存 end
					}
					//糖尿病数据 Start
				}

			}
		}catch (Exception e){
			e.getMessage();
		}
	}

	public void savePersonal(PersonalInfoModel personalInfo){
		try{
			String index = "singleDiseasePersonal";
			String type = "personal_info";
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
		}catch (Exception e){
			e.getMessage();
		}
	}

	public void saveCheckInfo(CheckInfoModel checkInfo){
		try{
			String index = "singleDiseaseCheck";
			String type = "check_info";
			Map<String, Object> source = new HashMap<>();
			String jsonCheck = objectMapper.writeValueAsString(checkInfo);
			source = objectMapper.readValue(jsonCheck,Map.class);
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
		}catch (Exception e){
			e.getMessage();
		}
	}

	public  CheckInfoModel setCheckInfoModel(CheckInfoModel baseCheckInfo ){
		CheckInfoModel checkInfo = new CheckInfoModel();
		checkInfo.setSexName(baseCheckInfo.getSexName());
		checkInfo.setSex(baseCheckInfo.getSex());
		checkInfo.setDemographicId(baseCheckInfo.getDemographicId());
		checkInfo.setCardId(baseCheckInfo.getCardId());
		checkInfo.setName(baseCheckInfo.getName());
		checkInfo.setCreateTime(DateUtils.addHours(new Date(),8));
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
	private List<Map<String,Object>> selectHbaseData(String table, List<String> list) throws Exception {
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
