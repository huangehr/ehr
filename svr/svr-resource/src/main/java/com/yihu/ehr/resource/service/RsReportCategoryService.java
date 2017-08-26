package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsReportCategoryDao;
import com.yihu.ehr.resource.model.RsReportCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源报表分类 Service
 *
 * @author 张进军
 * @created 2017.8.8 20:32
 */
@Service
@Transactional(readOnly = true)
public class RsReportCategoryService extends BaseJpaService<RsReportCategory, RsReportCategoryDao> {

    @Autowired
    private RsReportCategoryDao rsReportCategoryDao;

    /**
     * 根据ID，获取资源报表分类
     *
     * @param id 资源报表分类ID
     * @return RsReportCategory
     */
    public RsReportCategory getById(Integer id) {
        return rsReportCategoryDao.findOne(id);
    }

    /**
     * 根据父级ID，获取其子节点；父级ID为 -1 时，返回顶级节点。
     *
     * @param pid 父级ID。
     * @return 子节点集合
     */
    public List<RsReportCategory> getChildrenByPid(Integer pid) {
        List<RsReportCategory> children = new ArrayList<>();
        if (pid == -1) {
            children = rsReportCategoryDao.getTopParents();
        } else {
            children = rsReportCategoryDao.getChildrenByPid(pid);
        }
        return children;
    }

    /**
     * 获取资源报表分类的树形下拉框数据
     *
     * @return 资源报表分类的树形下拉框数据
     */
    public List<RsReportCategory> getAllTreeData() {
        return (List<RsReportCategory>) rsReportCategoryDao.findAll();
    }

    /**
     * 保存资源报表分类
     *
     * @param rsReportCategory 资源报表分类
     * @return RsReportCategory 资源报表分类
     */
    @Transactional(readOnly = false)
    public RsReportCategory save(RsReportCategory rsReportCategory) {
        return rsReportCategoryDao.save(rsReportCategory);
    }

    /**
     * 根据ID删除资源报表分类及其所有后代
     *
     * @param id 资源报表分类ID
     */
    @Transactional(readOnly = false)
    public void delete(Integer id) {
        List<RsReportCategory> descendantList = this.getDescendantIds(id);
        descendantList.add(this.getById(id));
        rsReportCategoryDao.delete(descendantList);
    }

    /**
     * 验证资源报表分类编码是否唯一
     *
     * @param id   主键
     * @param code 编码
     * @return true：唯一，false：不唯一
     */
    public Boolean isUniqueCode(Integer id, String code) {
        RsReportCategory rsReportCategory = rsReportCategoryDao.isUniqueCode(id, code);
        if (rsReportCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证资源报表分类编码是否唯一
     *
     * @param id   主键
     * @param name 名称
     * @return true：唯一，false：不唯一
     */
    public Boolean isUniqueName(Integer id, String name) {
        RsReportCategory rsReportCategory = rsReportCategoryDao.isUniqueName(id, name);
        if (rsReportCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据ID获取其所有后代ID
     *
     * @param pid 父节点ID
     * @return 所有后代ID
     */
    private List<RsReportCategory> getDescendantIds(Integer pid) {
        List<RsReportCategory> idList = new ArrayList<>();
        List<RsReportCategory> children = this.getChildrenByPid(pid);
        for (RsReportCategory child : children) {
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
    public List<RsReportCategory> getTreeByParents(List<RsReportCategory> parentList) {
        List<RsReportCategory> resultList = new ArrayList<>();
        for (int i = 0; i < parentList.size(); i++) {
            RsReportCategory parent = parentList.get(i);
            List<RsReportCategory> childList = this.getChildrenByPid(parent.getId());
            List<RsReportCategory> childTreeList = getTreeByParents(childList);
            parent.setChildren(childTreeList);
            resultList.add(parent);
        }
        return resultList;
    }

    /**
     * 递归不满足条件的父级集合，获取其满足条件的子集，并返回子集及其父级的树形结构
     *
     * @param parents  不满足条件的父级集合
     * @param codeName 资源报表分类编码或名称
     * @return 满足条件的子集及其父级的树形结构
     */
    public List<RsReportCategory> getTreeByParentsAndCodeName(List<RsReportCategory> parents, String codeName) throws ParseException {
        List<RsReportCategory> treeData = new ArrayList<>();
        for (RsReportCategory parent : parents) {
            Integer parentId = parent.getId();
            List<RsReportCategory> childrenTree = new ArrayList<>();

            List<RsReportCategory> children = this.getChildrenByPid(parentId);
            if (children.size() == 0) continue;

            // 获取满足条件的子节点
            String filters = "pid=" + parentId + ";code?" + codeName + " g1;name?" + codeName + " g1;";
            List<RsReportCategory> childrenIin = (List<RsReportCategory>) this.search(filters);
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

}
