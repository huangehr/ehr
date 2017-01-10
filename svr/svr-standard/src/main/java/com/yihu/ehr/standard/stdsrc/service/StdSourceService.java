package com.yihu.ehr.standard.stdsrc.service;

import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.1.20
 */
@Service
@Transactional
public class StdSourceService extends BaseJpaService<StandardSource, XStdSourceRepository> {

    @Autowired
    XStdSourceRepository standardSourceRepository;

    /**
     * 根据ID删除标准来源
     *
     * @param ids
     */
    public int deleteSource(String[] ids) {
        String hql = "DELETE FROM StandardSource WHERE id in (:ids)";
        Query query = currentSession().createQuery(hql);
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }


    /**
     * 判断是否code已存在
     *
     * @param code
     * @return
     */
    public boolean isSourceCodeExist(String code) throws ParseException {

        return getCount("code=" + code) > 0;
    }
}