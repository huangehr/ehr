package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourceMetadataDao;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import com.yihu.ehr.resource.service.intf.IResourceMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lyr on 2016/4/25.
 */
@Service
@Transactional
public class ResourceMetadataService extends BaseJpaService<RsResourceMetadata,ResourceMetadataDao> implements IResourceMetadataService {
    @Autowired
    private ResourceMetadataDao rsMetadataDao;

    /**
     * 资源数据元创建
     *
     * @param metadata RsResourceMetadata 资源数据元实体
     * @return RsResources 资源数据元实体
     */
    public RsResourceMetadata saveResourceMetadata(RsResourceMetadata metadata)
    {
        rsMetadataDao.save(metadata);
        return metadata;
    }

    /**
     * 资源数据元删除
     *
     * @param id String 资源数据元ID
     */
    public void deleteResourceMetadata(String id)
    {
        rsMetadataDao.delete(id);
    }

    /**
     * 根据资源ID删除资源数据元
     *
     * @param resourceId String 资源数据元ID
     */
    public void deleteRsMetadataByResourceId(String resourceId)
    {
        rsMetadataDao.deleteByResourcesId(resourceId);
    }

    /**
     * 资源数据元获取
     *
     * @param sorts String 排序
     * @param page int 页码
     * @param size int 分页大小
     * @return Page<RsResources> 资源数据元
     */
    public Page<RsResourceMetadata> getResourceMetadata(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return rsMetadataDao.findAll(pageable);
    }
}
