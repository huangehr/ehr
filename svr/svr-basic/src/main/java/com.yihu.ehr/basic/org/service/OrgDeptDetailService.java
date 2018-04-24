package com.yihu.ehr.basic.org.service;

import com.yihu.ehr.basic.org.dao.OrgDeptDetailRepository;
import com.yihu.ehr.basic.org.model.OrgDeptDetail;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
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
public class OrgDeptDetailService extends BaseJpaService<OrgDeptDetail, OrgDeptDetailRepository> {

    @Autowired
    private OrgDeptDetailRepository deptDetailRepository;

    public OrgDeptDetail searchByDeptId(Integer deptId) {
        List<OrgDeptDetail> orgDeptDetails = deptDetailRepository.searchByDeptId(deptId);
        if (orgDeptDetails != null && orgDeptDetails.size() > 0) {
            return orgDeptDetails.get(0);
        } else {
            return null;
        }
    }

    public String searchByOrgNameAndDeptName(String orgName,String deptName){
        String orgDeptDetailPlace = "";
        String sql = "select odd.* from org_dept_detail odd JOIN org_dept od on odd.dept_id=od.id JOIN organizations o on od.org_id =o.id where o.full_name=:orgName AND  od.name =:deptName";
        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("orgName", orgName);
        query.setParameter("deptName", deptName);
        List<Object> list  = query.list();
        if(null != list && list.size()>0){
            Object[] obj = (Object[]) list.get(0);
            orgDeptDetailPlace = obj[11] == null ? "":obj[11].toString();
        }
        return  orgDeptDetailPlace;
    }

}

