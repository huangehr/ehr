package com.yihu.ehr.adaption.commons;

import com.yihu.ehr.util.parm.*;
import com.yihu.ehr.util.parm.FieldCondition;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by Lincl on 2016/1/26.
 */
public class BaseManager<T, RESP> {
    @Autowired
    RESP resp;

    @PersistenceContext
    EntityManager entityManager;

    /**
     * 获取当前session
     * @return
     */
    protected Session currentSession(){
        return entityManager.unwrap(Session.class);
    }

    protected Query setQueryVal(Query query, PageModel pageModel){
        Map<String, FieldCondition> filters = pageModel.getFilters();
        for(String k: filters.keySet()){
            if(filters.get(k).getLogic().equals("in"))
                query.setParameterList(k, (Object[])filters.get(k).formatVal());
            else
                query.setParameter(k, filters.get(k).formatVal());
        }
        return query;
    }

    protected RESP getResp(){
        return resp;
    }
}
