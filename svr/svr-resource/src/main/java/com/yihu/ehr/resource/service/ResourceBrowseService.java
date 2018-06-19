package com.yihu.ehr.resource.service;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.doclint.Env;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.resource.MRsColumnsModel;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.query.common.model.QueryCondition;
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
public class ResourceBrowseService extends BaseJpaService {

    //忽略字段
    private List<String> basicField = new ArrayList<>(Arrays.asList("rowkey", "event_type", "event_no", "event_date", "demographic_id", "patient_id", "org_code", "org_name", "profile_id", "cda_version", "client_id", "profile_type", "patient_name", "org_area", "diagnosis", "health_problem"));

    @Autowired
    private SolrQuery solrQuery;
    @Autowired
    private RsResourceDao rsResourceDao;
    @Autowired
    private ResourceBrowseMetadataDao resourceBrowseMetadataDao;
    @Autowired
    private ResourceBrowseDao resourceBrowseDao;
    @Autowired
    private RsResourceDefaultQueryDao resourcesDefaultQueryDao;
    @Autowired
    private StdTransformClient stdTransformClient;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;


    /**
     * 资源浏览 -- 资源数据元结构
     * @param resourcesCode
     * @param roleId
     * @return
     * @throws Exception
     */
    public List<MRsColumnsModel> getResourceMetadata(String resourcesCode, String roleId) throws Exception{
        RsResource rsResource = rsResourceDao.findByCode(resourcesCode);
        if (rsResource == null) {
            return null;
        }
        Map<String, String> correspondMap = new HashMap<>();
        boolean isOtherVersion = false;
        String version = "";
        String code = "";
        if (resourcesCode.split("\\$").length > 1) {
            isOtherVersion = true;
            version = resourcesCode.split("\\$")[1];
            code = resourcesCode.split("\\$")[0];
        }
        List<DtoResourceMetadata> metadataList = resourceBrowseDao.getAccessMetadata(rsResource, roleId, correspondMap);
        //资源结构
        List<MRsColumnsModel> mRsColumnsModels = new ArrayList<>();
        if (metadataList != null) {
            for (DtoResourceMetadata r : metadataList) {
                MRsColumnsModel mRsColumnsModel = new MRsColumnsModel();
                if (!isOtherVersion) {
                    mRsColumnsModel.setValue(r.getName());
                    if (!StringUtils.isEmpty(r.getDictCode())) {
                        mRsColumnsModel.setCode(r.getId() + "_VALUE");
                    } else {
                        mRsColumnsModel.setCode(r.getId());
                    }
                    mRsColumnsModel.setType(r.getColumnType());
                    mRsColumnsModel.setDict(r.getDictCode());
                } else {
                    String name = stdTransformClient.stdMetadataName(version, code, correspondMap.get(r.getId()));
                    if (!StringUtils.isEmpty(name)) {
                        mRsColumnsModel.setValue(name);
                        if (!StringUtils.isEmpty(r.getDictCode())) {
                            mRsColumnsModel.setCode(r.getId() + "_VALUE");
                        } else {
                            mRsColumnsModel.setCode(r.getId());
                        }
                        mRsColumnsModel.setType(r.getColumnType());
                        mRsColumnsModel.setDict(r.getDictCode());
                    }
                }
                mRsColumnsModels.add(mRsColumnsModel);
            }
        }
        return mRsColumnsModels;
    }

    /**
     * 资源浏览 - 资源数据
     * @param resourcesCode
     * @param roleId
     * @param orgCode
     * @param areaCode
     * @param queryCondition
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getResourceData(String resourcesCode, String roleId, String orgCode, String areaCode, String queryCondition, Integer page, Integer size) throws Exception {
        String queryParams = "";
        //获取资源信息
        RsResource rsResources = rsResourceDao.findByCode(resourcesCode);
        if (rsResources != null) {
            RsResourceDefaultQuery resourcesQuery = resourcesDefaultQueryDao.findByResourcesId(rsResources.getId());
            List<QueryCondition> ql = new ArrayList<>();
            //设置参数
            if (!StringUtils.isEmpty(queryCondition) && !queryCondition.equals("{}")) {
                ql = parseCondition(queryCondition);
            } else if(resourcesQuery != null && resourcesQuery.getResourcesType() == 1) {
                String defaultQuery = resourcesQuery.getQuery();
                ql = parseCondition(defaultQuery);
            }
            queryParams = addParams(queryParams,"q", solrQuery.conditionToString(ql));
            return getResultData(resourcesCode, roleId, orgCode, areaCode, queryParams, page, size);
        }
        throw new ApiException(ErrorCode.OBJECT_NOT_FOUND, "无相关资源");
    }

    /**
     * 获取结果集
     * @param resourcesCode
     * @param roleId
     * @param orgCode
     * @param areaCode
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getResultData (String resourcesCode, String roleId, String orgCode, String areaCode, String queryParams, Integer page, Integer size) throws Exception {
        RsResource rsResources = rsResourceDao.findByCode(resourcesCode);
        if (rsResources != null) {
            StringBuilder saas = new StringBuilder();
            if ("*".equals(orgCode) && "*".equals(areaCode)) {
                saas.append("*");
            } else {
                List<String> orgCodeList = objectMapper.readValue(orgCode, List.class);
                List<String> areaCodeList = objectMapper.readValue(areaCode, List.class);
                if (orgCodeList != null && orgCodeList.size() > 0) {
                    orgCodeList.forEach(item -> {
                        if (saas.length()<= 0) {
                            saas.append("(org_code:" + item);
                        } else {
                            saas.append(" OR org_code:" + item);
                        }
                    });
                }
                if (areaCodeList != null && areaCodeList.size() > 0) {
                    areaCodeList.forEach(item -> {
                        if (saas.length() <= 0) {
                            saas.append("(org_area:" + item);
                        } else {
                            saas.append(" OR org_area:" + item);
                        }
                    });
                }
                if (saas.length() > 0) {
                    saas.append(")");
                } else {
                    throw new ApiException(ErrorCode.FORBIDDEN, "无SAAS权限访问资源");
                }
            }
            String method = rsResources.getRsInterface();
            Class clazz = resourceBrowseDao.getClass();
            Method _method = clazz.getMethod(method, new Class[]{String.class, String.class, String.class, String.class, Integer.class, Integer.class});
            _method.setAccessible(true);
            return (Envelop)_method.invoke(resourceBrowseDao, resourcesCode, roleId, saas.toString(), queryParams, page, size);
        }
        throw new ApiException(ErrorCode.OBJECT_NOT_FOUND, "无相关资源");
    }

    /**
     * 获取hbase细表数据
     * @param rowKey
     * @param version
     * @return
     * @throws Exception
     */
    public List<Object> getSubDateByRowkey(String rowKey, String version)throws Exception{
        //查询出所有细表的rowKey
        List<Object> resultList = new ArrayList<>();
        String q = "{\"q\":\"profile_id:" + rowKey + "\"}";
        Envelop page = resourceBrowseDao.getEhrCenterSub(null, "*", "*", q, 1, 1000);
        List<Map<String, Object>> pageContent = page.getDetailModelList();
        Map<String, Object> resultMap = new HashMap<>();
        for (Map<String, Object> temp : pageContent) {
            String subRowKey = temp.get("rowkey").toString();
            String dataSetCode = subRowKey.split("\\$")[1];
            if (!resultMap.containsKey(dataSetCode)) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("name", redisService.getDataSetName(version, dataSetCode));
                List<Map<String, Object>> dataList = new ArrayList<>();
                Map<String, Object> tempMap = new HashMap<>(temp.size());
                List<String> idsList = new ArrayList<>(temp.size());
                for (String id : temp.keySet()) {
                    if (id.startsWith("EHR")) {
                        idsList.add(id);
                    }
                }
                List<Map<String, Object>> metaList = resourceBrowseMetadataDao.getMetaData(idsList);
                metaList.stream().forEach(one->{
                    Object obj = temp.get(String.valueOf(one.get("id")));
                    tempMap.put(String.valueOf(one.get("name")), obj);
                });
                dataList.add(tempMap);
                dataMap.put("data", dataList);
                resultMap.put(dataSetCode, dataMap);
            } else {
                Map<String, Object> dataMap = (Map<String, Object>) resultMap.get(dataSetCode);
                List<Map<String, Object>> dataList = (List<Map<String,Object>>) dataMap.get("data");
                Map<String, Object> tempMap = new HashMap<>(temp.size());
                List<String> idsList = new ArrayList<>(temp.size());
                for (String id : temp.keySet()) {
                    if (id.startsWith("EHR")) {
                        idsList.add(id);
                    }
                }
                List<Map<String, Object>> metaList = resourceBrowseMetadataDao.getMetaData(idsList);
                metaList.stream().forEach(one->{
                    Object obj = temp.get(String.valueOf(one.get("id")));
                    tempMap.put(String.valueOf(one.get("name")), obj);
                });
                dataList.add(tempMap);
                dataMap.put("data", dataList);
                resultMap.put(dataSetCode, dataMap);
            }
        }
        resultList.addAll(resultMap.entrySet());
        return resultList;
    }

    /**
     * 综合查询档案数据检索
     * @param resourcesCodes
     * @param metaData
     * @param orgCode
     * @param areaCode
     * @param queryCondition
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getCustomizeData(String resourcesCodes, String metaData, String orgCode, String areaCode, String queryCondition, Integer page, Integer size) throws Exception {
        //获取资源编码列表
        List<String> codeList = (List<String>) objectMapper.readValue(resourcesCodes, List.class);
        //资源判空检查
        for (String code : codeList) {
            RsResource rsResources = rsResourceDao.findByCode(code);
            if (rsResources == null) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "无效的资源编码" + code);
            }
        }
        StringBuilder saas = new StringBuilder();
        if ("*".equals(orgCode) && "*".equals(areaCode)) {
            saas.append("*");
        } else {
            List<String> orgCodeList = objectMapper.readValue(orgCode, List.class);
            List<String> areaCodeList = objectMapper.readValue(areaCode, List.class);
            if (orgCodeList != null && orgCodeList.size() > 0) {
                orgCodeList.forEach(item -> {
                    if (saas.length()<= 0) {
                        saas.append("(org_code:" + item);
                    } else {
                        saas.append(" OR org_code:" + item);
                    }
                });
            }
            if (areaCodeList != null && areaCodeList.size() > 0) {
                areaCodeList.forEach(item -> {
                    if (saas.length() <= 0) {
                        saas.append("(org_area:" + item);
                    } else {
                        saas.append(" OR org_area:" + item);
                    }
                });
            }
            if (saas.length() > 0) {
                saas.append(")");
            } else {
                throw new ApiException(ErrorCode.FORBIDDEN, "无SAAS权限访问资源");
            }
        }
        List<QueryCondition> ql = new ArrayList<>();
        if (!StringUtils.isEmpty(queryCondition)) {
            ql = parseCondition(queryCondition);
        }
        String queryParams = "";
        if (ql.size() > 0) {
            if (solrQuery.conditionToString(ql).contains(":")) {
                queryParams = addParams(queryParams,"q", solrQuery.conditionToString(ql));
            } else {
                queryParams = addParams(queryParams,"q", "*:*");
            }
        } else {
            queryParams = addParams(queryParams,"q", "*:*");
        }
        queryParams = addParams(queryParams,"sort", "{\"create_date\":\"desc\"}");
        //基础数据字段
        queryParams = addParams(queryParams,"basicFl", org.apache.commons.lang3.StringUtils.join(basicField, ","));
        //数据元信息字段
        List<String> customizeList = (List<String>) objectMapper.readValue(metaData, List.class);
        //参数集合
        List<String> paramList = new ArrayList<>(customizeList.size() * 2);
        for (String id : customizeList) {
            paramList.add(id);
            String dictCode = redisService.getRsMetaData(id);
            if (!StringUtils.isEmpty(dictCode)) {
                paramList.add(id + "_VALUE");
            }
        }
        queryParams = addParams(queryParams,"dFl",  org.apache.commons.lang3.StringUtils.join(paramList, ","));
        return resourceBrowseDao.getEhrCenter(null, "*", saas.toString(), queryParams, page, size);
    }

    /**
     * 获取主表数据
     * @param resourcesCode
     * @param roleId
     * @param saas
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getEhrCenter(String resourcesCode, String roleId, String saas, String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.getEhrCenter(resourcesCode, roleId, saas, queryParams, page, size);
    }

    /**
     * 获取细表数据
     * @param resourcesCode
     * @param roleId
     * @param saas
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getEhrCenterSub(String resourcesCode,  String roleId, String saas, String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.getEhrCenterSub(resourcesCode, roleId, saas, queryParams, page, size);
    }

    /**
     * 主表统计
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String,Object>> countEhrCenter(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.countEhrCenter(queryParams, page, size);
    }

    /**
     * 细表统计
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String,Object>> countEhrCenterSub(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.countEhrCenterSub(queryParams, page, size);
    }

    /**
     * Mysql数据
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String,Object>> getMysqlData(String queryParams, Integer page, Integer size) throws Exception {
        return resourceBrowseDao.getMysqlData(queryParams, page, size);
    }

    /**
     * 非结构化数据
     * @param profileId
     * @param cdaDocumentId
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Page<Map<String,Object>> getRawFiles(String profileId, String cdaDocumentId, Integer page, Integer size) throws Exception{
        String queryParams = "{\"q\":\"rowkey:" + profileId + "*\"}";
        if (cdaDocumentId != null && cdaDocumentId.length() > 0) {
            queryParams = "{\"q\":\"rowkey:" + profileId + "* AND cda_document_id:" + cdaDocumentId + "\"}";
        }
        return resourceBrowseDao.getRawFiles(queryParams, page, size);
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
        return resourceBrowseDao.getSolrIndexs(queryParams, page, size);
    }

    /**
     * 查询条件转换
     * @param queryCondition
     * @return
     * @throws Exception
     */
    private List<QueryCondition> parseCondition(String queryCondition) throws Exception {
        List<QueryCondition> ql = new ArrayList<QueryCondition>();
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
        List<Map<String, Object>> list = objectMapper.readValue(queryCondition, javaType);
        if (list != null && list.size() > 0) {
            for (Map<String,Object> item : list) {
                String andOr = String.valueOf(item.get("andOr")).trim();
                String field = String.valueOf(item.get("field")).trim();
                String cond = String.valueOf(item.get("condition")).trim();
                String value = String.valueOf(item.get("value"));
                if (value.indexOf(",")>0) {
                    ql.add(new QueryCondition(andOr, cond, field, value.split(",")));
                } else {
                    ql.add(new QueryCondition(andOr, cond, field, value));
                }
            }
        }
        return ql;
    }

    /**
     * 新增参数
     * @param oldParams
     * @param key
     * @param value
     * @return
     */
    private String addParams(String oldParams, String key, String value) {
        String newParam;
        if (value.startsWith("[") && value.endsWith("]")) {
            newParam = "\"" + key + "\":" + value;
        } else {
            newParam = "\"" + key + "\":\""+ value.replace("\"","\\\"") + "\"";
        }
        if (oldParams != null && oldParams.length() > 3 && oldParams.startsWith("{") && oldParams.endsWith("}")) {
            return oldParams.substring(0, oldParams.length() - 1) + "," + newParam + "}";
        } else {
            return "{" + newParam + "}";
        }
    }


    /**
     * 获取档案包中数据集列表
     * @param packId
     * @param version
     * @return
     * @throws Exception
     */
    public List<String>  dataSetList(String packId, String version) throws Exception {
        Envelop envelop = new Envelop();
        List<String> dataSetCodes = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(packId) && !"null".equals(packId)){
            stringBuilder.append("pack_id=" + packId).append(";");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(version) && !"null".equals(version)){
            stringBuilder.append("version=" + version);
        }

        List<Map<String, Object>> list = elasticSearchUtil.list("json_archives_qc", "qc_dataset_info", stringBuilder.toString());
        for(Map<String, Object> map : list){
            List<Map<String,Object>> dataSets = objectMapper.readValue(map.get("details").toString(), List.class);
            dataSets.stream().forEach( dataSet -> {
                String code = (String) dataSet.keySet().toArray()[0];
                dataSetCodes.add(code);
            });
        }
        return dataSetCodes;
    }


    /**
     * 获取结果集
     * @param dataSets      数据集编码列表
     * @param roleId        角色ID
     * @param orgCode       机构编码
     * @param areaCode      区域编码
     * @param rowKey   查询条件
     * @param page          页数
     * @param size          条数
     * @return
     * @throws Exception
     */
    public Envelop  getResultDataList (String version,List<String> dataSets, String roleId, String orgCode, String areaCode, String rowKey) throws Exception {
       Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> resultData = null;
        for (String resourcesCode : dataSets) {
            Boolean isMultiRecord = redisService.getDataSetMultiRecord(version, resourcesCode);
            RsResource rsResources = rsResourceDao.findByCode(resourcesCode);
            if (rsResources != null) {
                StringBuilder saas = new StringBuilder();
                if ("*".equals(orgCode) && "*".equals(areaCode)) {
                    saas.append("*");
                } else {
                    List<String> orgCodeList = objectMapper.readValue(orgCode, List.class);
                    List<String> areaCodeList = objectMapper.readValue(areaCode, List.class);
                    if (orgCodeList != null && orgCodeList.size() > 0) {
                        orgCodeList.forEach(item -> {
                            if (saas.length() <= 0) {
                                saas.append("(org_code:" + item);
                            } else {
                                saas.append(" OR org_code:" + item);
                            }
                        });
                    }
                    if (areaCodeList != null && areaCodeList.size() > 0) {
                        areaCodeList.forEach(item -> {
                            if (saas.length() <= 0) {
                                saas.append("(org_area:" + item);
                            } else {
                                saas.append(" OR org_area:" + item);
                            }
                        });
                    }
                    if (saas.length() > 0) {
                        saas.append(")");
                    } else {
                        throw new ApiException(ErrorCode.FORBIDDEN, "无SAAS权限访问资源");
                    }
                }
                if (isMultiRecord) {
                    //细表
                    resultData = resourceBrowseDao.getEhrCenterSubByScan(resourcesCode,roleId,saas.toString(),rowKey);
                } else {
                    //主表
                    resultData = resourceBrowseDao.getEhrCenterByScan(resourcesCode,roleId,saas.toString(),rowKey);
                }
                if (resultData !=null && resultData.size()>0 ){
                    resultMap.put(resourcesCode,resultData);
                }
            }else {
//                throw new ApiException(ErrorCode.OBJECT_NOT_FOUND, "无相关资源");
                continue;
            }

        }
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(resultMap);
        return envelop;
    }

}


