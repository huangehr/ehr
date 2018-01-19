package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsRolesResourceMetadataDao;
import com.yihu.ehr.resource.model.RsRolesResourceMetadata;
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
 * Created by zdm on 2017/6/16
 */
@Service
@Transactional
public class RsRolesResourceMetadataGrantService extends BaseJpaService<RsRolesResourceMetadata, RsRolesResourceMetadataDao>  {

    @Autowired
    private RsRolesResourceMetadataDao rsRolesResourceMetadataDao;

    /**
     * 角色组资源元数据授权
     *
     * @param rsRolesMetadata RsRolesResourceMetadata 资源数据元实体
     * @return RsRolesResourceMetadata 资源数据元实体
     */
    public RsRolesResourceMetadata grantRsRolesMetadata(RsRolesResourceMetadata  rsRolesMetadata) {
        rsRolesResourceMetadataDao.save(rsRolesMetadata);
        return rsRolesMetadata;
    }

    /**
     * 资源数据元授权多个角色组
     *
     * @param rsMetadataList List<RsRolesResourceMetadata> 资源数据元授权实体集合
     * @return List<RsRolesResourceMetadata> 资源数据元授权实体集合
     */
    public List<RsRolesResourceMetadata> grantRsRolesMetadataBatch(List<RsRolesResourceMetadata> rsMetadataList) {
        rsRolesResourceMetadataDao.save(rsMetadataList);
        return rsMetadataList;
    }

    /**
     * 删除资源数据元授权
     *
     * @param id String 授权ID
     */
    public void deleteRsRolesMetadataGrant(String id) {
        String[] idArray = id.split(",");
        for(String _id:idArray) {
            rsRolesResourceMetadataDao.delete(_id);
        }
    }

    /**
     * 角色组资源授权获取
     *
     * @param sorts String 排序
     * @param page int 页码
     * @param size size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsRolesResourceMetadata> getRolesRsMetadataGrant(String sorts, int page, int size) {
        Pageable pageable =  new PageRequest(page, size, parseSorts(sorts));
        return rsRolesResourceMetadataDao.findAll(pageable);
    }

    /**
     * 根据ID获取资源数据元授权
     *
     * @param id String Id
     * @return RsRolesResource
     */
    public RsRolesResourceMetadata getRsRolesMetadataGrantById(String id) {
        return rsRolesResourceMetadataDao.findOne(id);
    }

    /**
     * 生失效操作
     *
     */
    public void rolesValid(String ids, int valid) {
        String hql = "update RsRolesResourceMetadata t set t.valid=:valid";
        hql += StringUtils.isEmpty(ids) ? "" : " where id in(:ids)";
        Query query = currentSession().createQuery(hql);
        if(!StringUtils.isEmpty(ids))
            query.setParameterList("ids", ids.split(","));
        query.setParameter("valid", String.valueOf(valid));
        query.executeUpdate();
    }

    /**
     * 角色组资源授权获取
     */
    public List<RsRolesResourceMetadata> getRolesRsMetadatas(String rolesResId, String rolesId, String resId) {
        String sql =
                "SELECT * FROM (" +
                    "SELECT * FROM " +
                        "(SELECT " +
                        "   '' AS id, :rolesResId AS rolesResourceId, :rolesId AS rolesId, m.`NAME` AS resourceMetadataName, rm.ID AS resourceMetadataId, " +
                        "   '' AS dimensionId, '' AS dimensionValue, 0 AS valid " +
                        "FROM " +
                        "   `rs_resource_metadata` rm " +
                        "LEFT JOIN rs_metadata m on rm.METADATA_ID=m.ID " +
                        "LEFT JOIN rs_roles_resource_metadata pm on (pm.RESOURCE_METADATA_ID=rm.ID AND pm.roles_resource_id=:rolesResId) " +
                        "WHERE " +
                        "   pm.ID IS NULL AND rm.resources_id=:resId ) a " +
                "UNION ALL " +
                    "SELECT b.id, b.ROLES_RESOURCE_ID as rolesResourceId, b.ROLES_ID as rolesId, b.RESOURCE_METADATA_NAME as resourceMetadataName, b.RESOURCE_METADATA_ID as resourceMetadataId," +
                            "b.DIMENSION_ID as dimensionId, b.DIMENSION_VALUE as dimensionValue, b.valid" +
                            " FROM rs_roles_resource_metadata b WHERE b.ROLES_RESOURCE_ID= :rolesResId" +
                ") p ORDER BY p.resourceMetadataName";

        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameter("rolesResId", rolesResId);
        query.setParameter("rolesId", rolesId);
        query.setParameter("resId", resId);
        query.setResultTransformer(Transformers.aliasToBean(RsRolesResourceMetadata.class));
        return query.list();
    }

    public List<Map> rolesMetaExistence(String[] rolesResId){
        String sql = "SELECT " +
                "ROLES_ID as rolesId, count(ROLES_ID) as total " +
                "FROM  " +
                "`rs_roles_resource_metadata` meta " +
                "WHERE  " +
                "ROLES_RESOURCE_ID in(:rolesResId) GROUP BY ROLES_ID HAVING count(ROLES_ID)>0";
        SQLQuery query = currentSession().createSQLQuery(sql);
        query.setParameterList("rolesResId", rolesResId);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    public List<RsRolesResourceMetadata> findByRolesResourceIdAndValid(String rolesResourceId, String valid){
        return rsRolesResourceMetadataDao.findByRolesResourceIdAndValid(rolesResourceId, valid);
    }

}
