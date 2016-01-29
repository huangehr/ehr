package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.patient.service.card.IdCard;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 * 身份证搜索策略
 * @author Sand
 * @version 1.0
 * @updated 16-6月-2015 16:45:12
 */
public class IDCardStragety extends AbstractPhysicalCardStragety  {

	public IDCardStragety(){
        super();
	}

	@Override
	public Criteria createCriteria(Session session){
		Criteria criteria=null;
		criteria = session.createCriteria(IdCard.class);
		return criteria;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}


}
