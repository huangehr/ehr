package com.yihu.ehr.adaption.orgdictitem.service;

import com.yihu.ehr.adaption.commons.BaseManager;
import com.yihu.ehr.adaption.orgdict.service.OrgDict;
import com.yihu.ehr.util.parm.PageModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构字典明细管理器。
 *
 * @author lincl
 * @version 1.0
 * @created 2016.1.29
 */

@Service
public class OrgDictItemManager extends BaseManager<OrgDictItem, XOrgDictItemRepository> {

    @Autowired
    XOrgDictItemRepository orgDictItemRepository;

    @Transactional(propagation = Propagation.SUPPORTS)
    public OrgDictItem getOrgDictItem(long id) {
        return orgDictItemRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isExistOrgDictItem(int orgDictSeq, String orgCode, String code) {
        return orgDictItemRepository.isExistOrgDictItem(orgDictSeq, orgCode, code).size() != 0;
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
    public int deleteOrgDictItem(long id) {
        Query query = currentSession().createQuery("delete from OrgDictItem where id = :id");
        query.setLong("id", id);
        query.executeUpdate();
        return 1;
    }

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

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteOrgDictItemList(Long[] ids) {
        Session session = currentSession();
        Query query = session.createQuery("delete from OrgDictItem where id in (:ids)");
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateOrgDictItem(OrgDictItem orgDictItem) {
        orgDictItemRepository.save(orgDictItem);
        return 1;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrgDictItem> searchOrgDictItems(PageModel pageModel) {
        Session session = currentSession();
        String hql = "select orgDictItem from OrgDictItem orgDictItem  ";
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
        String hql = "select count(*) from OrgDictItem ";
        String wh = pageModel.format();
        hql += wh.equals("") ? "" : wh;
        Query query = setQueryVal(session.createQuery(hql), pageModel);
        return ((Long) query.list().get(0)).intValue();
    }

    /**
     * 删除字典关联的所有字典项
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


    /////////*******************************  待定 *****************************//////
//
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public List<XOrgDictItem> getAllOrgDictItem(Map<String, Object> conditionMap) {
//        String orgCode = (String) conditionMap.get("orgCode");
//        String code = (String) conditionMap.get("code");
//        Integer page = (Integer) conditionMap.get("page");
//        Integer rows = (Integer) conditionMap.get("rows");
//
//        Session session = currentSession();
//        //动态SQL文拼接
//        StringBuilder sb = new StringBuilder();
//        sb.append("from OrgDictItem where 1=1 and organization = '"+orgCode+"'");
//        if (!(code==null || code.equals(""))) {
//            sb.append(" and (code like '%" + code + "%' or name like '%" + code + "%')");
//        }
//        sb.append(" order by  name asc");
//        String hql = sb.toString();
//        Query query = session.createQuery(hql);
//        if (page!=null&&page>0){
//            query.setMaxResults(rows);
//            query.setFirstResult((page - 1) * rows);
//        }
//        return query.list();
//    }
//
//    @Transactional(propagation= Propagation.SUPPORTS)
//    public List<XOrgDictItem> getAllOrgDictItemForDospatch(String orgCode) {
//
//        List<XOrgDictItem> xOrgDictItem = new ArrayList<>();
//        try {
//            String sql = "SELECT " +
//                    "a.id, " +
//                    "a.`code`," +
//                    "a.`name`," +
//                    "b.id dict_id," +
//                    "a.description " +
//                    "from org_std_dictentry a " +
//                    "left JOIN org_std_dict b on b.sequence = a.org_dict and a.organization=b.organization " +
//                    "where a.organization='" + orgCode + "'";
//
//            Session session = currentSession();
//            Query query = session.createSQLQuery(sql);
//            List<Object> records = query.list();
//
//
//            for (int i = 0; i < records.size(); ++i) {
//                Object[] record = (Object[]) records.get(i);
//
//                OrgDictItem orgDictItem = new OrgDictItem();
//                orgDictItem.setId(Long.parseLong(record[0].toString()));
//                orgDictItem.setCode(record[1].toString());
//                orgDictItem.setName(record[2].toString());
//                orgDictItem.setOrgDict(Integer.parseInt(record[3].toString()));
//                orgDictItem.setDescription(record[4] == null ? "" : record[4].toString());
//                xOrgDictItem.add(orgDictItem);
//            }
//        }
//        catch (Exception ex)
//        {
//            LogService.getLogger(OrgDictItemManager.class).error(ex.getMessage());
//        }
//        return xOrgDictItem;
//    }
}
