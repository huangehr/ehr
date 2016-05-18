package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsAdapterSchema;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/5/17.
 */
public interface AdapterSchemaDao extends PagingAndSortingRepository<RsAdapterSchema,String> {
}
