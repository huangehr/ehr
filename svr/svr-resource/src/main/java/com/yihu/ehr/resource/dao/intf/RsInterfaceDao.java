package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.RsInterface;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface RsInterfaceDao extends PagingAndSortingRepository<RsInterface,String> {
    List<RsInterface> findByName(String name);

    List<RsInterface> findByResourceInterface(String resourceInterface);
}