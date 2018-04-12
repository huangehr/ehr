package com.yihu.ehr.basic.user.service;

import com.yihu.ehr.basic.user.dao.RoleApiRelationDao;
import com.yihu.ehr.basic.user.entity.RoleApiRelation;
import com.yihu.ehr.query.BaseJpaService;
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
 * Created by yww on 2016/7/8.
 */
@Transactional
@Service
public class RoleApiRelationService extends BaseJpaService<RoleApiRelation,RoleApiRelationDao> {
    @Autowired
    private RoleApiRelationDao roleApiRelationRepository;

    public Page<RoleApiRelation> getRoleApiRelationList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return roleApiRelationRepository.findAll(pageable);
    }

    public RoleApiRelation findRelation(long apiId,long roleId){
        return  roleApiRelationRepository.findRelation(apiId, roleId);
    }

    public boolean deleteRoleApiRelationByRoleId(Long roleId){
        Collection<RoleApiRelation> relations =findByField("roleId", roleId);
        if (relations.size()>0){
            List<Long> deleteIds = new ArrayList<>();
            for (RoleApiRelation relation:relations){
                deleteIds.add(relation.getId());
            }
            delete(deleteIds);
        }
        return true;
    }

    public boolean batchUpdateRoleApiRelation(Long roleId,Long[] addApiIds,String deleteApiIds) throws Exception{
        if(!StringUtils.isEmpty(deleteApiIds)){
            List<RoleApiRelation> deleteList = search("roleId=" + roleId + ";apiId=" +deleteApiIds);
            for(RoleApiRelation m : deleteList){
                delete(m.getId());
            }
        }
        if(addApiIds == null || addApiIds.length == 0){
            return true;
        }
        List<RoleApiRelation> relationList = findByField("roleId",roleId);
        List<Long> oldApiIds = new ArrayList<>();
        if(relationList != null && relationList.size()>0){
            for(int i=0;i<relationList.size();i++){
                oldApiIds.add(relationList.get(i).getApiId());
            }
        }
        for (long apiId : addApiIds){
            if(oldApiIds !=null && oldApiIds.contains(apiId)){
                continue;
            }
            RoleApiRelation roleApiRelation = new RoleApiRelation();
            roleApiRelation.setRoleId(roleId);
            roleApiRelation.setApiId(apiId);
            save(roleApiRelation);
        }
        return true;
    }

    public void deleteByApiIdAndRoleId(long apiId, long roleId) {
        roleApiRelationRepository.deleteByApiIdAndRoleId(apiId, roleId);
    }
}
