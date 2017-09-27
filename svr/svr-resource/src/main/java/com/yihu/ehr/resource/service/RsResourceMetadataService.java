package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceMetadataDao;
import com.yihu.ehr.resource.model.RsResourceMetadata;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
@Service
@Transactional
public class RsResourceMetadataService extends BaseJpaService<RsResourceMetadata, RsResourceMetadataDao> {
    @Autowired
    private RsResourceMetadataDao rsMetadataDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 资源数据元创建
     *
     * @param metadata RsResourceMetadata 资源数据元实体
     * @return RsResources 资源数据元实体
     */
    public RsResourceMetadata saveResourceMetadata(RsResourceMetadata metadata) {
        rsMetadataDao.save(metadata);
        return metadata;
    }

    /**
     * 批量创建数据元
     *
     * @param metadataArray RsResourceMetadata[]
     * @return List<RsResourceMetadata>
     */
    public List<RsResourceMetadata> saveMetadataBatch(RsResourceMetadata[] metadataArray) {
        List<RsResourceMetadata> metadataList = new ArrayList<RsResourceMetadata>();

        for (RsResourceMetadata metadata : metadataArray) {
            metadataList.add(rsMetadataDao.save(metadata));
        }

        return metadataList;
    }

    /**
     * 资源数据元删除
     *
     * @param id String 资源数据元ID
     */
    public void deleteResourceMetadata(String id) {
        rsMetadataDao.delete(id);
    }

    /**
     * 根据资源ID删除资源数据元
     *
     * @param resourceId String 资源数据元ID
     */
    public void deleteRsMetadataByResourceId(String resourceId) {
        rsMetadataDao.deleteByResourcesId(resourceId);
    }

    /**
     * 资源数据元获取
     *
     * @param sorts String 排序
     * @param page  int 页码
     * @param size  int 分页大小
     * @return Page<RsResources> 资源数据元
     */
    public Page<RsResourceMetadata> getResourceMetadata(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));

        return rsMetadataDao.findAll(pageable);
    }


    /**
     * 获取资源数据元
     *
     * @param id String Id
     * @return RsResourceMetadata
     */
    public RsResourceMetadata getRsMetadataById(String id) {
        return rsMetadataDao.findOne(id);
    }

    public List<RsResourceMetadata> getRsMetadataByResourcesId(String resourcesId) {
        return rsMetadataDao.findByResourcesId(resourcesId);
    }

    public void deleteByResourcesIds(String[] resourcesIds) {
        Query query = currentSession().createQuery("delete from RsResourceMetadata rm where rm.resourcesId in (:resourcesIds)");
        query.setParameterList("resourcesIds", resourcesIds);
        query.executeUpdate();
    }
}
