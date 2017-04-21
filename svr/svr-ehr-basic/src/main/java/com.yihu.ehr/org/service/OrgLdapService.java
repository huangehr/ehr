package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.XLdapEntriesRepository;
import com.yihu.ehr.org.dao.XOrgDeptRepository;
import com.yihu.ehr.org.dao.XOrgMemberRelationRepository;
import com.yihu.ehr.org.dao.XOrganizationRepository;
import com.yihu.ehr.org.model.LdapEntries;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgMemberRelation;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.util.ldap.LdapUtil;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 机构ldap服务
 */
@Service
@Transactional(rollbackFor = {ServiceException.class})
public class OrgLdapService {

    @Autowired
    private XOrganizationRepository organizationRepository;

    @Autowired
    private XOrgDeptRepository orgDeptRepository;

    @Autowired
    private XOrgMemberRelationRepository orgMemberRelationRepository;

    @Autowired
    private XLdapEntriesRepository ldapEntriesRepository;

    @Autowired
    LdapUtil ldapUtil;

    @Value("${spring.ldap.root}")
    String rootDn;

    private void importOrgDept(String orgCode,Integer patientDept,Long parent,String parentDn)
    {
         List<OrgDept> deptList = orgDeptRepository.findByOrgIdAndParentDeptId(orgCode,patientDept);

        if(deptList!=null && deptList.size()>0)
        {
            for(OrgDept dept :deptList)
            {
                String dn = "ou="+dept.getCode()+","+parentDn;

                LdapEntries item = new LdapEntries();
                item.setDn(dn);
                item.setParent(parent);
                item.setOcMapId(2);
                item.setKeyval(Long.valueOf(dept.getId()));
                ldapEntriesRepository.save(item);

                importOrgDept(orgCode,dept.getId(),item.getId(),dn);
            }
        }
    }


    /**
     * 初始化ldap机构科室数据
     */
    private void importOrgAndDept(Long rootId) {
        //获取所有有效机构
        List<Organization> orgList = organizationRepository.findAllOrg();

        if(orgList!=null && orgList.size()>0)
        {
            for(Organization org:orgList)
            {
                try {
                    String dn = "o=" + org.getOrgCode() + "," + rootDn;

                    LdapEntries item = new LdapEntries();
                    item.setDn(dn);
                    item.setParent(rootId);
                    item.setOcMapId(3);
                    item.setKeyval(org.getId());
                    ldapEntriesRepository.save(item);

                    importOrgDept(org.getOrgCode(), 0, item.getId(), dn);
                    importMember(org.getOrgCode());
                }
                catch (Exception ex)
                {
                     System.out.print("机构"+org.getOrgCode()+"导入失败！"+ ex.getMessage());
                }
            }
        }

    }

    /**
     *  初始化ldap机构成员数据
     */
    private void importMember(String orgCode) throws Exception {
        //获取所有有效机构
        List<OrgMemberRelation> memberList = orgMemberRelationRepository.findByOrgId(orgCode);
        if(memberList!=null && memberList.size()>0)
        {
            List<LdapEntries> newList = new ArrayList<>();
            for(OrgMemberRelation member:memberList)
            {
                //找到所属科室
                LdapEntries parent = ldapEntriesRepository.findByOcMapIdAndKeyval(2,Long.valueOf(member.getDeptId()));
                if(parent!=null)
                {
                    String dn = "cn="+member.getId()+ ","+parent.getDn();
                    LdapEntries newEntries = new LdapEntries();
                    newEntries.setDn(dn);
                    newEntries.setParent(parent.getId());
                    newEntries.setOcMapId(1);
                    newEntries.setKeyval(Long.valueOf(member.getId()));
                    newList.add(newEntries);
                }
                else{
                    System.out.print("导入成员"+member.getUserId()+"失败,机构"+orgCode+"科室"+member.getDeptId()+"不存在！");
                }
            }
            ldapEntriesRepository.save(newList);
        }

    }

    //**************************************************************************************//*
    /**
      初始化Ldap数据
     */
    @Transactional
    public void importLdapData() throws Exception
    {
        //清空数据
        ldapEntriesRepository.deleteAll();
        ldapEntriesRepository.findAll();
        //创建根节点
        LdapEntries root = new LdapEntries();
        root.setDn(rootDn);
        root.setKeyval(Long.valueOf(0));
        root.setParent(Long.valueOf(0));
        root.setOcMapId(4);
        ldapEntriesRepository.save(root);

        importOrgAndDept(root.getId());
    }

    private String getObjectClass(Integer type)
    {
        String objectClass = "*";
        if(type!=null)
        {
            if(type.equals(1))
            {
                objectClass = "inetOrgPerson";
            }
            else if(type.equals(2))
            {
                objectClass = "organizationalUnit";
            }
            else if(type.equals(3))
            {
                objectClass = "organization";
            }
            else{
                objectClass = "null";
            }
        }

        return objectClass;
    }

    /**
       节点类型，1用户 2科室 3机构 为空全部
     */
    public List<Map<String,Object>> queryAllByObjectClass(String searchDN, Integer type) throws Exception
    {
        String objectClass = getObjectClass(type);
        return ldapUtil.queryAllByObjectClass(searchDN,objectClass);
    }

    /**
       节点类型，1用户 2科室 3机构 为空全部
     */
    public List<Map<String,Object>> queryChildren(String searchDN, Integer type) throws Exception
    {
        String objectClass = getObjectClass(type);
        return ldapUtil.queryChildren(searchDN,objectClass);
    }

     /**
       节点类型，1用户 2科室 3机构 为空全部
     */
    public Map<String,Object> queryBase(String searchDN, Integer type) throws Exception
    {
        String objectClass = getObjectClass(type);
        return ldapUtil.queryBase(searchDN,objectClass);
    }

    /**
       节点类型，1用户 2科室 3机构 为空全部
     */
    public List<Map<String,Object>> queryAllWithoutSelf(String searchDN, Integer type) throws Exception
    {
        String objectClass = getObjectClass(type);
        return ldapUtil.queryAllWithoutSelf(searchDN,objectClass);
    }
}

