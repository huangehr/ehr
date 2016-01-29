package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.patient.service.card.HealthCard;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 * 健康卡搜索策略
 * @author Sand
 * @version 1.0
 * @created 16-6月-2015 16:48:22
 */
public class HealthCardStragety extends AbstractPhysicalCardStragety  {

	public HealthCardStragety(){

	}
	@Override
	public Criteria createCriteria(Session session){
		Criteria criteria=null;
		criteria = session.createCriteria(HealthCard.class);
		return criteria;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}


}