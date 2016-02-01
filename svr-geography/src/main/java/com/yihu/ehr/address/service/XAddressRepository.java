package com.yihu.ehr.address.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAddressRepository extends PagingAndSortingRepository<Address, String> {


    @Query("select address from Address address where 1=1")
    List<Address> findAddressList();

    @Query("delete from Address address where address.id = :id")
    void delAddress(@Param("id") String id);

    @Query("select address from Address address where address.country = :country")
    List<Address> findAddressListByCountry(@Param("country") String country);

}
