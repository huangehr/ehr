package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.AdapterMetadataDao;
import com.yihu.ehr.resource.dao.intf.AdapterSchemaDao;
import com.yihu.ehr.resource.model.RsAdapterSchema;
import com.yihu.ehr.resource.service.intf.IAdapterSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 适配方案服务
 *
 * Created by lyr on 2016/5/17.
 */
@Service
@Transactional
public class AdapterSchemaService extends BaseJpaService<RsAdapterSchema,AdapterSchemaDao> implements IAdapterSchemaService {

    @Autowired
    private AdapterSchemaDao schemaDao;

    @Autowired
    private AdapterMetadataDao metadataDao;

    /**
     * 保存适配方案
     *
     * @param adapterSchema RsAdapterSchema 适配方案
     * @return RsAdapterSchema 适配方案
     */
    public RsAdapterSchema saveAdapterSchema(RsAdapterSchema adapterSchema)
    {
        return schemaDao.save(adapterSchema);
    }

    /**
     * 删除适配方案
     *
     * @param ids String 适配方案ID
     */
    public void deleteAdapterSchema(String ids)
    {
        String[] idsArray = ids.split(",");

        for(String id_ : idsArray)
        {
            metadataDao.deleteBySchemaId(id_);
            schemaDao.delete(id_);
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
    public Page<RsAdapterSchema> getAdapterSchema(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return schemaDao.findAll(pageable);
    }
}
