package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.model.RsAdapterMetadata;
import com.yihu.ehr.resource.service.intf.IAdapterMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 适配数据元服务
 *
 * Created by lyr on 2016/5/17.
 */
@Service
@Transactional
public class AdapterMetadataService extends BaseJpaService<RsAdapterMetadata,AdapterMetadataDao> implements IAdapterMetadataService{
    @Autowired
    private AdapterMetadataDao adMetadataDao;

    /**
     * 保存适配方案
     *
     * @param adapterMetadata RsAdapterMetadata 适配方案
     * @return RsAdapterSchema 适配方案
     */
    public RsAdapterMetadata saveAdapterMetadata(RsAdapterMetadata adapterMetadata)
    {
        return adMetadataDao.save(adapterMetadata);
    }

    /**
     * 删除适配方案
     *
     * @param id String 适配方案ID
     */
    public void deleteAdapterMetadata(String id)
    {
        String[] ids = id.split(",");

        for(String id_ : ids)
        {
            adMetadataDao.delete(id_);
        }
    }

    /**
     * 获取适配方案
     *
     * @param sorts String 排序
     * @param page int 分页
     * @param size int 分页大小
     * @return Page<RsAdapterSchema>
     */
    public Page<RsAdapterMetadata> getAdapterMetadata(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return adMetadataDao.findAll(pageable);
    }
}
