package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.PhysicalCard;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAbstractPhysicalCardRepository extends PagingAndSortingRepository<PhysicalCard, String> {

}
