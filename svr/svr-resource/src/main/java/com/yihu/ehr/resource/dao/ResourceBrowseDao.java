package com.yihu.ehr.resource.dao;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.common.sqlparser.ParserFactory;
import com.yihu.ehr.query.common.sqlparser.ParserSql;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.resource.client.QuotaStatisticsClient;
import com.yihu.ehr.resource.client.StdTransformClient;
import com.yihu.ehr.resource.model.*;
import com.yihu.ehr.resource.service.RedisService;
import com.yihu.ehr.resource.service.RsResourceService;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by hzp on 2016/4/22.
 * 资源查询底层接口
 */
@Repository
public class ResourceBrowseDao {

    private Integer defaultPage = 1;
    private Integer defaultSize = 1000;
    private String mainJoinCore = ResourceCore.MasterTable + "_shard1_replica1";
    private String subJoinCore = ResourceCore.SubTable + "_shard1_replica1";
    private static final String INDEX = "archive_relation";
    private static final String TYPE = "info";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HbaseQuery hbaseQuery;
    @Autowired
    private SolrQuery solr;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RsResourceDao rsResourceDao;
    @Autowired
    private ResourceBrowseMetadataDao resourceBrowseMetadataDao;
    @Autowired
    private RsResourceDefaultParamDao resourceDefaultParamDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RsRolesResourceMetadataDao rsRolesResourceMetadataDao;
    @Autowired
    private StdTransformClient stdTransformClient;
    @Autowired
    private RsRolesResourceDao rsRolesResourceDao;
    @Autowired
    private RsResourceQuotaDao rsResourceQuotaDao;
    @Autowired
    private QuotaStatisticsClient quotaStatisticsClient;
    @Autowired
    private HBaseDao hbaseDao;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private SolrQuery solrQuery;
    @Autowired
    private RsResourceService rsResourceService;

    /**
     * 获取资源授权数据元列表
     *
     * @param rsResource 资源
     * @param roleId 角色ID列表
     * @return
     * @throws Exception
     */
    public List<DtoResourceMetadata> getAccessMetadata(RsResource rsResource, String roleId, Map<String, String> correspondMap) throws Exception {
        Set<String> rsMetadataIdSet = new HashSet<>();
        String grantType = rsResource.getGrantType();
        boolean isOtherVersion = false;
        if (rsResource.getCode().split("\\$").length > 1) {
            isOtherVersion = true;
        }
        if (isOtherVersion) {
            String version = rsResource.getCode().split("\\$")[1];
            String code = rsResource.getCode().split("\\$")[0];
            String otherStdMetadataStr = stdTransformClient.stdMetadataCodes(version, code); //省平台临时数据处理
            if (null == otherStdMetadataStr) {
                throw new ApiException("省平台数据缓存为空");
            }
            String[] otherStdMetadataArr = otherStdMetadataStr.split(",");
            List<String> transformEhrMetadataList = new ArrayList<>(); // 此list存储其他标准数据集底下的数据元转换成的平台的数据元的id (EHR_XXXXX)
            for (String otherStdMetadata : otherStdMetadataArr) {
                String dataSetAndMetadata = stdTransformClient.adapterMetadataCode("5a6951bff0bb", code, otherStdMetadata); //适配版本号
                if (!StringUtils.isEmpty(dataSetAndMetadata) && dataSetAndMetadata.split("\\.").length > 1) {
                    String[] dataSetAndMetadataArr = dataSetAndMetadata.split("\\.");
                    String ehrMetadata = redisService.getRsAdapterMetaData("59083976eebd", dataSetAndMetadataArr[0], dataSetAndMetadataArr[1]);
                    if (!StringUtils.isEmpty(ehrMetadata)) {
                        transformEhrMetadataList.add(ehrMetadata);
                        correspondMap.put(ehrMetadata, otherStdMetadata);
                    }
                }
            }
            if (grantType.equals("1") && !roleId.equals("*")) {
                List<String> roleIdList = objectMapper.readValue(roleId, List.class);
                for (String id : roleIdList) {
                    RsRolesResource rsRolesResource = rsRolesResourceDao.findByResourceIdAndRolesId(rsResource.getId(), id);
                    if (rsRolesResource != null) {
                        List<RsRolesResourceMetadata> rsRolesResourceMetadataList = rsRolesResourceMetadataDao.findByRolesResourceIdAndValid(rsRolesResource.getId(), "1");
                        if (rsRolesResourceMetadataList != null) {
                            for (RsRolesResourceMetadata rsRolesResourceMetadata : rsRolesResourceMetadataList) {
                                if (transformEhrMetadataList.contains(rsRolesResourceMetadata.getResourceMetadataId())) { // 如果其他标准数据集包含该数据元
                                    rsMetadataIdSet.add(rsRolesResourceMetadata.getResourceMetadataId());
                                }
                            }
                        }
                    }
                }
                if (rsMetadataIdSet.size() > 0) {
                    StringBuilder rsMetadataIds = new StringBuilder();
                    for (String id : rsMetadataIdSet) {
                        rsMetadataIds.append("'");
                        rsMetadataIds.append(id);
                        rsMetadataIds.append("',");
                    }
                    if (rsMetadataIds.length() <= 0) {
                        return null;
                    }
                    return resourceBrowseMetadataDao.getAuthResourceMetadata(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
                } else {
                    return null;
                }
            } else {
                StringBuilder rsMetadataIds = new StringBuilder();
                for (String id : transformEhrMetadataList) {
                    rsMetadataIds.append("'");
                    rsMetadataIds.append(id);
                    rsMetadataIds.append("',");
                }
                if (rsMetadataIds.length() <= 0) {
                    return null;
                }
                return resourceBrowseMetadataDao.getRsMetadataByIds(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
            }
        } else { //EHR所用标准
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
                    StringBuilder rsMetadataIds = new StringBuilder();
                    for (String id : rsMetadataIdSet) {
                        rsMetadataIds.append("'");
                        rsMetadataIds.append(id);
                        rsMetadataIds.append("',");
                    }
                    return resourceBrowseMetadataDao.getAuthResourceMetadata(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
                } else {
                    return null;
                }
            } else {
                //返回所有数据元
                return resourceBrowseMetadataDao.getAllResourceMetadata(rsResource.getCode());
            }
        }
    }

    /**
     * @param resourcesCode 资源编码
     * @param roleId        角色ID列表
     * @param saas          权限
     * @param queryParams   查询条件 {"q":"*:*","fq":"*:*","basicFl":"","dFl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}"}
     * @param page          页码
     * @param size          页数
     * @return
     * @throws Exception
     */
    public Envelop getEhrCenter (String resourcesCode, String roleId, String saas, String queryParams, Integer page, Integer size) throws Exception {
        //获取Saas权限
        StringBuilder q = new StringBuilder();
        if (saas != null && !"*".equals(saas)) {
            q.append(saas);
        }
        String fq = "";
        final Map<String, String> sort = new HashMap<>();
        Map<String, String> query = new HashMap<>();
        if (queryParams != null) {
            query = objectMapper.readValue(queryParams, Map.class);
            if (query.containsKey("q") && !query.get("q").trim().equals("*:*")) {
                if (q.length() > 0) {
                    q.append(" AND (");
                    q.append(query.get("q"));
                    q.append(")");
                } else {
                    q.append(query.get("q"));
                }
            }
            if (query.containsKey("fq")) {
                fq = query.get("fq");
            }
            if (query.containsKey("sort")) {
                Map<String, String> temp = objectMapper.readValue((String) query.get("sort"), Map.class);
                temp.forEach((key, value) -> {
                    sort.put(key, value);
                });
            }
        }
        //通过资源代码获取默认参数
        List<RsResourceDefaultParam> paramsList = resourceDefaultParamDao.findByResourcesCode(resourcesCode);
        for (RsResourceDefaultParam param : paramsList) {
            if (param.getParamKey().equals("sort")) {
                Map<String, String> temp = objectMapper.readValue(param.getParamValue(), Map.class);
                temp.forEach((key, value) -> {
                    sort.put(key, value);
                });
            }
            if (param.getParamKey().equals("q")) {
                List<QueryCondition> ql = parseCondition(param.getParamValue());
                if (q.length() > 0) {
                    q.append(" AND ");
                    q.append(solrQuery.conditionToString(ql));
                } else {
                    q.append(solrQuery.conditionToString(ql));
                }
            }
        }
        if (resourcesCode != null) {
            RsResource rsResources = rsResourceService.getResourceByCategory(resourcesCode,"standard");
            //获取资源结构权限，该部分新增其他标准数据集的判断
            List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResources, roleId, new HashMap<>());
            if (metadataList != null && metadataList.size() > 0) {
                //数据元信息字段
                List<String> metadataIdList = new ArrayList<>();
                for (DtoResourceMetadata metadata : metadataList) {
                    String id = metadata.getId();
                    metadataIdList.add(id);
                    String dictCode = metadata.getDictCode();
                    if (!StringUtils.isEmpty(dictCode)) {
                        metadataIdList.add(id + "_VALUE");
                    }
                }
                Page<Map<String, Object>> result = hbaseQuery.queryBySolr(ResourceCore.MasterTable, q.toString(), objectMapper.writeValueAsString(sort), fq, StringUtils.join(ResourceCells.getMasterBasicCell(ProfileType.Standard), ","), StringUtils.join(metadataIdList, ","), page, size);
                Envelop envelop = new Envelop();
                envelop.setSuccessFlg(true);
                envelop.setCurrPage(result.getNumber());
                envelop.setPageSize(result.getSize());
                envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
                envelop.setDetailModelList(result.getContent());
                return envelop;
            }
        } else {
            String basicFl = "";
            String dFl = "";
            if (query.containsKey("basicFl")) {
                basicFl = query.get("basicFl");
            }
            if (query.containsKey("dFl")) {
                dFl = query.get("dFl");
            }
            Page<Map<String, Object>> result = hbaseQuery.queryBySolr(ResourceCore.MasterTable, q.toString(), objectMapper.writeValueAsString(sort), fq, basicFl, dFl, page, size);
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setCurrPage(result.getNumber());
            envelop.setPageSize(result.getSize());
            envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
            envelop.setDetailModelList(result.getContent());
            return envelop;
        }
        throw new ApiException("资源无相关数据元");
    }

    /**
     * @param resourcesCode 资源编码
     * @param roleId        角色ID列表
     * @param saas          权限
     * @param queryParams   查询条件 {"q":"*:*","fq":"*:*","basicFl":"","dFl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}"}
     * @param page          页码
     * @param size          页数
     * @return
     * @throws Exception
     */
    public Envelop getEhrCenterSub (String resourcesCode, String roleId, String saas, String queryParams, Integer page, Integer size) throws Exception {
        //获取Saas权限
        StringBuilder q = new StringBuilder();
        if (saas != null && !"*".equals(saas)) {
            q.append(saas);
        }
        String fq = "";
        final Map<String, String> sort = new HashMap<>();
        Map<String, String> query = new HashMap<>();
        if (queryParams != null) {
            query = objectMapper.readValue(queryParams, Map.class);
            if (query.containsKey("q") && !query.get("q").trim().equals("*:*")) {
                if (q.length() > 0) {
                    q.append(" AND (");
                    q.append(query.get("q"));
                    q.append(")");
                } else {
                    q.append(query.get("q"));
                }
            }
            if (query.containsKey("fq")) {
                fq = query.get("fq");
            }
            if (query.containsKey("sort")) {
                Map<String, String> temp = objectMapper.readValue((String) query.get("sort"), Map.class);
                temp.forEach((key, value) -> {
                    sort.put(key, value);
                });
            }
        }
        //通过资源代码获取默认参数
        List<RsResourceDefaultParam> paramsList = resourceDefaultParamDao.findByResourcesCode(resourcesCode);
        for (RsResourceDefaultParam param : paramsList) {
            if (param.getParamKey().equals("sort")) {
                Map<String, String> temp = objectMapper.readValue(param.getParamValue(), Map.class);
                temp.forEach((key, value) -> {
                    sort.put(key, value);
                });
            }
            if (param.getParamKey().equals("table")) {
                if (q.length() > 0) {
                    q.append(" AND (rowkey:*$" + param.getParamValue() + "$*)");
                } else {
                    q.append("rowkey:*$" + param.getParamValue() + "$*");
                }
            }
            if (param.getParamKey().equals("q")) {
                List<QueryCondition> ql = parseCondition(param.getParamValue());
                if (q.length() > 0) {
                    q.append(" AND ");
                    q.append(solrQuery.conditionToString(ql));
                } else {
                    q.append(solrQuery.conditionToString(ql));
                }
            }
        }
        if (resourcesCode != null) {
            RsResource rsResources = rsResourceService.getResourceByCategory(resourcesCode,"standard");
            //获取资源结构权限，该部分新增其他标准数据集的判断
            List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResources, roleId, new HashMap<>());
            if (metadataList != null && metadataList.size() > 0) {
                //数据元信息字段
                List<String> metadataIdList = new ArrayList<>();
                for (DtoResourceMetadata metadata : metadataList) {
                    String id = metadata.getId();
                    metadataIdList.add(id);
                    String dictCode = metadata.getDictCode();
                    if (!StringUtils.isEmpty(dictCode)) {
                        metadataIdList.add(id + "_VALUE");
                    }
                }
                Page<Map<String, Object>> result = hbaseQuery.queryBySolr(ResourceCore.SubTable, q.toString(), objectMapper.writeValueAsString(sort), fq, StringUtils.join(ResourceCells.getSubBasicCell(ProfileType.Standard), ","), StringUtils.join(metadataIdList, ","), page, size);
                Envelop envelop = new Envelop();
                envelop.setSuccessFlg(true);
                envelop.setCurrPage(result.getNumber());
                envelop.setPageSize(result.getSize());
                envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
                envelop.setDetailModelList(result.getContent());
                return envelop;
            }
        } else {
            String basicFl = "";
            String dFl = "";
            if (query.containsKey("basicFl")) {
                basicFl = query.get("basicFl");
            }
            if (query.containsKey("dFl")) {
                dFl = query.get("dFl");
            }
            Page<Map<String, Object>> result = hbaseQuery.queryBySolr(ResourceCore.SubTable, q.toString(), objectMapper.writeValueAsString(sort), fq, basicFl, dFl, page, size);
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setCurrPage(result.getNumber());
            envelop.setPageSize(result.getSize());
            envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
            envelop.setDetailModelList(result.getContent());
            return envelop;
        }
        throw new ApiException("资源无相关数据元");
    }

    /**
     * habse主表的solr分组统计
     * {"q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4","customGroup":[{"groupField":"lastUpdateTime","groupCondition":{"3Month":"last_update_time:[2016-02-16 TO *]","6Month":"last_update_time:[2015-11-10 TO *]"}}]}
     *
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String, Object>> countEhrCenter(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.MasterTable;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> params = objectMapper.readValue(queryParams, Map.class);
        String q = "";
        String fq = "";
        String groupFields = "";
        String statsFields = "";
        List<SolrGroupEntity> customGroup = new ArrayList<>();
        if (params.containsKey("q")) {
            fq = params.get("q").toString();
            if (params.containsKey("saas") && !params.get("saas").equals("*")) {
                fq += " AND (" + params.get("saas") + ")";
            }
        } else {
            if (params.containsKey("saas") && !params.get("saas").equals("*")) {
                fq = params.get("saas").toString();
            }
        }

        if (params.containsKey("groupFields")) {
            groupFields = params.get("groupFields").toString();
        }
        if (params.containsKey("statsFields")) {
            statsFields = params.get("statsFields").toString();
        }
        if (params.containsKey("customGroup")) {
            ArrayList listGroup = ((ArrayList) params.get("customGroup"));
            if (listGroup != null && listGroup.size() > 0) {
                for (int i = 0; i < listGroup.size(); i++) {
                    String groupField = ((LinkedHashMap) listGroup.get(i)).get("groupField").toString();
                    Map<String, String> groupCondition = (Map) ((LinkedHashMap) listGroup.get(i)).get("groupCondition");

                    customGroup.add(new SolrGroupEntity(groupField, groupCondition));
                }
            }
        }
        //join操作
        if (params.containsKey("join")) {
            String join = params.get("join").toString();
            q = "{!join fromIndex=" + subJoinCore + " from=profile_id to=rowkey}" + join;
        }

        if (groupFields.length() == 0 && customGroup.size() == 0) {
            throw new Exception("缺少分组条件！");
        }
        //数值统计
        if (statsFields != null && statsFields.length() > 0) {
            return solr.getStats(core, groupFields, statsFields, q, fq, customGroup);
        }
        //总数统计
        else {
            if (customGroup.size() == 0) {
                //默认第一页
                if (page == null) {
                    page = defaultPage;
                }
                //默认行数
                if (size == null) {
                    size = defaultSize;
                }

                //多分组
                if (groupFields.contains(",")) {
                    return solr.getGroupMult(core, groupFields, q, fq, page, size);
                } else {
                    return solr.getGroupCount(core, groupFields, q, fq, page, size);
                }
            } else {
                return solr.getGroupMult(core, groupFields, customGroup, q, fq);
            }
        }

    }

    /**
     * habse细表的solr分组统计
     *
     * @param queryParams {"table":"HDSD00_08","q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4",customGroup:""}
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String, Object>> countEhrCenterSub (String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.SubTable;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> params = objectMapper.readValue(queryParams, Map.class);

        String q = "";
        String fq = "";
        String groupFields = "";
        String statsFields = "";
        if (params.containsKey("q")) {
            fq = params.get("q");
        }
        if (params.containsKey("groupFields")) {
            groupFields = params.get("groupFields");
        } else {
            throw new Exception("缺少分组条件！");
        }
        if (params.containsKey("statsFields")) {
            statsFields = params.get("statsFields");
        }
        if (params.containsKey("table")) {
            if (fq.length() > 0) {
                fq += " AND rowkey:*" + params.get("table") + "*";
            } else {
                fq = "rowkey:*" + params.get("table") + "*";
            }
        }
        if (params.containsKey("saas")) {
            if (fq.length() > 0) {
                fq = "(" + fq + ") AND (" + params.get("saas") + ")";
            } else {
                fq = params.get("saas");
            }
        }
        //join操作
        if (params.containsKey("join")) {
            String join = params.get("join").toString();
            q = "{!join  fromIndex=" + mainJoinCore + " from=rowkey to=profile_id}" + join;
        }

        //数值统计
        if (statsFields != null && statsFields.length() > 0) {
            return solr.getStats(core, groupFields, statsFields, q, fq);
        }
        //总数统计
        else {
            if (groupFields.contains(",")) {//多分组
                return solr.getGroupMult(core, groupFields, null, q, fq); //自定义分组未完善
            } else { //单分组
                //默认第一页
                if (page == null) {
                    page = defaultPage;
                }
                //默认行数
                if (size == null) {
                    size = defaultSize;
                }
                return solr.getGroupCount(core, groupFields, q, fq, page, size);
            }
        }

    }

    /**
     * 获取指标数据
     * @param resourcesCode
     * @param roleId
     * @param saas
     * @param queryParams
     * @param page
     * @param size
     * @return
     */
    public Envelop getQuotaData(String resourcesCode, String roleId, String saas, String queryParams, Integer page, Integer size) throws IOException {
        RsResource rsResource = rsResourceService.getResourceByCategory(resourcesCode,"standard");
        if (rsResource.getDimension() != null && "orgHealthCategoryCode".equals(rsResource.getDimension())) {
            List<RsResourceQuota> list = rsResourceQuotaDao.findByResourceId(rsResource.getId());
            final StringBuilder quotaCodeStr = new StringBuilder();
            list.forEach(item -> {
                quotaCodeStr.append(item.getQuotaCode()).append(",");
            });
            List<Map<String, Object>> resultList = quotaStatisticsClient.getQuotaReportTwoDimensionalTable(quotaCodeStr.toString(), null, rsResource.getDimension(), null);
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resultList);
            return envelop;
        }
        return null;
    }

    /**
     * 获取非结构化资源
     *
     * @param filters key1=val1;key2>val2;key3<>val3
     * @param sorts
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getEhrFile(String filters, String sorts, Integer page, Integer size) throws Exception {
        List<Map<String, Object>> esList = elasticSearchUtil.page(INDEX, TYPE, filters, sorts, page, size);
        List<String> rowkeys = new ArrayList<>(esList.size());
        esList.forEach(item -> rowkeys.add((String) item.get("_id")));
        List<Map<String, Object>> hbaseList = new ArrayList<>(esList.size());
        Result[] results = hbaseDao.getResultList("HealthFile", rowkeys, "", ""); //hbase结果集
        if (results != null && results.length > 0) {
            for (Result result : results) {
                Map<String, Object> obj = hbaseQuery.resultToMap(result);
                if (obj != null) {
                    if (obj.get("file_list") != null) {
                        String file_list = (String) obj.get("file_list");
                        List<Map<String, String>> list = objectMapper.readValue(file_list, List.class);
                        obj.put("file_list", list);
                    }
                    if (obj.get("sub_rowkeys") != null) {
                        String sub_rowkeys = (String) obj.get("sub_rowkeys");
                        List<String> list = objectMapper.readValue(sub_rowkeys, List.class);
                        obj.put("sub_rowkeys", list);
                    }
                    hbaseList.add(obj);
                } else {
                    hbaseList.add(new HashMap<>());
                }
            }
        }
        int count = (int) elasticSearchUtil.count(INDEX, TYPE, filters);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(page);
        envelop.setPageSize(size);
        envelop.setTotalCount(count);
        envelop.setDetailModelList(hbaseList);
        return envelop;
    }

    /**
     * 获取Mysql配置库数据
     *
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String, Object>> getMysqlData(String queryParams, Integer page, Integer size) throws Exception {
        String sql = queryParams;

        //判定是否完整sql语句
        if (sql.toLowerCase().indexOf(" from ") <= 0) {
            sql = "select * from " + queryParams;
        }
        //查询总条数
        ParserSql parser = ParserFactory.getParserSql();
        String sqlCount = parser.getCountSql(sql);
        long count = jdbcTemplate.queryForObject(sqlCount, Long.class);
        //默认第一页
        if (page == null) {
            page = defaultPage;
        }
        //默认行数
        if (size == null) {
            size = defaultSize;
        }
        //分页查询
        List<Map<String, Object>> list;
        if (count > size) {
            String sqlList = parser.getPageSql(sql, page, size);
            list = jdbcTemplate.queryForList(sqlList);
        } else {
            list = jdbcTemplate.queryForList(sql);
        }
        return new PageImpl<>(list, new PageRequest(page - 1, size), count);
    }

    /**
     * 获取solr索引列表
     * queryParams可为solr表达式，也可为json例：{"q":"*:*","saas":"*","join":"*:*","fl":"","sort":"{\"field1\":\"asc\",\"field2\":\"desc\"}""}
     * 有join参数做join操作
     */
    public Page<String> getSolrIndexs(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.MasterTable;
        String q = "";
        String fq = "";
        String basicFl = "";
        String dFl = "";
        String sort = "";
        if (queryParams != null && queryParams.length() > 0) {
            if (queryParams.startsWith("{") && queryParams.endsWith("}")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if (obj.containsKey("q")) {
                    q = obj.get("q");
                    if (obj.containsKey("saas") && !obj.get("saas").equals("*")) {
                        q += " AND (" + obj.get("saas") + ")";
                    }
                } else {
                    if (obj.containsKey("saas") && !obj.get("saas").equals("*")) {
                        q = obj.get("saas");
                    }
                }
                if (obj.containsKey("basicFl")) {
                    basicFl = obj.get("basicFl");
                }
                if (obj.containsKey("dFl")) {
                    dFl = obj.get("dFl");
                }
                if (obj.containsKey("sort")) {
                    sort = obj.get("sort");
                }
                //join操作
                if (obj.containsKey("join")) {
                    String join = obj.get("join");
                    fq = q;
                    q = "{!join fromIndex=" + subJoinCore + " from=profile_id to=rowkey}" + join;
                }
            } else {
                q = queryParams;
            }
        }

        //默认第一页
        if (page == null) {
            page = defaultPage;
        }
        //默认行数
        if (size == null) {
            size = defaultSize;
        }
        return hbaseQuery.queryIndexBySolr(core, q, sort, fq, basicFl, dFl, page, size);
    }

    /**
     * @param resourcesCode 资源编码
     * @param roleId        角色ID列表
     * @param saas          权限
     * @param rowKey        主键
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getEhrCenterByScan (String resourcesCode, String roleId, String saas, String rowKey) throws Exception {
        Map<String, Object> query = new HashMap<>();
        if (resourcesCode != null) {
            RsResource rsResources = rsResourceService.getResourceByCategory(resourcesCode,"standard");
            //获取资源结构权限，该部分新增其他标准数据集的判断
            List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResources, roleId, new HashMap<>());
            if (metadataList != null && metadataList.size() > 0) {
                //数据元信息字段
                List<String> metadataIdList = new ArrayList<>();
                for (DtoResourceMetadata metadata : metadataList) {
                    String id = metadata.getId();
                    metadataIdList.add(id);
                    String dictCode = metadata.getDictCode();
                    if (!StringUtils.isEmpty(dictCode)) {
                        metadataIdList.add(id + "_VALUE");
                    }
                }
                List<Map<String, Object>> data = new ArrayList<>();
                //Scan查询
                String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.MasterTable, rowKey, rowKey.substring(0, rowKey.length() - 1) + "1", "^" + rowKey);
                List list = Arrays.asList(legacyRowKeys);
                Result[] resultList = hbaseDao.getResultList(ResourceCore.MasterTable, list, "", ""); //hbase结果集
                if (resultList != null && resultList.length > 0) {
                    for (Result result : resultList) {
                        Map<String, Object> obj = hbaseQuery.resultToMap(result);
                        if (obj != null) {
                            data.add(obj);
                        }
                    }
                }
                return data;
            }
        } else {
            String basicFl = "";
            String dFl = "";
            if (query.containsKey("basicFl")) {
                basicFl = (String) query.get("basicFl");
            }
            if (query.containsKey("dFl")) {
                dFl = (String) query.get("dFl");
            }
            List<Map<String, Object>> data = new ArrayList<>();
            //Scan查询
            String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.MasterTable, rowKey, rowKey.substring(0, rowKey.length() - 1) + "1", "^" + rowKey);
            List list = Arrays.asList(legacyRowKeys);
            Result[] resultList = hbaseDao.getResultList(ResourceCore.MasterTable, list, basicFl, dFl); //hbase结果集
            if (resultList != null && resultList.length > 0) {
                for (Result result : resultList) {
                    Map<String, Object> obj = hbaseQuery.resultToMap(result);
                    if (obj != null) {
                        data.add(obj);
                    }
                }
            }
            return data;
        }
        throw new ApiException("资源无相关数据元");
    }

    /**
     * @param dataSetCode 数据集编码
     * @param roleId      角色ID列表
     * @param saas        权限
     * @param rowKey
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getEhrCenterSubByScan (String dataSetCode, String roleId, String saas, String rowKey) throws Exception {
        Map<String, Object> query = new HashMap<>();
        if (dataSetCode != null) {
            RsResource rsResources = rsResourceService.getResourceByCategory(dataSetCode,"standard");
            //获取资源结构权限，该部分新增其他标准数据集的判断
            List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResources, roleId, new HashMap<>());
            if (metadataList != null && metadataList.size() > 0) {
                //数据元信息字段
                List<String> metadataIdList = new ArrayList<>();
                for (DtoResourceMetadata metadata : metadataList) {
                    String id = metadata.getId();
                    metadataIdList.add(id);
                    String dictCode = metadata.getDictCode();
                    if (!StringUtils.isEmpty(dictCode)) {
                        metadataIdList.add(id + "_VALUE");
                    }
                }
                List<Map<String, Object>> data = new ArrayList<>();
                String subrowKey = rowKey + "$" + dataSetCode + "$";

                //Scan查询
                String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.SubTable, subrowKey + "0", subrowKey + "z", "^" + rowKey);
//                String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.SubTable, rowKey, rowKey.substring(0, rowKey.length() - 1) + "1", "^" + rowKey);

                List list = Arrays.asList(legacyRowKeys);
                //获取各个数据集相关的rowkey

                Result[] resultList = hbaseDao.getResultList(ResourceCore.SubTable, list, StringUtils.join(ResourceCells.getSubBasicCell(ProfileType.Standard), ","), StringUtils.join(metadataIdList, ",")); //hbase结果集
                if (resultList != null && resultList.length > 0) {
                    for (Result result : resultList) {
                        Map<String, Object> obj = hbaseQuery.resultToMap(result);
                        if (obj != null) {
                            data.add(obj);
                        }
                    }
                }
                return data;
            }
        } else {
            String basicFl = "";
            String dFl = "";
            if (query.containsKey("basicFl")) {
                basicFl = (String) query.get("basicFl");
            }
            if (query.containsKey("dFl")) {
                dFl = (String) query.get("dFl");
            }
            List<Map<String, Object>> data = new ArrayList<>();
            //Scan查询
            String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.SubTable, rowKey, rowKey.substring(0, rowKey.length() - 1) + "1", "^" + rowKey);
            List list = Arrays.asList(legacyRowKeys);
            Result[] resultList = hbaseDao.getResultList(ResourceCore.SubTable, list, basicFl, dFl); //hbase结果集
            if (resultList != null && resultList.length > 0) {
                for (Result result : resultList) {
                    Map<String, Object> obj = hbaseQuery.resultToMap(result);
                    if (obj != null) {
                        data.add(obj);
                    }
                }
            }
            return data;
        }
        throw new ApiException("资源无相关数据元");
    }


    /**
     * 通过数据集编码集合，获取对应rowkey 下的资源数据
     *
     * @param dataSetCodes 数据集编码集合
     * @param roleId       角色ID列表
     * @param saas         权限
     * @param rowKey
     * @return
     * @throws Exception
     */
    public Map<String, Object> getEhrCenterSubMapByScan (List<String> dataSetCodes, String roleId, String saas, String rowKey) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, List<String>> rowKeyMap = new HashMap<>();
        //获取Saas权限
        StringBuilder q = new StringBuilder();
        if (saas != null && !"*".equals(saas)) {
            q.append(saas);
        }
        String fq = "";
        final Map<String, String> sort = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        if (query.containsKey("q")) {
            if (q.length() > 0) {
                q.append(" AND (");
                q.append("rowKey:" + rowKey);
                q.append(")");
            } else {
                q.append("(" + query.get("q") + ")");
            }
        }

        if (query.containsKey("sort")) {
            Map<String, String> temp = objectMapper.readValue((String) query.get("sort"), Map.class);
            temp.forEach((key, value) -> {
                sort.put(key, value);
            });
        }

        if (CollectionUtils.isNotEmpty(dataSetCodes)) {
            String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.SubTable, rowKey, rowKey.substring(0, rowKey.length() - 1) + "1", "^" + rowKey);
            List<String> list = Arrays.asList(legacyRowKeys);
            if (CollectionUtils.isNotEmpty(list)) {
                for (String dataSet : dataSetCodes) {
                    String subrowKey = rowKey + "$" + dataSet + "$";
                    List<String> keys = list.stream().filter(row -> row.contains(subrowKey)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(keys)) {
                        rowKeyMap.put(dataSet, keys);
                    }
                }
            }
        }

        Iterator<String> iterator = rowKeyMap.keySet().iterator();
        while (iterator.hasNext()) {
            String dataSetCode = iterator.next();
            //获取rowkey
            List<String> list = rowKeyMap.get(dataSetCode);

            if (dataSetCode != null) {
                RsResource rsResources = rsResourceService.getResourceByCategory(dataSetCode,"standard");
                //获取资源结构权限，该部分新增其他标准数据集的判断
                List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResources, roleId, new HashMap<>());
                if (metadataList != null && metadataList.size() > 0) {
                    //数据元信息字段
                    List<String> metadataIdList = new ArrayList<>();
                    for (DtoResourceMetadata metadata : metadataList) {
                        String id = metadata.getId();
                        metadataIdList.add(id);
                        String dictCode = metadata.getDictCode();
                        if (!StringUtils.isEmpty(dictCode)) {
                            metadataIdList.add(id + "_VALUE");
                        }
                    }
                    List<Map<String, Object>> data = new ArrayList<>();

                    Result[] resultList = hbaseDao.getResultList(ResourceCore.SubTable, list, StringUtils.join(ResourceCells.getSubBasicCell(ProfileType.Standard), ","), StringUtils.join(metadataIdList, ",")); //hbase结果集
                    if (resultList != null && resultList.length > 0) {
                        for (Result result : resultList) {
                            Map<String, Object> obj = hbaseQuery.resultToMap(result);
                            if (obj != null) {
                                data.add(obj);
                            }
                        }
                    }
                    resultMap.put(dataSetCode, data);
                }
            } else {
                String basicFl = "";
                String dFl = "";
                if (query.containsKey("basicFl")) {
                    basicFl = (String) query.get("basicFl");
                }
                if (query.containsKey("dFl")) {
                    dFl = (String) query.get("dFl");
                }
                List<Map<String, Object>> data = new ArrayList<>();
                //Scan查询
                Result[] resultList = hbaseDao.getResultList(ResourceCore.SubTable, list, basicFl, dFl); //hbase结果集
                if (resultList != null && resultList.length > 0) {
                    for (Result result : resultList) {
                        Map<String, Object> obj = hbaseQuery.resultToMap(result);
                        if (obj != null) {
                            data.add(obj);
                        }
                    }
                }
                resultMap.put(dataSetCode, data);
            }
        }

        return resultMap;

    }

    /**
     * 查询条件转换
     *
     * @param queryCondition
     * @return
     * @throws Exception
     */
    private List<QueryCondition> parseCondition(String queryCondition) throws Exception {
        List<QueryCondition> ql = new ArrayList<QueryCondition>();
        List<Map<String, Object>> list = objectMapper.readValue(queryCondition, List.class);
        if (list != null && list.size() > 0) {
            for (Map<String, Object> item : list) {
                String andOr = String.valueOf(item.get("andOr")).trim();
                String field = String.valueOf(item.get("field")).trim();
                String cond = String.valueOf(item.get("condition")).trim();
                String value = String.valueOf(item.get("value"));
                if (value.indexOf(",") > 0) {
                    ql.add(new QueryCondition(andOr, cond, field, value.split(",")));
                } else {
                    ql.add(new QueryCondition(andOr, cond, field, value));
                }
            }
        }
        return ql;
    }

}
