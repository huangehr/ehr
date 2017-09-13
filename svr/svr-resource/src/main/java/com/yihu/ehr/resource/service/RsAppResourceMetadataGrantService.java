package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsAppResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResourceMetadata;
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
 * Created by lyr on 2016/4/26.
 */
@Service
@Transactional
public class RsAppResourceMetadataGrantService extends BaseJpaService<RsAppResourceMetadata, RsAppResourceMetadataDao>  {
    @Autowired
    private RsAppResourceMetadataDao appRsMetadataDao;

    /**
     * 资源元数据授权
     *
     * @param rsAppMetadata RsAppResourceMetadata 资源数据元实体
     * @return RsAppResourceMetadata 资源数据元实体
     */
    public RsAppResourceMetadata grantRsMetadata(RsAppResourceMetadata  rsAppMetadata)
    {
        appRsMetadataDao.save(rsAppMetadata);

        return rsAppMetadata;
    }

    /**
     * 资源数据元授权多个APP
     *
     * @param rsMetadataList List<RsAppResourceMetadata> 资源数据元授权实体集合
     * @return List<RsAppResourceMetadata> 资源数据元授权实体集合
     */
    public List<RsAppResourceMetadata> grantRsMetadataBatch(List<RsAppResourceMetadata> rsMetadataList)
    {
        appRsMetadataDao.save(rsMetadataList);

        return rsMetadataList;
    }

    /**
     * 删除资源数据元授权
     *
     * @param id String 授权ID
     */
    public void deleteRsMetadataGrant(String id)
    {
        String[] idArray = id.split(",");

        for(String _id:idArray)
        {
            appRsMetadataDao.delete(_id);
        }
    }

    /**
     * 资源授权获取
     *
     * @param sorts String 排序
     * @param page int 页码
     * @param size size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsAppResourceMetadata> getAppRsMetadataGrant(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return appRsMetadataDao.findAll(pageable);
    }

    /**
     * 根据ID获取资源数据元授权
     *
     * @param id String Id
     * @return RsAppResource
     */
    public RsAppResourceMetadata getRsMetadataGrantById(String id)
    {
        return appRsMetadataDao.findOne(id);
    }

    /**
     * 生失效操作
     *
     */
    public void valid(String ids, int valid)
    {
        String hql = "update RsAppResourceMetadata t set t.valid=:valid";
        hql += StringUtils.isEmpty(ids) ? "" : " where id in(:ids)";
        Query query = currentSession().createQuery(hql);
        if(!StringUtils.isEmpty(ids))
            query.setParameterList("ids", ids.split(","));
        query.setParameter("valid", String.valueOf(valid));
        query.executeUpdate();
    }

    /**
     * 资源授权获取
     */
    public List<RsAppResourceMetadata> getAppRsMetadatas(String appResId, String appId, String resId)
    {
        String sql =
                "SELECT * FROM (" +
                    "SELECT * FROM " +
                        "(SELECT " +
                        "   '' AS id, :appResId AS appResourceId, :appId AS appId, m.`NAME` AS resourceMetadataName, rm.ID AS resourceMetadataId, " +
                        "   '' AS dimensionId, '' AS dimensionValue, 0 AS valid " +
                        "FROM " +
                        "   `rs_resource_metadata` rm " +
                        "LEFT JOIN rs_metadata m on rm.METADATA_ID=m.ID " +
                        "LEFT JOIN rs_app_resource_metadata pm on (pm.RESOURCE_METADATA_ID=rm.ID AND pm.app_resource_id=:appResId) " +
                        "WHERE " +
                        "   pm.ID IS NULL AND rm.resources_id=:resId ) a " +
                "UNION ALL " +
                    "SELECT b.id, b.APP_RESOURCE_ID as appResourceId, b.APP_ID as appId, b.RESOURCE_METADATA_NAME as resourceMetadataName, b.RESOURCE_METADATA_ID as resourceMetadataId," +
                            "b.DIMENSION_ID as dimensionId, b.DIMENSION_VALUE as dimensionValue, b.valid" +
                            " FROM rs_app_resource_metadata b WHERE b.APP_RESOURCE_ID= :appResId" +
                ") p ORDER BY p.resourceMetadataName";

        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("appResId", appResId);
        query.setParameter("appId", appId);
        query.setParameter("resId", resId);
        query.setResultTransformer(Transformers.aliasToBean(RsAppResourceMetadata.class));

        return query.list();
    }

    public List<Map> appMetaExistence(String[] appResId){
        String sql = "SELECT " +
                "APP_ID as appId, count(APP_ID) as total " +
                "FROM  " +
                "`rs_app_resource_metadata` meta " +
                "WHERE  " +
                "APP_RESOURCE_ID in(:appResId) GROUP BY APP_ID HAVING count(APP_ID)>0";
        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameterList("appResId", appResId);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }
}
