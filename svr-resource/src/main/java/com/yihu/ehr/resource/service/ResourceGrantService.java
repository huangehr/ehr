package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AppResourceDao;
import com.yihu.ehr.resource.dao.intf.AppResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResource;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ResourceGrantService extends BaseJpaService<RsAppResource,AppResourceDao> {
    @Autowired
    private AppResourceDao appRsDao;
    @Autowired
    private AppResourceMetadataDao appRsMetadataDao;

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
}
