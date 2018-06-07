package com.yihu.ehr.resource.dao;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.common.sqlparser.ParserFactory;
import com.yihu.ehr.query.common.sqlparser.ParserSql;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.resource.client.QuotaStatisticsClient;
import com.yihu.ehr.resource.client.StdTransformClient;
import com.yihu.ehr.resource.model.*;
import com.yihu.ehr.resource.service.RedisService;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;


/**
 * Created by hzp on 2016/4/22.
 * 资源查询底层接口
 */
@Repository
public class ResourceBrowseDao {

    private List<String> basicField =  new ArrayList<>(Arrays.asList("rowkey", "event_type", "event_no", "event_date", "demographic_id", "patient_id", "org_code", "org_name", "profile_id", "cda_version", "client_id", "profile_type", "patient_name", "org_area", "diagnosis", "health_problem"));

    private Integer defaultPage = 1;
    private Integer defaultSize = 1000;
    private String mainJoinCore = ResourceCore.MasterTable + "_shard1_replica1";
    private String subJoinCore = ResourceCore.SubTable + "_shard1_replica1";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HbaseQuery hbase;
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

    /**
     * 获取资源授权数据元列表
     * @param rsResource
     * @param roleId
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
            String [] otherStdMetadataArr = otherStdMetadataStr.split(",");
            List<String> transformEhrMetadataList = new ArrayList<>(); // 此list存储其他标准数据集底下的数据元转换成的平台的数据元的id (EHR_XXXXX)
            for (String otherStdMetadata : otherStdMetadataArr) {
                String dataSetAndMetadata = stdTransformClient.adapterMetadataCode("5a6951bff0bb", code, otherStdMetadata); //适配版本号
                if (!StringUtils.isEmpty(dataSetAndMetadata) && dataSetAndMetadata.split("\\.").length > 1 ) {
                    String [] dataSetAndMetadataArr = dataSetAndMetadata.split("\\.");
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
                    if (rsMetadataIds.length() <= 0 ) {
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
                if (rsMetadataIds.length() <= 0 ) {
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
     *
     * @param resourcesCode 资源编码
     * @param roleId 角色ID列表
     * @param saas 权限
     * @param queryParams 查询条件
     * @param page 页码
     * @param size 页数
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
        Map<String, Object> query = new HashMap<>();
        if (queryParams != null) {
            query = objectMapper.readValue(queryParams, Map.class);
            if (query.containsKey("q")) {
                if (q.length() > 0) {
                    q.append(" AND (");
                    q.append(query.get("q"));
                    q.append(")");
                } else {
                    q.append(query.get("q"));
                }
            }
            if (query.containsKey("fq")) {
                fq = (String) query.get("fq");
            }
            if (query.containsKey("sort")) {
                Map<String, String> temp  = objectMapper.readValue((String) query.get("sort"), Map.class);
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
        }
        if (resourcesCode != null) {
            RsResource rsResources = rsResourceDao.findByCode(resourcesCode);
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
                Page<Map<String, Object>>  result = hbase.queryBySolr(ResourceCore.MasterTable, q.toString(), objectMapper.writeValueAsString(sort), fq, StringUtils.join(basicField, ","), StringUtils.join(metadataIdList, ","), page, size);
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
                basicFl = (String) query.get("basicFl");
            }
            if (query.containsKey("dFl")) {
                dFl = (String) query.get("dFl");
            }
            Page<Map<String, Object>>  result = hbase.queryBySolr(ResourceCore.MasterTable, q.toString(), objectMapper.writeValueAsString(sort), fq, basicFl, dFl, page, size);
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
     *
     * @param resourcesCode 资源编码
     * @param roleId 角色ID列表
     * @param saas 权限
     * @param queryParams 查询条件
     * @param page 页码
     * @param size 页数
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
        Map<String, Object> query = new HashMap<>();
        if (queryParams != null) {
            query = objectMapper.readValue(queryParams, Map.class);
            if (query.containsKey("q")) {
                if (q.length() > 0) {
                    q.append(" AND (");
                    q.append(query.get("q"));
                    q.append(")");
                } else {
                    q.append("(" + query.get("q") + ")");
                }
            }
            if (query.containsKey("fq")) {
                fq = (String) query.get("fq");
            }
            if (query.containsKey("sort")) {
                Map<String, String> temp  = objectMapper.readValue((String) query.get("sort"), Map.class);
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
        }
        if (resourcesCode != null) {
            RsResource rsResources = rsResourceDao.findByCode(resourcesCode);
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
                Page<Map<String, Object>> result = hbase.queryBySolr(ResourceCore.SubTable, q.toString(), objectMapper.writeValueAsString(sort), fq, StringUtils.join(basicField, ","), StringUtils.join(metadataIdList, ","), page, size);
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
                basicFl = (String) query.get("basicFl");
            }
            if (query.containsKey("dFl")) {
                dFl = (String) query.get("dFl");
            }
            Page<Map<String, Object>> result = hbase.queryBySolr(ResourceCore.SubTable, q.toString(), objectMapper.writeValueAsString(sort), fq, basicFl, dFl, page, size);
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

    public Envelop getQuotaData (String resourcesCode, String roleId, String saas, String queryParams, Integer page, Integer size) {
        RsResource rsResource = rsResourceDao.findByCode(resourcesCode);
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
     * habse主表的solr分组统计
     * {"q":"*:*","groupFields":"key1,key2","statsFields":"key3,key4","customGroup":[{"groupField":"lastUpdateTime","groupCondition":{"3Month":"last_update_time:[2016-02-16 TO *]","6Month":"last_update_time:[2015-11-10 TO *]"}}]}
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String,Object>> countEhrCenter(String queryParams, Integer page, Integer size) throws Exception {
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
                fq += " AND ("+params.get("saas")+")";
            }
        } else{
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
            q = "{!join fromIndex="+subJoinCore+" from=profile_id to=rowkey}" +join;
        }

        if (groupFields.length() == 0 && customGroup.size() == 0) {
            throw new Exception("缺少分组条件！");
        }
        //数值统计
        if (statsFields != null && statsFields.length() > 0) {
            return solr.getStats(core, groupFields, statsFields, q,fq, customGroup);
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
                    return solr.getGroupMult(core, groupFields, q,fq, page, size);
                } else {
                    return solr.getGroupCount(core, groupFields, q,fq, page, size);
                }
            } else {
                return solr.getGroupMult(core, groupFields, customGroup, q,fq);
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
    public Page<Map<String,Object>> countEhrCenterSub(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.SubTable;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> params = objectMapper.readValue(queryParams, Map.class);

        String q = "";
        String fq="";
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
                fq = "("+fq+") AND (" + params.get("saas") + ")";
            } else {
                fq = params.get("saas");
            }
        }
        //join操作
        if (params.containsKey("join")) {
            String join = params.get("join").toString();
            q = "{!join  fromIndex="+mainJoinCore+" from=rowkey to=profile_id}" +join;
        }

        //数值统计
        if (statsFields != null && statsFields.length() > 0) {
            return solr.getStats(core, groupFields, statsFields, q,fq);
        }
        //总数统计
        else {
            if (groupFields.contains(",")) {//多分组
                return solr.getGroupMult(core, groupFields, null, q,fq); //自定义分组未完善
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
     * 获取Mysql配置库数据
     */
    public Page<Map<String,Object>> getMysqlData(String queryParams, Integer page, Integer size) throws Exception {
        String sql = queryParams;

        //判定是否完整sql语句
        if (sql.toLowerCase().indexOf(" from ") <= 0) {
            sql = "select * from " + queryParams;
        }
        //查询总条数
        ParserSql parser = ParserFactory.getParserSql();
        String sqlCount = parser.getCountSql(sql);
        long count = jdbcTemplate.queryForObject(sqlCount,Long.class);
        //默认第一页
        if (page == null) {
            page = defaultPage;
        }
        //默认行数
        if (size == null) {
            size = defaultSize;
        }
        //分页查询
        List<Map<String,Object>> list;
        if (count > size) {
            String sqlList = parser.getPageSql(sql, page, size);
            list = jdbcTemplate.queryForList(sqlList);
        } else {
            list = jdbcTemplate.queryForList(sql);
        }
        return new PageImpl<>(list,new PageRequest(page - 1, size), count);
    }

    /**
     * 获取非结构化档案
     * @return
     */
    public Page<Map<String,Object>> getRawFiles(String queryParams, Integer page, Integer size) throws Exception {
        String core = ResourceCore.FileTable;
        String q = "";
        String fq = "";
        String sort = "";
        if (queryParams != null && queryParams.length() > 0) {
            if (queryParams.startsWith("{") && queryParams.endsWith("}")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> obj = objectMapper.readValue(queryParams, Map.class);
                if (obj.containsKey("q")) {
                    q = obj.get("q");
                }
                if (obj.containsKey("sort")) {
                    sort = obj.get("sort");
                }
                //join操作
                if (obj.containsKey("join")) {
                    String join = obj.get("join");
                    fq = q;
                    q = "{!join fromIndex=" + mainJoinCore + " from=rowkey to=profile_id}" + join;
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
        return hbase.queryBySolr(core, q, sort, fq, page, size);
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
                } else{
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
                    q = "{!join fromIndex=" + subJoinCore + " from=profile_id to=rowkey}" +join;
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
        return hbase.queryIndexBySolr(core, q, sort, fq, basicFl, dFl, page, size);
    }

}
