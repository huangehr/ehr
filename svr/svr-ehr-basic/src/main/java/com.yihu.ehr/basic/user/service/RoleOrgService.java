package com.yihu.ehr.basic.user.service;

import com.yihu.ehr.basic.user.dao.XRoleOrgRepository;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.basic.user.entity.RoleOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by janseny on 2017/10/18.
 */
@Transactional
@Service
public class RoleOrgService extends BaseJpaService<RoleOrg,XRoleOrgRepository> {
    @Autowired
    private XRoleOrgRepository roleOrgRepository;

    /**
     * 删除
     * @param id
     */
    public void deleteRoleOrg(Long id) {
        roleOrgRepository.delete(id);
    }

    public RoleOrg findRelation(String roleId,String orgCode){
        return roleOrgRepository.findRelation(Long.valueOf(roleId),orgCode);
    }

}
