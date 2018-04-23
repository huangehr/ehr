package com.yihu.ehr.basic.org.service;

import com.yihu.ehr.basic.org.model.Organization;
import com.yihu.ehr.basic.org.dao.OrgDeptRepository;
import com.yihu.ehr.basic.org.model.OrgDept;
import com.yihu.ehr.basic.org.model.OrgDeptDetail;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/15.
 */
@Service
@Transactional
public class OrgDeptImportService extends BaseJpaService<OrgDept,OrgDeptRepository> {

    @Autowired
    private OrgService orgService;
    @Autowired
    private OrgDeptService orgDeptService;
    @Autowired
    private OrgDeptDetailService orgDeptDetailService;

    public List codeExistence(String[] code) {
        String sql = "SELECT code FROM Org_Dept WHERE code in(:code)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("code", code);
        return sqlQuery.list();
    }

    public List nameExistence(String[] name) {
        String sql = "SELECT name FROM Org_Dept WHERE name in(:name)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("name", name);
        return sqlQuery.list();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addOrgDeptBatch(List<Map<String, Object>> orgDepts) {
        Map<String, Object> map;
        for(int i=1; i <= orgDepts.size(); i++){
            map = orgDepts.get(i-1);
            String parentDeptId = map .get("parentDeptId").toString();
            Integer parentId = orgDeptService.getParentIdByCode(parentDeptId);
            String orgId = orgService.getOrgIdByOrgCode(map .get("orgCode").toString());
            int sortNo = orgDeptService.searchParentIdOfMaxSortNo(parentId) + i;
            OrgDept newOrgDept = new OrgDept();
            newOrgDept.setParentDeptId(parentId);
            newOrgDept.setOrgId(orgId);
            newOrgDept.setCode(map .get("code").toString());
            newOrgDept.setName(map .get("name").toString());
            newOrgDept.setDelFlag(0);
            newOrgDept.setSortNo(sortNo);
            OrgDept save = orgDeptService.save(newOrgDept);

            Organization org = orgService.getOrg(map.get("orgCode").toString());
            if (null != org && "Hospital".equalsIgnoreCase(org.getOrgType())) {
                String gloryId = map.get("gloryId").toString();
                String[] arr = gloryId.split(",");
                String ids = "";
                for (String s : arr) {
                    switch (s) {
                        case "国家重点科室" :
                            ids += 1 + ",";
                            break;
                        case "省级重点科室" :
                            ids += 2 + ",";
                            break;
                        case "医院特色专科" :
                            ids += 3 + ",";
                            break;
                    }
                }
                ids = "".equals(ids) ? "" : ids.substring(0,ids.length()-1);
                String pyCode = map .get("pyCode").toString();
                OrgDeptDetail orgDeptDetail = new OrgDeptDetail();
                orgDeptDetail.setDeptId(save.getId());
                orgDeptDetail.setCode(UUID.randomUUID().toString().replace("-",""));
                orgDeptDetail.setName(map .get("name").toString());
                orgDeptDetail.setPhone(map .get("phone").toString());
                orgDeptDetail.setGloryId(ids);
                orgDeptDetail.setOrgId(orgId);
                orgDeptDetail.setIntroduction(map .get("introduction").toString());
                orgDeptDetail.setPlace(map .get("place").toString());
                orgDeptDetail.setPyCode("门诊科室".equals(pyCode) ? 1 + "" : 2 + "");
                orgDeptDetail.setDisplayStatus(0);
                orgDeptDetail.setInsertTime(new Timestamp(new Date().getTime()));
                orgDeptDetail.setUpdateTime(new Timestamp(new Date().getTime()));
                orgDeptDetail.setSortNo(sortNo);
                orgDeptDetailService.save(orgDeptDetail);
            }
        }
        return true;
    }

    private Object null2Space(Object o){
        return o==null? "" : o;
    }
}
