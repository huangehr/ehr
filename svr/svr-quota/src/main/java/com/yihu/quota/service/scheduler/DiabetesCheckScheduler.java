package com.yihu.quota.scheduler;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * ���򲡵�����  ������ ����ͳ��
 */
@Component
public class DiabetesCheckScheduler {

	private static final Logger log = LoggerFactory.getLogger(DiabetesCheckScheduler.class);

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


	/**
	 * ����Ҫ��һ����ʼ������
	 * ÿ��2�� ִ��һ��
	 * @throws Exception
	 */
	@Scheduled(cron = "0 20 2 * * ?")
	public void validatorIdentityScheduler(){
		try {
			String q2 = "EHR_000394:*������*2HѪ��* OR EHR_000394:*������*�ո�Ѫ��* OR EHR_000394:*��������������*";
			String fq = ""; // ��������
			String keyEventDate = "event_date";
			String keyArea = "EHR_001225";
			String keyAreaName = "EHR_001225_VALUE";
			String keyPatientName = "patient_name";
			String keyDemographicId = "demographic_id";//���֤
			String keyCardId = "card_id	";
			String keySex = "EHR_000019";//�Ա�
			String keySexValue = "EHR_000019_VALUE";
			String keyAge = "EHR_000007";//�������� ����
			String keyAddress = "EHR_001211"; //��ַ
			String keyDiseaseSymptom = "EHR_000112";//����֢  �������(����)
			String keyDiseaseSymptom2 = "EHR_000295";//����֢  ������ƣ�סԺ��
			String keysugarToleranceName = "EHR_000392";//  ����-��Ŀ��� - ���������LOINC����  14995-5 ������ֵ  14771-0 �ո�Ѫ��
			String keysugarToleranceVal = "EHR_000387";//����-��Ŀ��� -  ���ֵ  ������ֵ
			String keyChineseName = "EHR_000394";//����Ŀ��������
			String keyWestMedicine= "EHR_000100";  //��ҩ
			String keyChineseMedicine= "EHR_000131";//��ҩ

			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			BasesicUtil basesicUtil = new BasesicUtil();
			String initializeDate = "2018-03-20";
			Date now = new Date();
			String nowDate = DateUtil.formatDate(now,DateUtil.DEFAULT_DATE_YMD_FORMAT);
			boolean flag = true;
			String startDate = "2015-01-01";
			String endDate = "2015-02-01";
			while(flag){
				//  ��ǰʱ����ڳ�ʼ��ʱ�䣬���������ݳ�ʼ����ÿ���µ�����ѯ����ǰʱ��С���ڳ�ʼʱ��ÿ���ȡ
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
				//�ҳ����򲡵ľ��ﵵ��
				System.out.println("��ʼ��ѯ ������solr, fq = " + fq);
				List<String> subRrowKeyList = new ArrayList<>() ; //ϸ��rowkey
				subRrowKeyList = selectSubRowKey(ResourceCore.SubTable, q2, fq, 10000);
				System.out.println("�������ѯ���������"+subRrowKeyList.size());
				if(subRrowKeyList != null && subRrowKeyList.size() > 0){
					//�������� Start
					for(String subRowkey:subRrowKeyList){//ѭ������ �ҵ������������Ϣ
						//��ѯ�˴ξ����¼��������� ���浽����¼��
						String name = "";
						String demographicId = "";
						String cardId = "";
						Integer sex = 0;
						String sexName = "";
						String mainRowkey = subRowkey.substring(0, subRowkey.indexOf("$"));
						Map<String,Object> map = hbaseDao.getResultMap(ResourceCore.MasterTable, mainRowkey);
						if(map !=null){
							if(map.get(keyDemographicId) != null){
								demographicId = map.get(keyDemographicId).toString();
							}
							if(map.get(keyCardId) != null){
								cardId = map.get(keyCardId).toString();
							}
							if(map.get(keySex) != null) {
								if(StringUtils.isNotEmpty(map.get(keySex).toString())){
//									if(map.get(keySex).toString().equals("��")){
//										sex =1;
//										sexName ="��";
//									}else if(map.get(keySex).toString().equals("Ů")){
//										sex =2;
//										sexName ="Ů";
//									}else {
//										sex =0;
//										sexName ="δ֪";
//									}
									sex = Integer.valueOf(map.get(keySex).toString());
									sexName = map.get(keySexValue).toString();
								}else {
									sex =0;
									sexName ="δ֪";
								}
							}
							if(map.get(keyPatientName) != null){
								name = map.get(keyPatientName).toString();
							}
						}

						CheckInfoModel baseCheckInfo = new CheckInfoModel();
						baseCheckInfo.setName(name);
						baseCheckInfo.setDemographicId(demographicId);
						baseCheckInfo.setCardId(cardId);
						baseCheckInfo.setSex(sex);
						baseCheckInfo.setSexName(sexName);
						Map<String,Object> submap = hbaseDao.getResultMap(ResourceCore.SubTable, subRowkey);
						if(submap !=null){
							//�����Ϣ ����,���֤�����￨��,����֢���ո�Ѫ��ֵ������������ֵ����ҩ���ƣ������Ϣcode ��CH001 ����֢,CH002 �ո�Ѫ��,CH003 ����������,CH004 ��ҩ���ƣ�
							boolean fast = false;
							if(submap.get(keyChineseName) != null){
								// "������(�ո�Ѫ��)" "��������������"
								String val = submap.get(keyChineseName).toString();
								fast = (val.contains("������") && val.contains("�ո�Ѫ��"))||val.equals("��������������") ;
							}
							if(fast){
								//7.8mmol/l ���� 2��7.8-11.1mmol/l  3:11.1 ����
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
									fastname = "7.0mmol/L����";
									fastcode = "3";
								}
								CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
								checkInfo.setFastingBloodGlucoseName(fastname);
								checkInfo.setFastingBloodGlucoseCode(fastcode);
								checkInfo.setCheckCode("CH002");
								//���浽ES��
								saveCheckInfo(checkInfo);
							}
							boolean tolerance = false;
							if(submap.get(keyChineseName) != null){
								String val = submap.get(keyChineseName).toString();
								//	"������(2HѪ��)";
								tolerance = val.contains("������") && val.contains("2HѪ��") ;
							}
							//�����ǣ��ڷ�75 g�����Ǻ�2 h)
							if(tolerance){
								//7.8mmol/l ���� 2��7.8-11.1mmol/l  3:11.1 ����
								String sugarTolename = "";
								String sugarToleCode = "";
								double val = Double.valueOf(submap.get(keysugarToleranceVal).toString());
								if(val < 7.8){
									sugarTolename = "7.8 mmol/L����";
									sugarToleCode = "1";
								}
								if(val >= 7.8 && val < 11.1){
									sugarTolename = "7.8~11.1 mmol/L";
									sugarToleCode = "2";
								}
								if( val > 11.1){
									sugarTolename = "11.1 mmol/L����";
									sugarToleCode = "3";
								}
								CheckInfoModel checkInfo = setCheckInfoModel(baseCheckInfo);
								checkInfo.setSugarToleranceName(sugarTolename);
								checkInfo.setSugarToleranceCode(sugarToleCode);
								checkInfo.setCheckCode("CH003");
								//���浽ES��
								saveCheckInfo(checkInfo);
							}
						}
					}
					//�������� ������� end
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
		return  checkInfo;
	}
	//��ȡά�ȵ��ֵ���
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

	//��ȡ��ѯ����е�rowKey
	private List<String> selectSubRowKey(String core ,String q,String fq,long count) throws Exception {
		List<String> data = new ArrayList<>();
		/***** Solr��ѯ ********/
		SolrDocumentList solrList = solrUtil.query(core, q , fq, null, 1,count);
		if(solrList!=null && solrList.getNumFound()>0){
			for (SolrDocument doc : solrList){
				String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
				data.add(rowkey);
			}
		}
		return  data;
	}

	//��ѯhabase��������
	private List<Map<String,Object>> selectHbaseData(String table, List<String> list) throws Exception {
		List<Map<String,Object>> data = new ArrayList<>();
		/***** Hbase��ѯ ********/
		Result[] resultList = hbaseDao.getResultList(table,list, "", ""); //hbase�����
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

	//��ѯhabase�������� ����
	private Map<String,Object> selectSingleHbaseData(String table, String rowKey) throws Exception {
		return hbaseDao.getResultMap(table,rowKey);
	}

	/**
	 * Result ת JSON
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
