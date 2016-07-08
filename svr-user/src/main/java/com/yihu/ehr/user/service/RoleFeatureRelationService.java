package com.yihu.ehr.user.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yww on 2016/7/7.
 */
@Transactional
@Service
public class RoleFeatureRelationService extends BaseJpaService<RoleFeatureRelation,XRoleFeatureRelationRepository> {
    @Autowired
    private XRoleFeatureRelationRepository roleFeatureRelationRepository;

    public Page<RoleFeatureRelation> getRoleUserList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return roleFeatureRelationRepository.findAll(pageable);
    }

}
