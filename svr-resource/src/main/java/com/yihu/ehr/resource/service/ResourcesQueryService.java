package com.yihu.ehr.resource.service;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.resource.dao.ResourcesMetadataQueryDao;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemeDao;
import com.yihu.ehr.resource.dao.intf.ResourceDefaultParamDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
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
@Service("resourcesQueryService")
public class ResourcesQueryService  {

    @Autowired
    SolrQuery solr;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private ResourcesMetadataQueryDao resourceMetadataQueryDao;

    @Autowired
    private ResourcesQueryDao resourcesQueryDao;

    @Autowired
    ResourceDefaultParamDao resourceDefaultParamDao;

    @Autowired
    AdapterSchemeDao adapterSchemeDao;

    @Autowired
    AdapterMetadataDao adapterMetadataDao;

    @Autowired
    ObjectMapper objectMapper;

    //忽略字段
    private List<String> ignoreField = new ArrayList<String>(Arrays.asList("rowkey","event_type", "event_no","event_date","demographic_id", "patient_id","org_code","org_name","profile_id", "cda_version", "client_id","profile_type"));

    /**
     * 新增参数
     * @return
     */
    private String addParams(String oldParams,String key,String value)
    {
        String newParam = "";
        if(value.startsWith("[") && value.endsWith("]"))
        {
            newParam = "\""+key+"\":"+value;
        }
        else{
            newParam = "\""+key+"\":\""+value.replace("\"","\\\"")+"\"";
        }
        if(oldParams!=null && oldParams.length()>3 && oldParams.startsWith("{") && oldParams.endsWith("}"))
        {
            return oldParams.substring(0,oldParams.length()-1)+","+newParam+"}";
        }
        else{
            return "{"+newParam+"}";
        }
    }

    /**
     * 获取资源
     * @param resourcesCode
     * @param appId
     * @param queryParams Mysql为sql语句，Hbase为solr查询语法
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public Envelop getResources(String resourcesCode,String appId,String queryParams,Integer page,Integer size) throws Exception {
        //获取资源信息
        RsResources rs = resourcesDao.findByCode(resourcesCode);
        Envelop re = new Envelop();

        if(rs!=null)
        {
            String methodName = rs.getRsInterface(); //执行函数
            String grantType = rs.getGrantType(); //访问方式 0开放 1授权
            //资源结构
            List<DtoResourceMetadata> metadataList;
            if(grantType=="1" && appId!="JKZL")
            {
                //判断是否有资源权限
                RsAppResource appResource = resourceMetadataQueryDao.loadAppResource(appId);
                if(appResource!=null)
                {
                    //授权数据源
                    metadataList = resourceMetadataQueryDao.getResourceMetadata(resourcesCode,appResource.getId());
                }
                else
                {
                    throw new Exception("该应用[appId="+appId+"]无权访问资源[resourcesCode="+resourcesCode+"]");
                }
            }
            else{
                //所有数据元
                metadataList = resourceMetadataQueryDao.getResourceMetadata(resourcesCode);
            }

            //通过资源代码获取默认参数
            List<ResourceDefaultParam> paramsList = resourceDefaultParamDao.findByResourcesCode(resourcesCode);
            for(ResourceDefaultParam param:paramsList)
            {
                queryParams = addParams(queryParams,param.getParamKey(),param.getParamValue());
            }

            if(metadataList!=null && metadataList.size()>0)
            {
                /*************** 分组统计数据元 *********************/
                String groupFields = "";
                String statsFields = "";
                String customGroup = "";
                //遍历资源数据元，获取分组/统计字段
                for(DtoResourceMetadata metadada : metadataList) {
                    String key = metadada.getId();

                    String groupType = metadada.getGroupType();
                    String groupData =  metadada.getGroupData();

                    if(groupType!=null&&groupType.length()>0)
                    {
                        if(grantType.equals("0")) //分组字段
                        {
                            if(groupData!=null && groupData.length()>0)
                            {
                                customGroup +="{\"groupField\":\""+key+"\",\"groupCondition\":"+groupData+"},";
                            }
                            else {
                                groupFields += key + ",";
                            }
                        }
                        else if(grantType.equals("1")) //统计字段
                        {
                            statsFields += key +",";
                        }
                    }
                }
                if(groupFields.length()>0)
                {
                    groupFields = groupFields.substring(0,groupFields.length()-1);
                    queryParams = addParams(queryParams,"groupFields",groupFields);
                }
                if(statsFields.length()>0)
                {
                    statsFields = statsFields.substring(0,statsFields.length()-1);
                    queryParams = addParams(queryParams,"statsFields",statsFields);
                }
                if(customGroup.length()>0)
                {
                    customGroup = "["+customGroup.substring(0,customGroup.length()-1)+"]";
                    queryParams = addParams(queryParams,"customGroup",customGroup);
                }

                /************** 执行函数 *************************/
                Class<ResourcesQueryDao> classType = ResourcesQueryDao.class;
                Method method = classType.getMethod(methodName, new Class[]{String.class,Integer.class,Integer.class});
                Page<Map<String,Object>> result = (Page<Map<String,Object>>)method.invoke(resourcesQueryDao, queryParams, page, size);

                if (result != null) {
                    re.setSuccessFlg(true);
                    re.setCurrPage(result.getNumber());
                    re.setPageSize(result.getSize());
                    re.setTotalCount(new Long(result.getTotalElements()).intValue());

                    if(result.getContent()!=null&&result.getContent().size()>0)
                    {
                        //是否统计

                        //转译
                        List<Map<String,Object>> list = new ArrayList<>();
                        //遍历所有行
                        for(int i=0;i<result.getContent().size();i++)
                        {
                            Map<String,Object> oldObj = (Map<String,Object>)result.getContent().get(i);
                            Map<String,Object> newObj = new HashMap<>();
                            //遍历资源数据元
                            for(DtoResourceMetadata metadada : metadataList) {
                                String key = metadada.getId();

                                if(oldObj.containsKey(key))
                                {
                                    newObj.put(metadada.getId(),oldObj.get(key));
                                    if(metadada.getDictCode()!=null&& metadada.getDictCode().length()>0&&!metadada.getDictCode().equals("0"))
                                    {
                                        if(oldObj.containsKey(key+"_VALUE"))
                                        {
                                            newObj.put(metadada.getId()+"_VALUE",oldObj.get(key+"_VALUE"));
                                        }
                                    }
                                }
                            }


                            for(String key : oldObj.keySet())
                            {
                                //统计字段
                                if (key.startsWith("$")) {
                                    newObj.put(key,oldObj.get(key));
                                }

                                //忽略字段
                                if (ignoreField.contains(key)) {
                                    newObj.put(key,oldObj.get(key));
                                }
                            }
                            list.add(newObj);
                        }
                        re.setDetailModelList(list);
                        return re;
                    }
                }else {
                    re.setSuccessFlg(false);
                }
                return re;
            }
            return re;
        }
        return re;

    }


    /**
     * 资源浏览 -- 资源数据元结构
     * @return
     */
    public String getResourceMetadata(String resourcesCode) throws Exception{
        Map<String, Object> mapParam = new HashMap<String, Object>();
        try {
            //获取资源信息
            RsResources rs = resourcesDao.findByCode(resourcesCode);
            //资源结构
            List<DtoResourceMetadata> metadataList = resourceMetadataQueryDao.getResourceMetadata(resourcesCode);
            List<String> colunmName = new ArrayList<String>();
            List<String> colunmCode = new ArrayList<String>();
            List<String> colunmType = new ArrayList<String>();
            List<String> colunmDict = new ArrayList<String>();
            for (DtoResourceMetadata r : metadataList) {
                colunmName.add(r.getName());
                colunmCode.add(r.getId());
                colunmType.add(r.getColumnType());
                colunmDict.add(r.getDictCode());
            }
            //设置动态datagrid值
            mapParam.put("colunmName", colunmName);
            mapParam.put("colunmCode", colunmCode);
            mapParam.put("colunmDict", colunmDict);
            mapParam.put("colunmType", colunmType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectMapper.writeValueAsString(mapParam);
    }

    /**
     * 资源浏览
     * @return
     */
    public Envelop getResourceData(String resourcesCode,String queryCondition,Integer page,Integer size) throws Exception
    {
        String queryParams = "";
        //获取资源信息
        RsResources rs = resourcesDao.findByCode(resourcesCode);
        if(rs!=null) {
            List<QueryCondition> ql = new ArrayList<>();

            //设置参数
            if (!StringUtils.isEmpty(queryCondition) && !"{}".equals(queryCondition)) {
                ObjectMapper mapper = new ObjectMapper();
                JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
                List<Map<String,Object>> list = objectMapper.readValue(queryCondition,javaType);
                if(list!=null && list.size()>0)
                {
                    for(Map<String,Object> item : list)
                    {
                        String andOr = String.valueOf(item.get("andOr")).trim();
                        String field = String.valueOf(item.get("field")).trim();
                        String cond = String.valueOf(item.get("condition")).trim();
                        String value = String.valueOf(item.get("value"));
                        if(value.indexOf(",")>0)
                        {
                            ql.add(new QueryCondition(andOr, cond, field, value.split(",")));
                        }
                        else{
                            ql.add(new QueryCondition(andOr, cond, field, value));
                        }
                    }
                }
            }
            queryParams = addParams(queryParams,"q",solr.conditionToString(ql));


        }
        return getResources(resourcesCode,"JKZL",queryParams,page,size);
    }


    /**
     * 获取非结构化数据
     * @return
     * @throws Exception
     */
    public Envelop getRawFiles(String profileId,String cdaDocumentId,Integer page,Integer size) throws Exception{
        Envelop re = new Envelop();
        String queryParams = "{\"q\":\"rowkey:"+profileId+"*\"}";
        if(cdaDocumentId!=null && cdaDocumentId.length()>0)
        {
            queryParams = "{\"q\":\"rowkey:"+profileId+"* AND cda_document_id:"+cdaDocumentId+"\"}";
        }

        Page<Map<String,Object>> result = resourcesQueryDao.getRawFiles(queryParams,page,size);
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
}
