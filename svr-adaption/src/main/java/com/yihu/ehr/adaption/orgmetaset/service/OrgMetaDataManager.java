package com.yihu.ehr.adaption.orgmetaset.service;

import com.yihu.ehr.adaption.commons.BaseManager;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSet;
import com.yihu.ehr.util.parm.PageModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构数据元实现
 *
 * @author lincl
 * @version 1.0
 * @created 2016.1.29
 */
@Service
public class OrgMetaDataManager extends BaseManager<OrgMetaData, XOrgMetaDataRepository> {

    @Autowired
    XOrgMetaDataRepository orgMetaDataRepository;


    /**
     * 根据数据集关联的所有数据元
     *
     * @param dataSet
     * @return
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgMetaDataBySet(OrgDataSet dataSet) {
        Query query = currentSession().createQuery("delete from OrgMetaData where orgDataSet = :setId AND organization= :organization");
        query.setLong("setId", dataSet.getSequence());
        query.setString("organization", dataSet.getOrganization());
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public OrgMetaData getOrgMetaData(long id) {
        return orgMetaDataRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isExistOrgMetaData(int orgDataSetSeq, String orgCode, String code) {
        return orgMetaDataRepository.isExistOrgMetaData(orgDataSetSeq, orgCode, code).size() != 0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrgMetaData createOrgMetaData(OrgMetaData orgMetaData) {
        orgMetaData.setSequence(getMaxSeq(orgMetaData.getOrganization()) + 1);
        Session s = currentSession();
        String sql =
                " insert into org_std_metadata " +
                        " (code, name, create_date, update_date, create_user, update_user, description, organization, sequence, org_dataset, column_length, column_type) " +
                        " values " +
                        " (:code, :name, :create_date, :update_date, :create_user, :update_user, :description, :organization, :sequence, :org_dataset, :column_length, :column_type) ";
        Query q = s.createSQLQuery(sql);
        q.setParameter("code", orgMetaData.getCode());
        q.setParameter("name", orgMetaData.getName());
        q.setParameter("create_date", orgMetaData.getCreateDate());
        q.setParameter("update_date", orgMetaData.getUpdateDate());
        q.setParameter("create_user", orgMetaData.getCreateUser());
        q.setParameter("update_user", orgMetaData.getUpdateUser());
        q.setParameter("description", orgMetaData.getDescription());
        q.setParameter("organization", orgMetaData.getOrganization());
        q.setParameter("sequence", orgMetaData.getSequence());
        q.setParameter("org_dataset", orgMetaData.getOrgDataSet());
        q.setParameter("column_length", orgMetaData.getColumnLength());
        q.setParameter("column_type", orgMetaData.getColumnType());
        int rs = q.executeUpdate();
        return orgMetaData;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgMetaData(long id) {
        orgMetaDataRepository.delete(id);
        return 1;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgMetaDataList(Long[] ids) {
        Session session = currentSession();
        Query query = session.createQuery("delete from OrgMetaData where id in (:ids)");
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateOrgMetaData(OrgMetaData orgMetaData) {
        orgMetaDataRepository.save(orgMetaData);
        return 1;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrgMetaData> searchOrgMetaDatas(PageModel pageModel) {
        Session session = currentSession();
        String hql = "select metadata from OrgMetaData metadata  ";
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
        String hql = "select count(*) from OrgMetaData ";
        String wh = pageModel.format();
        hql += wh.equals("") ? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), pageModel);
        return ((Long) query.list().get(0)).intValue();
    }

    public int getMaxSeq(String orgCode) {
        Session session = currentSession();
        Query query = session.createQuery("select max(sequence) from OrgMetaData where organization = :orgCode");
        query.setParameter("orgCode", orgCode);
        Integer seq = (Integer) query.uniqueResult();
        if (seq == null) {
            seq = 0;
        }
        return seq;
    }
}
