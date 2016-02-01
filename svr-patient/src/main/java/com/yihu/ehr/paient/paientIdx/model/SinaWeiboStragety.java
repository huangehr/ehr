package com.yihu.ehr.paient.paientIdx.model;


import com.yihu.ehr.paient.service.card.AbstractVirtualCard;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Map;

/**
 * 新浪微博搜索策略
 * @author zqb
 * @version 1.0
 * @created 19-六月-2015 14:59:09
 */
public class SinaWeiboStragety extends AbstractVirtualCardStragety {

	public SinaWeiboStragety(){
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