package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.AppResourceDao;
import com.yihu.ehr.resource.dao.AppResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResource;
import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import com.yihu.ehr.resource.service.intf.IResourceGrantService;
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
public class ResourceGrantService extends BaseJpaService<RsAppResource,AppResourceDao> implements IResourceGrantService {
    @Autowired
    private AppResourceDao appRsDao;
    @Autowired
    private AppResourceMetadataDao appRsMetadataDao;

    /*
     * 资源授权单个App
     *
     * @param 资源授权实体
     * @return 资源授权实体
     */
    public RsAppResource grantResource(RsAppResource rsAppResource)
    {
        appRsDao.save(rsAppResource);

        return rsAppResource;
    }

    /*
     * 资源授权多个APP
     *
     * @param 资源授权实体集合
     * @return 资源授权实体集合
     */
    public List<RsAppResource> grantResourceBatch(List<RsAppResource> appRsList)
    {
        appRsDao.save(appRsList);

        return appRsList;
    }

    /*
     * 删除资源授权
     *
     * @param 授权ID
     */
    public void deleteResourceGrant(String id)
    {
        appRsMetadataDao.deleteByAppResourceId(id);
        appRsDao.delete(id);
    }

    /*
     *资源授权获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsAppResource> getAppResourceGrant(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return appRsDao.findAll(pageable);
    }

    /*
     *资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsAppResourceMetadata> getAppRsMetadataGrant(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return appRsMetadataDao.findAll(pageable);
    }


}
