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
 * Created by yww on 2016/7/7.
 */
@Transactional
@Service
public class RoleUserService extends BaseJpaService<RoleUser,XRoleUserRepository> {
    @Autowired
    private XRoleUserRepository roleUserRepository;

    public Page<RoleUser> getRoleUserList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return roleUserRepository.findAll(pageable);
    }

    public RoleUser findRelation(String userId,long roleId){
        return roleUserRepository.findRelation(userId, roleId);
    }

    public boolean batchDeleteRoleUserRelation(String userId,String roleIds) throws Exception{
        List<RoleUser> roleUsers = this.search("userId=" + userId + ",roleId=" + roleIds);
        for(RoleUser roleUser : roleUsers){
            delete(roleUser.getId());
        }
        return true;
    }

    public boolean batchCreateRoleUsersRelation(String userId,String roleIds){
        for(String roleId : roleIds.split(",")){
            RoleUser roleUser = new RoleUser();
            roleUser.setUserId(userId);
            roleUser.setRoleId(Long.parseLong(roleId));
            save(roleUser);
        }
        return true;
    }

    public boolean batchUpdateRoleUsersRelation(String userId,String roleIds) throws Exception {
        List<RoleUser> roleUsers = search("userId=" + userId + ";roleId<>" + roleIds);
        for (RoleUser roleUser : roleUsers) {
            delete(roleUser.getId());
        }
        List<RoleUser> roleUserList = search("userId=" + userId + ";roleId=" + roleIds);
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < roleUserList.size(); i++) {
            ids.add(roleUserList.get(i).getRoleId());
        }
        for (String roleId : roleIds.split(",")) {
            if (ids.contains(Long.parseLong(roleId))) {
                continue;
            }
            RoleUser roleUser = new RoleUser();
            roleUser.setUserId(userId);
            roleUser.setRoleId(Long.parseLong(roleId));
            save(roleUser);
        }
        return true;
    }
}
