package com.yihu.ehr.user.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XUserPrivilegeRepository extends PagingAndSortingRepository<UserPrivilege, String> {

    List<UserPrivilege>  findByUserId(String userName);

}
