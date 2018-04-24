package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.VirtualCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAbstractVirtualCardRepository extends PagingAndSortingRepository<VirtualCard, String> {

    @Query("select abstractVirtualCard from VirtualCard abstractVirtualCard where 1=1")
    public List<VirtualCard> getOrgByCode(@Param("orgCode") String orgCode);

}
