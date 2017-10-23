package com.yihu.ehr.user.service;

import com.yihu.ehr.org.dao.XOrgMemberRelationRepository;
import com.yihu.ehr.org.dao.XOrganizationRepository;
import com.yihu.ehr.org.model.OrgMemberRelation;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.user.dao.XRoleUserRepository;
import com.yihu.ehr.user.dao.XRolesRepository;
import com.yihu.ehr.user.dao.XUserRepository;
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
    @Autowired
    private XOrganizationRepository organizationRepository;
    @Autowired
    private XUserRepository userRepository;
    @Autowired
    private OrgMemberRelationService orgMemberRelationService;

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

    public String getIdCardNo(String orgCode) throws Exception{
        String idCardNos = "";
        if (!StringUtils.isEmpty(orgCode)) {
            String[] split = orgCode.split(",");
            List orgCodeList = java.util.Arrays.asList(split);
            //1、根据orgCode到organizations表获取orgId
            List<String> orgIdList = organizationRepository.getIdByOrgCode(orgCodeList);
            if (null != orgIdList && orgIdList.size() > 0) {
                //2、根据orgIdList到OrgMemberRelation表获取userId
                List<OrgMemberRelation> list = orgMemberRelationService.search("orgId=" + StringUtils.join(orgIdList, ","));
                List<String> userIdList = new ArrayList<>();
                for (OrgMemberRelation o : list) {
                    userIdList.add(o.getUserId());
                }
                if (null != userIdList && userIdList.size() > 0) {
                    //3、根据userId到users表获取用户idCardNo
                    List<String> idCardNoList = userRepository.findIdCardNoById(userIdList);
                    idCardNos = StringUtils.join(idCardNoList, ",");
                }
            }
        }
        return idCardNos;
    }
}
