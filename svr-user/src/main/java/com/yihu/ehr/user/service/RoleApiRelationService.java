package com.yihu.ehr.user.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yww on 2016/7/8.
 */
@Transactional
@Service
public class RoleApiRelationService extends BaseJpaService<RoleApiRelation,XRoleApiRelationRepository> {
    @Autowired
    private XRoleApiRelationRepository roleApiRelationRepository;

    public Page<RoleApiRelation> getRoleApiRelationList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return roleApiRelationRepository.findAll(pageable);
    }

    public RoleApiRelation findRelation(long apiId,long roleId){
        return  roleApiRelationRepository.findRelation(apiId, roleId);
    }

    public boolean batchCreateRoleApiRelation(long roleId,String apiIds){
        for(String apiId : apiIds.split(",")){
            RoleApiRelation roleApiRelation = new RoleApiRelation();
            roleApiRelation.setApiId(Long.parseLong(apiId));
            roleApiRelation.setRoleId(roleId);
            roleApiRelationRepository.save(roleApiRelation);
        }
        return true;
    }

    public boolean batchUpdateRoleApiRelation(long roleId,String apiIds) throws Exception{
        List<RoleApiRelation> deleteList = search("roleId=" + roleId + ";userId<>" + apiIds);
        for(RoleApiRelation m : deleteList){
            delete(m.getId());
        }
        List<RoleApiRelation> oldList = search("roleId=" + roleId + ";userI=" + apiIds);
        List<String> oldApiIds = new ArrayList<>();
        for(int i=0;i<oldList.size();i++){
            oldApiIds.add(oldList.get(i).getApiId()+"");
        }
        for(String apiId : apiIds.split(",")){
            if(oldApiIds.contains(apiId)){
                continue;
            }
            RoleApiRelation roleFeatureRelation = new RoleApiRelation();
            roleFeatureRelation.setRoleId(roleId);
            roleFeatureRelation.setApiId(Long.parseLong(apiId));
            save(roleFeatureRelation);
        }
        return true;
    }

}
