/****************************************************************************
 * Copyright(c) Yamaha Motor Solutions CO.,Ltd. 2010 All Rights Reserved
 * <p>
 * System Name：(smart)Human Resource Management System
 * SubSystem Name：-
 * service for all substystems
 * <p>
 * File Name: DateUtil
 * <p>
 * HISTORY RECORD
 * Ver.   Date           Create User/Update     Comment
 * -------------------------------------------------------------------------
 * 1.0 　  2010/07/12 　  tuchengye              New Making
 ***************************************************************************/
package com.yihu.ehr.util.ldap;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.SubentriesRequestControl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * LDAP操作类
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LdapUtil {

    @Value("${spring.ldap.host}")
    String ldapHost;

    @Value("${spring.ldap.port}")
    Integer ldapPort;

    @Value("${spring.ldap.bindDn}")
    String ldapBindDN;

    @Value("${spring.ldap.password}")
    String ldapPassword;



    // 当前配置信息
    /*private static String ldapHost = "172.19.103.87";
    private static int ldapPort = 389;
    private static String ldapBindDN = "cn=admin,dc=ehr,dc=jkzl";
    private static String ldapPassword = "jkzl";*/
    private LDAPConnection connection = null;

    /** 连接LDAP */
    private void openConnection() throws Exception {
        if (connection == null) {
            try {
                connection = new LDAPConnection(ldapHost, ldapPort, ldapBindDN, ldapPassword);
            } catch (Exception e) {
                System.out.println("连接LDAP出现错误：\n" + e.getMessage());
            }

        }

    }

    /** 创建DC */
    public void createDC(String baseDN, String dc) {
        String entryDN = "dc=" + dc + "," + baseDN;
        try {
            // 连接LDAP
            openConnection();

            SearchResultEntry entry = connection.getEntry(entryDN);
            if (entry == null) {
                // 不存在则创建
                ArrayList<Attribute> attributes = new ArrayList<Attribute>();
                attributes.add(new Attribute("objectClass", "top", "organization", "dcObject"));
                attributes.add(new Attribute("dc", dc));
                attributes.add(new Attribute("o", dc));
                connection.add(entryDN, attributes);
                System.out.println("创建DC" + entryDN + "成功！");
            } else {
                System.out.println("DC " + entryDN + "已存在！");
            }
        } catch (Exception e) {
            System.out.println("创建DC出现错误：\n" + e.getMessage());
        }
    }

    /** 创建组织 */
    public void createO(String baseDN, String o) {
        String entryDN = "o=" + o + "," + baseDN;
        try {
            // 连接LDAP
            openConnection();

            SearchResultEntry entry = connection.getEntry(entryDN);
            if (entry == null) {
                // 不存在则创建
                ArrayList<Attribute> attributes = new ArrayList<Attribute>();
                attributes.add(new Attribute("objectClass", "top", "organization"));
                attributes.add(new Attribute("o", o));
                connection.add(entryDN, attributes);
                System.out.println("创建组织" + entryDN + "成功！");
            } else {
                System.out.println("组织" + entryDN + "已存在！");
            }
        } catch (Exception e) {
            System.out.println("创建组织出现错误：\n" + e.getMessage());
        }
    }

    /** 创建组织单元 */
    public void createOU(String baseDN, String ou) {
        String entryDN = "ou=" + ou + "," + baseDN;
        try {
            // 连接LDAP
            openConnection();

            SearchResultEntry entry = connection.getEntry(entryDN);
            if (entry == null) {
                // 不存在则创建
                ArrayList<Attribute> attributes = new ArrayList<Attribute>();
                attributes.add(new Attribute("objectClass", "top", "organizationalUnit"));
                attributes.add(new Attribute("ou", ou));
                connection.add(entryDN, attributes);
                System.out.println("创建组织单元" + entryDN + "成功！");
            } else {
                System.out.println("组织单元" + entryDN + "已存在！");
            }
        } catch (Exception e) {
            System.out.println("创建组织单元出现错误：\n" + e.getMessage());
        }
    }

    /*************************************** 用户操作 ******************************************************************/
    /** 创建用户 */
    public void createEntry(String baseDN, String uid) {
        String entryDN = "uid=" + uid + "," + baseDN;
        try {
            // 连接LDAP
            openConnection();

            SearchResultEntry entry = connection.getEntry(entryDN);
            if (entry == null) {
                // 不存在则创建
                ArrayList<Attribute> attributes = new ArrayList<Attribute>();
                attributes.add(new Attribute("objectClass", "top", "account"));
                attributes.add(new Attribute("uid", uid));
                connection.add(entryDN, attributes);
                System.out.println("创建用户" + entryDN + "成功！");
            } else {
                System.out.println("用户" + entryDN + "已存在！");
            }
        } catch (Exception e) {
            System.out.println("创建用户出现错误：\n" + e.getMessage());
        }
    }

    /** 修改用户信息 */
    public void modifyEntry(String requestDN, Map<String,String> data) {
        try {
            // 连接LDAP
            openConnection();

            SearchResultEntry entry = connection.getEntry(requestDN);
            if (entry == null) {
                System.out.println(requestDN + " user:" + requestDN + " 不存在");
                return;
            }
            // 修改信息
            ArrayList<Modification> md = new ArrayList<Modification>();
            for(String key : data.keySet()) {
                md.add(new Modification(ModificationType.REPLACE, key, data.get(key)));
            }
            connection.modify(requestDN, md);

            System.out.println("修改用户信息成！");
        } catch (Exception e) {
            System.out.println("修改用户信息出现错误：\n" + e.getMessage());
        }
    }

    /** 删除用户信息 */
    public void deleteEntry(String requestDN) {
        try {
            // 连接LDAP
            openConnection();

            SearchResultEntry entry = connection.getEntry(requestDN);
            if (entry == null) {
                System.out.println(requestDN + " user:" + requestDN + "不存在");
                return;
            }
            // 删除
            connection.delete(requestDN);
            System.out.println("删除用户信息成！");
        } catch (Exception e) {
            System.out.println("删除用户信息出现错误：\n" + e.getMessage());
        }
    }

    /************************************* 查询接口 **********************************************************/

    /**
     * 根据对象类型查询某节点下所有
     */
    public List<Map<String,Object>> queryAllByObjectClass(String searchDN, String objectClass) {
        return queryLdap(SearchScope.SUB,searchDN,objectClass);
    }

    /**
     * 查询一级子节点
     */
    public List<Map<String,Object>> queryChildren(String searchDN, String objectClass) {
        return queryLdap(SearchScope.ONE,searchDN,objectClass);
    }

    /**
     * 查询单个子节点
     */
    public Map<String,Object> queryBase(String searchDN, String objectClass) {
        List<Map<String,Object>> list = queryLdap(SearchScope.BASE,searchDN,objectClass);
        if(list!=null && list.size()>0)
        {
            return list.get(0);
        }
        else{
            return null;
        }
    }

    /**
     * 查询所有子节点(不包含自己)
     */
    public List<Map<String,Object>> queryAllWithoutSelf(String searchDN, String objectClass) {
        return queryLdap(SearchScope.SUBORDINATE_SUBTREE,searchDN,objectClass);
    }

    /** 查询接口 */
    private List<Map<String,Object>> queryLdap(SearchScope searchScope,String searchDN, String objectClass) {
        List<Map<String,Object>> re = new ArrayList<>();
        try {
            // 连接LDAP
            openConnection();

            // 查询企业所有用户
            SearchRequest searchRequest = new SearchRequest(searchDN, searchScope, "(objectClass=" + objectClass + ")");
            searchRequest.addControl(new SubentriesRequestControl());
            SearchResult searchResult = connection.search(searchRequest);
            System.out.println(">>>共查询到" + searchResult.getSearchEntries().size() + "条记录");

            if(searchResult!=null && searchResult.getSearchEntries().size()>0)
            {
                int index = 1;
                for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                    Map<String,Object> obj = new HashMap<>();
                    obj.put("dn",entry.getDN());
                    Iterator<Attribute> attrList = entry.getAttributes().iterator();
                    while (attrList.hasNext())
                    {
                        Attribute attr = attrList.next();
                        obj.put(attr.getName(),attr.getValue());
                    }
                    System.out.println((index++) + "\t" + entry.getDN());
                    re.add(obj);
                }
            }

        } catch (Exception e) {
            System.out.println("查询错误，错误信息如下：\n" + e.getMessage());
        }

        return re;
    }


}