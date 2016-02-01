package com.yihu.ehr.patient.paientIdx.model;


import com.yihu.ehr.patient.service.card.AbstractVirtualCard;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Map;

/**
 * 支付宝搜索策略
 * @author zqb
 * @version 1.0
 * @created 19-六月-2015 14:58:52
 */
public class AlipayStragety extends AbstractVirtualCardStragety  {

	public AlipayStragety(){
		super();
	}

	@Override
	public Criteria createCriteria(Session session){
		Criteria criteria=null;
		criteria = session.createCriteria(AbstractVirtualCard.class);
		return criteria;
	}

	@Override
	public Criteria generateQuery(Session session, Map<String, Object> args) {
		return super.generateQuery(session,args);
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}