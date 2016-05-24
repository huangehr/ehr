package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsMetadata;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 数据元服务接口
 *
 * Created by lyr on 2016/5/16.
 */
public interface IMetadataService {
    /**
     * 删除数据元
     *
     * @param id String 数据元ID
     */
    void deleteMetadata(String id);

    /**
     * 创建数据元
     *
     * @param metadata RsMetadata 数据元
     * @return RsMetadata 数据元
     */
    RsMetadata saveMetadata(RsMetadata metadata);

    /**
     * 获取数据元
     *
     * @param id String Id
     * @return RsMetadata
     */
     RsMetadata getMetadataById(String id);

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResource> 资源
     */
    Page<RsMetadata> getMetadata(String sorts, int page, int size);

    /**
     * 批量创建数据元
     *
     * @param metadataArray RsMetadata[]
     * @return List<RsMetadata>
     */
     List<RsMetadata> saveMetadataBatch(RsMetadata[] metadataArray);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

}
