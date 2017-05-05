package com.yihu.ehr.geography.dao;

import com.yihu.ehr.entity.geography.Geography;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XGeographyRepository extends PagingAndSortingRepository<Geography, String> {

    @Query("select geography from Geography geography where geography.country = :country")
    List<Geography> findAddressListByCountry(@Param("country") String country);

}
