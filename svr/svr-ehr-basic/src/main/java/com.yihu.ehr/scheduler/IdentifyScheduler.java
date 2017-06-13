package com.yihu.ehr.scheduler;

import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.report.service.JsonReportService;
import com.yihu.ehr.report.service.QcDailyReportResolveService;
import com.yihu.ehr.report.service.QcDailyStatisticsService;
import com.yihu.ehr.report.service.QuotaStatisticsService;
import com.yihu.ehr.util.IdcardValidator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;

@Component
public class IdentifyScheduler {

	private static final Logger log = LoggerFactory.getLogger(IdentifyScheduler.class);
	private final static String TempPath = System.getProperty("java.io.tmpdir") + File.separator;
	public final static String FileDataPath = "ehr/data";
	public final static String FileReportPath = "ehr/report";

	@Autowired
	QcDailyStatisticsService qcDailyStatisticsService;
	@Autowired
	JsonReportService jsonReportService;
	@Autowired
	private QcDailyReportResolveService qcDailyReportResolveService;
	@Autowired
	private QuotaStatisticsService quotaStatisticsService;

	/**
	 * 每天2点 执行一次
	 * 校验档案中身份证号的 是否可以识别
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0 2 * * ?")
	public void validatorIdentityScheduler() throws Exception{
		validatorIdentity();
	}

	public void validatorIdentity(){
		long start = System.currentTimeMillis();
		IdcardValidator idcardValidator = new IdcardValidator();
		List<ArchiveRelation> archiveRelationList = qcDailyStatisticsService.getArchiveRelationList();
		for(ArchiveRelation archiveRelation : archiveRelationList){
			boolean re = true ;
			if(StringUtils.isEmpty(archiveRelation.getIdentifyFlag()) && StringUtils.isNotEmpty(archiveRelation.getIdCardNo())){
				if(archiveRelation.getIdCardNo().equals("null")){
					re = false;
				}else{

					if(archiveRelation.getIdCardNo().length()==18){
						re = idcardValidator.is18Idcard(archiveRelation.getIdCardNo());
					}else if(archiveRelation.getIdCardNo().length() == 15) {
						re = idcardValidator.is15Idcard(archiveRelation.getIdCardNo());
					}else {
						re = false;
					}
				}
				if(re==false){
					archiveRelation.setIdentifyFlag("0");
				}else {
					archiveRelation.setIdentifyFlag("1");
				}
			}else{
				archiveRelation.setIdentifyFlag("0");
			}
			qcDailyStatisticsService.save(archiveRelation);
		}
		long end = System.currentTimeMillis();
		String message = "身份可识别标识  - 调度执行完成，共更新：" + archiveRelationList.size() +"条数据，总耗时："+ (end-start);
		System.out.println(message);
		log.debug(message);

	}

    
	
}
