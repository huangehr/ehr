package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.resource.dao.ResourcesMetadataQueryDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.model.DtoResourceMetadata;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.service.intf.IResourcesQueryService;
import org.springframework.beans.factory.annotation.Autowired;
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
        RsResource rs = resourcesDao.findByCode(resourcesCode);
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
                Method method = classType.getMethod(methodName, new Class[]{String.class,Integer.class,Integer.class});
                DataList re = (DataList)method.invoke(resourcesQueryDao, queryParams, page, size);

                if(re.getList()!=null&&re.getList().size()>0)
                {
                    /**** 转译 *****/
                    List<Map<String,Object>> list = new ArrayList<>();
                    //遍历所有行
                    for(int i=0;i<re.getList().size();i++)
                    {
                        Map<String,Object> oldObj = (Map<String,Object>)re.getList().get(i);
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
                                newObj.put(metadada.getCode(),oldObj.get(key));
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
                    re.setList(list);
                }


                return re;
            }
        }


        throw new Exception("未找到资源" + resourcesCode +"，或者该资源为空！");
    }



}
