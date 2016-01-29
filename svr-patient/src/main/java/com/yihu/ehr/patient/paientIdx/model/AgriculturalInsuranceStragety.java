package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.patient.service.card.AICard;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 * 农保卡搜索策略
 * @author Sand
 * @version 1.0
 * @created 16-6月-2015 16:48:26
 */
public class AgriculturalInsuranceStragety extends AbstractPhysicalCardStragety {

	public AgriculturalInsuranceStragety(){

	}
	@Override
	public Criteria createCriteria(Session session){
		Criteria criteria=null;
		criteria = session.createCriteria(AICard.class);
		return criteria;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}


}