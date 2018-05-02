package com.yihu.quota.service.singledisease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.CheckInfoModel;
import com.yihu.quota.vo.DictModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 糖尿病单病种  药 数据统计
 */
@Service
public class DiabetesMedicineService {

	private static final Logger log = LoggerFactory.getLogger(DiabetesMedicineService.class);

	@Autowired
	private SolrUtil solrUtil;
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


	public void validatorIdentity(){
		try {
//			String q =  null; // 查询条件 health_problem:HP0047  HP0047 为糖尿病
			String q2 = "EHR_000295:*糖尿病* OR EHR_000112:*糖尿病*"; //门诊和住院 诊断名称
			String fq = ""; // 过滤条件
			String keyEventDate = "event_date";
			String keyArea = "EHR_001225";
			String keyAreaName = "EHR_001225_VALUE";
			String keyPatientName = "patient_name";
			String keyDemographicId = "demographic_id";//身份证
			String keyCardId = "card_id	";
			String keySex = "EHR_000019";//性别
			String keySexValue = "EHR_000019_VALUE";
			String keyAge = "EHR_000007";//出生日期 年龄
			String keyAddress = "EHR_001211"; //地址
			String keyDiseaseSymptom = "EHR_000112";//并发症  诊断名称(门诊)
			String keyDiseaseSymptom2 = "EHR_000295";//并发症  诊断名称（住院）
			String keysugarToleranceName = "EHR_000392";//  检验-项目结果 - 报告子项的LOINC编码  14995-5 糖耐量值  14771-0 空腹血糖
			String keysugarToleranceVal = "EHR_000387";//检验-项目结果 -  结果值  糖耐量值
			String keyChineseName = "EHR_000394";//子项目中文名称
//			String keyEnglishName = "EHR_000393";//子项目英文名称
			String keyWestMedicine= "EHR_000100";  //西药
			String keyChineseMedicine= "EHR_000131";//中药

			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			BasesicUtil basesicUtil = new BasesicUtil();
			String initializeDate = "2018-04-10";// job初始化时间
			String executeInitDate = "2015-06-01";
			Date now = new Date();
			String nowDate = DateUtil.formatDate(now,DateUtil.DEFAULT_DATE_YMD_FORMAT);
			boolean flag = true;
			String startDate = "2015-01-01";
			String endDate = "2015-02-01";
			while(flag){
				fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T00:00:00Z]";
				Date sDate = DateUtils.addDays(DateUtil.parseDate(startDate, DateUtil.DEFAULT_DATE_YMD_FORMAT), 15);
				startDate = DateUtil.formatDate(sDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
				Date eDate = DateUtils.addDays(DateUtil.parseDate(startDate, DateUtil.DEFAULT_DATE_YMD_FORMAT), 15);
				endDate = DateUtil.formatDate(eDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
				if(basesicUtil.compareDate("2017-05-01",startDate) != 1){//结束时间
					flag = false;
				}
				System.out.println("medicine startDate=" + startDate);
				log.error("startDate=" + startDate);
				//找出糖尿病的就诊档案

				List<String> subRrowKeyList = new ArrayList<>() ; //细表rowkey
				subRrowKeyList = selectSubRowKey(ResourceCore.SubTable, q2, fq, 10000);
				System.out.println("medicine 药物开始查询medicine solr, fq = " + fq);
				System.out.println("medicine subRrowKeyList, size = " + subRrowKeyList.size());
				log.error("medicine subRrowKeyList, size = " + subRrowKeyList.size());
				if(subRrowKeyList != null && subRrowKeyList.size() > 0){
					//糖尿病数据 Start
					for(String subRowkey:subRrowKeyList){//循环糖尿病 找到主表就诊人信息
						String mainRowkey = subRowkey.substring(0, subRowkey.indexOf("$"));
						//查询此次就诊记录的相关数据 保存到检测记录中
						String name = "";
						String demographicId = "";
						String cardId = "";
						Integer sex = 0;
						String sexName = "";
						String symptomName = "";
						String diseaseType = "";
						String diseaseTypeName = "";
						String birthday = "";
						int birthYear = 0;
						Date eventDate = null;
						Map<String,Object> subMap = hbaseDao.getResultMap(ResourceCore.SubTable, subRowkey);
						if(subMap !=null){
							String diseaseName = "";
							if(subMap.get(keyDiseaseSymptom) != null){
								diseaseName = subMap.get(keyDiseaseSymptom).toString();
							}else if(subMap.get(keyDiseaseSymptom2) != null ){
								diseaseName = subMap.get(keyDiseaseSymptom2).toString();
							}
							if(StringUtils.isNotEmpty(diseaseName)){
								if(diseaseName.contains("并发症")){
									symptomName = diseaseName;
								}
								if(diseaseName.contains("1型")){
									diseaseType = "1";
									diseaseTypeName = "I型糖尿病";
								}else if(diseaseName.contains("2型")){
									diseaseType = "2";
									diseaseTypeName = "II型糖尿病";
								}else if(diseaseName.contains("妊娠")){
									diseaseType = "3";
									diseaseTypeName = "妊娠糖尿病";
								}else{
									diseaseType = "4";
									diseaseTypeName = "其他糖尿病";
								}
							}
						}

							Map<String,Object> map = hbaseDao.getResultMap(ResourceCore.MasterTable, mainRowkey);
							if(map !=null){
								if(map.get(keyEventDate) != null){
									eventDate = DateUtil.formatCharDate(map.get(keyEventDate).toString(), DateUtil.DATE_WORLD_FORMAT);
									eventDate = DateUtils.addHours(eventDate,8);
								}
								if(map.get(keyAge) != null){
									birthday= map.get(keyAge).toString().substring(0, 10);
									birthYear = Integer.valueOf(map.get(keyAge).toString().substring(0, 4));
								}else {
									birthday = "birthday无数据";
								}
								if(map.get(keyDemographicId) != null){
									demographicId = map.get(keyDemographicId).toString();
								}else {
									demographicId = "demographicId无数据";
								}
								if(map.get(keyCardId) != null){
									cardId = map.get(keyCardId).toString();
								}else {
									cardId = "cardId无数据";
								}
								if(map.get(keySex) != null) {
									if(StringUtils.isNotEmpty(map.get(keySex).toString())){
										if(map.get(keySex).toString().contains("男")){
											sex =1;
											sexName ="男";
										}else if(map.get(keySex).toString().contains("女")){
											sex =2;
											sexName ="女";
										}else {
											sex = Integer.valueOf(map.get(keySex).toString());
											sexName = map.get(keySexValue).toString();
										}
									}else {
										sex =0;
										sexName ="未知";
									}
								}else {
									sex =0;
									sexName ="未知";
								}
								if(map.get(keyPatientName) != null){
									name = map.get(keyPatientName).toString();
								}else {
									name = "name无数据";
								}
							}

							fq = "profile_id:"+ mainRowkey +"* AND EHR_000131:*";
							//查询主表对应的细表的数据 循环解析
							List<String> subRrowKeyList2 = selectSubRowKey(ResourceCore.SubTable, null, fq, 10000);
							System.out.println("meidcine 药物 查询结果条数："+subRrowKeyList2.size());
							//细表解析保存 start
							if(subRrowKeyList2 !=null && subRrowKeyList2.size() > 0){
								List<Map<String,Object>> subhbaseDataList = selectHbaseData(ResourceCore.SubTable, subRrowKeyList2);
								if( subhbaseDataList != null && subhbaseDataList.size() > 0 ){
									for(Map<String,Object> submap : subhbaseDataList){
										CheckInfoModel baseCheckInfo = new CheckInfoModel();
										baseCheckInfo.setName(name);
										baseCheckInfo.setDemographicId(demographicId);
										baseCheckInfo.setCardId(cardId);
										baseCheckInfo.setSex(sex);
										baseCheckInfo.setSexName(sexName);
										baseCheckInfo.setBirthday(birthday);
										baseCheckInfo.setBirthYear(birthYear);
										baseCheckInfo.setDiseaseType(diseaseType);
										baseCheckInfo.setDiseaseTypeName(diseaseTypeName);
										baseCheckInfo.setSymptomName(symptomName);
										baseCheckInfo.setEventDate(eventDate);
										if(submap.get(keyWestMedicine) != null){
//											CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
											baseCheckInfo.setCreateTime(DateUtils.addHours(new Date(),8));
											baseCheckInfo.setCheckCode("CH004");
											baseCheckInfo.setMedicineName(submap.get(keyWestMedicine).toString());
											//保存到ES库
											saveCheckInfo(baseCheckInfo);
										}
										if(submap.get(keyChineseMedicine) != null) {
											baseCheckInfo.setCreateTime(DateUtils.addHours(new Date(),8));
											baseCheckInfo.setCheckCode("CH004");
											baseCheckInfo.setMedicineName(submap.get(keyChineseMedicine).toString());
											//保存到ES库
											saveCheckInfo(baseCheckInfo);
										}
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

	public void saveCheckInfo(CheckInfoModel checkInfo){
		try{
			String index = "singleDiseaseCheck";
			String type = "check_info";
			Map<String, Object> source = new HashMap<>();
			String jsonCheck = objectMapper.writeValueAsString(checkInfo);
			source = objectMapper.readValue(jsonCheck,Map.class);
			if(checkInfo.getCheckCode().equals("CH001") && checkInfo.getDemographicId() != null){
				List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "demographicId",checkInfo.getDemographicId());
				if( !(relist != null && relist.size() >0)){
					elasticSearchClient.index(index,type, source);
				}
			}else if(checkInfo.getCheckCode().equals("CH001") && checkInfo.getCardId() != null){
				List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "cardId",checkInfo.getCardId());
				if( !(relist != null && relist.size() >0)){
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
		checkInfo.setEventDate(baseCheckInfo.getEventDate());
		checkInfo.setDiseaseTypeName(baseCheckInfo.getDiseaseTypeName());
		checkInfo.setDiseaseType(baseCheckInfo.getDiseaseType());
		checkInfo.setBirthday(baseCheckInfo.getBirthday());
		checkInfo.setBirthYear(baseCheckInfo.getBirthYear());
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
