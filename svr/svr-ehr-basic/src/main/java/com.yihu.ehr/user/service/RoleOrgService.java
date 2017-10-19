package com.yihu.ehr.user.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.dao.XRoleOrgRepository;
import com.yihu.ehr.user.dao.XRolesRepository;
import com.yihu.ehr.user.entity.RoleOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/10/18.
 */
@Transactional
@Service
public class RoleOrgService extends BaseJpaService<RoleOrg,XRoleOrgRepository> {
    @Autowired
    private XRoleOrgRepository roleOrgRepository;
    @Autowired
    private XRolesRepository rolesRepository;


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
