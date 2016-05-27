package com.yihu.ehr.resource.service;


import com.yihu.ehr.resource.dao.ResourcesMetadataQueryDao;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.model.DtoResourceMetadata;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IResourcesQueryService;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hzp on 2016/4/13.
 */
@Service("resourcesQueryService")
public class ResourcesQueryService implements IResourcesQueryService {

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private ResourcesMetadataQueryDao resourceMetadataQueryDao;

    @Autowired
    private ResourcesQueryDao resourcesQueryDao;


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
            newParam = "\""+key+"\":\""+value+"\"";
        }
        if(oldParams.length()>3 && oldParams.startsWith("{") && oldParams.endsWith("}"))
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
        if(rs!=null)
        {
            String methodName = rs.getRsInterface(); //执行函数
            String grantType = rs.getGrantType(); //访问方式 0开放 1授权
            //资源结构
            List<DtoResourceMetadata> metadataList;
            if(grantType=="1")
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

            if(metadataList!=null && metadataList.size()>0)
            {
                /*************** 分组统计数据元 *********************/
                String groupFields = "";
                String statsFields = "";
                String customGroup = "";
                //遍历资源数据元，获取分组/统计字段
                for(DtoResourceMetadata metadada : metadataList) {
                    String key = metadada.getStdCode();//***先用std标准代码映射
                    if(metadada.getDictCode()!=null&& metadada.getDictCode().length()>0&&!metadada.getDictCode().equals("0"))
                    {
                        key += "_CODE_"+metadada.getColumnType().substring(0,1);
                    }
                    else{
                        key += "_"+metadada.getColumnType().substring(0, 1);
                    }
                    String groupType = metadada.getGroupType();
                    String groupData =  metadada.getGroupData();

                    if(groupType!=null)
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
                    addParams(queryParams,"groupFields",groupFields);
                }
                if(statsFields.length()>0)
                {
                    statsFields = statsFields.substring(0,statsFields.length()-1);
                    addParams(queryParams,"statsFields",statsFields);
                }
                if(customGroup.length()>0)
                {
                    customGroup = "["+customGroup.substring(0,customGroup.length()-1)+"]";
                    addParams(queryParams,"customGroup",customGroup);
                }

                /************** 执行函数 *************************/
                Class<ResourcesQueryDao> classType = ResourcesQueryDao.class;
                Method method = classType.getMethod(methodName, new Class[]{String.class,Integer.class,Integer.class});
                Page<Map<String,Object>> result = (Page<Map<String,Object>>)method.invoke(resourcesQueryDao, queryParams, page, size);

                Envelop re = new Envelop();
                if(result!=null)
                {
                    re.setCurrPage(result.getNumber());
                    re.setPageSize(result.getSize());
                    re.setTotalCount(new Long(result.getTotalElements()).intValue());

                    if(result.getContent()!=null&&result.getContent().size()>0)
                    {
                        /**** 转译 *****/
                        List<Map<String,Object>> list = new ArrayList<>();
                        //遍历所有行
                        for(int i=0;i<result.getContent().size();i++)
                        {
                            Map<String,Object> oldObj = (Map<String,Object>)result.getContent().get(i);
                            Map<String,Object> newObj = new HashMap<>();
                            //遍历资源数据元
                            for(DtoResourceMetadata metadada : metadataList) {
                                String key = metadada.getStdCode();//***先用std标准代码映射
                                if(metadada.getDictCode()!=null&& metadada.getDictCode().length()>0&&!metadada.getDictCode().equals("0"))
                                {
                                    key += "_CODE_"+metadada.getColumnType().substring(0,1);
                                }
                                else{
                                    key += "_"+metadada.getColumnType().substring(0, 1);
                                }

                                if(oldObj.containsKey(key))
                                {
                                    newObj.put(metadada.getId(),oldObj.get(key));
                                }
                            }

                            //统计字段
                            for(String key : oldObj.keySet())
                            {
                                if (key.startsWith("$")) {
                                    newObj.put(key,oldObj.get(key));
                                }
                            }
                            list.add(newObj);
                        }
                        re.setDetailModelList(list);
                    }
                }
                return re;
            }
        }

        throw new Exception("未找到资源" + resourcesCode +"，或者该资源为空！");

    }



}
