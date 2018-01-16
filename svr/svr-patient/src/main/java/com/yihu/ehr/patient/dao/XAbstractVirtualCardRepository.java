package com.yihu.ehr.patient.dao;

import com.yihu.ehr.patient.service.card.AbstractVirtualCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAbstractVirtualCardRepository extends PagingAndSortingRepository<AbstractVirtualCard, String> {

    @Query("select abstractVirtualCard from AbstractVirtualCard abstractVirtualCard where 1=1")
    public List<AbstractVirtualCard> getOrgByCode(@Param("orgCode") String orgCode);

}
