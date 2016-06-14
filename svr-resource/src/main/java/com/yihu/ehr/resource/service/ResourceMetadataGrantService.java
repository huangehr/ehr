package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AppResourceMetadataDao;
import com.yihu.ehr.resource.dao.intf.ResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by lyr on 2016/4/26.
 */
@Service
@Transactional
public class ResourceMetadataGrantService  extends BaseJpaService<RsAppResourceMetadata,AppResourceMetadataDao>  {
    @Autowired
    private AppResourceMetadataDao appRsMetadataDao;

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
}
