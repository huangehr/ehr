package com.yihu.ehr.org.service;

import com.yihu.ehr.org.dao.OrgHealthCategoryDao;
import com.yihu.ehr.org.model.OrgHealthCategory;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 卫生机构类别 Service
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@Service
@Transactional
public class OrgHealthCategoryService extends BaseJpaService<OrgHealthCategory, OrgHealthCategoryDao> {

    @Autowired
    OrgHealthCategoryDao orgHealthCategoryDao;

    public OrgHealthCategory getById(Integer id) {
        return orgHealthCategoryDao.findOne(id);
    }

    public List<OrgHealthCategory> findAll() {
        return orgHealthCategoryDao.findAll();
    }

    @Transactional(readOnly = false)
    public OrgHealthCategory save(OrgHealthCategory orgHealthCategory) {
        return orgHealthCategoryDao.save(orgHealthCategory);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        List<OrgHealthCategory> descendantList = this.getDescendantIds(id);
        descendantList.add(this.getById(id));
        orgHealthCategoryDao.deleteInBatch(descendantList);
    }

    public Boolean isUniqueCode(Integer id, String code) {
        OrgHealthCategory OrgHealthCategory = orgHealthCategoryDao.isUniqueCode(id, code);
        if (OrgHealthCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueName(Integer id, String name) {
        OrgHealthCategory OrgHealthCategory = orgHealthCategoryDao.isUniqueName(id, name);
        if (OrgHealthCategory == null) {
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
    public List<OrgHealthCategory> getChildrenByPid(Integer pid) {
        List<OrgHealthCategory> children = new ArrayList<>();
        if (pid == -1) {
            children = orgHealthCategoryDao.getTopParents();
        } else {
            children = orgHealthCategoryDao.getChildrenByPid(pid);
        }
        return children;
    }

    /**
     * 根据ID获取其所有后代ID
     *
     * @param pid 父节点ID
     * @return 所有后代ID
     */
    private List<OrgHealthCategory> getDescendantIds(Integer pid) {
        List<OrgHealthCategory> idList = new ArrayList<>();
        List<OrgHealthCategory> children = this.getChildrenByPid(pid);
        for (OrgHealthCategory child : children) {
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
    public List<OrgHealthCategory> getTreeByParents(List<OrgHealthCategory> parentList) {
        List<OrgHealthCategory> resultList = new ArrayList<>();
        for (int i = 0; i < parentList.size(); i++) {
            OrgHealthCategory parent = parentList.get(i);
            List<OrgHealthCategory> childList = this.getChildrenByPid(parent.getId());
            List<OrgHealthCategory> childTreeList = getTreeByParents(childList);
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
    public List<OrgHealthCategory> getTreeByParentsAndCodeName(List<OrgHealthCategory> parents, String codeName) throws ParseException {
        List<OrgHealthCategory> treeData = new ArrayList<>();
        for (OrgHealthCategory parent : parents) {
            Integer parentId = parent.getId();
            List<OrgHealthCategory> childrenTree = new ArrayList<>();

            List<OrgHealthCategory> children = this.getChildrenByPid(parentId);
            if (children.size() == 0) continue;

            // 获取满足条件的子节点
            String filters = "pid=" + parentId + ";code?" + codeName + " g1;name?" + codeName + " g1;";
            List<OrgHealthCategory> childrenIin = (List<OrgHealthCategory>) this.search(filters);
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
            OrgHealthCategory entity = this.getById(pid);
            if (entity.getPid() != null) {
                pid = getTopPidByPid(entity.getPid());
            }
            return pid;
        }
    }

}