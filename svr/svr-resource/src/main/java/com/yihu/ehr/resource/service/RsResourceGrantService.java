package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsAppResourceDao;
import com.yihu.ehr.resource.dao.RsAppResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResource;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Created by lyr on 2016/4/26.
 */
@Service
@Transactional
public class RsResourceGrantService extends BaseJpaService<RsAppResource, RsAppResourceDao> {
    @Autowired
    private RsAppResourceDao appRsDao;
    @Autowired
    private RsAppResourceMetadataDao appRsMetadataDao;
    @Autowired
    private RsAppResourceMetadataGrantService rsMetadataGrantService;

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
    public void deleteGrantByIds(String[] appResIds)
    {
        String hql = "delete from RsAppResource res where id in(:appResIds)";
        Query query = currentSession().createQuery(hql);
        query.setParameterList("appResIds", appResIds);
        query.executeUpdate();

        hql = "delete from RsAppResourceMetadata meta where appResourceId in(:appResIds)";
        query = currentSession().createQuery(hql);
        query.setParameterList("appResIds", appResIds);
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
