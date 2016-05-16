package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AppResourceMetadataDao;
import com.yihu.ehr.resource.dao.intf.ResourceMetadataDao;
import com.yihu.ehr.resource.model.RsAppResourceMetadata;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.service.intf.IResourceMetadataGrantService;
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
public class ResourceMetadataGrantService  extends BaseJpaService<RsResourceMetadata,ResourceMetadataDao> implements IResourceMetadataGrantService {
    @Autowired
    private AppResourceMetadataDao appRsMetadataDao;

    /*
    * 资源元数据授权
    *
    * @param 资源数据元实体
    * @return 资源数据元实体
    */
    public RsAppResourceMetadata grantRsMetadata(RsAppResourceMetadata  rsAppMetadata)
    {
        appRsMetadataDao.save(rsAppMetadata);

        return rsAppMetadata;
    }

    /*
    * 资源数据元授权多个APP
    *
    * @param 资源数据元授权实体集合
    * @return 资源数据元授权实体集合
    */
    public List<RsAppResourceMetadata> grantRsMetadataBatch(List<RsAppResourceMetadata> rsMetadataList)
    {
        appRsMetadataDao.save(rsMetadataList);

        return rsMetadataList;
    }

    /*
     * 删除资源数据元授权
     *
     * @param 授权ID
     */
    public void deleteRsMetadataGrant(String id)
    {
        appRsMetadataDao.delete(id);
    }

    /*
     *资源授权获取
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
