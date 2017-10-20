package com.yihu.ehr.user.service;

import com.yihu.ehr.org.dao.XOrgMemberRelationRepository;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.user.dao.XRoleUserRepository;
import com.yihu.ehr.user.dao.XRolesRepository;
import org.apache.commons.lang.StringUtils;
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
    @Autowired
    private XRoleUserRepository roleUserRepository;
    @Autowired
    private XRolesRepository rolesRepository;

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

    /**
     * 获取用户所在角色组列表
     */
    public List<Long> getRolesIdByUserId(String userId) {
        List<Long> roleIdList = roleUserRepository.findRoleIdByUserId(userId);
        return roleIdList;
    }

    /**
     * 获取角色所对应的appId
     * @param userId
     * @return 例：格式为1002,1003...
     */
    public String getAppsId(String userId) {
        List<Long> roleIdList = getRolesIdByUserId(userId);
        String appsId = "";
        if (null != roleIdList && roleIdList.size() > 0) {
            List<String> appIdList = rolesRepository.findAppIdById(roleIdList);
            appsId = StringUtils.join(appIdList,",");
        }
        return appsId;
    }
}
