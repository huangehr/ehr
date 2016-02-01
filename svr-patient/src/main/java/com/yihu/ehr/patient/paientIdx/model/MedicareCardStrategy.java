package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.patient.service.card.MediCard;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 * 医保 卡搜索策略
 * @author Sand
 * @version 1.0
 * @updated 16-6月-2015 16:45:38
 */
public class MedicareCardStrategy extends AbstractPhysicalCardStragety  {

	public MedicareCardStrategy(){
	}

	@Override
	public Criteria createCriteria(Session session){
		Criteria criteria=null;
		criteria = session.createCriteria(MediCard.class);
		return criteria;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}
}