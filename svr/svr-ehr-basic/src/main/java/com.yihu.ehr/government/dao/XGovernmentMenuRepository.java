package com.yihu.ehr.government.dao;

import com.yihu.ehr.entity.government.GovernmentMenu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by wxw on 2017/11/2.
 */
public interface XGovernmentMenuRepository extends PagingAndSortingRepository<GovernmentMenu, Integer> {
    @Query("select menu from GovernmentMenu menu where menu.status = 1")
    List<GovernmentMenu> findByStatus();
}
