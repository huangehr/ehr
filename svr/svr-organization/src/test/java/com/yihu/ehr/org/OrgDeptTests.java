package com.yihu.ehr.org;

import com.yihu.ehr.SvrOrgApplication;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgMemberRelation;
import com.yihu.ehr.org.service.OrgDeptService;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SvrOrgApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Transactional
public class OrgDeptTests {

    @Autowired
    private OrgDeptService orgDeptService;
    @Autowired
    private OrgMemberRelationService relationService;

    @Test
    public void saveDept(){
        OrgDept dept = new OrgDept();
        dept.setId(555);
        dept.setParentDeptId(1);
        dept.setName("测试部门添加");
        dept.setCode("test");
        dept.setOrgId("111");
        orgDeptService.saveOrgDept(dept);
    }

    @Test
    public void updateDept(){
        Integer deptId = 555;
        String deptName = "修改后的部门名称";
        OrgDept dept = orgDeptService.updateOrgDept(deptId,deptName);
    }

    @Test
    public void deleteDept(){
        Integer deptId = 555;
        OrgDept dept = orgDeptService.deleteOrgDept(deptId);
    }

    /* *********************** 部门成员 *************************/

    @Test
    public void saveDeptMember(){
        OrgMemberRelation deptMember = new OrgMemberRelation();
        deptMember.setParentDeptId(1);
        deptMember.setDeptId(555);
        deptMember.setUserName("测试部门中的成员1");
        deptMember.setStatus(0);
        deptMember.setOrgId("111");
        deptMember.setParentUserId("1");
        deptMember.setUserId("2");
        deptMember.setDeptName("职务：部长");
        deptMember.setRemark("成员描述");
        relationService.save(deptMember);
    }

    @Test
    public void deleteDeptMember(){
        Integer deptId = 555;
        OrgMemberRelation deptMember = relationService.deleteDeptMember(deptId);
    }

    @Test
    public void isHadDeptMember(){
        //部门是否有成员
        Integer deptId = 555;
        boolean succ = relationService.hadMemberRelation(deptId);
        System.out.println(succ);
    }

    @Test
    public void changeDeptSort(){
        orgDeptService.changeOrgDeptSort(5,6);
    }

}

