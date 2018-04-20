package com.yihu.ehr.basic.getui.dao;

import com.yihu.ehr.basic.getui.model.GeTuiUserClient;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author litaohong on 2018/4/20
 * @project ehr
 */
public interface GeTuiClientRepository extends PagingAndSortingRepository<GeTuiUserClient, Long> {

    /**
     * 根据用户id获取对应的clientId
     * @param userId
     * @return
     */
    @Query("From GeTuiUserClient where userId = ?1")
    public GeTuiUserClient getUserGeTuiClientByUserId(String userId);

    /**
     * 根据用户id修改clientId
     * @param userId
     * @param clientId
     * @return
     */
    @Modifying
    @Query("update GeTuiUserClient set clientId = ?2  where userId = ?1")
    public void updateClientIdByUserId(String userId, String clientId);



    @Query("select geTuiUserClient from GeTuiUserClient geTuiUserClient where geTuiUserClient.userId in (:userIds) " )
    List<GeTuiUserClient> getListByuserId(@Param("userIds") List<String> userIds);

}
