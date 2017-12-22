package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.OrgHealthCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 卫生机构类别 DAO
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
public interface OrgHealthCategoryDao extends JpaRepository<OrgHealthCategory, Integer> {

    @Query(" FROM OrgHealthCategory rmc WHERE rmc.id <> :id AND rmc.code = :code ")
    OrgHealthCategory isUniqueCode(@Param("id") Integer id, @Param("code") String code);

    @Query(" FROM OrgHealthCategory rmc WHERE rmc.id <> :id AND rmc.name = :name ")
    OrgHealthCategory isUniqueName(@Param("id") Integer id, @Param("name") String name);

    @Query(" FROM OrgHealthCategory rc WHERE rc.pid = :pid ")
    List<OrgHealthCategory> getChildrenByPid(@Param("pid") Integer pid);

    @Query(" FROM OrgHealthCategory rc WHERE rc.pid = null ")
    List<OrgHealthCategory> getTopParents();

}
