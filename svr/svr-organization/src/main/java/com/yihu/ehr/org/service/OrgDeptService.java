package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.XOrgDeptRepository;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构- 部门业务类
 *
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@Service
@Transactional(rollbackFor={ServiceException.class})
public class OrgDeptService extends BaseJpaService<OrgDept, XOrgDeptRepository> {

    @Autowired
    private XOrgDeptRepository orgDeptRepository;

    public OrgDept searchBydeptId(Integer deptId) {
        return orgDeptRepository.findOne(deptId);
    }

    public List<OrgDept> searchByOrgId(String orgId) {
        return orgDeptRepository.searchByOrgId(orgId);
    }

    public List<OrgDept> searchByParentId(Integer parentDeptId) {
        return orgDeptRepository.searchByParentDeptId(parentDeptId);
    }

    public OrgDept saveOrgDept(OrgDept dept) {
        dept.setDelFlag(0);
        dept.setSortNo(1);// TODO set sortNo
        orgDeptRepository.save(dept);
        return dept;
    }

    public OrgDept updateOrgDept( Integer deptId, String name) {
        OrgDept dept = orgDeptRepository.findOne(deptId);
        dept.setName(name);
        orgDeptRepository.save(dept);
        return dept;
    }

    public OrgDept deleteOrgDept(Integer deptId) {
        OrgDept dept = orgDeptRepository.findOne(deptId);
        dept.setDelFlag(1);
        orgDeptRepository.save(dept);
        return dept;
    }

    /**
     *  交换部门排序
     * @param deptId1
     * @return
     */
    public void changeOrgDeptSort(Integer deptId1,Integer deptId2) {
        OrgDept orgDept1 = orgDeptRepository.findOne(deptId1);
        OrgDept orgDept2 = orgDeptRepository.findOne(deptId2);

        Integer sortNo1 = orgDept1.getSortNo();
        orgDept1.setSortNo(orgDept2.getSortNo());
        orgDeptRepository.save(orgDept1);

        orgDept2.setSortNo(sortNo1);
        orgDeptRepository.save(orgDept2);

    }

}

