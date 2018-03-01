package com.yihu.quota.scheduler;

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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
	private SolrQuery solrQuery;
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
	@Scheduled(cron = "0 0 2 * * ?")
	public void validatorIdentityScheduler() throws Exception{
		List<Map<String, Object>> list = new ArrayList<>();
		String q = "health_problem:HP0047"; // 查询条件
		String fq = null; // 过滤条件
		String dictSql = "";
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
		String keyWestMedicine= "EHR_000100";  //西药
		String keyChineseMedicine= "EHR_000131 ";//中药


		List<PersonalInfoModel> personalInfoList = new ArrayList<>();
		List<CheckInfoModel> checkInfoList = new ArrayList<>();


//		//找出糖尿病的就诊档案
		long count = solrUtil.count(ResourceCore.MasterTable, q,fq);
		List<String> rowKeyList = selectSubRowKey(ResourceCore.MasterTable, q, fq, count);

		//查询糖尿病的患者
		SolrDocumentList solrList = solrUtil.query(ResourceCore.MasterTable, q , fq, null, 1,10000000);
		if(solrList!=null && solrList.getNumFound()>0){}
		if(rowKeyList != null && rowKeyList.size() > 0){
			List<Map<String,Object>> hbaseDataList = selectHbaseData(rowKeyList);
			if( hbaseDataList != null && hbaseDataList.size() > 0 ){
				for(Map<String,Object> map : hbaseDataList){
					//个人信息 > 姓名，身份证，就诊卡号，性别，出生日期，出生年份，区县，常住地址，常住地址经纬度，疾病名称，疾病code
					PersonalInfoModel personalInfo = new PersonalInfoModel();
					CheckInfoModel checkInfo = new CheckInfoModel();
					personalInfo.setCreateTime(new Date());
					checkInfo.setCreateTime(new Date());
					if(map.get(keyOrgArea) != null){
						personalInfo.setTown(map.get(keyOrgArea).toString());
						personalInfo.setTownName(map.get(keyOrgName).toString());
					}
					if(map.get(keyPatientName) != null){
						personalInfo.setName(map.get(keyPatientName).toString());
						checkInfo.setName(map.get(keyPatientName).toString());
					}
					if(map.get(keyDemographicId) != null){
						personalInfo.setDemographicId(map.get(keyDemographicId).toString());
						checkInfo.setDemographicId(map.get(keyDemographicId).toString());
					}
					if(map.get(keyCardId) != null){
						personalInfo.setCarId(map.get(keyCardId).toString());
						checkInfo.setCarId(map.get(keyCardId).toString());
					}
					if(map.get(keySex) != null){
						personalInfo.setSex(map.get(keySex).toString());
						personalInfo.setSexName(map.get(keySexValue).toString());
						checkInfo.setSex(map.get(keySex).toString());
						checkInfo.setSexName(map.get(keySexValue).toString());
					}
					if(map.get(keyAge) != null){
						personalInfo.setBirthday( map.get(keyAge).toString().substring(0, 10));
						personalInfo.setBirthYear(map.get(keyAge).toString().substring(0, 4));
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
						personalInfo.setDiseaseName(dictMap.get(keyHealthProblem));
						personalInfo.setDisease(map.get(keyHealthProblem).toString());
					}

					personalInfoList.add(personalInfo);


					//检查信息 姓名,身份证，就诊卡号,并发症，空腹血糖值，葡萄糖耐量值，用药名称，检查信息code （CH001 并发症,CH002 空腹血糖,CH003 葡萄糖耐量,CH004 用药名称）
					if(map.get(keyDiseaseSymptom) != null){
						checkInfo.setCheckCode("CH001");
						checkInfo.setSymptom(map.get(keyDiseaseSymptom).toString());
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
						checkInfo.setFastingBloodGlucoseName(fastname);
						checkInfo.setFastingBloodGlucoseCode(fastcode);
						checkInfo.setCheckCode("CH002");
						checkInfoList.add(checkInfo);
					}

					if(map.get(keyWestMedicine) != null){
						checkInfo.setCheckCode("CH003");
						checkInfo.setMedicineName(map.get(keyWestMedicine).toString());
						checkInfoList.add(checkInfo);
					}
					if(map.get(keyChineseMedicine) != null) {
						checkInfo.setCheckCode("CH003");
						checkInfo.setMedicineName(map.get(keyChineseMedicine).toString());
						checkInfoList.add(checkInfo);
					}


				}
				//保存到ES库
				//个人信息保存 去重处理，已保存的不在保存  身份证和就诊卡ID
				String index = "";
				String type = "";
				for(PersonalInfoModel personalInfo : personalInfoList){
					Map<String, Object> source = new HashMap<>();
					String jsonPer = objectMapper.writeValueAsString(personalInfo);
					source = objectMapper.readValue(jsonPer, Map.class);
					if(personalInfo.getDemographicId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index, type, "demographicId", personalInfo.getDemographicId());
						if(relist== null || relist.size() ==0){
							elasticSearchClient.index(index,type, source);
						}
					}
					if(personalInfo.getCarId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "cardId",personalInfo.getCarId());
						if(relist== null || relist.size() ==0){
							elasticSearchClient.index(index,type, source);
						}
					}
				}
				//检查信息保存  并发症 去重处理，已保存的不在保存  身份证和就诊卡ID
				for(CheckInfoModel checkInfo : checkInfoList){
					Map<String, Object> source = new HashMap<>();
					String jsonCheck = objectMapper.writeValueAsString(checkInfo);
					source = objectMapper.readValue(jsonCheck,Map.class);
					elasticSearchClient.index(index,type, source);
					if(checkInfo.getCheckCode().equals("CH001") && checkInfo.getDemographicId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "demographicId",checkInfo.getDemographicId());
						if(relist== null || relist.size() ==0){
							elasticSearchClient.index(index,type, source);
						}
					}else if(checkInfo.getCheckCode().equals("CH001") && checkInfo.getCarId() != null){
						List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "cardId",checkInfo.getCarId());
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

	/**
	 * 将一个 Map 对象转化为一个 JavaBean
	 * @param type 要转化的类型
	 * @param map 包含属性值的 map
	 * @return 转化出来的 JavaBean 对象
	 * @throws IntrospectionException
	 *             如果分析类属性失败
	 * @throws IllegalAccessException
	 *             如果实例化 JavaBean 失败
	 * @throws InstantiationException
	 *             如果实例化 JavaBean 失败
	 * @throws InvocationTargetException
	 *             如果调用属性的 setter 方法失败
	 */
	public static Object convertMap(Class type, Map map)
			throws IntrospectionException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance(); // 创建 JavaBean 对象

		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
		for (int i = 0; i< propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();

			if (map.containsKey(propertyName)) {
				// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
				Object value = map.get(propertyName);

				Object[] args = new Object[1];
				args[0] = value;

				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
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
