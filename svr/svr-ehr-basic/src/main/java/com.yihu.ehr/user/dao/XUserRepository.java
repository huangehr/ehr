package com.yihu.ehr.user.dao;

import com.yihu.ehr.user.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XUserRepository extends PagingAndSortingRepository<User, String> {

    List<User>  findByRealName(String userName);

    User findByLoginCode(String loginCode);

    List<User> findByTelephone(String telephone);

    List<User> findByIdCardNo(String idCardNo);

    @Modifying
    @Query("update User user set user.password = :password where user.id = :userId")
    void changePassWord(@Param ("userId") String userId, @Param("password") String password);
}
