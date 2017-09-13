package com.yihu.ehr.user.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.dao.XRoleReportRelationRepository;
import com.yihu.ehr.user.entity.RoleReportRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wxw on 2017/8/22.
 */
@Transactional
@Service
public class RoleReportRelationService extends BaseJpaService<RoleReportRelation, XRoleReportRelationRepository> {

    @Autowired
    private XRoleReportRelationRepository roleReportRelationRepository;

    public void deleteByRoleId(Long roleId) {
        roleReportRelationRepository.deleteByRoleId(roleId);
    }
}
