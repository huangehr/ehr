package com.yihu.ehr.adaption.orgdataset.service;


import com.yihu.ehr.adaption.commons.BaseManager;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaDataManager;
import com.yihu.ehr.util.parm.PageModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构数据集管理器。
 *
 * @author linaz
 * @version 1.0
 * @created 23-10月-2015 10:19:06
 */
@Transactional
@Service
public class OrgDataSetManager extends BaseManager<OrgDataSet, XOrgDataSetRepository> {
    @Autowired
    XOrgDataSetRepository orgDataSetRepository;
    @Autowired
    OrgMetaDataManager orgMetaDataManager;

    @Transactional(propagation = Propagation.SUPPORTS)
    public OrgDataSet getOrgDataSet(long id) {
        return orgDataSetRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isExistOrgDataSet(String orgCode, String code, String name) {
        return orgDataSetRepository.isExistOrgDataSet(code, name).size() != 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrgDataSet createOrgDataSet(OrgDataSet orgDataSet) {
        orgDataSet.setSequence(getMaxSeq(orgDataSet.getOrganization()) + 1);
        Session s = currentSession();

        String sql =
                " insert into org_std_dataset " +
                        " (code, name, create_date, update_date, create_user, update_user, description, organization, sequence) " +
                        " values " +
                        " (:code, :name, :create_date, :update_date, :create_user, :update_user, :description, :organization, :sequence) ";
        Query q = s.createSQLQuery(sql);
        q.setParameter("code", orgDataSet.getCode());
        q.setParameter("name", orgDataSet.getName());
        q.setParameter("create_date", orgDataSet.getCreateDate());
        q.setParameter("update_date", orgDataSet.getUpdateDate());
        q.setParameter("create_user", orgDataSet.getCreateUser());
        q.setParameter("update_user", orgDataSet.getUpdateUser());
        q.setParameter("description", orgDataSet.getDescription());
        q.setParameter("organization", orgDataSet.getOrganization());
        q.setParameter("sequence", orgDataSet.getSequence());
        int rs = q.executeUpdate();
        return orgDataSet;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgDataSet(long id) {
        OrgDataSet orgDataSet = orgDataSetRepository.findOne(id);
        if (orgDataSet == null)
            return 0;
        Query query = currentSession().createQuery("delete from OrgDataSet where id = :id");
        query.setLong("id", id);
        int rs = query.executeUpdate();
        if (rs > 0) {
            //删除关联的数据元
            orgMetaDataManager.deleteOrgMetaDataBySet(orgDataSet);
        }
        return rs;
    }

    public int getMaxSeq(String orgCode) {
        Session session = currentSession();
        Query query = session.createQuery("select max(sequence) from OrgDataSet where organization = :orgCode");
        query.setParameter("orgCode", orgCode);
        Integer seq = (Integer) query.uniqueResult();
        if (seq == null) {
            seq = 0;
        }
        return seq;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrgDataSet updateOrgDataSet(OrgDataSet orgDataSet) {
        return orgDataSetRepository.save(orgDataSet);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrgDataSet> searchOrgDataSets(PageModel pageModel) {
        Session session = currentSession();
        String hql = "select dataset from OrgDataSet dataset  ";
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
        String hql = "select count(*) from OrgDataSet ";
        String wh = pageModel.format();
        hql += wh.equals("") ? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), pageModel);
        return ((Long) query.list().get(0)).intValue();
    }

}
