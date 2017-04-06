package com.yihu.ehr.patient.service.arapply;

import com.yihu.ehr.patient.dao.XArRelationRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@Service
@Transactional
public class ArRelationService extends BaseJpaService<ArRelation, XArRelationRepository> {

    @Transactional(propagation = Propagation.REQUIRED)
    public ArRelation saveModel(ArRelation arRelation){
        String hql = "delete ArRelation where arApplyId=:arApplyId";
        currentSession().createQuery(hql).setParameter("arApplyId", arRelation.getArApplyId()).executeUpdate();
        return save(arRelation);
    }
}
