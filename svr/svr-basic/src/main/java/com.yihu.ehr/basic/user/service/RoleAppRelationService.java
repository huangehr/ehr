package com.yihu.ehr.basic.user.service;

import com.yihu.ehr.basic.user.dao.RoleAppRelationDao;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
@Transactional
@Service
public class RoleAppRelationService extends BaseJpaService<RoleAppRelation,RoleAppRelationDao> {
    @Autowired
    private RoleAppRelationDao roleAppRelationRepository;

    public RoleAppRelation findRelation(String appId,long roleId){
        return  roleAppRelationRepository.findRelation(appId, roleId);
    }

    public Page<RoleAppRelation> getRoleAppList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return roleAppRelationRepository.findAll(pageable);
    }

    public boolean batchCreateRoleAppRelation(String appId,String roleIds){
        for(String roleId : roleIds.split(",")){
            RoleAppRelation relation = new RoleAppRelation();
            relation.setAppId(appId);
            relation.setRoleId(Long.parseLong(roleId));
            save(relation);
        }
        return true;
    }

    public boolean batchUpdateRoleAppRelation(String appId,String roleIds) throws Exception {
        List<RoleAppRelation> roleAppRelations = search("appId=" + appId + ";roleId<>" + roleIds);
        for (RoleAppRelation relation : roleAppRelations) {
            delete(relation.getId());
        }
        List<RoleAppRelation> roleAppRelationList = search("appId=" + appId + ";roleId=" + roleIds);
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < roleAppRelationList.size(); i++) {
            ids.add(roleAppRelationList.get(i).getRoleId());
        }
        for (String roleId : roleIds.split(",")) {
            if (ids.contains(Long.parseLong(roleId))) {
                continue;
            }
            RoleAppRelation relation = new RoleAppRelation();
            relation.setAppId(appId);
            relation.setRoleId(Long.parseLong(roleId));
            save(relation);
        }
        return true;
    }
}
