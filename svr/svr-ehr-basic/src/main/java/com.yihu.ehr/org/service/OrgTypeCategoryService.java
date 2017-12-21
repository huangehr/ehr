package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.OrgTypeCategoryDao;
import com.yihu.ehr.org.model.OrgTypeCategory;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 机构类型管理 Service
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@Service
@Transactional
public class OrgTypeCategoryService extends BaseJpaService<OrgTypeCategory, OrgTypeCategoryDao> {

    @Autowired
    OrgTypeCategoryDao orgTypeCategoryDao;

    public OrgTypeCategory getById(Integer id) {
        return orgTypeCategoryDao.findOne(id);
    }

    public List<OrgTypeCategory> findAll() {
        return orgTypeCategoryDao.findAll();
    }

    @Transactional(readOnly = false)
    public OrgTypeCategory save(OrgTypeCategory OrgTypeCategory) {
        return orgTypeCategoryDao.save(OrgTypeCategory);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        List<OrgTypeCategory> descendantList = this.getDescendantIds(id);
        descendantList.add(this.getById(id));
        orgTypeCategoryDao.deleteInBatch(descendantList);
    }

    public Boolean isUniqueCode(Integer id, String code) {
        OrgTypeCategory OrgTypeCategory = orgTypeCategoryDao.isUniqueCode(id, code);
        if (OrgTypeCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueName(Integer id, String name) {
        OrgTypeCategory OrgTypeCategory = orgTypeCategoryDao.isUniqueName(id, name);
        if (OrgTypeCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据父级ID，获取其子节点；父级ID为 -1 时，返回顶级节点。
     *
     * @param pid 父级ID。
     * @return 子节点集合
     */
    public List<OrgTypeCategory> getChildrenByPid(Integer pid) {
        List<OrgTypeCategory> children = new ArrayList<>();
        if (pid == -1) {
            children = orgTypeCategoryDao.getTopParents();
        } else {
            children = orgTypeCategoryDao.getChildrenByPid(pid);
        }
        return children;
    }

    /**
     * 根据ID获取其所有后代ID
     *
     * @param pid 父节点ID
     * @return 所有后代ID
     */
    private List<OrgTypeCategory> getDescendantIds(Integer pid) {
        List<OrgTypeCategory> idList = new ArrayList<>();
        List<OrgTypeCategory> children = this.getChildrenByPid(pid);
        for (OrgTypeCategory child : children) {
            idList.add(child);
            idList.addAll(getDescendantIds(child.getId()));
        }
        return idList;
    }

    /**
     * 根据父级集合，递归获取父级及其自子级集合，形成树形结构
     *
     * @param parentList 父级集合
     * @return 父级及其子集的树形结构数据
     */
    public List<OrgTypeCategory> getTreeByParents(List<OrgTypeCategory> parentList) {
        List<OrgTypeCategory> resultList = new ArrayList<>();
        for (int i = 0; i < parentList.size(); i++) {
            OrgTypeCategory parent = parentList.get(i);
            List<OrgTypeCategory> childList = this.getChildrenByPid(parent.getId());
            List<OrgTypeCategory> childTreeList = getTreeByParents(childList);
            parent.setChildren(childTreeList);
            resultList.add(parent);
        }
        return resultList;
    }

    /**
     * 递归不满足条件的父级集合，获取其满足条件的子集，并返回子集及其父级的树形结构
     *
     * @param parents  不满足条件的父级集合
     * @param codeName 编码或名称
     * @return 满足条件的子集及其父级的树形结构
     */
    public List<OrgTypeCategory> getTreeByParentsAndCodeName(List<OrgTypeCategory> parents, String codeName) throws ParseException {
        List<OrgTypeCategory> treeData = new ArrayList<>();
        for (OrgTypeCategory parent : parents) {
            Integer parentId = parent.getId();
            List<OrgTypeCategory> childrenTree = new ArrayList<>();

            List<OrgTypeCategory> children = this.getChildrenByPid(parentId);
            if (children.size() == 0) continue;

            // 获取满足条件的子节点
            String filters = "pid=" + parentId + ";code?" + codeName + " g1;name?" + codeName + " g1;";
            List<OrgTypeCategory> childrenIin = (List<OrgTypeCategory>) this.search(filters);
            if (childrenIin.size() != 0) {
                childrenTree.addAll(getTreeByParents(childrenIin));
            }
            // 递归不满足条件的子节点
            children.removeAll(childrenIin);
            if (children.size() != 0) {
                childrenTree.addAll(getTreeByParentsAndCodeName(children, codeName));
            }

            if (childrenTree.size() != 0) {
                parent.setChildren(childrenTree);
                treeData.add(parent);
            }
        }
        return treeData;
    }

    /**
     * 根据父级主键获取其最顶级节点主键
     *
     * @param pid 父级主键
     * @return 对应的最顶级节点主键
     */
    public Integer getTopPidByPid(Integer pid) {
        if (pid == null) {
            return null;
        } else {
            OrgTypeCategory entity = this.getById(pid);
            if (entity.getPid() != null) {
                pid = getTopPidByPid(entity.getPid());
            }
            return pid;
        }
    }

}