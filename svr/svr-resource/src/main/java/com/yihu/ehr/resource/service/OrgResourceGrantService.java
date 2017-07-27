package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.OrgResourceDao;
import com.yihu.ehr.resource.dao.intf.OrgResourceMetadataDao;
import com.yihu.ehr.resource.model.RsOrgResource;
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
public class OrgResourceGrantService extends BaseJpaService<RsOrgResource,OrgResourceDao> {

    @Autowired
    private OrgResourceDao orgRsDao;
    @Autowired
    private OrgResourceMetadataDao orgRsMetadataDao;
    @Autowired
    private ResourceMetadataGrantService rsMetadataGrantService;

    @Value("${deploy.region}")
    Short deployRegion = 3502;
    /**
     * 资源授权单个App
     *
     * @param rsOrgResource RsAppResource 资源授权实体
     * @return RsOrgResource 资源授权实体
     */
    public RsOrgResource grantResource(RsOrgResource rsOrgResource)
    {
        orgRsDao.save(rsOrgResource);

        return rsOrgResource;
    }

    /**
     * 资源授权多个机构
     *
     * @param orgRsList List<RsOrgResource> 资源授权实体集合
     * @return List<RsOrgResource> 资源授权实体集合
     */
    public List<RsOrgResource> grantResourceBatch(List<RsOrgResource> orgRsList)
    {
        orgRsDao.save(orgRsList);

        return orgRsList;
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
            orgRsMetadataDao.deleteByOrganizationResourceId(_id);
            orgRsDao.delete(_id);
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
    public Page<RsOrgResource> getOrgResourceGrant(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return orgRsDao.findAll(pageable);
    }


    /**
     * 根据ID获取资源授权
     *
     * @param id String Id
     * @return RsOrgResource
     */
    public RsOrgResource getRsOrgGrantById(String id)
    {
        return orgRsDao.findOne(id);
    }


    /**
     * 删除资源授权
     *
     */
    public void deleteGrantByIds(String[] orgResIds)
    {
        String hql = "delete from RsOrgResource res where id in(:orgResIds)";
        Query query = currentSession().createQuery(hql);
        query.setParameterList("orgResIds", orgResIds);
        query.executeUpdate();

        hql = "delete from RsOrgResourceMetadata meta where organizationResourceId in(:orgResIds)";
        query = currentSession().createQuery(hql);
        query.setParameterList("orgResIds", orgResIds);
        query.executeUpdate();
    }

    /**
     * 新增资源授权
     *
     */
    public List<RsOrgResource> addList(List<RsOrgResource> orgRsList, String resourcesId)
    {
        if(orgRsList.size()==0)
            return orgRsList;

//        String sql = "SELECT sm.id, m.name FROM rs_resource_metadata sm LEFT JOIN rs_metadata m ON sm.METADATA_ID=m.ID WHERE " +
//                "sm.resources_id=:resourcesId";

//        SQLQuery query = currentSession().createSQLQuery(sql);
//        query.setParameter("resourcesId", resourcesId);
//        List<Object[]> rsResourceMetadatas = query.list();

        orgRsDao.save(orgRsList);

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
        return orgRsList;
    }

}
