package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.XOrgDeptDetailRepository;
import com.yihu.ehr.org.model.OrgDeptDetail;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构- 部门下科室业务类
 *
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@Service
@Transactional(rollbackFor = {ServiceException.class})
public class OrgDeptDetailService extends BaseJpaService<OrgDeptDetail, XOrgDeptDetailRepository> {

    @Autowired
    private XOrgDeptDetailRepository deptDetailRepository;


    public OrgDeptDetail searchByDeptId(Integer deptId) {
        List<OrgDeptDetail> orgDeptDetails = deptDetailRepository.searchByDeptId(deptId);
        if (orgDeptDetails != null && orgDeptDetails.size() > 0) {
            return orgDeptDetails.get(0);
        } else {
            return null;
        }
    }

}

