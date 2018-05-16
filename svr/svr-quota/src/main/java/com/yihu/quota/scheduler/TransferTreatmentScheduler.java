package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.extract.ExtractUtil;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.util.LatitudeUtils;
import com.yihu.quota.util.SolrHbaseUtil;
import com.yihu.quota.vo.CheckInfoModel;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.PersonalInfoModel;
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
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 分级诊疗监测  统计
 */
@Component
public class TransferTreatmentScheduler {

	private static final Logger log = LoggerFactory.getLogger(TransferTreatmentScheduler.class);
	@Autowired
	private ElasticSearchUtil elasticSearchUtil;
	@Autowired
	private ElasticSearchClient elasticSearchClient;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private SolrHbaseUtil solrHbaseUtil;
	@Autowired
	private JdbcTemplate jdbcTemplate;


//	@Scheduled(cron = "0 10 16 * * ?")
	public void transferTreatment(){
		try {
			String q =  "EHR_000240:*"; // 查询条件 EHR_000240 转诊标识 不为空
			String fq = "";				// 过滤条件
			String eventDateKey = "event_date";	//就诊时间
			String eventIdKey = "EHR_006202";  	//门（急）诊号
			String registerTypeKey = "EHR_001240";	//挂号类别/41专家门诊
			String transferInOrgKey = "EHR_000306";  	//转入机构
			String transferOutOrgKey = "EHR_000310";  //转出机构
			String chronicDiseaseKey = "HDSB04_87";	// 慢病诊断

			String orgSql = "SELECT hos_type_id as code from organizations where orgCode = ";
			String orgTypCodeHospitalSql = "SELECT code,name from org_health_category where top_pid = " + Contant.orgHealthTypeCode.hospital_Id;
			String orgTypCodeBasicMedicalSql = "SELECT code,name from org_health_category where top_pid = " + Contant.orgHealthTypeCode.basicMedical_Id;
			List<DictModel> hospitalDictDatas = jdbcTemplate.query(orgTypCodeHospitalSql, new BeanPropertyRowMapper(DictModel.class));
			List<DictModel> basicMedicalDictDatas = jdbcTemplate.query(orgTypCodeBasicMedicalSql, new BeanPropertyRowMapper(DictModel.class));
			List<String> hospitalCodeList = new ArrayList<>();
			List<String> basicMedicalCodeList = new ArrayList<>();
			if(hospitalDictDatas != null && hospitalDictDatas.size() > 0){
				for(DictModel dictModel : hospitalDictDatas){
					hospitalCodeList.add(dictModel.getCode());
				}
			}
			if(basicMedicalDictDatas != null && basicMedicalDictDatas.size() > 0){
				for(DictModel dictModel : basicMedicalDictDatas){
					basicMedicalCodeList.add(dictModel.getCode());
				}
			}


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
				//  当前时间大于初始化时间，就所有数据初始化，每个月递增查询，当前时间小于于初始时间每天抽取
				if(basesicUtil.compareDate(initializeDate,nowDate) == -1){
					Date exeStartDate = DateUtil.parseDate(initializeDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(exeStartDate);
					int day1 = calendar.get(Calendar.DAY_OF_YEAR);
					Calendar endCalendar = Calendar.getInstance();
					endCalendar.setTime(now);
					int day2 = endCalendar.get(Calendar.DAY_OF_YEAR);
					int num = day2 - day1;
					//每天采集10天的数据
					Date executeStartDate = DateUtils.addDays(DateUtil.parseDate(executeInitDate, DateUtil.DEFAULT_DATE_YMD_FORMAT), 10*(num-1));
					Date executeEndDate = DateUtils.addDays(DateUtil.parseDate(executeInitDate, DateUtil.DEFAULT_DATE_YMD_FORMAT), 10*num);
					startDate = DateUtil.formatDate(executeStartDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
					endDate = DateUtil.formatDate(executeEndDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
					fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T23:59:59Z]";
					flag = false;
				}else{
					fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T00:00:00Z]";
					Date sDate = DateUtils.addDays(DateUtil.parseDate(startDate, DateUtil.DEFAULT_DATE_YMD_FORMAT), 15);
					startDate = DateUtil.formatDate(sDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
					Date eDate = DateUtils.addDays(DateUtil.parseDate(startDate, DateUtil.DEFAULT_DATE_YMD_FORMAT), 15);
					endDate = DateUtil.formatDate(eDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
					if(basesicUtil.compareDate("2016-06-01",startDate) != 1){//结束时间
						fq = "event_date:[" + startDate + "T00:00:00Z TO 2016-06-01T00:00:00Z]";
						flag = false;
					}
					System.out.println("startDate=" + startDate);
				}
				//门诊-挂号 数据集
				//找出转诊的门诊 记录  提取门诊记录号         住院待看是否有此标识
				List<Map<String,Object>> eventSetList = new ArrayList<>() ;
				eventSetList = solrHbaseUtil.selectFieldValueList(ResourceCore.SubTable, q, fq, 10000);
				for(Map<String,Object> eventSet : eventSetList){
					String eventId = "";
					String registerType = "";
					Date eventDate = null;
					String transferInOrg = "";
					String transferOutOrg = "";
					if(eventSet.get(eventIdKey) != null){
						eventId = eventSet.get(eventIdKey).toString();
					}
					if(eventSet.get(eventDateKey) != null){
						String eventDateStr = eventSet.get(eventDateKey).toString();
						try {
							eventDate = DateUtil.formatCharDate(eventDateStr, DateUtil.DATE_WORLD_FORMAT);
						}catch (Exception e){
							throw new Exception("就诊时间数据有误！" + eventDateStr );
						}
					}
					if(eventSet.get(registerTypeKey) != null){
						registerType = eventSet.get(registerTypeKey).toString();
					}
					//转诊（院）记录 数据集
					List<Map<String,Object>> inHospitalSetList = new ArrayList<>() ;
					q = eventIdKey + ":" + eventId;//最好加住院的标识 过滤其他数据集
					inHospitalSetList = solrHbaseUtil.selectFieldValueList(ResourceCore.SubTable, q, fq, 10000);
					if(inHospitalSetList != null && inHospitalSetList.size() > 0){
						Map<String,Object> inHospitalSet = inHospitalSetList.get(0);
						if(inHospitalSet.get(transferInOrgKey) != null){
							transferInOrg = inHospitalSet.get(transferInOrgKey).toString();
						}
						if(inHospitalSet.get(transferOutOrgKey) != null){
							transferOutOrg = inHospitalSet.get(transferOutOrgKey).toString();
						}
						//判断转诊类型 向上转诊 还是 向下转诊
						if(StringUtils.isNotEmpty(transferInOrg) && StringUtils.isNotEmpty(transferOutOrg)){
							//查询机构特殊类型code 并 判断机构类型
							List<DictModel> inOrgDictDatas = jdbcTemplate.query(orgSql+transferInOrg, new BeanPropertyRowMapper(DictModel.class));
							List<DictModel> outOrgDictDatas = jdbcTemplate.query(orgSql+transferOutOrg, new BeanPropertyRowMapper(DictModel.class));
//							if(basicMedicalCodeList.contains()){
//
//							}
//							if(hospitalCodeList.contains()){
//
//							}

							//提取公共方法  查询 四个特殊机构类型下的 code


						}
					}
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
			log.error("开始保存数据" + jsonPer);
			if(personalInfo.getDemographicId() != null){
				List<Map<String, Object>> relist = elasticSearchUtil.findByField(index, type, "demographicId", personalInfo.getDemographicId());
				if( !(relist != null && relist.size() >0)){
					elasticSearchClient.index(index,type, source);
				}
			}else if(personalInfo.getCardId() != null){
				List<Map<String, Object>> relist = elasticSearchUtil.findByField(index,type, "cardId",personalInfo.getCardId());
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
		return  checkInfo;
	}



}
