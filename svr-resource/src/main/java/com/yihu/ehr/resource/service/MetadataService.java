package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourceMetadataDao;
import com.yihu.ehr.resource.dao.intf.RsMetadataDao;
import com.yihu.ehr.resource.model.RsMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 数据源服务
 *
 * Created by lyr on 2016/5/16.
 */
@Service
@Transactional
public class MetadataService extends BaseJpaService<RsMetadata,ResourceMetadataDao> implements IMetadataService {

    @Autowired
    private RsMetadataDao metadataDao;

    /**
     * 删除数据元
     *
     * @param id String 数据元ID
     */
    public void deleteMetadata(String id)
    {
        String[] ids = id.split(",");

        for(String id_ : ids)
        {
            RsMetadata metadata = metadataDao.findOne(id);
            metadata.setValid("0");
            metadataDao.save(metadata);
        }
    }

    /**
     * 创建数据元
     *
     * @param metadata RsMetadata 数据元
     * @return RsMetadata 数据元
     */
    public RsMetadata saveMetadata(RsMetadata metadata){
        return metadataDao.save(metadata);
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsMetadata> getMetadata(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return metadataDao.findAll(pageable);
    }


}
