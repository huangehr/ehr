package com.yihu.ehr.scheduler;

import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.report.service.QcDailyStatisticsService;
import com.yihu.ehr.util.IdcardValidator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class IdentifyScheduler {

	private static final Logger log = LoggerFactory.getLogger(IdentifyScheduler.class);

	@Autowired
	QcDailyStatisticsService qcDailyStatisticsService;

	/**
	 * 每10分钟 执行一次
	 *
	 * @throws Exception
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void validatorIdentityScheduler() throws Exception{
		validatorIdentity();
	}

	//每次更新1000条
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
//			System.out.println("idcardNo = "+archiveRelation.getIdCardNo()==null ?"null":archiveRelation.getIdCardNo() + re );
			qcDailyStatisticsService.save(archiveRelation);
		}
		long end = System.currentTimeMillis();
		String message = "身份调度执行完成，共更新：" + archiveRelationList.size() +"条数据，总耗时："+ (end-start);
		System.out.println(message);
		log.debug(message);



	}

    
	
}
