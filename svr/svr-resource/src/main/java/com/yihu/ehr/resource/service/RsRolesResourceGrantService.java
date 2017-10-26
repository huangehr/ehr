package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsRolesResourceDao;
import com.yihu.ehr.resource.dao.RsRolesResourceMetadataDao;
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
public class RsRolesResourceGrantService extends BaseJpaService<RsRolesResource, RsRolesResourceDao> {

    @Autowired
    private RsRolesResourceDao rsRolesResourceDao;
    @Autowired
    private RsRolesResourceMetadataDao rolesRsMetadataDao;

    @Value("${deploy.region}")
    private Short deployRegion = 3502;

    /**
     * 资源授权单个App
     *
     * @param rsRolesResource RsAppResource 资源授权实体
     * @return RsRolesResource 资源授权实体
     */
    public RsRolesResource grantResource(RsRolesResource rsRolesResource) {
        rsRolesResourceDao.save(rsRolesResource);
        return rsRolesResource;
    }

    /**
     * 资源授权多个APP
     *
     * @param rolesRsList List<RsRolesResource> 资源授权实体集合
     * @return List<RsRolesResource> 资源授权实体集合
     */
    public List<RsRolesResource> grantResourceBatch(List<RsRolesResource> rolesRsList) {
        rsRolesResourceDao.save(rolesRsList);
        return rolesRsList;
    }

    /**
     * 删除资源授权
     *
     * @param id String 授权ID
     */
    public void deleteResourceGrant(String id) {
        String[] idArray = id.split(",");
        for(String _id:idArray) {
            rolesRsMetadataDao.deleteByRolesResourceId(_id);
            rsRolesResourceDao.delete(_id);
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
    public Page<RsRolesResource> getRolesResourceGrant(String sorts, int page, int size) {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));
        return rsRolesResourceDao.findAll(pageable);
    }


    /**
     * 根据ID获取资源授权
     *
     * @param id String Id
     * @return RsAppResource
     */
    public RsRolesResource getRsRolesGrantById(String id)
    {
        return rsRolesResourceDao.findOne(id);
    }


    /**
     * 删除资源授权
     *
     */
    public void deleteGrantByIds(String[] rolesResIds) {
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
    public List<RsRolesResource> addList(List<RsRolesResource> rolesRsList, String resourcesId) {
        if(rolesRsList.size()==0)
            return rolesRsList;
//        String sql = "SELECT sm.id, m.name FROM rs_resource_metadata sm LEFT JOIN rs_metadata m ON sm.METADATA_ID=m.ID WHERE " +
//                "sm.resources_id=:resourcesId";
//        SQLQuery query = currentSession().createSQLQuery(sql);
//        query.setParameter("resourcesId", resourcesId);
//        List<Object[]> rsResourceMetadatas = query.list();
        rsRolesResourceDao.save(rolesRsList);
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

    /**
     * 根据资源id和角色id
     * @param resourceId
     * @param roleId
     * @return
     */
    public RsRolesResource findByResourceIdAndRolesId(String resourceId, String roleId) {
        return rsRolesResourceDao.findByResourceIdAndRolesId(resourceId, roleId);
    }

}
