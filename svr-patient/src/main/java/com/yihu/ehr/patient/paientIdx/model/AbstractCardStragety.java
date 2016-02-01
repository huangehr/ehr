package com.yihu.ehr.patient.paientIdx.model;

import com.yihu.ehr.patient.service.card.AbstractCard;
import com.yihu.ehr.patient.service.demographic.DemographicInfo;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by zqb on 2015/6/29.
 */
public class AbstractCardStragety extends AbstractSearchStragety {
    @Override
    public Criteria addNotNull(Session session,Criteria criteria){
        //不为空条件，默认卡搜索策略中卡表的demographicId不为空(即卡已绑定)，返回人口学信息
        criteria.add(Restrictions.isNotNull("demographicId"));
        if (criteria!=null) {
            List<AbstractCard> cards = criteria.list();
            if (cards.size()>0) {
                criteria = session.createCriteria(DemographicInfo.class);
                for (int i = 0; i < cards.size(); i++) {
                    criteria.add(Restrictions.or(Restrictions.eq("id", cards.get(i).getDemographicId())));
                }
            }else {
                criteria=null;
            }
        }
        return criteria;
    }
}
