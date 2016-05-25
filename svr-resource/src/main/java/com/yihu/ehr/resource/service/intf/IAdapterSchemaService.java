package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsAdapterSchema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by lyr on 2016/5/17.
 */
public interface IAdapterSchemaService {
    /**
     * 获取适配方案
     *
     * @param sorts String 排序
     * @param page int 分页
     * @param size int 分页大小
     * @return Page<RsAdapterSchema>
     */
    Page<RsAdapterSchema> getAdapterSchema(String sorts, int page, int size);


    /**
     * 删除适配方案
     *
     * @param ids String 适配方案ID
     */
    void deleteAdapterSchema(String ids);

    /**
     * 获取适配方案
     *
     * @param id String Id
     * @return RsAdapterSchema
     */
    RsAdapterSchema getAdapterSchemaById(String id);

    /**
     * 保存适配方案
     *
     * @param adapterSchema RsAdapterSchema 适配方案
     * @return RsAdapterSchema 适配方案
     */
    RsAdapterSchema saveAdapterSchema(RsAdapterSchema adapterSchema);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;
}
