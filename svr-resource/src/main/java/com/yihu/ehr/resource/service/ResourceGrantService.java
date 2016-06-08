package com.yihu.ehr.resource.service;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AppResourceDao;
import com.yihu.ehr.resource.dao.intf.AppResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import com.yihu.ehr.util.ObjectId;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyr on 2016/4/26.
 */
@Service
@Transactional
public class ResourceGrantService extends BaseJpaService<RsAppResource,AppResourceDao> {
    @Autowired
    private AppResourceDao appRsDao;
    @Autowired
    private AppResourceMetadataDao appRsMetadataDao;
    @Autowired
    private ResourceMetadataGrantService rsMetadataGrantService;

    @Value("${deploy.region}")
    Short deployRegion = 3502;
    /**
     * 资源授权单个App
     *
     * @param rsAppResource RsAppResource 资源授权实体
     * @return RsAppResource 资源授权实体
     */
    public RsAppResource grantResource(RsAppResource rsAppResource)
    {
        appRsDao.save(rsAppResource);

        return rsAppResource;
    }

    /**
     * 资源授权多个APP
     *
     * @param appRsList List<RsAppResource> 资源授权实体集合
     * @return List<RsAppResource> 资源授权实体集合
     */
    public List<RsAppResource> grantResourceBatch(List<RsAppResource> appRsList)
    {
        appRsDao.save(appRsList);

        return appRsList;
    }

    /**
     * 删除资源授权
     *
     * @param id String 授权ID
     */
    public void deleteResourceGrant(String id)
    {
        String[] idArray = id.split(",");

        for(String _id:idArray)
        {
            appRsMetadataDao.deleteByAppResourceId(_id);
            appRsDao.delete(_id);
        }
    }

    /**
     * 资源授权获取
     *
     * @param sorts String 排序
     * @param page int 页码
     * @param size int 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsAppResource> getAppResourceGrant(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return appRsDao.findAll(pageable);
    }


    /**
     * 根据ID获取资源授权
     *
     * @param id String Id
     * @return RsAppResource
     */
    public RsAppResource getRsAppGrantById(String id)
    {
        return appRsDao.findOne(id);
    }


    /**
     * 删除资源授权
     *
     */
    public void deleteGrantByResId(String resId, String[] appIds)
    {
        String hql = "delete from RsAppResource res where resourceId=:resourceId and appId in(:appId)";
        Query query = currentSession().createQuery(hql);
        query.setParameter("resourceId", resId);
        query.setParameterList("appId", appIds);
        query.executeUpdate();

        hql = "delete from RsAppResourceMetadata meta where appResourceId=:resourceId and appId in(:appId)";
        query = currentSession().createQuery(hql);
        query.setParameter("resourceId", resId);
        query.setParameterList("appId", appIds);
        query.executeUpdate();
    }

    /**
     * 新增资源授权
     *
     */
    public List<RsAppResource> addList(List<RsAppResource> appRsList, String resourcesId)
    {
        if(appRsList.size()==0)
            return appRsList;

//        String sql = "SELECT sm.id, m.name FROM rs_resource_metadata sm LEFT JOIN rs_metadata m ON sm.METADATA_ID=m.ID WHERE " +
//                "sm.resources_id=:resourcesId";

//        SQLQuery query = currentSession().createSQLQuery(sql);
//        query.setParameter("resourcesId", resourcesId);
//        List<Object[]> rsResourceMetadatas = query.list();

        appRsDao.save(appRsList);

//        List<RsAppResourceMetadata> appRsMetadataList = new ArrayList<>();
//        RsAppResourceMetadata appRsMetadata;
//        for(RsAppResource appResource: appRsList){
//            for(Object[] rsResourceMetadata: rsResourceMetadatas){
//                appRsMetadata = new RsAppResourceMetadata();
//                appRsMetadata.setId(new ObjectId(deployRegion, BizObject.AppResourceMetadata).toString());
//                appRsMetadata.setAppResourceId(appResource.getId());
//                appRsMetadata.setAppId(appResource.getAppId());
//                appRsMetadata.setResourceMetadataId(StringUtils.isEmpty(rsResourceMetadata[0])?"":(String)rsResourceMetadata[0]);
//                appRsMetadata.setResourceMetadataName(StringUtils.isEmpty(rsResourceMetadata[1])?"":(String)rsResourceMetadata[1]);
//                appRsMetadataList.add(appRsMetadata);
//            }
//        }
//        rsMetadataGrantService.grantRsMetadataBatch(appRsMetadataList);
        return appRsList;
    }
}
