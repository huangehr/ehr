package com.yihu.ehr.patient.service.card;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * SNS平台管理接口实现类.
 * @author Sand
 * @version 1.0
 * @created 05-6月-2015 11:09:50
 */
@Transactional
@Service
public class SNSPlatformManager {

	@PersistenceContext
	protected EntityManager entityManager;

	public SNSPlatformManager(){
	}

	enum OrderBy{
		HomePage,
		Name,
		CreateDate
	}

	public void deletePlatform(String url){
		Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("delete SNSPlatform where homePage = :value");
        query.setString("value", url);
        query.executeUpdate();
	}

    @Transactional(value = Transactional.TxType.SUPPORTS, rollbackOn = MalformedURLException.class)
	public SNSPlatform getPlatform(String url) throws MalformedURLException{
        // 判断URL是否合法
        {
            URL urlTest = new URL(url);
        }

        SNSPlatform platform = (SNSPlatform) entityManager.unwrap(org.hibernate.Session.class)
                .createQuery("from SNSPlatform where homePage = :value")
                .setString("value", url)
                .uniqueResult();
        return platform;
	}

    @Transactional(Transactional.TxType.SUPPORTS)
	public SNSPlatform[] getPlatformList(int from, int to, OrderBy orderBy, boolean asc){
		Session session = entityManager.unwrap(org.hibernate.Session.class);
        Criteria criteria = session.createCriteria(SNSPlatform.class);
        criteria.setFirstResult(from);
        criteria.setMaxResults(to);

        String column = getSortProperty(orderBy);
        criteria.addOrder(asc ? Order.asc(column) : Order.desc(column));

        List<SNSPlatform> platList = criteria.list();
        SNSPlatform[] platforms = new SNSPlatform[platList.size()];

        return platList.toArray(platforms);
	}

    @Transactional(Transactional.TxType.SUPPORTS)
	public SNSPlatform createPlatform(String name, String url, String notes) throws MalformedURLException {
        if(name == null || name.length() == 0) throw new IllegalArgumentException("平台名称不能为空.");
        if(url == null || url.length() == 0) throw new IllegalArgumentException("平台URL不能为空.");

        SNSPlatform platform = new SNSPlatform();
        platform.setHomePage(new URL(url));
        platform.setName(name);
        platform.setNotes(notes);

        return platform;
	}

	public void registerPlatform(SNSPlatform platform) throws MalformedURLException {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        session.save(platform);
    }

	public void updatePlatform(SNSPlatform plt){
		Session session = entityManager.unwrap(org.hibernate.Session.class);
        session.update(plt);
	}

	/**
	 * 获取排序属性名. 注意使用的是Hibernate绑定的对象属性名称.
	 * @param orderBy
	 * @return
	 */
	String getSortProperty(OrderBy orderBy){
		switch(orderBy){
			case HomePage:
				return "homePage";

			case Name:
				return "Name";

			case CreateDate:
				return "createDate";

			default:
				return "";
		}
	}
}