package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.model.org.MOrganization;
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
import com.yihu.quota.vo.TransferTreatmentModel;
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
			String q =  "EHR_000083:* AND EHR_000310:* AND EHR_000306:*"; // 查询条件 EHR_000310 转入医院 EHR_000306 转出医院 不为空  转诊数据集
			String fq = "";									// 过滤条件
			String townKey = "org_area";			//区县
			String eventDateKey = "event_date";	//就诊时间
			String transferFlag = "EHR_000083";	//转诊标志
			String inhospitalKey = "EHR_005074";  	//住院号
			String eventIdKey = "EHR_006202";  	//门（急）诊号
			String registerTypeKey = "EHR_001240";	//挂号类别/41专家门诊
			String transferInOrgKey = "EHR_000310";//转入医疗机构代码
			String transferOutOrgKey = "EHR_000306";//转出医疗机构代码

			String orgDictSql = "SELECT org_code as orgCode,hos_type_id as hosTypeId,administrative_division as administrativeDivision, level_id as levelId  from organizations where org_code=";
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
				//找出转诊的门诊 记录  				
				List<Map<String,Object>> transferSetList = new ArrayList<>() ;
				transferSetList = solrHbaseUtil.selectFieldValueList(ResourceCore.MasterTable, q, fq, 10000);
				for(Map<String,Object> transferSet : transferSetList){
					TransferTreatmentModel transferTreatmentModel = new TransferTreatmentModel();
					String rowKey = transferSet.get("rowkey").toString();
					String town = "";
					String eventId = "";
					int level = 0;
					String registerType = "";
					Date eventDate = null;
					String transferInOrg = "";
					String transferOutOrg = "";
					int transFerType = 0;
					int transEventType = 0;
					if(transferSet.get(eventIdKey) != null){
						eventId = transferSet.get(eventIdKey).toString();
						transEventType = 0;
					}
					if(transferSet.get(inhospitalKey) != null){
						transEventType = 1;
					}
					if(transferSet.get(eventDateKey) != null){
						String eventDateStr = transferSet.get(eventDateKey).toString();
						try {
							eventDate = DateUtil.formatCharDate(eventDateStr, DateUtil.DATE_WORLD_FORMAT);
						}catch (Exception e){
							throw new Exception("就诊时间数据有误！" + eventDateStr );
						}
					}
					if(transferSet.get(townKey) != null){
						town = transferSet.get(townKey).toString();
					}
					if(transferSet.get(transferInOrgKey) != null){
						transferInOrg = transferSet.get(transferInOrgKey).toString();
					}
					if(transferSet.get(transferOutOrgKey) != null){
						transferOutOrg = transferSet.get(transferOutOrgKey).toString();
					}
					//判断转诊类型 向上转诊 还是 向下转诊
					if(StringUtils.isNotEmpty(transferInOrg) && StringUtils.isNotEmpty(transferOutOrg)) {
						//查询机构特殊类型code 并 判断机构类型
						transferInOrg += "'" + transferInOrg + "'";
						transferOutOrg += "'" + transferOutOrg + "'";
						List<MOrganization> inOrgDictDatas = jdbcTemplate.query( transferInOrg, new BeanPropertyRowMapper(MOrganization.class));
						List<MOrganization> outOrgDictDatas = jdbcTemplate.query( transferOutOrg, new BeanPropertyRowMapper(MOrganization.class));
						int inOrgType = 0;
						int outOrgType = 0;
						int inLevel = 9;
						int outLevel = 9;
						if(inOrgDictDatas != null && inOrgDictDatas.size() > 0){
							if (basicMedicalCodeList.contains(inOrgDictDatas.get(0).getHosTypeId())) {
								inOrgType = 1;
							}
							if (hospitalCodeList.contains(inOrgDictDatas.get(0).getHosTypeId())) {
								inOrgType = 2;
							}
							inLevel = Integer.parseInt(inOrgDictDatas.get(0).getLevelId() );
						}
						if(outOrgDictDatas != null && outOrgDictDatas.size() > 0){
							if (basicMedicalCodeList.contains(outOrgDictDatas.get(0).getHosTypeId())) {
								outOrgType = 1;
							}
							if (hospitalCodeList.contains(outOrgDictDatas.get(0).getHosTypeId())) {
								outOrgType = 2;
							}
							outLevel = Integer.parseInt(outOrgDictDatas.get(0).getLevelId() );
						}
						if (inOrgType != 0 && outOrgType != 0 && inOrgType != outOrgType) {
							if (inOrgType > outOrgType) {
								transFerType = 1;//基层医院向上到医院转诊
							}
							if (inOrgType < outOrgType) {
								transFerType = 2;//医院向下到基层机构转诊
							}
						} else {
							transFerType = 0;//其他
						}
						if (transferSet.get(registerTypeKey) != null) {
							registerType = transferSet.get(registerTypeKey).toString();
						}

						transferTreatmentModel.set_id(rowKey);
						transferTreatmentModel.setTown(town);
						transferTreatmentModel.setEventDate(eventDate);
						transferTreatmentModel.setInOrgLevel(outLevel);
						transferTreatmentModel.setInOrgLevel(inLevel);
						transferTreatmentModel.setInOrg(transferInOrg);
						transferTreatmentModel.setOutOrg(transferOutOrg);
						transferTreatmentModel.setRegisterType(registerType);
						transferTreatmentModel.setTransEventType(transEventType);
						transferTreatmentModel.setCreateTime(new Date());
						saveTransferTreatmentModel(transferTreatmentModel);

					}
				}




			}
		}catch (Exception e){
			e.getMessage();
		}
	}

	public void saveTransferTreatmentModel(TransferTreatmentModel transferTreatmentModel){
		try{
			String index = "transferTreatmentIndex";
			String type = "transfer_treatment";
			Map<String, Object> source = new HashMap<>();
			String jsonPer = objectMapper.writeValueAsString(transferTreatmentModel);
			source = objectMapper.readValue(jsonPer, Map.class);
			log.error("开始保存数据" + jsonPer);
			elasticSearchClient.index(index,type, source);

		}catch (Exception e){
			e.getMessage();
		}
	}


}
