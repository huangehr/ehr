package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsReportCategoryDao;
import com.yihu.ehr.resource.model.RsReportCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源报表分类 Service
 *
 * @author 张进军
 * @created 2017.8.8 20:32
 */
@Service
@Transactional
public class RsReportCategoryService extends BaseJpaService<RsReportCategory, RsReportCategoryDao> {

    @Autowired
    private RsReportCategoryDao rsReportCategoryDao;

    /**
     * 根据ID，获取资源报表分类
     *
     * @param id 资源报表分类ID
     * @return MRsReportCategory
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
     * @param name 资源报表分类名称
     * @return 资源报表分类的树形下拉框数据
     */
    public List<RsReportCategory> getComboTreeData(String name) {
        return rsReportCategoryDao.getComboTreeData(name);
    }

    /**
     * 保存资源报表分类
     *
     * @param rsReportCategory 资源报表分类
     * @return MRsReportCategory 资源报表分类
     */
    public RsReportCategory save(RsReportCategory rsReportCategory) {
        return rsReportCategoryDao.save(rsReportCategory);
    }

    /**
     * 删除资源报表分类
     *
     * @param id 资源报表分类ID
     */
    public void delete(Integer id) {
        RsReportCategory rsReportCategory = rsReportCategoryDao.findOne(id);
        rsReportCategoryDao.delete(rsReportCategory);
    }

    /**
     * 验证资源报表分类编码是否唯一
     * @param id 主键
     * @param code 编码
     * @return true：唯一，false：不唯一
     */
    public Boolean isUniqueCode(Integer id, String code) {
        RsReportCategory rsReportCategory = rsReportCategoryDao.isUniqueName(id, code);
        if(rsReportCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证资源报表分类编码是否唯一
     * @param id 主键
     * @param name 名称
     * @return true：唯一，false：不唯一
     */
    public Boolean isUniqueName(Integer id, String name) {
        RsReportCategory rsReportCategory = rsReportCategoryDao.isUniqueName(id, name);
        if(rsReportCategory == null) {
            return true;
        } else {
            return false;
        }
    }

}
