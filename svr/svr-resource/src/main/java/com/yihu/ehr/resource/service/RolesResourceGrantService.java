package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AppResourceDao;
import com.yihu.ehr.resource.dao.intf.AppResourceMetadataDao;
import com.yihu.ehr.resource.dao.intf.RolesResourceDao;
import com.yihu.ehr.resource.dao.intf.RolesResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsRolesResource;
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
 * Created by zdm on 2017/6/16.
 */
@Service
@Transactional
public class RolesResourceGrantService extends BaseJpaService<RsRolesResource,RolesResourceDao> {

    @Autowired
    private RolesResourceDao rolesRsDao;
    @Autowired
    private RolesResourceMetadataDao rolesRsMetadataDao;
    @Autowired
    private ResourceMetadataGrantService rsMetadataGrantService;

    @Value("${deploy.region}")
    Short deployRegion = 3502;
    /**
     * 资源授权单个App
     *
     * @param rsRolesResource RsAppResource 资源授权实体
     * @return RsRolesResource 资源授权实体
     */
    public RsRolesResource grantResource(RsRolesResource rsRolesResource)
    {
        rolesRsDao.save(rsRolesResource);

        return rsRolesResource;
    }

    /**
     * 资源授权多个APP
     *
     * @param rolesRsList List<RsRolesResource> 资源授权实体集合
     * @return List<RsRolesResource> 资源授权实体集合
     */
    public List<RsRolesResource> grantResourceBatch(List<RsRolesResource> rolesRsList)
    {
        rolesRsDao.save(rolesRsList);

        return rolesRsList;
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
            rolesRsMetadataDao.deleteByRolesResourceId(_id);
            rolesRsDao.delete(_id);
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
    public Page<RsRolesResource> getRolesResourceGrant(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return rolesRsDao.findAll(pageable);
    }


    /**
     * 根据ID获取资源授权
     *
     * @param id String Id
     * @return RsAppResource
     */
    public RsRolesResource getRsRolesGrantById(String id)
    {
        return rolesRsDao.findOne(id);
    }


    /**
     * 删除资源授权
     *
     */
    public void deleteGrantByIds(String[] rolesResIds)
    {
        String hql = "delete from RsRolesResource res where id in(:rolesResIds)";
        Query query = currentSession().createQuery(hql);
        query.setParameterList("rolesResIds", rolesResIds);
        query.executeUpdate();

        hql = "delete from RsRolesResourceMetadata meta where rolesResourceId in(:rolesResIds)";
        query = currentSession().createQuery(hql);
        query.setParameterList("rolesResIds", rolesResIds);
        query.executeUpdate();
    }

    /**
     * 新增资源授权
     *
     */
    public List<RsRolesResource> addList(List<RsRolesResource> rolesRsList, String resourcesId)
    {
        if(rolesRsList.size()==0)
            return rolesRsList;

//        String sql = "SELECT sm.id, m.name FROM rs_resource_metadata sm LEFT JOIN rs_metadata m ON sm.METADATA_ID=m.ID WHERE " +
//                "sm.resources_id=:resourcesId";

//        SQLQuery query = currentSession().createSQLQuery(sql);
//        query.setParameter("resourcesId", resourcesId);
//        List<Object[]> rsResourceMetadatas = query.list();

        rolesRsDao.save(rolesRsList);

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
        return rolesRsList;
    }

}
