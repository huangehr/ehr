package com.yihu.ehr.user.service;

import com.yihu.ehr.geography.service.GeographyService;
import com.yihu.ehr.org.dao.XOrgMemberRelationRepository;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.orgSaas.service.OrgSaasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
@Service
@Transactional
public class GetInfoService {

    @Autowired
    private XOrgMemberRelationRepository relationRepository;
    @Autowired
    private OrgService orgService;

    public List<String> getOrgCodes(String userId) {
        List<String> orgIds = getOrgIds(userId);
        List<String> orgCodes = new ArrayList<>();
        for (String s : orgIds) {
            String orgCode = orgService.getOrgCodeByOrgId(Long.valueOf(s));
            orgCodes.add(orgCode);
        }
        return orgCodes;
    }

    public List<String> getOrgIds(String userId) {
        List<String> userIds = relationRepository.findOrgIdByUserId(userId);
        return userIds;
    }
}
