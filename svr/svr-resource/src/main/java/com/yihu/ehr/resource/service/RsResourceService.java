package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.*;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.model.RsResourceCategory;
import com.yihu.ehr.resource.model.RsRolesResource;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
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
    private RsResourceDefaultParamDao rsResourceDefaultParamDao;
    @Autowired
    private RsRolesResourceDao rsRolesResourceDao;
    @Autowired
    private RsRolesResourceMetadataDao rsRolesResourceMetadataDao;

    /**
     * 资源创建
     *
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
        for (String id_ : ids) {
            rsResourceDefaultParamDao.deleteByResourcesId(id_);
            rsResourceMetadataDao.deleteByResourcesId(id_);
            List<RsRolesResource> rsRolesResourceList = rsRolesResourceDao.findByResourceId(id_);
            rsRolesResourceList.forEach(item -> {
                rsRolesResourceMetadataDao.deleteByRolesResourceId(item.getId());
            });
            rsRolesResourceDao.deleteByResourceId(id_);
            rsResourceDao.delete(id_);
        }
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page  页码
     * @param size  分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsResource> getResources(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return rsResourceDao.findAll(pageable);
    }

    /**
     * 根据资源分类获取资源
     *
     * @param CategoryId
     * @return
     */
    public List<RsResource> findByCategoryId(String CategoryId) {
        return rsResourceDao.findByCategoryId(CategoryId);
    }

    /**
     * 根据ID获取资源
     *
     * @param id String Id
     * @return RsResources
     */
    public RsResource getResourceById(String id) {
        return rsResourceDao.findOne(id);
    }

    /**
     * 根据code获取资源
     *
     * @param code
     * @return
     */
    public RsResource getResourceByCode(String code) {

        return getResourceByCategory(code,"standard");
    }

    /**
     * 获取资源列表树
     *
     * @param dataSource
     * @param userResource
     * @param filters
     * @return
     */
    public List<Map<String, Object>> getResourceTree(Integer dataSource, String userResource, String filters) throws IOException {
        List<RsResourceCategory> rsCateList;
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        if (userResource.equals("*")) {
            rsCateList = rsResourceCategoryDao.findByPid("");
            for (RsResourceCategory rsResourceCategory : rsCateList) {
                Map<String, Object> childMap = getTreeMap(rsResourceCategory, 0, dataSource, null, filters);
                resultList.add(childMap);
            }
        } else {
            rsCateList = rsResourceCategoryDao.findByCodeAndPid("derived", "");
            List<String> userResourceList = objectMapper.readValue(userResource, List.class);
            if (userResourceList.size() <= 0) {
                userResourceList.add("NO_AUTH_RS");
            }
            String[] ids = new String[userResourceList.size()];
            userResourceList.toArray(ids);
            for (RsResourceCategory rsResourceCategory : rsCateList) {
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
     *
     * @param rsResourceCategory
     * @param filters
     * @return
     */
    private Map<String, Object> getTreeMap(RsResourceCategory rsResourceCategory, int level, Integer dataSource, String[] ids, String filters) {
        Map<String, Object> masterMap = new HashMap<String, Object>();
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
        if (rsResourceCategory != null) {
            /**
             * 处理自身数据
             */
            masterMap.put("level", level);
            masterMap.put("id", rsResourceCategory.getId());
            masterMap.put("name", rsResourceCategory.getName());
            masterMap.put("pid", rsResourceCategory.getId());
            List<RsResource> rsList;
            if (StringUtils.isEmpty(filters)) {
                if (null == ids) {
                    rsList = rsResourceDao.findByCategoryIdAndDataSource(rsResourceCategory.getId(), dataSource);
                } else {
                    rsList = rsResourceDao.findByCategoryIdAndDataSourceAndIdsOrGrantType(rsResourceCategory.getId(), dataSource, ids, "0");
                }
            } else {
                if (null == ids) {
                    rsList = rsResourceDao.findByCategoryIdAndDataSourceAndName(rsResourceCategory.getId(), dataSource, filters);
                } else {
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
                    detailMap.put("rs_interface", rsResource.getRsInterface());
                    detailMap.put("category_name", rsResourceCategory.getName());
                    detailMap.put("isNestedPie", "nestedPie".equalsIgnoreCase(rsResource.getEchartType()) ? true : false);
                    detailList.add(detailMap);
                }
            }
            masterMap.put("detailList", detailList);
            //处理下级数据
            List<RsResourceCategory> hList = rsResourceCategoryDao.findByPid(rsResourceCategory.getId());
            if (hList != null) {
                for (RsResourceCategory category : hList) {
                    childList.add(getTreeMap(category, level + 1, dataSource, ids, filters));
                }
                masterMap.put("child", childList);
            }
        }
        if (ids != null && detailList.size() <= 0 && childList.size() <= 0) {
            return null;
        }
        return masterMap;
    }

    public List<RsResource> getResourcePage(String userResource, String userId, int page, int size) throws IOException {
        Session session = currentSession();
        if (page <= 0) {
            page = 1;
        }
        List<String> rsList = objectMapper.readValue(userResource, List.class);
        if (rsList.size() <= 0) {
            rsList.add("NO_AUTH_RS");
        }
        String hql = "SELECT rsResource FROM RsResource rsResource WHERE rsResource.categoryId IN (SELECT id FROM RsResourceCategory rsResourceCategory WHERE rsResourceCategory.code = 'derived') AND (rsResource.grantType = '0' OR rsResource.creator = :creator OR rsResource.id IN (:ids)) " +
                "ORDER BY rsResource.createDate DESC";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setParameter("creator", userId);
        query.setParameterList("ids", rsList);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.list();
    }

    public Integer getResourceCount(String userResource, String userId) throws IOException {
        Session session = currentSession();
        List<String> rsList = objectMapper.readValue(userResource, List.class);
        if (rsList.size() <= 0) {
            rsList.add("NO_AUTH_RS");
        }
        String hql = "SELECT count(rsResource.id) FROM RsResource rsResource WHERE rsResource.categoryId IN (SELECT id FROM RsResourceCategory rsResourceCategory WHERE rsResourceCategory.code = 'derived') AND (rsResource.grantType = '0' OR rsResource.creator = :creator OR rsResource.id IN (:ids)) ";
        Query query = session.createQuery(hql);
        query.setParameter("creator", userId);
        query.setParameterList("ids", rsList);
        return ((Long) query.list().get(0)).intValue();
    }

    /**
     * 根据分类id获取资源视图，不分主细表
     * @param rsResourceCategoryId
     * @param dataSource
     * @return
     */
    public List<RsResource> findByCategoryIdAndDataSource(String rsResourceCategoryId, Integer dataSource) {
        List<RsResource> rsList = rsResourceDao.findByCategoryIdAndDataSource(rsResourceCategoryId, dataSource);
        return rsList;
    }

    /**
     * 根据分类id获取资源视图，不分主细表
     * @param rsResourceCategoryId
     * @param dataSource
     * @return
     */
    public List<String> findIdByCategoryIdAndDataSource(String rsResourceCategoryId, Integer dataSource) {
        List<String> rsList = rsResourceDao.findIdByCategoryIdAndDataSourceAndName(rsResourceCategoryId, dataSource);
        return rsList;
    }

    public RsResource getResourceByCategory(String resourceCode, String categoryCode)  {
        RsResource rsResource =null;
        Session session = currentSession();
        String hql="SELECT rsResource FROM RsResource rsResource WHERE rsResource.code = :resourceCode ";
        Query query = session.createQuery(hql);
        query.setFlushMode(FlushMode.COMMIT);
        query.setParameter("resourceCode", resourceCode);
        List list = query.list();
        //因为标准视图和业务视图的编码（使用数据集编码）重复，导致根据视图编码查视图会出现不唯一的情况。但是指标（指标视图为派生分类）也使用该接口，直接传“standard”会导致指标查不出数据。
        //所以临时的方案是先根据视图code查询（指标视图id唯一），若结果大于1条，则加resourceCode=standard 查询。--zdm
        if (null != list && list.size() > 1) {
            hql = "SELECT rsResource FROM RsResource rsResource,RsResourceCategory rsResourceCategory WHERE rsResource.categoryId =rsResourceCategory.id and rsResource.code = :resourceCode and rsResourceCategory.code= :categoryCode";
            Query queryNew = session.createQuery(hql);
            queryNew.setFlushMode(FlushMode.COMMIT);
            queryNew.setParameter("resourceCode", resourceCode);
            queryNew.setParameter("categoryCode", categoryCode);
            rsResource = (RsResource) queryNew.uniqueResult();
        } else if (null != list && list.size() == 1) {
            rsResource = (RsResource) list.get(0);
        }
        return rsResource;
    }


}
