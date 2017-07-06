package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.OrgResourceMetadataDao;
import com.yihu.ehr.resource.model.RsOrgResourceMetadata;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zdm on 2017/7/4
 */
@Service
@Transactional
public class OrgResourceMetadataGrantService extends BaseJpaService<RsOrgResourceMetadata,OrgResourceMetadataDao>  {
    @Autowired
    private OrgResourceMetadataDao OrgResourceMetadataDao;

    /**
     * 机构资源元数据授权
     *
     * @param rsOrgMetadata RsOrgResourceMetadata 资源数据元实体
     * @return RsOrgResourceMetadata 资源数据元实体
     */
    public RsOrgResourceMetadata grantRsOrgMetadata(RsOrgResourceMetadata  rsOrgMetadata)
    {
        OrgResourceMetadataDao.save(rsOrgMetadata);

        return rsOrgMetadata;
    }

    /**
     * 资源数据元授权多个机构
     *
     * @param rsMetadataList List<RsOrgResourceMetadata> 资源数据元授权实体集合
     * @return List<RsOrgResourceMetadata> 资源数据元授权实体集合
     */
    public List<RsOrgResourceMetadata> grantRsOrgMetadataBatch(List<RsOrgResourceMetadata> rsMetadataList)
    {
        OrgResourceMetadataDao.save(rsMetadataList);

        return rsMetadataList;
    }

    /**
     * 删除资源数据元授权
     *
     * @param id String 授权ID
     */
    public void deleteRsOrgMetadataGrant(String id)
    {
        String[] idArray = id.split(",");

        for(String _id:idArray)
        {
            OrgResourceMetadataDao.delete(_id);
        }
    }

    /**
     * 机构资源授权获取
     *
     * @param sorts String 排序
     * @param page int 页码
     * @param size size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsOrgResourceMetadata> getOrgRsMetadataGrant(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return OrgResourceMetadataDao.findAll(pageable);
    }

    /**
     * 根据ID获取资源数据元授权
     *
     * @param id String Id
     * @return RsOrgResource
     */
    public RsOrgResourceMetadata getRsOrgMetadataGrantById(String id)
    {
        return OrgResourceMetadataDao.findOne(id);
    }

    /**
     * 生失效操作
     *
     */
    public void orgValid(String ids, int valid)
    {
        String hql = "update RsOrgResourceMetadata t set t.valid=:valid";
        hql += StringUtils.isEmpty(ids) ? "" : " where id in(:ids)";
        Query query = currentSession().createQuery(hql);
        if(!StringUtils.isEmpty(ids))
            query.setParameterList("ids", ids.split(","));
        query.setParameter("valid", String.valueOf(valid));
        query.executeUpdate();
    }

    /**
     * 机构资源授权获取
     */
    public List<RsOrgResourceMetadata> getOrgRsMetadatas(String organizationResId, String organizationId, String resId)
    {
        String sql =
                "SELECT * FROM (" +
                    "SELECT * FROM " +
                        "(SELECT " +
                        "   '' AS id, :organizationResId AS organizationResourceId, :organizationId AS organizationId, m.`NAME` AS resourceMetadataName, rm.ID AS resourceMetadataId, " +
                        "   '' AS dimensionId, '' AS dimensionValue, 0 AS valid " +
                        "FROM " +
                        "   `rs_resource_metadata` rm " +
                        "LEFT JOIN rs_metadata m on rm.METADATA_ID=m.ID " +
                        "LEFT JOIN rs_organization_resource_metadata pm on (pm.RESOURCE_METADATA_ID=rm.ID AND pm.ORGANIZATION_RESOURCE_ID=:organizationResId) " +
                        "WHERE " +
                        "   pm.ID IS NULL AND rm.resources_id=:resId ) a " +
                "UNION ALL " +
                    "SELECT b.id, b.ORGANIZATION_RESOURCE_ID as organizationResourceId, b.ORGANIZATION_ID as organizationId, b.RESOURCE_METADATA_NAME as resourceMetadataName, b.RESOURCE_METADATA_ID as resourceMetadataId," +
                            "b.DIMENSION_ID as dimensionId, b.DIMENSION_VALUE as dimensionValue, b.valid" +
                            " FROM rs_organization_resource_metadata b WHERE b.ORGANIZATION_RESOURCE_ID= :organizationResId" +
                ") p ORDER BY p.resourceMetadataName";

        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("organizationResId", organizationResId);
        query.setParameter("organizationId", organizationId);
        query.setParameter("resId", resId);
        query.setResultTransformer(Transformers.aliasToBean(RsOrgResourceMetadata.class));

        return query.list();
    }

    public List<Map> orgMetaExistence(String[] organizationResId){
        String sql = "SELECT " +
                "ORGANIZATION_ID as organizationId, count(ORGANIZATION_ID) as total " +
                "FROM  " +
                "`rs_organization_resource_metadata` meta " +
                "WHERE  " +
                "ORGANIZATION_RESOURCE_ID in(:organizationResId) GROUP BY ORGANIZATION_ID HAVING count(ORGANIZATION_ID)>0";
        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameterList("organizationResId", organizationResId);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }




}
