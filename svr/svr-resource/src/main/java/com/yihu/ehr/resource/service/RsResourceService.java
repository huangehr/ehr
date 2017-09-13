package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceCategoryDao;
import com.yihu.ehr.resource.dao.RsResourceMetadataDao;
import com.yihu.ehr.resource.dao.RsResourceDao;
import com.yihu.ehr.resource.dao.RsResourceDefaultQueryDao;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.model.RsResourceCategory;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lyr on 2016/4/25.
 */
@Service
@Transactional
public class RsResourceService extends BaseJpaService<RsResource, RsResourceDao> {

    @Autowired
    private RsResourceDao rsResourceDao;
    @Autowired
    private RsResourceMetadataDao rsMetadataDao;
    @Autowired
    private RsResourceDefaultQueryDao resourcesDefaultQueryDao;
    @Autowired
    private RsResourceCategoryDao rsResourceCategoryDao;

    /**
     * 资源创建
     * @param resource 资源实体
     * @return RsResources 资源实体
     */
    public RsResource saveResource(RsResource resource) {
        return rsResourceDao.save(resource);
    }

    /**
     * 资源删除
     *
     * @param id 资源ID
     */
    public void deleteResource(String id) {
        String[] ids = id.split(",");
        for(String id_ : ids) {
            resourcesDefaultQueryDao.deleteByResourcesId(id_);
            rsMetadataDao.deleteByResourcesId(id_);
            rsResourceDao.delete(id_);
        }
    }

    /**
     * 资源获取
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsResource> getResources(String sorts, int page, int size) {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));
        return rsResourceDao.findAll(pageable);
    }

    /**
     * 根据资源分类获取资源
     * @param CategoryId
     * @return
     */
    public List<RsResource> findByCategoryId(String CategoryId){
        return rsResourceDao.findByCategoryId(CategoryId);
    }

    /**
     * 根据ID获取资源
     *
     * @param id String Id
     * @return RsResources
     */
    public RsResource getResourceById(String id)
    {
        return rsResourceDao.findOne(id);
    }

    /**
     * 根据code获取资源
     * @param code
     * @return
     */
    public RsResource getResourceByCode(String code) {
        return rsResourceDao.findByCode(code);
    }

    /**
     * 获取资源列表树
     * @param filters
     * @return
     */
    public List<Map<String, Object>> getResourceTree(Integer dataSource, String filters){
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<RsResourceCategory> rsCateList = rsResourceCategoryDao.findByPid("");
        for(RsResourceCategory rsResourceCategory : rsCateList) {
            Map<String, Object> childMap = getTreeMap(rsResourceCategory, 0, dataSource, filters);
            resultList.add(childMap);
        }
        return resultList;
    }

    /**
     * 递归获取数据
     * @param rsResourceCategory
     * @param filters
     * @return
     */
    private Map<String, Object> getTreeMap(RsResourceCategory rsResourceCategory, int level, Integer dataSource, String filters) {
        Map<String, Object> masterMap = new HashMap<String, Object>();
        if(rsResourceCategory != null) {
            /**
             * 处理自身数据
             */
            masterMap.put("level", level);
            masterMap.put("id", rsResourceCategory.getId());
            masterMap.put("name", rsResourceCategory.getName());
            masterMap.put("pid", rsResourceCategory.getId());
            List<RsResource> rsList;
            if(StringUtils.isEmpty(filters)) {
                rsList = rsResourceDao.findByCategoryIdAndDataSource(rsResourceCategory.getId(), dataSource);
            }else {
                rsList = rsResourceDao.findByCategoryIdAndDataSourceAndName(rsResourceCategory.getId(), dataSource, filters);
            }
            //处理下属资源
            if (rsList != null) {
                List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
                for (RsResource RsResource : rsList) {
                    Map<String, Object> detailMap = new HashMap<String, Object>();
                    detailMap.put("level", level + 1);
                    detailMap.put("id", RsResource.getId());
                    detailMap.put("name", RsResource.getName());
                    detailMap.put("code", RsResource.getCode());
                    detailMap.put("category_id", RsResource.getCategoryId());
                    detailMap.put("data_source", RsResource.getDataSource());
                    detailList.add(detailMap);
                }
                masterMap.put("detailList", detailList);
            } else {
                masterMap.put("detailList", null);
            }
            //处理下级数据
            List<RsResourceCategory> hList = rsResourceCategoryDao.findByPid(rsResourceCategory.getId());
            if(hList != null) {
                List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
                for(RsResourceCategory category: hList) {
                    childList.add(getTreeMap(category, level + 1, dataSource, filters));
                }
                masterMap.put("child", childList);
            }
        }
        return masterMap;
    }
}
