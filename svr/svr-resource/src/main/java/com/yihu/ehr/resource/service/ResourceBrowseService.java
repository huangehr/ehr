package com.yihu.ehr.resource.service;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.resource.client.StdTransformClient;
import com.yihu.ehr.resource.dao.*;
import com.yihu.ehr.resource.model.*;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;



/**
 * Created by hzp on 2016/4/13.
 */
@Service
public class ResourceBrowseService {

    @Autowired
    private SolrQuery solrQuery;
    @Autowired
    private HbaseQuery hbaseQuery;
    @Autowired
    private RsResourceDao rsResourceDao;
    @Autowired
    private ResourceBrowseMetadataDao resourceBrowseMetadataDao;
    @Autowired
    private ResourceBrowseDao resourceBrowseDao;
    @Autowired
    private RsResourceDefaultParamDao resourceDefaultParamDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RsResourceDefaultQueryDao resourcesDefaultQueryDao;
    @Autowired
    private RsRolesResourceGrantService rsRolesResourceGrantService;
    @Autowired
    private RsRolesResourceMetadataGrantService rsRolesResourceMetadataGrantService;
    @Autowired
    private StdTransformClient stdTransformClient;
    @Autowired
    private HBaseDao hbaseDao;

    //忽略字段
    private List<String> ignoreField = new ArrayList<String>(Arrays.asList("rowkey", "event_type", "event_no", "event_date", "demographic_id", "patient_id", "org_code", "org_name", "profile_id", "cda_version", "client_id", "profile_type", "patient_name", "org_area", "diagnosis", "health_problem"));

    /**
     * 资源浏览 -- 资源数据元结构
     * @param resourcesCode
     * @param roleId
     * @return
     * @throws Exception
     */
    public String getResourceMetadata(String resourcesCode, String roleId) throws Exception{
        RsResource rsResource = rsResourceDao.findByCode(resourcesCode);
        if (rsResource == null) {
            return null;
        }
        Map<String, String> correspondMap = new HashMap<>();
        boolean isOtherVersion = false;
        String version = "";
        String code = "";
        if(resourcesCode.split("\\$").length > 1) {
            isOtherVersion = true;
            version = resourcesCode.split("\\$")[1];
            code = resourcesCode.split("\\$")[0];
        }
        List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResource, roleId, correspondMap);
        //资源结构
        List<String> columnName = new ArrayList<String>();
        List<String> columnCode = new ArrayList<String>();
        List<String> columnType = new ArrayList<String>();
        List<String> columnDict = new ArrayList<String>();
        if(metadataList != null) {
            for (DtoResourceMetadata r : metadataList) {
                if(!isOtherVersion) {
                    columnName.add(r.getName());
                    if (!StringUtils.isEmpty(r.getDictCode())) {
                        columnCode.add(r.getId() + "_VALUE");
                    } else {
                        columnCode.add(r.getId());
                    }
                    columnType.add(r.getColumnType());
                    columnDict.add(r.getDictCode());
                }else {
                    String name = stdTransformClient.stdMetadataName(version, code, correspondMap.get(r.getId()));
                    if(!StringUtils.isEmpty(name)) {
                        columnName.add(name);
                        if (!StringUtils.isEmpty(r.getDictCode())) {
                            columnCode.add(r.getId() + "_VALUE");
                        } else {
                            columnCode.add(r.getId());
                        }
                        columnType.add(r.getColumnType());
                        columnDict.add(r.getDictCode());
                    }
                }
            }
        }
        //设置动态datagrid值
        Map<String, Object> mapParam = new HashMap<String, Object>();
        mapParam.put("colunmName", columnName);
        mapParam.put("colunmCode", columnCode);
        mapParam.put("colunmType", columnType);
        mapParam.put("colunmDict", columnDict);
        return objectMapper.writeValueAsString(mapParam);
    }

    /**
     * 资源浏览
     * @return
     */
    public Envelop getResourceData(String resourcesCode, String roleId, String orgCode, String areaCode, String queryCondition, Integer page, Integer size) throws Exception {
        Envelop envelop = new Envelop();
        String queryParams = "";
        //获取资源信息
        RsResource rsResources = rsResourceDao.findByCode(resourcesCode);
        if(rsResources != null) {
            RsResourceDefaultQuery resourcesQuery = resourcesDefaultQueryDao.findByResourcesId(rsResources.getId());
            List<QueryCondition> ql = new ArrayList<>();
            //设置参数
            if (!StringUtils.isEmpty(queryCondition) && !queryCondition.equals("{}")) {
                ql = parseCondition(queryCondition);
            }else if(resourcesQuery != null && resourcesQuery.getResourcesType() == 1) {
                String defaultQuery = resourcesQuery.getQuery();
                ql = parseCondition(defaultQuery);
            }
            queryParams = addParams(queryParams,"q", solrQuery.conditionToString(ql));
            return resourcesBrowse(resourcesCode, rsResources.getRsInterface(), roleId, orgCode, areaCode, queryParams, page, size);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无效资源！");
            return envelop;
        }
    }

    /**
     * 资源浏览（细表数据处理）
     * @param resourcesCode
     * @param orgCode
     * @param areaCode
     * @param queryParams Mysql为sql语句，Hbase为solr查询语法
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop resourcesBrowse(String resourcesCode, String methodName, String roleId, String orgCode, String areaCode, String queryParams, Integer page, Integer size) throws Exception {
        //获取结果集
        Envelop envelop = getResultData(resourcesCode, roleId, orgCode, areaCode, queryParams, page, size);
        //细表的话追加主表的数据
        if(methodName.endsWith("Sub") && envelop.isSuccessFlg() && envelop.getDetailModelList() != null) {
            List<Map<String,Object>> oldList = envelop.getDetailModelList();
            for(Map<String, Object> temp : oldList) {
                String masterRowKey = (String)temp.get("profile_id");
                if(masterRowKey != null) {
                    Map<String, Object> masterMap = hbaseQuery.queryByRowKey(ResourceCore.MasterTable, masterRowKey);
                    temp.put("event_date", masterMap.get("event_date"));
                    temp.put("org_name", masterMap.get("org_name"));
                    temp.put("org_code", masterMap.get("org_code"));
                    temp.put("demographic_id", masterMap.get("demographic_id"));
                    temp.put("patient_name", masterMap.get("patient_name"));
                    String eventType = (String)masterMap.get("event_type");
                    if(null != eventType) {
                        if ("0".equals(eventType)) {
                            temp.put("event_type", "门诊");
                        } else if ("1".equals(eventType)) {
                            temp.put("event_type", "住院");
                        } else if ("2".equals(eventType)) {
                            temp.put("event_type", "体检");
                        }
                    }else {
                        temp.put("event_type", "未知类型");
                    }
                }
            }
        }
        return envelop;
    }

    /**
     * 综合查询档案数据检索
     * @return
     */
    public Envelop getCustomizeData(String resourcesCodes, String metaData, String orgCode, String areaCode, String queryCondition, Integer page, Integer size) throws Exception {
        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        String queryParams = "";
        StringBuilder saas = new StringBuilder();
        //获取资源编码列表
        List<String> codeList = (List<String>) mapper.readValue(resourcesCodes, List.class);
        /**
         * 资源判空检查
         */
        for(String code : codeList) {
            RsResource rsResources = rsResourceDao.findByCode(code);
            if(rsResources == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无效的资源编码：" + code);
                return envelop;
            }
        }
        //获取Saas权限参数
        if(orgCode.equals("*") && areaCode.equals("*")) {
            saas.append("*");
        }else {
            List<String> orgCodeList = objectMapper.readValue(orgCode, List.class);
            List<String> areaCodeList = objectMapper.readValue(areaCode, List.class);
            if((orgCodeList != null && orgCodeList.size() > 0)) {
                for(String oCode : orgCodeList) {
                    if(StringUtils.isEmpty(saas.toString())) {
                        saas.append("org_code:" + oCode);
                    }else {
                        saas.append(" OR org_code:" + oCode);
                    }
                }
            }
            if(areaCodeList != null && areaCodeList.size() > 0) {
                for(String aCode : areaCodeList) {
                    if(StringUtils.isEmpty(saas.toString())) {
                        saas.append("org_area:" + aCode);
                    } else {
                        saas.append(" OR org_area:" + aCode);
                    }
                }
            }
        }
        if(StringUtils.isEmpty(saas)) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无SAAS权限访问资源");
            return envelop;
        }
        List<QueryCondition> ql = new ArrayList<>();
        if (!StringUtils.isEmpty(queryCondition)) {
            ql = parseCondition(queryCondition);
        }
        if(ql.size() > 0) {
            if(solrQuery.conditionToString(ql).contains(":")) {
                queryParams = addParams(queryParams,"q", solrQuery.conditionToString(ql));
            }else {
                queryParams = addParams(queryParams,"q", "*:*");
            }
        }else {
            queryParams = addParams(queryParams,"q", "*:*");
        }
        queryParams = addParams(queryParams,"saas", saas.toString());
        queryParams = addParams(queryParams,"sort", "{\"create_date\":\"desc\"}");
        //基础数据字段
        String basicStr = ignoreField.toString();
        String dealBasicStr = basicStr.substring(1, basicStr.length() - 1).replaceAll(" ", "");
        queryParams = addParams(queryParams,"basicFl", dealBasicStr);
        //数据元信息字段
        if(metaData.equals("")) {
            queryParams = addParams(queryParams,"dFl", "");
        }else {
            //原始集合
            List<String> customizeList = (List<String>) mapper.readValue(metaData, List.class);
            //参数集合
            List<String> paramList = new ArrayList<String>(customizeList.size() * 2);
            for(String id : customizeList) {
                paramList.add(id);
                String dictCode = redisService.getRsMetaData(id);
                if(!StringUtils.isEmpty(dictCode)) {
                    paramList.add(id + "_VALUE");
                }
            }
            String dStr = paramList.toString();
            String dealDStr = dStr.substring(1, dStr.length() - 1).replaceAll(" ", "");
            queryParams = addParams(queryParams,"dFl", dealDStr);
        }
        Page<Map<String, Object>> result = resourceBrowseDao.getEhrCenter(queryParams, page, size);
        if(result != null) {
            envelop.setSuccessFlg(true);
            envelop.setCurrPage(result.getNumber());
            envelop.setPageSize(result.getSize());
            envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
            envelop.setDetailModelList(result.getContent());
        }else {
            envelop.setErrorMsg("数据库数据检索失败");
        }
        return envelop;
    }

    /**
     * 获取最终数据
     * @param resourcesCode
     * @param orgCode
     * @param areaCode
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getResultData(String resourcesCode, String roleId, String orgCode, String areaCode, String queryParams, Integer page, Integer size) throws Exception{
        Envelop envelop = new Envelop();
        RsResource rsResources = rsResourceDao.findByCode(resourcesCode);
        if(rsResources != null) {
            String methodName = rsResources.getRsInterface(); //执行函数
            //获取资源结构权限，该部分新增其他标准数据集的判断
            List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResources, roleId, new HashMap<>());
            //获取Saas权限
            StringBuilder saas = new StringBuilder();
            if (orgCode.equals("*") && areaCode.equals("*")) {
                saas.append("*");
            } else {
                List<String> orgCodeList = objectMapper.readValue(orgCode, List.class);
                List<String> areaCodeList = objectMapper.readValue(areaCode, List.class);
                if ((orgCodeList != null && orgCodeList.size() > 0)) {
                    for (String oCode : orgCodeList) {
                        if (StringUtils.isEmpty(saas.toString())) {
                            saas.append("org_code:" + oCode);
                        } else {
                            saas.append(" OR org_code:" + oCode);
                        }
                    }
                }
                if (areaCodeList != null && areaCodeList.size() > 0) {
                    for (String aCode : areaCodeList) {
                        if (StringUtils.isEmpty(saas.toString())) {
                            saas.append("org_area:" + aCode);
                        } else {
                            saas.append(" OR org_area:" + aCode);
                        }
                    }
                }
            }
            if(StringUtils.isEmpty(saas)) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无SAAS权限访问资源[resourcesCode=" + resourcesCode+ "]");
                return envelop;
            }
            else {
                queryParams = addParams(queryParams,"saas", saas.toString());
            }
            //通过资源代码获取默认参数
            List<RsResourceDefaultParam> paramsList = resourceDefaultParamDao.findByResourcesCode(resourcesCode);
            for(RsResourceDefaultParam param : paramsList) {
                queryParams = addParams(queryParams, param.getParamKey(), param.getParamValue());
            }
            if(metadataList != null && metadataList.size() > 0) {
                /**
                String groupFields = "";
                String statsFields = "";
                String customGroup = "";
                for(DtoResourceMetadata metadata : metadataList) {
                    String key = metadata.getId();
                    String groupType = metadata.getGroupType();
                    String groupData =  metadata.getGroupData();
                    if(groupType != null && groupType.length() > 0) {
                        if(grantType.equals("0")) { //分组字段
                            if(groupData!=null && groupData.length()>0) {
                                customGroup += "{\"groupField\":\"" + key + "\",\"groupCondition\":" + groupData + "},";
                            }
                            else {
                                groupFields += key + ",";
                            }
                        }
                        else if(grantType.equals("1")) {//统计字段
                            statsFields += key + ",";
                        }
                    }
                }
                if(groupFields.length() > 0) {
                    groupFields = groupFields.substring(0, groupFields.length() - 1);
                    queryParams = addParams(queryParams,"groupFields", groupFields);
                }
                if(statsFields.length() > 0) {
                    statsFields = statsFields.substring(0, statsFields.length() - 1);
                    queryParams = addParams(queryParams,"statsFields", statsFields);
                }
                if(customGroup.length()>0) {
                    customGroup = "[" + customGroup.substring(0, customGroup.length() -1 ) + "]";
                    queryParams = addParams(queryParams,"customGroup", customGroup);
                }
                */
                //基础信息字段
                String basicStr = ignoreField.toString();
                String dealBasicStr = basicStr.substring(1, basicStr.length() - 1).replaceAll(" ", "");
                queryParams = addParams(queryParams,"basicFl", dealBasicStr);
                //数据元信息字段
                List<String> metadataIdList = new ArrayList<String>();
                for(DtoResourceMetadata metadata : metadataList) {
                    String id = metadata.getId();
                    metadataIdList.add(id);
                    String dictCode = metadata.getDictCode();
                    if(!StringUtils.isEmpty(dictCode)) {
                        metadataIdList.add(id + "_VALUE");
                    }
                }
                String dStr = metadataIdList.toString();
                String dealDStr = dStr.substring(1, dStr.length() - 1).replaceAll(" ", "");
                queryParams = addParams(queryParams,"dFl", dealDStr);
                //执行函数
                Class<ResourceBrowseDao> classType = ResourceBrowseDao.class;
                Method method = classType.getMethod(methodName, new Class[]{String.class, Integer.class, Integer.class});
                Page<Map<String,Object>> result = (Page<Map<String,Object>>)method.invoke(resourceBrowseDao, queryParams, page, size);
                if (result != null) {
                    envelop.setSuccessFlg(true);
                    envelop.setCurrPage(result.getNumber());
                    envelop.setPageSize(result.getSize());
                    envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
                    envelop.setDetailModelList(result.getContent());
                }
                else {
                    envelop.setErrorMsg("数据库数据检索失败");
                }
            }else {
                envelop.setErrorMsg("资源无相关数据元");
            }
        }else {
            envelop.setErrorMsg("无效资源");
        }
        return envelop;
    }

    /**
     * 查询条件转换
     * @param queryCondition
     * @return
     * @throws Exception
     */
    public List<QueryCondition> parseCondition(String queryCondition) throws Exception {
        List<QueryCondition> ql = new ArrayList<QueryCondition>();
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
        List<Map<String, Object>> list = objectMapper.readValue(queryCondition, javaType);
        if(list != null && list.size() > 0) {
            for(Map<String,Object> item : list) {
                String andOr = String.valueOf(item.get("andOr")).trim();
                String field = String.valueOf(item.get("field")).trim();
                String cond = String.valueOf(item.get("condition")).trim();
                String value = String.valueOf(item.get("value"));
                if(value.indexOf(",")>0) {
                    ql.add(new QueryCondition(andOr, cond, field, value.split(",")));
                }
                else{
                    ql.add(new QueryCondition(andOr, cond, field, value));
                }
            }
        }
        return ql;
    }

    /**
     * 获取非结构化数据
     * @return
     * @throws Exception
     */
    public Envelop getRawFiles(String profileId,String cdaDocumentId,Integer page,Integer size) throws Exception{
        Envelop re = new Envelop();
        String queryParams = "{\"q\":\"rowkey:"+profileId+"*\"}";
        if(cdaDocumentId!=null && cdaDocumentId.length()>0) {
            queryParams = "{\"q\":\"rowkey:"+profileId+"* AND cda_document_id:"+cdaDocumentId+"\"}";
        }
        Page<Map<String,Object>> result = resourceBrowseDao.getRawFiles(queryParams, page, size);
        if (result != null) {
            re.setSuccessFlg(true);
            re.setCurrPage(result.getNumber());
            re.setPageSize(result.getSize());
            re.setTotalCount(new Long(result.getTotalElements()).intValue());
            re.setDetailModelList(result.getContent());
        }
        else{
            re.setSuccessFlg(false);
        }

        return re;
    }

    /**
     * 新增参数
     * @return
     */
    private String addParams(String oldParams, String key,String value) {
        String newParam = "";
        if(value.startsWith("[") && value.endsWith("]")) {
            newParam = "\"" + key + "\":" + value;
        }
        else{
            newParam = "\"" + key + "\":\""+ value.replace("\"","\\\"") + "\"";
        }
        if(oldParams!=null && oldParams.length()>3 && oldParams.startsWith("{") && oldParams.endsWith("}")) {
            return oldParams.substring(0, oldParams.length() - 1) + "," + newParam + "}";
        }
        else{
            return "{" + newParam + "}";
        }
    }

    /**
     * 获取资源授权数据元列表
     * @param rsResource
     * @param roleId
     * @return
     * @throws Exception
     */
    private List<DtoResourceMetadata> getAccessMetadata(RsResource rsResource, String roleId, Map<String, String> correspondMap) throws Exception{
        Set<String> rsMetadataIdSet = new HashSet<String>();
        String grantType = rsResource.getGrantType();
        boolean isOtherVersion = false;
        if(rsResource.getCode().split("\\$").length > 1) {
            isOtherVersion = true;
        }
        if(isOtherVersion) {
            String version = rsResource.getCode().split("\\$")[1];
            String code = rsResource.getCode().split("\\$")[0];
            String otherStdMetadataStr = stdTransformClient.stdMetadataCodes(version, code); //省平台临时数据处理
            String [] otherStdMetadataArr = otherStdMetadataStr.split(",");
            List<String> transformEhrMetadataList = new ArrayList<>(); // 此list存储其他标准数据集底下的数据元转换成的平台的数据元的id (EHR_XXXXX)
            for(String otherStdMetadata : otherStdMetadataArr) {
                String dataSetAndMetadata = stdTransformClient.adapterMetadataCode("5a6951bff0bb", code, otherStdMetadata); //适配版本号
                if(!StringUtils.isEmpty(dataSetAndMetadata) && dataSetAndMetadata.split("\\.").length > 1 ) {
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
                    RsRolesResource rsRolesResource = rsRolesResourceGrantService.findByResourceIdAndRolesId(rsResource.getId(), id);
                    if (rsRolesResource != null) {
                        List<RsRolesResourceMetadata> rsRolesResourceMetadataList = rsRolesResourceMetadataGrantService.findByRolesResourceIdAndValid(rsRolesResource.getId(), "1");
                        if (rsRolesResourceMetadataList != null) {
                            for (RsRolesResourceMetadata rsRolesResourceMetadata : rsRolesResourceMetadataList) {
                                if(transformEhrMetadataList.contains(rsRolesResourceMetadata.getResourceMetadataId())) { // 如果其他标准数据集包含该数据元
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
                    if(rsMetadataIds.length() <= 0 ) {
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
                if(rsMetadataIds.length() <= 0 ) {
                    return null;
                }
                return resourceBrowseMetadataDao.getRsMetadataByIds(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
            }
        } else { //EHR所用标准
            if (grantType.equals("1") && !roleId.equals("*")) {
                List<String> roleIdList = objectMapper.readValue(roleId, List.class);
                for (String id : roleIdList) {
                    RsRolesResource rsRolesResource = rsRolesResourceGrantService.findByResourceIdAndRolesId(rsResource.getId(), id);
                    if (rsRolesResource != null) {
                        List<RsRolesResourceMetadata> rsRolesResourceMetadataList = rsRolesResourceMetadataGrantService.findByRolesResourceIdAndValid(rsRolesResource.getId(), "1");
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

    public Page<Map<String, Object>> getEhrCenter(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.getEhrCenter(queryParams, page, size);
    }

    public Page<Map<String,Object>> getEhrCenterSub(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.getEhrCenterSub(queryParams, page, size);
    }

    public Page<Map<String,Object>> countEhrCenter(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.countEhrCenter(queryParams, page, size);
    }

    public Page<Map<String,Object>> countEhrCenterSub(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.countEhrCenterSub(queryParams, page, size);
    }

    public Page<Map<String,Object>> getMysqlData(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.getMysqlData(queryParams, page, size);
    }

    /**
     * 区域并集
     */
    private List<String> containArea(List<String> areaListApp,String[] areaArray) {
        List<String> areaList = new ArrayList<>();
        for(String area : areaArray) {
            //省
            if(area.endsWith("0000")) {
                //全省
                if(areaListApp.contains(area)) {
                    areaList.add(area);
                }
                //选定省所属
                else {
                    for(String areaApp:areaListApp) {
                        //截取省代码
                        if(area.substring(0,area.length()-4).equals(areaApp.substring(0,area.length()-4))) {
                            areaList.add(areaApp);
                        }
                    }
                }
            }
            //市
            else if(area.endsWith("00")) {
                //全市
                if(areaListApp.contains(area) || areaListApp.contains(area.substring(0,area.length()-4)+"0000")) {
                    areaList.add(area);
                }
                //选定市所属
                else{
                    for(String areaApp:areaListApp) {
                        //截取市代码
                        if(area.substring(0,area.length()-2).equals(areaApp.substring(0,area.length()-2))) {
                            areaList.add(areaApp);
                        }
                    }
                }
            }
            //区
            else {
                if(areaListApp.contains(area) || areaListApp.contains(area.substring(0,area.length()-4)+"0000") || areaListApp.contains(area.substring(0,area.length()-2)+"00")) {
                    areaList.add(area);
                }
            }
        }
        return areaList;
    }

    /**
     *
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<String> getSolrIndexs(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.getSolrIndexs(queryParams,page,size);
    }

    public List<Object> getSubDateByRowkey(String rowKey, String version)throws Exception{
        //查询出所有细表的rowKey
        List<Object> resultList = new ArrayList<>();
        String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.SubTable, "^" + rowKey);
        Map<String, Object> resultMap = new HashMap<>();
        for(String subRowKey : legacyRowKeys) {
            String dataSetCode = subRowKey.split("\\$")[1];
            if(!resultMap.containsKey(dataSetCode)) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("name", redisService.getDataSetName(version, dataSetCode));
                List<Map<String, Object>> dataList = new ArrayList<>();
                Map<String, Object> detailedMap = hbaseDao.getResultMap(ResourceCore.SubTable, subRowKey);
                Map<String, Object> tempMap = new HashMap<>(detailedMap.size());
                List<String> idsList = new ArrayList<>(detailedMap.size());
                for(String id : detailedMap.keySet()) {
                    if (id.startsWith("EHR")) {
                        idsList.add(id);
                    }
                }
                List<Map<String, Object>> metaList = resourceBrowseMetadataDao.getMetaData(idsList);
                metaList.stream().forEach(one->{
                    Object obj = detailedMap.get(String.valueOf(one.get("ID")));
                    tempMap.put(String.valueOf(one.get("NAME")), obj);
                });
                dataList.add(tempMap);
                dataMap.put("data", dataList);
                resultMap.put(dataSetCode, dataMap);
            }else {
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.get(dataSetCode);
                List<Map<String, Object>> dataList = (List<Map<String,Object>>) dataMap.get("data");
                Map<String, Object> detailedMap = hbaseDao.getResultMap(ResourceCore.SubTable, subRowKey);
                Map<String, Object> tempMap = new HashMap<>(detailedMap.size());
                List<String> idsList = new ArrayList<>(detailedMap.size());
                for(String id : detailedMap.keySet()) {
                    if (id.startsWith("EHR")) {
                        idsList.add(id);
                    }
                }
                List<Map<String, Object>> metaList = resourceBrowseMetadataDao.getMetaData(idsList);
                metaList.stream().forEach(one->{
                    Object obj = detailedMap.get(String.valueOf(one.get("ID")));
                    tempMap.put(String.valueOf(one.get("NAME")), obj);
                });
                dataList.add(tempMap);
                dataMap.put("data", dataList);
                resultMap.put(dataSetCode, dataMap);
            }
        }
        resultList.addAll(resultMap.entrySet());
        return resultList;
    }
}


