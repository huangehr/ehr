package com.yihu.ehr.adaption.orgdict.service;

import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItemService;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构字典管理器。
 *
 * @author lincl
 * @version 1.0
 * @created 2016.1.29
 */
@Service
public class OrgDictService extends BaseJpaService<OrgDict, XOrgDictRepository> {

    @Autowired
    OrgDictItemService orgDictItemManager;

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isExistOrgDict(String orgCode, String code) {
        return ((XOrgDictRepository) getRepository()).isExistOrgDict(orgCode, code).size() != 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrgDict createOrgDict(OrgDict orgDict) {
        orgDict.setSequence(getMaxSeq(orgDict.getOrganization()) + 1);
        Session s = currentSession();
        String sql =
                " insert into healtharchive.org_std_dict " +
                        " (code, name, create_date, update_date, create_user, update_user, description, organization, sequence) " +
                        " values " +
                        " (:code, :name, :create_date, :update_date, :create_user, :update_user, :description, :organization, :sequence) ";
        Query q = s.createSQLQuery(sql);
        q.setParameter("code", orgDict.getCode());
        q.setParameter("name", orgDict.getName());
        q.setParameter("create_date", orgDict.getCreateDate());
        q.setParameter("update_date", orgDict.getUpdateDate());
        q.setParameter("create_user", orgDict.getCreateUser());
        q.setParameter("update_user", orgDict.getUpdateUser());
        q.setParameter("description", orgDict.getDescription());
        q.setParameter("organization", orgDict.getOrganization());
        q.setParameter("sequence", orgDict.getSequence());
        int rs = q.executeUpdate();
        return orgDict;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getMaxSeq(String orgCode) {
        Session session = currentSession();
        Query query = session.createQuery("select max(sequence) from OrgDict where organization = :orgCode");
        query.setParameter("orgCode", orgCode);
        Integer seq = (Integer) query.uniqueResult();
        if (seq == null) {
            seq = 0;
        }
        return seq;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOrgDict(long id) {
        OrgDict orgDict = retrieve(id);
        if (orgDict == null)
            return;
        delete(orgDict);
        orgDictItemManager.deleteOrgDictItemByDict(orgDict);
    }

    public OrgDict getOrgDictBySequence(String orgCode,int sequence)
    {
        return ((XOrgDictRepository) getRepository()).getOrgDictBySequence(orgCode, sequence);
    }
}
