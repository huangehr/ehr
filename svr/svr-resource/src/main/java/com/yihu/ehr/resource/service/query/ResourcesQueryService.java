package com.yihu.ehr.resource.service.query;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.resource.dao.*;
import com.yihu.ehr.resource.feign.AppClient;
import com.yihu.ehr.resource.feign.RedisClient;
import com.yihu.ehr.resource.model.*;
import com.yihu.ehr.resource.service.RsRolesResourceGrantService;
import com.yihu.ehr.resource.service.RsRolesResourceMetadataGrantService;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;


/**
 * Created by hzp on 2016/4/13.
 */
@Service
public class ResourcesQueryService  {

    @Autowired
    private SolrQuery solr;
    @Autowired
    private HbaseQuery hbase;
    @Autowired
    private RsResourceDao resourcesDao;
    @Autowired
    private ResourcesMetadataQueryDao resourceMetadataQueryDao;
    @Autowired
    private ResourcesQueryDao resourcesQueryDao;
    @Autowired
    private RsResourceDefaultParamDao resourceDefaultParamDao;
    @Autowired
    private RedisClient redisServiceClient;
    @Autowired
    private AppClient appClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RsResourceDefaultQueryDao resourcesDefaultQueryDao;
    @Autowired
    private RsRolesResourceGrantService rsRolesResourceGrantService;
    @Autowired
    private RsRolesResourceMetadataGrantService rsRolesResourceMetadataGrantService;

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
        Map<String, Object> mapParam = new HashMap<String, Object>();
        List<DtoResourceMetadata> metadataList;
        Set<String> rsMetadataIdSet = new HashSet<String>();
        //获取资源信息
        RsResource rsResource = resourcesDao.findByCode(resourcesCode);
        String grantType = rsResource.getGrantType();
        if(grantType.equals("1") && !roleId.equals("*")) {
            List<String> roleIdList = objectMapper.readValue(roleId, List.class);
            for(String id : roleIdList) {
                RsRolesResource rsRolesResource = rsRolesResourceGrantService.findByResourceIdAndRolesId(rsResource.getId(), id);
                if(rsRolesResource != null) {
                    List<RsRolesResourceMetadata> rsRolesResourceMetadataList = rsRolesResourceMetadataGrantService.findByRolesResourceIdAndValid(rsRolesResource.getId(), "1");
                    if(rsRolesResourceMetadataList != null) {
                        for (RsRolesResourceMetadata rsRolesResourceMetadata : rsRolesResourceMetadataList) {
                            rsMetadataIdSet.add(rsRolesResourceMetadata.getResourceMetadataId());
                        }
                    }
                }
            }
            if(rsMetadataIdSet.size() > 0) {
                String rsMetadataIds = "";
                for (String id : rsMetadataIdSet) {
                    rsMetadataIds += "'" + id + "'" + ",";
                }
                metadataList = resourceMetadataQueryDao.getAuthResourceMetadata(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
            }else {
                metadataList = null;
            }
        } else{
            metadataList = resourceMetadataQueryDao.getAllResourceMetadata(rsResource.getCode());
        }
        //资源结构
        List<String> colunmName = new ArrayList<String>();
        List<String> colunmCode = new ArrayList<String>();
        List<String> colunmType = new ArrayList<String>();
        List<String> colunmDict = new ArrayList<String>();
        if(metadataList != null) {
            for (DtoResourceMetadata r : metadataList) {
                colunmName.add(r.getName());
                if (!StringUtils.isEmpty(r.getDictCode())) {
                    colunmCode.add(r.getId() + "_VALUE");
                } else {
                    colunmCode.add(r.getId());
                }
                colunmType.add(r.getColumnType());
                colunmDict.add(r.getDictCode());
            }
        }
        //设置动态datagrid值
        mapParam.put("colunmName", colunmName);
        mapParam.put("colunmCode", colunmCode);
        mapParam.put("colunmDict", colunmDict);
        mapParam.put("colunmType", colunmType);
        return objectMapper.writeValueAsString(mapParam);
    }

    /**
     * 获取资源数据
     * @param resourcesCode
     * @param orgCode
     * @param areaCode
     * @param queryParams Mysql为sql语句，Hbase为solr查询语法
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getResources(String resourcesCode, String roleId, String orgCode, String areaCode, String queryParams, Integer page, Integer size) throws Exception {
        return getResultData(resourcesCode, roleId, orgCode, areaCode, queryParams, page, size, false);
    }

    /**
     * 获取资源数据(档案浏览器主要健康问题诊断详情)
     * @param resourcesCode
     * @param orgCode
     * @param areaCode
     * @param queryParams Mysql为sql语句，Hbase为solr查询语法
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getResourcesSub(String resourcesCode, String roleId, String orgCode, String areaCode, String queryParams, Integer page, Integer size) throws Exception {
        return getResultData(resourcesCode, roleId, orgCode, areaCode, queryParams, page, size, true);
    }

    /**
     * 资源浏览
     * @return
     */
    public Envelop getResourceData(String resourcesCode, String roleId, String orgCode, String areaCode, String queryCondition, Integer page, Integer size) throws Exception {
        Envelop envelop = new Envelop();
        String queryParams = "";
        //获取资源信息
        RsResource rsResources = resourcesDao.findByCode(resourcesCode);
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
            queryParams = addParams(queryParams,"q", solr.conditionToString(ql));
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
        Envelop envelop = getResultData(resourcesCode, roleId, orgCode, areaCode, queryParams, page, size, false);
        //如果资源查询为细表则增加主表信息
        if(methodName.endsWith("Sub") && envelop.isSuccessFlg() && envelop.getDetailModelList() != null) {
            List<Map<String,Object>> oldList = envelop.getDetailModelList();
            for(Map<String, Object> temp : oldList) {
                String masterRowKey = (String)temp.get("profile_id");
                if(masterRowKey != null) {
                    Map<String, Object> masterMap = hbase.queryByRowKey(ResourceCore.MasterTable, masterRowKey);
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
            RsResource rsResources = resourcesDao.findByCode(code);
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
            if(solr.conditionToString(ql).contains(":")) {
                queryParams = addParams(queryParams,"q", solr.conditionToString(ql));
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
            List<String> customizeList = (List<String>) mapper.readValue(metaData, List.class);
            String dStr = customizeList.toString();
            String dealDStr = dStr.substring(1, dStr.length() - 1).replaceAll(" ", "");
            queryParams = addParams(queryParams,"dFl", dealDStr);
        }
        Page<Map<String,Object>> result = (Page<Map<String,Object>>)resourcesQueryDao.getEhrCenter(queryParams, page, size);
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
     * @param isSpecialScan
     * @return
     * @throws Exception
     */
    private Envelop getResultData(String resourcesCode, String roleId, String orgCode, String areaCode, String queryParams, Integer page, Integer size, boolean isSpecialScan) throws Exception{
        Envelop envelop = new Envelop();
        RsResource rsResources = resourcesDao.findByCode(resourcesCode);
        if(rsResources != null) {
            String methodName = rsResources.getRsInterface(); //执行函数
            //获取资源结构权限
            List<DtoResourceMetadata> metadataList = getAccessMetadata(rsResources, roleId);
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
            //分组统计数据元
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
                    metadataIdList.add(metadata.getId());
                }
                String dStr = metadataIdList.toString();
                String dealDStr = dStr.substring(1, dStr.length() - 1).replaceAll(" ", "");
                queryParams = addParams(queryParams,"dFl", dealDStr);
                //执行函数
                Class<ResourcesQueryDao> classType = ResourcesQueryDao.class;
                Method method = classType.getMethod(methodName, new Class[]{String.class, Integer.class, Integer.class});
                Page<Map<String,Object>> result = (Page<Map<String,Object>>)method.invoke(resourcesQueryDao, queryParams, page, size);
                if (result != null) {
                    envelop.setSuccessFlg(true);
                    envelop.setCurrPage(result.getNumber());
                    envelop.setPageSize(result.getSize());
                    envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
                    if(result.getContent() != null && result.getContent().size() > 0) {
                        //转译
                        //List<Map<String,Object>> list = new ArrayList<>();
                        //遍历所有行
                        /**
                        for(int i=0;i<result.getContent().size();i++) {
                            Map<String,Object> oldObj = result.getContent().get(i);
                            Map<String,Object> newObj = new HashMap<>();
                            //遍历资源数据元
                            for(DtoResourceMetadata metadata : metadataList) {
                                String key = metadata.getId();
                                if(oldObj.containsKey(key)) {
                                    newObj.put(metadata.getId(),oldObj.get(key));
                                    if(metadata.getDictCode()!=null && metadata.getDictCode().length()>0 && !metadata.getDictCode().equals("0")) {
                                        if(oldObj.containsKey(key+"_VALUE")) {
                                            newObj.put(metadata.getId()+"_VALUE",oldObj.get(key+"_VALUE"));
                                        }
                                    }
                                }
                            }
                            for(String key : oldObj.keySet()) {
                                //统计字段
                                if (key.startsWith("$")) {
                                    newObj.put(key,oldObj.get(key));
                                }
                            }
                            list.add(newObj);
                        }
                        */
                        envelop.setDetailModelList(result.getContent());
                    }
                    //for -> 档案浏览器主要健康问题诊断详情
                    if (isSpecialScan) {
                        if ((resourcesCode.equals("RS_OUTPATIENT_DIAGNOSIS") || resourcesCode.equals("RS_HOSPITALIZED_DIAGNOSIS")) && envelop.getDetailModelList() != null) {
                            List<Map<String, Object>> transformKeyList = new ArrayList<>();
                            for (Map<String, Object> temp : (List<Map<String, Object>>) envelop.getDetailModelList()) {
                                Map<String, Object> newKeyObject = new HashMap<String, Object>();
                                newKeyObject.put("DiagnosticTypeCode", temp.get("EHR_000111") != null ? temp.get("EHR_000111") : "");
                                newKeyObject.put("DiagnosticDate", temp.get("EHR_000113") != null ? temp.get("EHR_000113") : "");
                                newKeyObject.put("SignatureDoctor", temp.get("EHR_000106") != null ? temp.get("EHR_000106") : "");
                                newKeyObject.put("DiagnosticName", temp.get("EHR_000112") != null ? temp.get("EHR_000112") : "");
                                newKeyObject.put("DiagnosticInstructions", temp.get("EHR_000114") != null ? temp.get("EHR_000114") : "");
                                transformKeyList.add(newKeyObject);
                            }
                            envelop.setDetailModelList(transformKeyList);
                        }
                    }
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
        Page<Map<String,Object>> result = resourcesQueryDao.getRawFiles(queryParams, page, size);
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
            newParam = "\""+key+"\":" + value;
        }
        else{
            newParam = "\""+key+"\":\""+value.replace("\"","\\\"") + "\"";
        }
        if(oldParams!=null && oldParams.length()>3 && oldParams.startsWith("{") && oldParams.endsWith("}")) {
            return oldParams.substring(0,oldParams.length()-1)+"," + newParam + "}";
        }
        else{
            return "{"+newParam+"}";
        }
    }

    /**
     * 获取资源授权数据元列表
     * @param rsResource
     * @param roleId
     * @return
     * @throws Exception
     */
    private List<DtoResourceMetadata> getAccessMetadata(RsResource rsResource, String roleId) throws Exception{
        Set<String> rsMetadataIdSet = new HashSet<String>();
        String grantType = rsResource.getGrantType();
        if(grantType.equals("1") && !roleId.equals("*")) {
            List<String> roleIdList = objectMapper.readValue(roleId, List.class);
            for(String id : roleIdList) {
                RsRolesResource rsRolesResource = rsRolesResourceGrantService.findByResourceIdAndRolesId(rsResource.getId(), id);
                if(rsRolesResource != null) {
                    List<RsRolesResourceMetadata> rsRolesResourceMetadataList = rsRolesResourceMetadataGrantService.findByRolesResourceIdAndValid(rsRolesResource.getId(), "1");
                    if(rsRolesResourceMetadataList != null) {
                        for (RsRolesResourceMetadata rsRolesResourceMetadata : rsRolesResourceMetadataList) {
                            rsMetadataIdSet.add(rsRolesResourceMetadata.getResourceMetadataId());
                        }
                    }
                }
            }
            if(rsMetadataIdSet.size() > 0) {
                String rsMetadataIds = "";
                for (String id : rsMetadataIdSet) {
                    rsMetadataIds += "'" + id + "'" + ",";
                }
                return resourceMetadataQueryDao.getAuthResourceMetadata(rsMetadataIds.substring(0, rsMetadataIds.length() - 1));
            }else {
                return null;
            }
        } else{
            //返回所有数据元
            return  resourceMetadataQueryDao.getAllResourceMetadata(rsResource.getCode());
        }
    }

    /**
     * 获取权限并集
     */
    private String getSaas(String appId, String orgCode) throws Exception {
        String saas = "";
        String appSaasArea = "*";
        String appSaasOrg = "*";
        List<String> areaList = new ArrayList<>();
        List<String> orgList = new ArrayList<>();

        //APP权限范围，不为JKZL的话获取所属机构权限范围
        if(!appId.toUpperCase().equals("JKZL")) {
            MApp app = appClient.getApp(appId);
            if(app == null) {
                throw new Exception("无效appId.");
            }
            //获取APP所属机构
            String appOrg = app.getOrg();
            appSaasArea = redisServiceClient.getOrgSaasAreaRedis(appOrg);
            appSaasOrg = redisServiceClient.getOrgSaasOrgRedis(appOrg);
        }

        //单独APP权限控制
        if(StringUtils.isEmpty(orgCode)) { //机构为空
            //所有权限
            if("*".equals(appSaasArea) && "*".equals(appSaasOrg)) {
                saas = "*";
            } else{
                //【APP权限】存在区域授权范围
                if(!StringUtils.isEmpty(appSaasArea) && !appSaasArea.equals("*")) {
                    String[] arrayArea = appSaasArea.split(",");
                    for(String area : arrayArea) {
                        if(!areaList.contains(area)) {
                            areaList.add(area);
                        }
                    }
                }
                //【APP权限】存在机构授权范围
                if(!StringUtils.isEmpty(appSaasOrg) && !appSaasOrg.equals("*")) {
                    String[] arrayOrg = appSaasOrg.split(",");
                    for(String org : arrayOrg) {
                        if(!orgList.contains(org)) {
                            orgList.add(org);
                        }
                    }
                }
            }
        }
        //机构权限控制
        else {
            //String[] orgCodeList = orgCode.split(",");
            List<String> areaListApp = new ArrayList<>();
            List<String> orgListApp = new ArrayList<>();
            //for (String orgCo:orgCodeList) {

            //String orgSaasArea = redisServiceClient.getOrgSaasAreaRedis(orgCo);
            //String orgSaasOrg = redisServiceClient.getOrgSaasOrgRedis(orgCo);
            String orgSaasArea = redisServiceClient.getOrgSaasAreaRedis(orgCode);
            String orgSaasOrg = redisServiceClient.getOrgSaasOrgRedis(orgCode);

            //************* 单独机构权限控制 *************
            if ("*".equals(appSaasArea) && "*".equals(appSaasOrg)) {

                //【机构权限】存在区域授权范围
                if (!StringUtils.isEmpty(orgSaasArea) && !orgSaasArea.equals("*")) {
                    String[] arrayArea = orgSaasArea.split(",");
                    for (String area : arrayArea) {
                        if (!areaList.contains(area)) {
                            areaList.add(area);
                        }
                    }
                }
                //【机构权限】存在机构授权范围
                if (!StringUtils.isEmpty(orgSaasOrg) && !orgSaasOrg.equals("*")) {
                    String[] arrayOrg = orgSaasOrg.split(",");
                    for (String org : arrayOrg) {
                        if (!orgList.contains(org)) {
                            orgList.add(org);
                        }
                    }
                }
                //所有权限
                if ("*".equals(orgSaasArea) && "*".equals(orgSaasOrg)) {
                    saas = "*";
                }
            }
            //************* APP权限和机构权限并集 *************
            else {
                //【APP权限】存在区域授权范围
                if (!StringUtils.isEmpty(appSaasArea) && !appSaasArea.equals("*")) {
                    String[] arrayArea = appSaasArea.split(",");
                    for (String area : arrayArea) {
                        if (!areaListApp.contains(area)) {
                            areaListApp.add(area);
                        }
                    }
                }
                //【APP权限】存在机构授权范围
                if (!StringUtils.isEmpty(appSaasOrg) && !appSaasOrg.equals("*")) {
                    String[] arrayOrg = appSaasOrg.split(",");
                    for (String org : arrayOrg) {
                        if (!orgListApp.contains(org)) {
                            orgListApp.add(org);
                        }
                    }
                }
                //********【机构权限】存在区域授权范围【区域并集】  ***************
                if (!StringUtils.isEmpty(orgSaasArea)) {
                    if ("*".equals(orgSaasArea)) {
                        areaList = areaListApp;
                    } else {
                        String[] arrayArea = orgSaasArea.split(",");
                        areaList = containArea(areaListApp, arrayArea);
                    }
                }
                //【机构权限】存在机构授权范围
                if (!StringUtils.isEmpty(orgSaasOrg)) {
                    if ("*".equals(orgSaasOrg)) {
                        orgList = orgListApp;
                    } else {
                        String[] arrayOrg = orgSaasOrg.split(",");
                        for (String org : arrayOrg) {
                            //判断机构权限是否在APP权限范围内
                            if (orgListApp.contains(org)) {
                                orgList.add(org);
                            }
                        }
                    }
                }

            }
        }
        if(areaList.size()>0) {
            for(String area : areaList) {
                if(area.endsWith("0000")) { //省
                    area = area.substring(0,area.length()-4)+"*";
                }
                else if(area.endsWith("00")) { //市
                    area = area.substring(0,area.length()-2)+"*";
                }
                if(StringUtils.isEmpty(saas)) {
                    saas = "org_area:"+area;
                } else{
                    saas += " OR org_area:" + area;
                }
            }
        }
        if(orgList.size()>0) {
            for(String org :orgList) {
                if(StringUtils.isEmpty(saas)) {
                    saas = "org_code:" + org;
                }
                else{
                    saas += " OR org_code:" + org;
                }
            }
        }
        return saas;
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
}

