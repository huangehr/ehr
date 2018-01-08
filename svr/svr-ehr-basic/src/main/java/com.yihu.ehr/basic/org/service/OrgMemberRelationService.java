package com.yihu.ehr.basic.org.service;

import com.yihu.ehr.basic.org.dao.OrgMemberRelationRepository;
import com.yihu.ehr.basic.org.model.OrgMemberRelation;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/16.
 */
@Service
@Transactional
public class OrgMemberRelationService extends BaseJpaService<OrgMemberRelation, OrgMemberRelationRepository> {

    @Autowired
    private OrgMemberRelationRepository relationRepository;
    @Autowired
    private OrgService orgService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<OrgMemberRelation> searchByDeptId(Integer deptId) {
        return relationRepository.searchByDeptId(deptId);
    }

    /**
     * 部门 是否存在成员
     * @param deptId    部门id
     * @return
     */
    public boolean hadMemberRelation(Integer deptId){
        Integer count = relationRepository.countByDeptId(deptId);
        if (count!=null && count>0){
            return true;
        }else {
            return false;
        }
    }

    public OrgMemberRelation updateStatusDeptMember(Integer memRelationId,int status){
        OrgMemberRelation relation = relationRepository.findOne(memRelationId);
        relation.setStatus(status);
        relationRepository.save(relation);
        return relation;
    }

    public void deleteDeptMember(Integer memRelationId){
        relationRepository.delete(memRelationId);
    }

    public OrgMemberRelation getOrgMemberRelation(Long memRelationId) {
        OrgMemberRelation relation = relationRepository.findOne(Integer.valueOf( String.valueOf(memRelationId) ) );
        return relation;
    }

    public int getCountByOrgIdAndUserId(String orgId,String userId, Integer deptId) {
        List<OrgMemberRelation> orgOrgMemberRelations = relationRepository.searchByOrgIdAndUserId(orgId, userId, deptId);
        if (orgOrgMemberRelations!=null && !orgOrgMemberRelations.isEmpty()){
            return orgOrgMemberRelations.size();
        }else {
            return 0;
        }
    }

    public List<String> getOrgIds(String userId) {
        List<String> userIds = relationRepository.findOrgIdByUserId(userId);
        return userIds;
    }

    public List<String> getOrgCodes(String userId) {
        List<String> orgIds = this.getOrgIds(userId);
        List<String> orgCodes = new ArrayList<>();
        for (String s : orgIds) {
            String orgCode = orgService.getOrgCodeByOrgId(Long.valueOf(s));
            orgCodes.add(orgCode);
        }
        return orgCodes;
    }

    public List<String> getUserIdByOrgId(List<String> orgId) {
        return relationRepository.findUserIdByOrgId(orgId);
    }

    public List<Integer> getDeptIds(String userId) {
        List<Integer> deptIds = relationRepository.findDeptIdByUserId(userId);
        return deptIds;
    }

    public void updateByOrgId(String[] orgId,String userId) {
        relationRepository.updateByOrgId(orgId, userId);
    }

    public List<OrgMemberRelation> getByUserId(String userId) {
        List<OrgMemberRelation> relationList = relationRepository.findByUserId(userId);
        return relationList;
    }

    /**
     *  查询机构人员去重复  分页
     * @param orgId
     * @param searchParm
     * @param size
     * @param page
     * @return
     */
    public List<OrgMemberRelation> getOrgDeptMembers(String orgId,String searchParm, int size, int page) {
        Session session = entityManager.unwrap(Session.class);
        String sql="select distinct r.userId as userId,r.userName as userName from OrgMemberRelation r where r.status=0 and r.orgId=:orgId and r.userName like :searchParm";
        Query query = session.createQuery(sql);
        query.setResultTransformer(Transformers.aliasToBean(OrgMemberRelation.class));
        query.setString("orgId", orgId);
        query.setString("searchParm","%"+searchParm+"%");
        query.setMaxResults(size);
        query.setFirstResult((page - 1) * size);
        List<OrgMemberRelation> list;
        list = query.list();
        return list;
    }
    /**
     *  查询机构人员数量去重复
     * @param orgId
     * @param searchParm
     * @return
     */
    public Integer getOrgDeptMembersInt(String orgId,String searchParm) {
        Session session = entityManager.unwrap(Session.class);
        String sql="select count(distinct r.userId) from OrgMemberRelation r where r.status=0 and r.orgId=:orgId and r.userName like :searchParm";
        Query query = session.createQuery(sql);
        query.setString("orgId", orgId);
        query.setString("searchParm","%"+searchParm+"%");
        return ((Long)query.list().get(0)).intValue();
    }
    /**
     *  查询机构人员去重复
     * @param orgId
     * @param searchParm
     * @return
     */
    public List<OrgMemberRelation> getAllOrgDeptMemberDistinct(String orgId,String searchParm) {
        Session session = entityManager.unwrap(Session.class);
        String sql="select distinct r.orgId as orgId,r.orgName as orgName,coalesce(r.parentUserId,'') as parentUserId,coalesce(r.parentUserName,'') as parentUserName," +
                " r.userId as userId,r.userName as userName  from OrgMemberRelation r" +
                " where r.status=0 and r.orgId=:orgId and r.userName like :searchParm";
        Query query = session.createQuery(sql);
        query.setResultTransformer(Transformers.aliasToBean(OrgMemberRelation.class));
        query.setString("orgId", orgId);
        query.setString("searchParm","%"+searchParm+"%");
        List<OrgMemberRelation> list = query.list();
        return list;
    }

    /**
     * 修改上级成员
     * @param model
     */
    public void updateOrgDeptMemberParent(OrgMemberRelation model){
        List<OrgMemberRelation> relationList = relationRepository.findByUserId(model.getUserId());
        for(OrgMemberRelation item : relationList){
            item.setParentUserId(model.getParentUserId());
            item.setParentUserName(model.getParentUserName());
            save(item);
        }
    }
}
