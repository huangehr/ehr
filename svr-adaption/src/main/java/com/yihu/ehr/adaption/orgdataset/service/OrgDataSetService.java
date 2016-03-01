package com.yihu.ehr.adaption.orgdataset.service;


import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaDataService;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 机构数据集管理器。
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Transactional
@Service
public class OrgDataSetService extends BaseJpaService<OrgDataSet, XOrgDataSetRepository> {
    @Autowired
    OrgMetaDataService orgMetaDataService;


    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isExistOrgDataSet(String orgCode, String code, String name) {
        return ((XOrgDataSetRepository) getRepository()).isExistOrgDataSet(code, name).size() != 0;
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
    public void deleteOrgDataSet(long id) {
        OrgDataSet orgDataSet = retrieve(id);
        if (orgDataSet == null)
            return;
        delete(orgDataSet);
        orgMetaDataService.deleteOrgMetaDataBySet(orgDataSet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
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

}
