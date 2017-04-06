package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.XOrgMemberRelationRepository;
import com.yihu.ehr.org.model.OrgMemberRelation;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/16.
 */
@Service
@Transactional
public class OrgMemberRelationService extends BaseJpaService<OrgMemberRelation, XOrgMemberRelationRepository> {

    @Autowired
    private XOrgMemberRelationRepository relationRepository;

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
}
