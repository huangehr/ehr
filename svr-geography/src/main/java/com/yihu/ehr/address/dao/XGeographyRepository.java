package com.yihu.ehr.address.dao;

import com.yihu.ehr.address.service.Geography;
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


    @Query("select address from Address address where 1=1")
    List<Geography> findAddressList();

    @Query("delete from Address address where address.id = :id")
    void delAddress(@Param("id") String id);

    @Query("select address from Address address where address.country = :country")
    List<Geography> findAddressListByCountry(@Param("country") String country);

}
