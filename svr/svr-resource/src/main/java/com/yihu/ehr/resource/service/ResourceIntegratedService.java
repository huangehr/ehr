package com.yihu.ehr.resource.service;


import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.entity.quota.TjQuotaCategory;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.*;
import com.yihu.ehr.resource.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by Sxy on 2016/08/01.
 */
@Service
@Transactional
public class ResourceIntegratedService extends BaseJpaService {

    @Autowired
    private ResourceBrowseMetadataDao resourceBrowseMetadataDao;
    @Autowired
    private RsResourceDao rsResourceDao;
    @Autowired
    private RsResourceMetadataDao rsResourceMetadataDao;
    @Autowired
    private RsResourceQuotaDao rsResourceQuotaDao;
    @Autowired
    private RsRolesResourceDao rsRolesResourceDao;
    @Autowired
    private RsRolesResourceMetadataDao rsRolesResourceMetadataDao;
    @Autowired
    private RsDictionaryDao rsDictionaryDao;
    @Autowired
    private RsResourceCategoryDao rsResourceCategoryDao;
    @Autowired
    private RsResourceDefaultParamDao resourceDefaultParamDao;

    /**
     * 获取档案数据资源列表
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<RsResource> findFileMasterList(String ids, String filters, String categoryCode) {
        String sql = "";
        if (filters != null) {
            if (ids != null) {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code ='" + categoryCode + "') " +
                        "AND (rr.rs_interface = 'getEhrCenter' AND rr.id IN (" + ids + "))) " +
                        "OR ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = '" + categoryCode + "') AND rr.rs_interface = 'getEhrCenter' AND rr.grant_type = '0'))) " +
                        "AND rr.name like " + "'%" + filters + "%'";
            } else {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE rr.category_id IN (SELECT id FROM rs_resource_category WHERE code ='" + categoryCode + "') " +
                        "AND rr.rs_interface = 'getEhrCenter' " +
                        "AND rr.name like " + "'%" + filters + "%'";
            }
        } else {
            if (ids != null) {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code ='" + categoryCode + "') " +
                        "AND (rr.rs_interface = 'getEhrCenter' AND rr.id IN (" + ids + "))) " +
                        "OR ((rr.category_id IN (SELECT id FROM rs_resource_category WHERE code = 'standard') AND rr.rs_interface = 'getEhrCenter' AND rr.grant_type = '0')))";
            } else {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr WHERE rr.category_id IN (SELECT id FROM rs_resource_category WHERE code ='" + categoryCode + "') " +
                        "AND rr.rs_interface = 'getEhrCenter'";
            }
        }
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(RsResource.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 获取档案数据资源列表
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<RsResource> findFileMasterList(String categoryId, String ids, String filters, String categoryCode) {
        String sql;
        if (filters != null) {
            if (ids != null) {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr ,rs_resource_category rrc  WHERE" +
                        " rr.category_id=rrc.ID AND rrc. CODE = '" + categoryCode + "' " +
                        "AND rr.category_id = '" + categoryId + "' " +
                        "AND rr.rs_interface = 'getEhrCenter' " +
                        "AND (rr.id IN (" + ids + ")) " + "OR rr.grant_type = '0') " +
                        "AND rr.name like " + "'%" + filters + "%'";
            } else {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr ,rs_resource_category rrc   WHERE" +
                        " rr.category_id=rrc.ID AND rrc. CODE = '" + categoryCode + "' " +
                        "AND rr.category_id = '" + categoryId + "'" +
                        "AND rr.rs_interface = 'getEhrCenter' " +
                        "AND rr.name like " + "'%" + filters + "%'";
            }
        } else {
            if (ids != null) {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr ,rs_resource_category rrc   WHERE" +
                        " rr.category_id=rrc.ID AND rrc. CODE = '" + categoryCode + "' " +
                        "AND rr.category_id = '" + categoryId + "'" +
                        "AND rr.rs_interface = 'getEhrCenter' " +
                        "AND rr.id IN (" + ids + ")) " + "OR rr.grant_type = '0') ";
            } else {
                sql = "SELECT rr.id, rr.code, rr.name, rr.category_id, rr.rs_interface, rr.grant_type FROM rs_resource rr ,rs_resource_category rrc   WHERE" +
                        " rr.category_id=rrc.ID AND rrc. CODE = '" + categoryCode + "' " +
                        "AND rr.category_id = '" + categoryId + "'" +
                        "AND rr.rs_interface = 'getEhrCenter'";
            }
        }
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(RsResource.class);
        return jdbcTemplate.query(sql, rowMapper);
    }


    /**
     * 获取档案数据资源数据元列表
     *
     * @param rsResource
     * @param roleId
     * @return
     */
    @Transactional(readOnly = true)
    public List<RsMetadata> findFileMetadataList(RsResource rsResource, String roleId) throws Exception {
        List<DtoResourceMetadata> metadataList;
        Set<String> rsMetadataIdSet = new HashSet<String>();
        String grantType = rsResource.getGrantType();
        //获取数据元信息
        if (grantType.equals("1") && !roleId.equals("*")) {
            List<String> roleIdList = objectMapper.readValue(roleId, List.class);
            for (String id : roleIdList) {
                RsRolesResource rsRolesResource = rsRolesResourceDao.findByResourceIdAndRolesId(rsResource.getId(), id);
                if (rsRolesResource != null) {
                    List<RsRolesResourceMetadata> rsRolesResourceMetadataList = rsRolesResourceMetadataDao.findByRolesResourceIdAndValid(rsRolesResource.getId(), "1");
                    if (rsRolesResourceMetadataList != null) {
                        for (RsRolesResourceMetadata rsRolesResourceMetadata : rsRolesResourceMetadataList) {
                            rsMetadataIdSet.add(rsRolesResourceMetadata.getResourceMetadataId());
                        }
                    }
                }
            }
            if (rsMetadataIdSet.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (String id : rsMetadataIdSet) {
                    builder.append("'");
                    builder.append(id);
                    builder.append("',");
                }
                String rsMetadataIds = builder.toString();
                metadataList = resourceBrowseMetadataDao.getAuthResourceMetadata(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
            } else {
                metadataList = null;
            }
        } else {
            metadataList = resourceBrowseMetadataDao.getAllResourceMetadata(rsResource.getCode());
        }
        if (metadataList != null && metadataList.size() > 0) {
            String metadataIds = "";
            for (DtoResourceMetadata dtoResourceMetadata : metadataList) {
                metadataIds += "'" + dtoResourceMetadata.getId() + "'" + ",";
            }
            String sql = "";
            sql = "select * from rs_metadata rm where rm.id in (" + metadataIds.substring(0, metadataIds.length() - 1) + ")";
            RowMapper rowMapper = BeanPropertyRowMapper.newInstance(RsMetadata.class);
            return jdbcTemplate.query(sql, rowMapper);
        } else {
            return null;
        }
    }

    /**
     * 根据parentId获取指标分类主体列表
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<TjQuotaCategory> findQuotaCategoryList(int parentId, String filters) {
        String sql;
        if (parentId != 0 && filters != null) {
            sql = "select * from tj_quota_category where parent_id = " + parentId + " AND name like " + "'%" + filters + "%'";
        } else {
            sql = "select * from tj_quota_category where parent_id = " + parentId;
        }
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(TjQuotaCategory.class);
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 根据指标分类获取指标
     *
     * @param quotaCategory
     * @return
     */
    @Transactional(readOnly = true)
    public List<TjQuota> findQuotaMetadataList(TjQuotaCategory quotaCategory) {
        String sql = "";
        if (quotaCategory != null) {
            sql = "select * from tj_quota tj where tj.quota_type = " + quotaCategory.getId();
            RowMapper rowMapper = BeanPropertyRowMapper.newInstance(TjQuota.class);
            return jdbcTemplate.query(sql, rowMapper);
        } else {
            return null;
        }
    }

    /**
     * 递归获取指标分类包含的子集分类和指标
     *
     * @param quotaCategory
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTreeMap(TjQuotaCategory quotaCategory, int level, String filters) {
        Map<String, Object> masterMap = new HashMap<String, Object>();
        if (quotaCategory != null) {
            /**
             * 处理自身数据
             */
            masterMap.put("level", level);
            masterMap.put("id", quotaCategory.getId());
            masterMap.put("name", quotaCategory.getName());
            masterMap.put("parent_id", quotaCategory.getParentId());
            masterMap.put("code", quotaCategory.getCode());
            masterMap.put("note", quotaCategory.getNote());
            List<TjQuota> tList = findQuotaMetadataList(quotaCategory);
            if (level != 0) {
                if (tList != null) {
                    List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
                    for (TjQuota tjQuota : tList) {
                        Map<String, Object> detailMap = new HashMap<String, Object>();
                        detailMap.put("level", level + 1);
                        detailMap.put("id", tjQuota.getId());
                        detailMap.put("name", tjQuota.getName());
                        detailMap.put("code", tjQuota.getCode());
                        detailMap.put("status", tjQuota.getStatus());
                        detailMap.put("data_level", tjQuota.getDataLevel());
                        detailMap.put("quota_type", tjQuota.getQuotaType());
                        detailList.add(detailMap);
                    }
                    masterMap.put("detailList", detailList);
                } else {
                    masterMap.put("detailList", null);
                }
            }
            /**
             * 处理子集数据
             */
            List<TjQuotaCategory> hList = findQuotaCategoryList(quotaCategory.getId(), filters);
            if (hList != null) {
                List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
                for (TjQuotaCategory quotaCategory1 : hList) {
                    childList.add(getTreeMap(quotaCategory1, level + 1, filters));
                }
                masterMap.put("child", childList);
            }
        }
        return masterMap;
    }

    /**
     * 综合查询档案数据列表树
     *
     * @param filters
     * @return
     */
    @Transactional(readOnly = true)
    public List<Object> getMetadataList(String userResource, String roleId, String categoryCode, String filters) throws Exception {
        List<Object> resultList = new ArrayList<>();
        Map<String, Object> baseMap = new HashMap<String, Object>();
        baseMap.put("level", "0");
        baseMap.put("baseInfo", new ArrayList<>());
        resultList.add(baseMap);
        List<RsResource> rrList;
        if (userResource.equals("*")) {
            rrList = findFileMasterList(null, filters, categoryCode);
        } else {
            //授权资源
            List<String> userResourceList = objectMapper.readValue(userResource, List.class);
            StringBuilder builder = new StringBuilder();
            for (String id : userResourceList) {
                builder.append("'");
                builder.append(id);
                builder.append("',");
            }
            String ids = builder.toString();
            if (StringUtils.isEmpty(ids)) {
                rrList = findFileMasterList("''", filters, categoryCode);
            } else {
                rrList = findFileMasterList(ids.substring(0, ids.length() - 1), filters, categoryCode);
            }
        }
        Map<String, Object> finalMap = new HashMap<>();
        Map<String, String> categoryMap = new HashMap<>();
        if (rrList != null) {
            for (RsResource rsResources : rrList) {
                String categoryId = rsResources.getCategoryId();
                Map<String, Object> masterMap = new HashMap<String, Object>();
                masterMap.put("level", "2");
                masterMap.put("code", rsResources.getCode());
                masterMap.put("name", rsResources.getName());
                List<RsMetadata> rmList = findFileMetadataList(rsResources, roleId);
                if (rmList != null) {
                    List<Map<String, Object>> metadataList = new ArrayList<Map<String, Object>>();
                    for (RsMetadata rsMetadata : rmList) {
                        Map<String, Object> metadataMap = new HashMap<String, Object>();
                        metadataMap.put("level", "3");
                        metadataMap.put("code", rsMetadata.getId());
                        metadataMap.put("name", rsMetadata.getName());
                        String dictCode = rsMetadata.getDictCode();
                        metadataMap.put("dictCode", rsMetadata.getDictCode());
                        if (!StringUtils.isEmpty(dictCode)) {
                            if (dictCode.equals("DATECONDITION")) {
                                metadataMap.put("dictName", "时间");
                            } else {
                                RsDictionary rsDictionary = rsDictionaryDao.findByCode(rsMetadata.getDictCode());
                                if (rsDictionary != null) {
                                    metadataMap.put("dictName", rsDictionary.getName());
                                }
                            }
                        }
                        metadataList.add(metadataMap);
                    }
                    masterMap.put("children", metadataList);
                    if (categoryMap.containsKey(categoryId)) {
                        List<Map> dataList = (List) ((Map) finalMap.get(categoryId)).get("children");
                        if (dataList != null) {
                            dataList.add(masterMap);
                        }
                    } else {
                        RsResourceCategory resourceCategory = rsResourceCategoryDao.findOne(categoryId);
                        if (resourceCategory != null) {
                            String categoryName = resourceCategory.getName();
                            categoryMap.put(categoryId, categoryName);
                            List<Map> dataList = new ArrayList<>();
                            dataList.add(masterMap);
                            Map<String, Object> dataMap = new HashMap<>();
                            dataMap.put("level", "1");
                            dataMap.put("name", categoryName);
                            dataMap.put("children", dataList);
                            finalMap.put(categoryId, dataMap);
                        }
                    }
                }
            }
            List<Object> finalList = new ArrayList<>();
            for (String key : finalMap.keySet()) {
                finalList.add(finalMap.get(key));
            }
            resultList.add(finalList);
        }
        return resultList;
    }

    /**
     * 综合查询档案数据分类列表
     *@param categoryCode 追加视图分类：standard-标准分类、business-业务分类
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> intCategory(String categoryCode) throws Exception {
        List<RsResourceCategory> rsResourceCategoryList = rsResourceCategoryDao.findByCode(categoryCode);
        List<Map<String, Object>> cateMapList = new ArrayList<>(rsResourceCategoryList.size());
        rsResourceCategoryList.forEach(item -> {
            Map<String, Object> cateMap = new HashMap<>();
            cateMap.put("level", 1);
            cateMap.put("name", item.getName());
            cateMap.put("id", item.getId());
            cateMapList.add(cateMap);
        });
        return cateMapList;
    }

    /**
     * 综合查询档案数据列表树
     *
     * @param filters
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> intMetadata(String categoryId, String userResource, String roleId, String categoryCode, String filters) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<RsResource> rrList;
        if (userResource.equals("*")) {
            rrList = findFileMasterList(categoryId, null, filters, categoryCode);
        } else {
            //授权资源
            List<String> userResourceList = objectMapper.readValue(userResource, List.class);
            StringBuilder builder = new StringBuilder();
            for (String id : userResourceList) {
                builder.append("'");
                builder.append(id);
                builder.append("',");
            }
            String ids = builder.toString();
            if (StringUtils.isEmpty(ids)) {
                rrList = findFileMasterList(categoryId, "''", filters, categoryCode);
            } else {
                rrList = findFileMasterList(categoryId, ids.substring(0, ids.length() - 1), filters, categoryCode);
            }
        }
        if (rrList != null) {
            for (RsResource rsResources : rrList) {
                Map<String, Object> masterMap = new HashMap<String, Object>();
                masterMap.put("level", "2");
                masterMap.put("code", rsResources.getCode());
                masterMap.put("name", rsResources.getName());
                masterMap.put("id", rsResources.getId());
                List<RsMetadata> rmList = findFileMetadataList(rsResources, roleId);
                if (rmList != null) {
                    List<Map<String, Object>> metadataList = new ArrayList<Map<String, Object>>();
                    for (RsMetadata rsMetadata : rmList) {
                        Map<String, Object> metadataMap = new HashMap<String, Object>();
                        metadataMap.put("level", "3");
                        metadataMap.put("code", rsMetadata.getId());
                        metadataMap.put("name", rsMetadata.getName());
                        metadataMap.put("id", UUID.randomUUID().toString());
                        String dictCode = rsMetadata.getDictCode();
                        metadataMap.put("dictCode", rsMetadata.getDictCode());
                        if (!StringUtils.isEmpty(dictCode)) {
                            if (dictCode.equals("DATECONDITION")) {
                                metadataMap.put("dictName", "时间");
                            } else {
                                RsDictionary rsDictionary = rsDictionaryDao.findByCode(rsMetadata.getDictCode());
                                if (rsDictionary != null) {
                                    metadataMap.put("dictName", rsDictionary.getName());
                                }
                            }
                        }
                        metadataList.add(metadataMap);
                    }
                    masterMap.put("children", metadataList);
                }
                resultList.add(masterMap);
            }
        }
        return resultList;
    }

    /**
     * 综合查询指标统计列表树
     *
     * @param filters
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getQuotaList(String filters) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        //获取最上级目录
        List<TjQuotaCategory> parentList = findQuotaCategoryList(0, filters);
        if (parentList != null) {
            for (TjQuotaCategory quotaCategory : parentList) {
                Map<String, Object> childMap = getTreeMap(quotaCategory, 0, filters);
                if (filters != null && !filters.equals("")) {
                    if (((List<String>) childMap.get("child")).size() > 0) {
                        resultList.add(childMap);
                    }
                } else {
                    resultList.add(childMap);
                }
            }
        }
        return resultList;
    }

    public RsResource profileCompleteSave(RsResource rsResource, List<RsResourceMetadata> metadataList, RsResourceDefaultParam resourceDefaultParam) {
        RsResource newRsResource = rsResourceDao.save(rsResource);
        metadataList.forEach(item -> rsResourceMetadataDao.save(item));
        if (resourceDefaultParam != null) {
            resourceDefaultParamDao.save(resourceDefaultParam);
        }
        return newRsResource;
    }

    public RsResource quotaCompleteSave(RsResource rsResource, List<RsResourceQuota> resourceQuotaList, RsResourceDefaultParam rsResourceDefaultParam) {
        RsResource newRsResource = rsResourceDao.save(rsResource);
        resourceQuotaList.forEach(item -> rsResourceQuotaDao.save(item));
        if (rsResourceDefaultParam != null) {
            resourceDefaultParamDao.save(rsResourceDefaultParam);
        }
        return newRsResource;
    }

}
