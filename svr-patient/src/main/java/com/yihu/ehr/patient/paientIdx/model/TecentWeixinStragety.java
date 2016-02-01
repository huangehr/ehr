package com.yihu.ehr.patient.paientIdx.model;


import com.yihu.ehr.patient.service.card.AbstractVirtualCard;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Map;

/**
 * 腾讯微信搜索策略
 * @author zqb
 * @version 1.0
 * @created 19-六月-2015 14:59:15
 */
public class TecentWeixinStragety extends AbstractVirtualCardStragety {

	public TecentWeixinStragety(){
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