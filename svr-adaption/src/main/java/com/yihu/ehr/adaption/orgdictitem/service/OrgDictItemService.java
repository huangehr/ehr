package com.yihu.ehr.adaption.orgdictitem.service;

import com.yihu.ehr.adaption.orgdict.service.OrgDict;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构字典项
 *
 * @author lincl
 * @version 1.0
 * @created 2016.1.29
 */

@Service
public class OrgDictItemService extends BaseJpaService<OrgDictItem, XOrgDictItemRepository> {


    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isExistOrgDictItem(long orgDictSeq, String orgCode, String code) {
        return ((XOrgDictItemRepository) getRepository()).isExistOrgDictItem(orgDictSeq, orgCode, code).size() != 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrgDictItem createOrgDictItem(OrgDictItem orgDictItem) {
        orgDictItem.setSequence(getMaxSeq(orgDictItem.getOrganization()) + 1);
        Session s = currentSession();
        String sql =
                " insert into healtharchive.org_std_dictentry " +
                        " (code, name, create_date, update_date, create_user, update_user, description, organization, sequence, org_dict, sort) " +
                        " values " +
                        " (:code, :name, :create_date, :update_date, :create_user, :update_user, :description, :organization, :sequence, :org_dict, :sort) ";
        Query q = s.createSQLQuery(sql);
        q.setParameter("code", orgDictItem.getCode());
        q.setParameter("name", orgDictItem.getName());
        q.setParameter("create_date", orgDictItem.getCreateDate());
        q.setParameter("update_date", orgDictItem.getUpdateDate());
        q.setParameter("create_user", orgDictItem.getCreateUser());
        q.setParameter("update_user", orgDictItem.getUpdateUser());
        q.setParameter("description", orgDictItem.getDescription());
        q.setParameter("organization", orgDictItem.getOrganization());
        q.setParameter("sequence", orgDictItem.getSequence());
        q.setParameter("org_dict", orgDictItem.getOrgDict());
        q.setParameter("sort", orgDictItem.getSort());
        int rs = q.executeUpdate();
        return orgDictItem;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getMaxSeq(String orgCode) {
        Session session = currentSession();
        Query query = session.createQuery("select max(sequence) from OrgDictItem where organization = :orgCode");
        query.setParameter("orgCode", orgCode);
        Integer seq = (Integer) query.uniqueResult();
        if (seq == null) {
            seq = 0;
        }
        return seq;
    }

    /**
     * 删除字典关联的字典项
     *
     * @param dict
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgDictItemByDict(OrgDict dict) {
        Query query = currentSession().createQuery("delete from OrgDictItem where orgDict = :dictId and organization=:organization");
        query.setLong("dictId", dict.getSequence());
        query.setString("organization", dict.getOrganization());
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public int getNextSort(long dictId) {
        Session session = currentSession();
        Query query = session.createQuery("select max(sort) from OrgDictItem  where orgDict= '" + dictId + "'");
        int result;
        if (query.uniqueResult() == null) {
            result = 1;
        } else {
            result = Integer.parseInt(query.uniqueResult().toString()) + 1;
        }
        return result;
    }

    public List findByDict(long orgDictSeq, String orgCode) {
        String hql = " from OrgDictItem where orgDict=:orgDictSeq and organization=:orgCode";
        Query query = currentSession().createQuery(hql);
        query.setParameter("orgDictSeq", orgDictSeq);
        query.setParameter("orgCode", orgCode);
        return query.list();
    }

    public OrgDictItem getOrgDicEntryBySequence(String orgCode,int sequence){
        return ((XOrgDictItemRepository) getRepository()).getOrgDicEntryBySequence(orgCode, sequence);
    }
}
