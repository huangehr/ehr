package com.yihu.quota.service.singledisease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * 糖尿病单病种  监测检验 数据统计
 */
@Service
public class DiabetesCheckService {

	private static final Logger log = LoggerFactory.getLogger(DiabetesCheckService.class);

	@Autowired
	private SolrUtil solrUtil;
	@Autowired
	private ElasticSearchUtil elasticSearchUtil;
	@Autowired
	private HBaseDao hbaseDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ObjectMapper objectMapper;


	public void validatorIdentity(){
		try {
			String q2 = "EHR_000394:*糖耐量*2H血糖* OR EHR_000394:*糖耐量*空腹血糖* OR EHR_000394:*空腹葡萄糖* OR EHR_000394:*葡萄糖耐量试验*";//子项目中文名称
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
			String keyWestMedicine= "EHR_000100";  //西药
			String keyChineseMedicine= "EHR_000131";//中药

			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			BasesicUtil basesicUtil = new BasesicUtil();
			String initializeDate = "2018-04-20";// job初始化时间
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
				if(basesicUtil.compareDate("2018-05-01",startDate) != 1){//结束时间
					flag = false;
				}
				System.out.println("startDate=" + startDate);
				log.error("startDate=" + startDate);
				//找出糖尿病的就诊档案
				System.out.println("tangnai 开始查询 检验检测tangnai solr, fq = " + fq);
				List<String> subRrowKeyList = new ArrayList<>() ; //细表rowkey
				subRrowKeyList = selectSubRowKey(ResourceCore.SubTable, q2, fq, 10000);
				log.error("tangnai 检验检测查询结果条数 tangnai count ：" + subRrowKeyList.size());
				System.out.println("tangnai 检验检测查询结果条数 tangnai count ：" + subRrowKeyList.size());
				if(subRrowKeyList != null && subRrowKeyList.size() > 0){
					//糖尿病数据 Start
					for(String subRowkey:subRrowKeyList){//循环糖尿病 找到主表就诊人信息
						//查询此次就诊记录的相关数据 保存到检测记录中
						String name = "";
						String demographicId = "";
						String cardId = "";
						Integer sex = 0;
						String sexName = "";
						String diseaseType = "";
						String diseaseTypeName = "";
						String birthday = "";
						int birthYear = 0;
						Date eventDate = null;
						String subEventDate = "";
						Map<String,Object> subMap = hbaseDao.getResultMap(ResourceCore.SubTable, subRowkey);
						if(subMap !=null){
							if(subMap.get(keyEventDate) != null){
								subEventDate = subMap.get(keyEventDate).toString();
							}
							String diseaseName = "";
							if(subMap.get(keyDiseaseSymptom) != null){
								diseaseName = subMap.get(keyDiseaseSymptom).toString();
							}else if(subMap.get(keyDiseaseSymptom2) != null ){
								diseaseName = subMap.get(keyDiseaseSymptom2).toString();
							}
							if(StringUtils.isNotEmpty(diseaseName)){
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
						String mainRowkey = subRowkey.substring(0, subRowkey.indexOf("$"));
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
										if(sex == 1){
											sexName ="男";
										}else  if(sex == 2){
											sexName ="女";
										}else {
											if(map.get(keySexValue) != null){
												sexName = map.get(keySexValue).toString();
											}else {
												sexName ="未知";
											}
										}
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

						CheckInfoModel baseCheckInfo = new CheckInfoModel();
						String mapContent = objectMapper.writeValueAsString(map);
						baseCheckInfo.setRowKey(mainRowkey + "=subEventDate" + subEventDate +"【" + mapContent +"】");
						baseCheckInfo.setName(name);
						baseCheckInfo.setDemographicId(demographicId);
						baseCheckInfo.setCardId(cardId);
						baseCheckInfo.setSex(sex);
						baseCheckInfo.setSexName(sexName);
						baseCheckInfo.setBirthday(birthday);
						baseCheckInfo.setBirthYear(birthYear);
						baseCheckInfo.setDiseaseType(diseaseType);
						baseCheckInfo.setDiseaseTypeName(diseaseTypeName);
						baseCheckInfo.setEventDate(eventDate);
						Map<String,Object> submap = hbaseDao.getResultMap(ResourceCore.SubTable, subRowkey);
						if(submap !=null){
							//检查信息 姓名,身份证，就诊卡号,并发症，空腹血糖值，葡萄糖耐量值，用药名称，检查信息code （CH001 并发症,CH002 空腹血糖,CH003 葡萄糖耐量,CH004 用药名称）
							boolean fast = false;
							if(submap.get(keyChineseName) != null){
								// "糖耐量(空腹血糖)" "葡萄糖耐量试验"
								String val = submap.get(keyChineseName).toString();
								fast = (val.contains("糖耐量") && val.contains("空腹血糖"))||val.equals("葡萄糖耐量试验")||val.equals("空腹葡萄糖")  ;
							}
							if(fast){
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
//								CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
								baseCheckInfo.setCreateTime(DateUtils.addHours(new Date(),8));
								baseCheckInfo.setFastingBloodGlucoseName(fastname);
								baseCheckInfo.setFastingBloodGlucoseCode(fastcode);
								baseCheckInfo.setCheckCode("CH002");
								//保存到ES库
								saveCheckInfo(baseCheckInfo);
							}
							boolean tolerance = false;
							if(submap.get(keyChineseName) != null){
								String val = submap.get(keyChineseName).toString();
								//	"糖耐量(2H血糖)";
								tolerance = val.contains("糖耐量") && val.contains("2H血糖") ;
							}
							//葡萄糖（口服75 g葡萄糖后2 h)
							if(tolerance){
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
//								CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
								baseCheckInfo.setCreateTime(DateUtils.addHours(new Date(),8));
								baseCheckInfo.setSugarToleranceName(sugarTolename);
								baseCheckInfo.setSugarToleranceCode(sugarToleCode);
								baseCheckInfo.setCheckCode("CH003");
								//保存到ES库
								saveCheckInfo(baseCheckInfo);
							}
						}
					}
					//糖尿病数据 检查数据 end
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
					elasticSearchUtil.index(index,type, source);
				}
			}else if(checkInfo.getCheckCode().equals("CH001") && checkInfo.getCardId() != null){
				List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "cardId",checkInfo.getCardId());
				if( !(relist != null && relist.size() >0)){
					elasticSearchUtil.index(index,type, source);
				}
			}else {
				elasticSearchUtil.index(index,type, source);
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
		SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 0,count);
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
