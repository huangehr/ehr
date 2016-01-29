package com.yihu.ehr.paient.paientIdx.model;

import com.yihu.ehr.paient.service.card.OfficersCard;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 * 军官证搜索策略
 * @author Sand
 * @version 1.0
 * @created 16-6月-2015 16:48:24
 */
public class OfficersCertStragety extends AbstractPhysicalCardStragety {

	public OfficersCertStragety(){

	}
	@Override
	public Criteria createCriteria(Session session){
		Criteria criteria=null;
		criteria = session.createCriteria(OfficersCard.class);
		return criteria;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}