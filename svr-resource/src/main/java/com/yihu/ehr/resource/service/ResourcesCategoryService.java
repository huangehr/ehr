package com.yihu.ehr.resource.service;


import com.yihu.ehr.resource.common.ActionResult;
import com.yihu.ehr.resource.common.DataGridResult;
import com.yihu.ehr.resource.common.Result;
import com.yihu.ehr.resource.dao.ResourcesCategoryDao;
import com.yihu.ehr.resource.dao.ResourcesDao;
import com.yihu.ehr.resource.model.RsCategory;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IResourcesCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;


/**
 * Created by hzp on 2016/4/19.
 */
@Service("resourcesCategoryService")
public class ResourcesCategoryService implements IResourcesCategoryService {

    @Resource(name = "resourcesCategoryDao")
    private ResourcesCategoryDao resourcesCategoryDao;

    @Resource(name = "resourcesDao")
    private ResourcesDao resourcesDao;

    /**
     * 新增资源分类
     */
    public Result addResourcesCategory(RsCategory obj) throws Exception{
        obj.setId(UUID.randomUUID().toString().replace("-",""));
        if(resourcesCategoryDao.addResourcesCategory(obj))
        {
            ActionResult re = new ActionResult(true,"新增资源分类成功！");
            re.setData(obj.getId());
            return re;
        }
        else{
            return Result.error("新增资源分类失败！");
        }
    }

    /**
     * 修改资源分类
     */
    public Result editResourcesCategory(RsCategory obj) throws Exception{
        if(resourcesCategoryDao.editResourcesCategory(obj))
        {
            return Result.success("修改资源分类成功！");
        }
        else{
            return Result.error("修改资源分类失败！");
        }
    }

    /**
     * 删除资源分类
     */
    public Result deleteResourcesCategory(String id) throws Exception{
        //判断是否有子节点
        List<RsCategory> childrenList = resourcesCategoryDao.getChildrenCategory(id);
        if(childrenList!=null && childrenList.size()>0)
        {
            return Result.error("包含子分类，无法删除！");
        }
        //判断是否包含资源
        List<RsResources> rsList = resourcesDao.getResourcesByCategory(id);
        if(rsList!=null && rsList.size()>0)
        {
            return Result.error("该分类包含资源，无法删除！");
        }

        if(resourcesCategoryDao.deleteResourcesCategory(id))
        {
            return Result.success("删除资源分类成功！");
        }
        else{
            return Result.error("删除资源分类失败！");
        }
    }


    public DataGridResult queryResourcesCategory(String id,String name,String pid) throws Exception {

        return resourcesCategoryDao.queryResourcesCategory(id,name,pid);
    }

}
