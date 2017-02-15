package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.XOrgDeptRepository;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *  机构- 部门业务类
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@Service
@Transactional
public class OrgDeptService  extends BaseJpaService<OrgDept, XOrgDeptRepository> {

    @Autowired
    private XOrgDeptRepository orgDeptRepository;


    public List<OrgDept> searchByOrgId(String orgId){
        return orgDeptRepository.searchByOrgId(orgId);
    }

    public List<OrgDept> searchByParentId(Integer parentDeptId){
        return orgDeptRepository.searchByParentDeptId(parentDeptId);
    }



}

