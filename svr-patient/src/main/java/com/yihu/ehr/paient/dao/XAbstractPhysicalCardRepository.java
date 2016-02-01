package com.yihu.ehr.paient.dao;

import com.yihu.ehr.paient.service.card.AbstractPhysicalCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAbstractPhysicalCardRepository extends PagingAndSortingRepository<AbstractPhysicalCard, String> {

    @Query("select abstractPhysicalCard from AbstractPhysicalCard abstractPhysicalCard where 1=1")
    public List<AbstractPhysicalCard> getOrgByCode(@Param("orgCode") String orgCode);

}
