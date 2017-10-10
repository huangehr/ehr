package com.yihu.ehr.org.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.org.dao.XOrgDeptDetailRepository;
import com.yihu.ehr.org.dao.XOrgDeptRepository;
import com.yihu.ehr.org.dao.XOrgMemberRelationRepository;
import com.yihu.ehr.org.model.OrgDept;
import com.yihu.ehr.org.model.OrgDeptDetail;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 机构- 部门业务类
 *
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@Service
@Transactional(rollbackFor = {ServiceException.class})
public class OrgDeptService extends BaseJpaService<OrgDept, XOrgDeptRepository> {

    @Autowired
    private XOrgDeptRepository orgDeptRepository;

    @Autowired
    private XOrgDeptDetailRepository deptDetailRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected XOrgMemberRelationRepository orgMemberRelationRepository;

    public OrgDept searchBydeptId(Integer deptId) {
        return orgDeptRepository.findOne(deptId);
    }

    public OrgDept searchByOrgIdAndName(String orgId,String name) {
        List<OrgDept> orgDepts = orgDeptRepository.searchByOrgIdAndName(orgId, name);
        if (orgDepts!=null && !orgDepts.isEmpty()){
            return orgDepts.get(0);
        }else {
            return null;
        }
    }

    public int getCountByOrgIdAndName(String orgId,String name) {
        List<OrgDept> orgDepts = orgDeptRepository.searchByOrgIdAndName(orgId, name);
        if (orgDepts!=null && !orgDepts.isEmpty()){
            return orgDepts.size();
        }else {
            return 0;
        }
    }

    public List<OrgDept> searchByOrgId(String orgId) {
        return orgDeptRepository.searchByOrgId(orgId);
    }

    public List<OrgDept> searchByParentId(Integer parentDeptId) {
        return orgDeptRepository.searchByParentDeptId(parentDeptId);
    }

    public int searchParentIdOfMaxSortNo(Integer parentDeptId) {
        return orgDeptRepository.searchParentIdOfMaxSortNo(parentDeptId);
    }

    public OrgDept saveOrgDept(OrgDept dept) {
        dept.setDelFlag(0);
        dept.setSortNo(searchParentIdOfMaxSortNo(dept.getParentDeptId()));

        OrgDeptDetail deptDetail = dept.getDeptDetail();
        OrgDept save = orgDeptRepository.save(dept);
        if (deptDetail != null) {
            deptDetail.setDeptId(save.getId());
            deptDetail.setUpdateTime(new Timestamp(new Date().getTime()));
            deptDetail.setInsertTime(new Timestamp(new Date().getTime()));
            deptDetailRepository.save(deptDetail);
        }
        return dept;
    }

    public OrgDept updateOrgDeptName(Integer deptId, String name) {
        OrgDept dept = orgDeptRepository.findOne(deptId);
        dept.setName(name);
        orgDeptRepository.save(dept);
        return dept;
    }

    public OrgDept updateDept(OrgDept dept) {
        OrgDeptDetail deptDetail = dept.getDeptDetail();
        if (deptDetail != null) {
            deptDetail.setUpdateTime(new Timestamp(new Date().getTime()));
//            deptDetail.setInsertTime(new Timestamp(new Date().getTime()));
            deptDetailRepository.save(deptDetail);
        }
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
     * 交换部门排序
     *
     * @param deptId1
     * @return
     */
    public void changeOrgDeptSort(Integer deptId1, Integer deptId2) {
        OrgDept orgDept1 = orgDeptRepository.findOne(deptId1);
        OrgDept orgDept2 = orgDeptRepository.findOne(deptId2);
        Integer sortNo1 = orgDept1.getSortNo();
        orgDept1.setSortNo(orgDept2.getSortNo());
        orgDeptRepository.save(orgDept1);

        orgDept2.setSortNo(sortNo1);
        orgDeptRepository.save(orgDept2);
    }


    //Excel导入部门信息(有deptId的导入）
    public boolean importDataByExcel(List<Map<Object, Object>> list) {
        boolean succ = false;
        if (!list.isEmpty()) {
            try {
                for (Map<Object, Object> map : list) {
                    String mapJson = objectMapper.writeValueAsString(map);
                    OrgDept dept = objectMapper.readValue(mapJson, OrgDept.class);
                    orgDeptRepository.save(dept);
                }
                succ = true;
            } catch (Exception e) {
                succ = false;
                e.printStackTrace();
            }
        }
        return succ;
    }

    //Excel导入部门信息(只有部门名称的导入）
    public boolean importDataByExce2l(List<Map<Object, Object>> list) {
        boolean succ = false;
        if (!list.isEmpty()) {
            try {
                OrgDept dept = null;
                for (Map<Object, Object> map : list) {
                    dept = new OrgDept();
                    Object parentName = map.get("parentName");
                    String name = map.get("name").toString();
                    String orgId =  map.get("orgId").toString();
                    if (parentName!=null && !parentName.toString().isEmpty()){
                        //查询父部门ID ,未存在则添加
                        OrgDept parent = searchByOrgIdAndName(orgId, parentName.toString());
                        if (parent == null){
                            parent = new OrgDept();
                            parent.setOrgId(orgId);
                            parent.setCode(UUID.randomUUID().toString());
                            parent.setName(parentName.toString());
                            parent.setDelFlag(0);
                            orgDeptRepository.save(parent);
                        }
                        dept.setParentDeptId(parent.getId());
                        //保存部门信息
                    }
                    dept.setOrgId(orgId);
                    dept.setCode(UUID.randomUUID().toString());
                    dept.setName(name);
                    dept.setDelFlag(0);

                    orgDeptRepository.save(dept);
                }
                succ = true;
            } catch (Exception e) {
                succ = false;
                e.printStackTrace();
            }
        }
        return succ;
    }

    /**
     * 导入部门成员关系
     * @param list
     * @return
     */
    public boolean importDeptMembers(List<Map<Object, Object>> list) {
        boolean succ = false;
        if (!list.isEmpty()) {
            try {
                OrgDept dept = null;
                for (Map<Object, Object> map : list) {
                    dept = new OrgDept();
                    Object parentDeptName = map.get("parentDeptName");
                    String deptName = map.get("deptName").toString();
                    String orgId =  map.get("orgId").toString();
                    if (parentDeptName!=null && !parentDeptName.toString().isEmpty()){
                        //查询父部门ID ,未存在则添加
                        OrgDept parent = searchByOrgIdAndName(orgId, parentDeptName.toString());
                        if (parent == null){
                            parent = new OrgDept();
                            parent.setOrgId(orgId);
                            parent.setCode(UUID.randomUUID().toString());
                            parent.setName(parentDeptName.toString());
                            parent.setDelFlag(0);
                            orgDeptRepository.save(parent);
                        }
                        dept.setParentDeptId(parent.getId());
                        //保存部门信息
                    }
                    dept.setOrgId(orgId);
                    dept.setCode(UUID.randomUUID().toString());
                    dept.setName(deptName);
                    dept.setDelFlag(0);

                    orgDeptRepository.save(dept);
                }
                succ = true;
            } catch (Exception e) {
                succ = false;
                e.printStackTrace();
            }
        }
        return succ;
    }


    public int getOrgDeptByOrgIdAndName(String orgId,String name) {
        List<OrgDept> orgDepts = orgDeptRepository.searchByOrgIdAndName(orgId, name);
        if (orgDepts!=null && !orgDepts.isEmpty()){
            OrgDept orgDept=orgDepts.get(0);
            return orgDept.getId();
        }else {
            return 0;
        }
    }

    public List<String> searchByUserId(String userId) {
        return orgMemberRelationRepository.searchByUserId(userId);
    }

    public List<OrgDept> searchByOrgIds(String[] orgId) {
        return orgDeptRepository.searchByOrgIds(orgId);
    }
}

