package com.yihu.ehr.user.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.dao.XRoleFeatureRelationRepository;
import com.yihu.ehr.user.dao.XRoleUserRepository;
import com.yihu.ehr.user.entity.RoleFeatureRelation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
@Transactional
@Service
public class RoleFeatureRelationService extends BaseJpaService<RoleFeatureRelation,XRoleFeatureRelationRepository> {
    @Autowired
    private XRoleFeatureRelationRepository roleFeatureRelationRepository;

    @Autowired
    private XRoleUserRepository roleUserRepository;

    public Page<RoleFeatureRelation> getRoleUserList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return roleFeatureRelationRepository.findAll(pageable);
    }

    public RoleFeatureRelation findRelation(long featureId, long roleId){
        return roleFeatureRelationRepository.findRelation(featureId, roleId);
    }

    public boolean deleteRoleFeatureRelationByRoleId(Long roleId){
        Collection<RoleFeatureRelation> relations =findByField("roleId", roleId);
        if(relations.size()>0){
            List<Long> deleteIds = new ArrayList<>();
            for(RoleFeatureRelation relation:relations){
                deleteIds.add(relation.getId());
            }
            delete(deleteIds);
        }
        return true;
    }

    public boolean batchUpdateRoleFeatureRelation(Long roleId,Long[] addFeatureIds,String deleteFeatureIds) throws Exception{
        if(!StringUtils.isEmpty(deleteFeatureIds)){
            List<RoleFeatureRelation> deleteList = search("roleId=" + roleId + ";featureId=" +deleteFeatureIds);
            for(RoleFeatureRelation m : deleteList){
                delete(m.getId());
            }
        }
        if(addFeatureIds == null || addFeatureIds.length == 0){
            return true;
        }
        List<RoleFeatureRelation> relationList = findByField("roleId",roleId);
        List<Long> oldFeatureIds = new ArrayList<>();
        if(relationList != null && relationList.size()>0){
            for(int i=0;i<relationList.size();i++){
                oldFeatureIds.add(relationList.get(i).getFeatureId());
            }
        }
        for(long featureId : addFeatureIds){
            if(oldFeatureIds.contains(featureId)){
                continue;
            }
            RoleFeatureRelation roleFeatureRelation = new RoleFeatureRelation();
            roleFeatureRelation.setRoleId(roleId);
            roleFeatureRelation.setFeatureId(featureId);
            save(roleFeatureRelation);
        }
        return true;
    }

    public boolean hasPermission(String userId) {
        List<Long> roleIdList = roleUserRepository.findRoleIdByUserId(userId);
        if (null != roleIdList && roleIdList.size() > 0) {
            int num = roleFeatureRelationRepository.findNumByRoleIds(roleIdList);
            return num > 0 ? true : false;
        }
        return false;
    }
}
