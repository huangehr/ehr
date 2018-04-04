package com.yihu.ehr.basic.user.service;

import com.yihu.ehr.basic.user.dao.XRolesRepository;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by yww on 2016/7/7.
 */
@Transactional
@Service
public class RolesService extends BaseJpaService<Roles,XRolesRepository> {
    @Autowired
    private XRolesRepository rolesRepository;

    public Page<Roles> getRolesList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return rolesRepository.findAll(pageable);
    }

    public Roles getRoleByRoleId(long roleId) {
        return rolesRepository.findById(roleId);
    }

    public List<Roles> getRoleByAppId(String appId) {
        return rolesRepository.findByAppId(appId);
    }

    public List<Map<String,Object>> findRolesByUserIdAndAppId(String userId, String appId){
        String sql = "select DISTINCT r.code,r.`name`,r.id FROM roles r JOIN role_user u ON r.id = u.role_id WHERE u.user_id =? AND r.app_id = ?";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,new Object[]{userId,appId});
        return list;
    }

    public Roles findByCodeAndAppId(String code, String appId) {
        return rolesRepository.findByCodeAndAppId(code, appId);
    }
}
