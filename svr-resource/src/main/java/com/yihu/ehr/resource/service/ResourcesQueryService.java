package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.resource.dao.ResourcesMetadataQueryDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.model.DtoResourceMetadata;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IResourcesQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
     * 获取资源
     * @param resourcesCode
     * @param appId
     * @param queryParams Mysql为sql语句，Hbase为solr查询语法
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public DataList getResources(String resourcesCode,String appId,String queryParams,Integer page,Integer size) throws Exception {
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
                //执行函数
                Class<ResourcesQueryDao> classType = ResourcesQueryDao.class;
                Method method = classType.getMethod(methodName, null);
                DataList re = (DataList)method.invoke(classType, queryParams, page, size);

                /**** 转译 *****/
                List<Map<String,Object>> list = new ArrayList<>();
                for(DtoResourceMetadata obj : metadataList)
                {
                    /*String code = obj.getColumnCode();
                    String displayCode = obj.getDisplayCode();

                    metadata += code + ",";
                    if(displayCode!=null && displayCode.length()>0 && !code.equals(displayCode))
                    {
                        needTranslation = true;
                    }

                    String statsType = obj.getStatsType();
                    if(statsType!=null && statsType.equals("0"))
                    {
                        groupFields += code + ",";
                    }
                    else if(statsType!=null && statsType.equals("1"))
                    {
                        statsFields += code + ",";
                    }*/
                }

/*


                String metadata = ""; //逗号分隔，展示数据元
                String groupFields = ""; //逗号分隔，分组数据元
                String statsFields = ""; //逗号分隔，统计数据元

                Boolean needTranslation = false;
                for(DtoResourceMetadata obj : metadataList)
                {
                    String code = obj.getColumnCode();
                    String displayCode = obj.getDisplayCode();

                    metadata += code + ",";
                    if(displayCode!=null && displayCode.length()>0 && !code.equals(displayCode))
                    {
                        needTranslation = true;
                    }

                    String statsType = obj.getStatsType();
                    if(statsType!=null && statsType.equals("0"))
                    {
                        groupFields += code + ",";
                    }
                    else if(statsType!=null && statsType.equals("1"))
                    {
                        statsFields += code + ",";
                    }
                }
                metadata = metadata.substring(0,metadata.length()-1);
                if(groupFields.length()>0)
                {
                    groupFields = groupFields.substring(0,groupFields.length()-1);
                }
                if(statsFields.length()>0)
                {
                    statsFields = statsFields.substring(0,statsFields.length()-1);
                }






                //输出结果转化
                if(needTranslation)
                {
                    List<Map<String,Object>> lits = re.getList();
                    for(Map<String,Object> row : lits)
                    {
                        //遍历字段
                        for(RsResourceMetadata obj : list)
                        {
                            String code = obj.getColumnCode();
                            String displayCode = obj.getDisplayCode();

                            if(displayCode!=null && displayCode.length()>0 && !code.equals(displayCode) && row.containsKey(code))
                            {
                                Object val = row.get(code);
                                row.remove(code);
                                row.put(displayCode,val);
                            }
                        }
                    }
                }*/

                return re;
            }
        }


        throw new Exception("未找到资源" + resourcesCode +"，或者该资源为空！");
    }



}
