package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.resource.dao.ResourceMetadataDao;
import com.yihu.ehr.resource.dao.ResourcesDao;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.model.ResourcesType;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IResourcesQueryService;
import jdk.management.resource.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ResourceMetadataDao resourceMetadataDao;

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
            //资源类型
            String type = rs.getType();
            String dataset = rs.getRelatedDatasets();
            String datasource = rs.getRelatedDatasource();
            String action = rs.getRelatedAction();

            //资源结构/权限
            List<RsResourceMetadata> list = resourceMetadataDao.findByResourcesId(rs.getId());
            if(list!=null && list.size()>0)
            {
                String metadata = ""; //逗号分隔，展示数据元
                String groupFields = ""; //逗号分隔，分组数据元
                String statsFields = ""; //逗号分隔，统计数据元

                Boolean needTranslation = false;
                for(RsResourceMetadata obj : list)
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


                DataList re = null;
                if(type.equals(ResourcesType.HBASE_SINGLE_CORE)) //habse单core
                {
                    re = resourcesQueryDao.getHbaseSingleCore(dataset, metadata, queryParams, page, size);
                }
                else if(type.equals(ResourcesType.HBASE_MULTI_CORE)) //habse多core
                {
                    re = resourcesQueryDao.getHbaseMultiCore(dataset, metadata, queryParams, page, size);
                }
                else if(type.equals(ResourcesType.HBASE_SOLR)) //habse的solr分组统计
                {
                    re = resourcesQueryDao.getHbaseSolr(dataset, groupFields, statsFields,queryParams, page, size);
                }
                else if(type.equals(ResourcesType.RS_ETL)) //ETL数据
                {
                    re = resourcesQueryDao.getEtlData("datasource", dataset, metadata, queryParams, page, size);
                }
                else if(type.equals(ResourcesType.RS_CONFIG)) //配置数据
                {
                    re = resourcesQueryDao.getConfigData("datasource", dataset, metadata, queryParams, page, size);
                }
                else if(type.equals(ResourcesType.RS_DICT)) //字典数据
                {
                    re = resourcesQueryDao.getDictData("dictName");
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
                }

                return re;
            }
        }
        //测试代码
        else
        {
            return resourcesQueryDao.getHbaseMultiCore("EHR_CENTER_SUB", null, null, page, size);
        }

        throw new Exception("未找到资源" + resourcesCode +"，或者该资源为空！");
    }



}
