package com.yihu.ehr.basic.address.dao;

import com.yihu.ehr.entity.address.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface AddressRepository extends PagingAndSortingRepository<Address, String> {

    @Query("select geography from Address geography where geography.country = :country")
    List<Address> findAddressListByCountry(@Param("country") String country);

    @Query("select geography.id from Address geography where geography.district in(:district)")
    List<String> findIdByDistrict(@Param("district") List<String> district);
}
