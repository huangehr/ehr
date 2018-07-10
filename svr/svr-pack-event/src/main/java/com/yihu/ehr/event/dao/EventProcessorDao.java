package com.yihu.ehr.event.dao;

import com.yihu.ehr.entity.event.EventProcessor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by progr1mmer on 2018/7/5.
 */
public interface EventProcessorDao extends PagingAndSortingRepository<EventProcessor, Integer>  {

    EventProcessor findByName(String name);

}
