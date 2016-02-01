package com.yihu.ehr.adaption.orgdict.service;

import com.yihu.ehr.adaption.commons.BaseManager;
import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItemManager;
import com.yihu.ehr.util.parm.PageModel;
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
public class OrgDictManager extends BaseManager<OrgDict, XOrgDictRepository> {

    @Autowired
    XOrgDictRepository orgDictRepository;
    @Autowired
    OrgDictItemManager orgDictItemManager;

    @Transactional(propagation = Propagation.SUPPORTS)
    public OrgDict getOrgDict(long id) {
        return orgDictRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isExistOrgDict(String orgCode, String code) {
        return orgDictRepository.isExistOrgDict(orgCode, code).size() != 0;
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
    public int updateOrgDict(OrgDict orgDict) {
        orgDictRepository.save(orgDict);
        return 1;
    }

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

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrgDict> searchOrgDicts(PageModel pageModel) {
        Session session = currentSession();
        String hql = "select orgDict from OrgDict orgDict  ";
        String wh = pageModel.format();
        hql += wh.equals("") ? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), pageModel);
        int page = pageModel.getPage();
        if (page > 0) {
            query.setMaxResults(pageModel.getRows());
            query.setFirstResult((page - 1) * pageModel.getRows());
        }
        return query.list();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public int searchTotalCount(PageModel pageModel) {
        Session session = currentSession();
        String hql = "select count(*) from OrgDict ";
        String wh = pageModel.format();
        hql += wh.equals("") ? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), pageModel);
        return ((Long) query.list().get(0)).intValue();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgDict(long id) {
        OrgDict orgDict = orgDictRepository.findOne(id);
        if (orgDict == null)
            return 0;
        Query query = currentSession().createQuery("delete from OrgDict where id = :id");
        query.setLong("id", id);
        int rows = query.executeUpdate();
        orgDictItemManager.deleteOrgDictItemByDict(orgDict);
        return rows;
    }
}
