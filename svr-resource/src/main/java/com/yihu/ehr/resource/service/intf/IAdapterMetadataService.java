package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsAdapterMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by lyr on 2016/5/17.
 */
public interface IAdapterMetadataService {
    /**
     * 保存适配方案
     *
     * @param adapterMetadata RsAdapterMetadata 适配方案
     * @return RsAdapterSchema 适配方案
     */
    RsAdapterMetadata saveAdapterMetadata(RsAdapterMetadata adapterMetadata);

    /**
     * 删除适配方案
     *
     * @param id String 适配方案ID
     */
    void deleteAdapterMetadata(String id);

    /**
     * 获取适配方案
     *
     * @param sorts String 排序
     * @param page int 分页
     * @param size int 分页大小
     * @return Page<RsAdapterSchema>
     */
    Page<RsAdapterMetadata> getAdapterMetadata(String sorts, int page, int size);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;
}
