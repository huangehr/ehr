package com.yihu.ehr.medicalRecord.dao.intf;


import com.yihu.ehr.medicalRecord.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */
public interface UserInfoDao extends PagingAndSortingRepository<User,String> {

    User findByLoginCode(String loginCode);
}
