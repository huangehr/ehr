package com.yihu.ehr.paient.paientIdx.model;


import com.yihu.ehr.paient.service.demographic.DemographicInfo;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

/**
 * 人口学索引搜索策略生成器.
 * @author Sand
 * @version 1.0
 * @created 16-6月-2015 14:14:34
 */
@Transactional(Transactional.TxType.SUPPORTS)
@Service
public class DemographicIndexStragety {

    @PersistenceContext
    protected EntityManager entityManager;

	/**
	 * 策略列表
	 */
	private AbstractCardStragety stragety;

	public DemographicIndexStragety(){
        stragety = null;
	}

	/**
	 * 生成策略
	 */
	protected void makeStragety(){
        stragety = new IDCardStragety();
        stragety.setNextStragety(new MedicareCardStrategy())
                .setNextStragety(new HealthCardStragety())
                .setNextStragety(new OfficersCertStragety())
                .setNextStragety(new AgriculturalInsuranceStragety())
                .setNextStragety(new AlipayStragety())
                .setNextStragety(new SinaWeiboStragety())
                .setNextStragety(new TecentWeixinStragety()
                .setNextStragety(new CombinedInfoStragety()));
	}

	/**
	 * 执行搜索策略, 并将各个搜索策略的返回值合并
	 * 
	 * @param args
	 */
	public DemographicInfo[] search(Map<String, Object> args){
        synchronized(this){
            if(stragety == null){
                makeStragety();
            }
        }

        if(stragety == null) return null;

        List<DemographicInfo> demographicInfos = new ArrayList<DemographicInfo>();
        Session session = entityManager.unwrap(org.hibernate.Session.class);

        AbstractSearchStragety tempStragety = stragety;
        while (tempStragety != null){
             Criteria criteria = tempStragety.generateQuery(session, args);
            if(criteria != null) {
                demographicInfos.addAll(criteria.list());
            }

            tempStragety = tempStragety.getNextStragety();
        };

        //去重
        DemographicInfo[] demoinfo = demographicInfos.toArray(new DemographicInfo[demographicInfos.size()]);
        Set<DemographicInfo> set =new HashSet<DemographicInfo>();
        Collections.addAll(set, demoinfo.clone());
        demoinfo=set.toArray(new DemographicInfo[0]);

        return demoinfo;
	}
}