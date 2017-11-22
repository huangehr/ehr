package com.yihu.ehr.resource.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.*;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.model.RsResourceCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
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
    private RsResourceMetadataDao rsResourceMetadataDao;
    @Autowired
    private RsResourceCategoryDao rsResourceCategoryDao;
    @Autowired
    private RsResourceDefaultQueryDao rsResourceDefaultQueryDao;
    @Autowired
    private RsResourceDefaultParamDao rsResourceDefaultParamDao;
    @Autowired
    private ObjectMapper objectMapper;

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
            rsResourceDefaultQueryDao.deleteByResourcesId(id_);
            rsResourceDefaultParamDao.deleteByResourcesId(id_);
            rsResourceMetadataDao.deleteByResourcesId(id_);
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
     * @param dataSource
     * @param userResource
     * @param filters
     * @return
     */
    public List<Map<String, Object>> getResourceTree(Integer dataSource, String userResource, String filters) throws IOException{
        List<RsResourceCategory> rsCateList;
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        if(userResource.equals("*")){
            rsCateList = rsResourceCategoryDao.findByPid("");
            for(RsResourceCategory rsResourceCategory : rsCateList) {
                Map<String, Object> childMap = getTreeMap(rsResourceCategory, 0, dataSource, null, filters);
                resultList.add(childMap);
            }
        }else {
            rsCateList = rsResourceCategoryDao.findByCodeAndPid("derived", "");
            List<String> userResourceList = objectMapper.readValue(userResource, List.class);
            String [] ids = new String[userResourceList.size()];
            userResourceList.toArray(ids);
            for(RsResourceCategory rsResourceCategory : rsCateList) {
                Map<String, Object> childMap = getTreeMap(rsResourceCategory, 0, dataSource, ids, filters);
                if (childMap != null) {
                    resultList.add(childMap);
                }
            }
        }
        return resultList;
    }

    /**
     * 递归获取数据
     * @param rsResourceCategory
     * @param filters
     * @return
     */
    private Map<String, Object> getTreeMap(RsResourceCategory rsResourceCategory, int level, Integer dataSource, String [] ids, String filters) {
        Map<String, Object> masterMap = new HashMap<String, Object>();
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
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
                if(null == ids) {
                    rsList = rsResourceDao.findByCategoryIdAndDataSource(rsResourceCategory.getId(), dataSource);
                }else {
                    rsList = rsResourceDao.findByCategoryIdAndDataSourceAndIdsOrGrantType(rsResourceCategory.getId(), dataSource, ids, "0");
                }
            }else {
                if(null == ids) {
                    rsList = rsResourceDao.findByCategoryIdAndDataSourceAndName(rsResourceCategory.getId(), dataSource, filters);
                }else {
                    rsList = rsResourceDao.findByCategoryIdAndDataSourceAndIdsOrGrantTypeAndName(rsResourceCategory.getId(), dataSource, ids, "0", filters);
                }
            }
            //处理下属资源
            if (rsList != null) {
                for (RsResource rsResource : rsList) {
                    Map<String, Object> detailMap = new HashMap<String, Object>();
                    detailMap.put("level", level + 1);
                    detailMap.put("id", rsResource.getId());
                    detailMap.put("name", rsResource.getName());
                    detailMap.put("code", rsResource.getCode());
                    detailMap.put("category_id", rsResource.getCategoryId());
                    detailMap.put("data_source", rsResource.getDataSource());
                    detailMap.put("grant_type", rsResource.getGrantType());
                    detailMap.put("category_name", rsResourceCategory.getName());
                    detailList.add(detailMap);
                }
            }
            masterMap.put("detailList", detailList);
            //处理下级数据
            List<RsResourceCategory> hList = rsResourceCategoryDao.findByPid(rsResourceCategory.getId());
            if(hList != null) {
                for(RsResourceCategory category: hList) {
                    childList.add(getTreeMap(category, level + 1, dataSource, ids, filters));
                }
                masterMap.put("child", childList);
            }
        }
        if(ids != null && detailList.size() <= 0 && childList.size() <= 0) {
            return null;
        }
        return masterMap;
    }
}
