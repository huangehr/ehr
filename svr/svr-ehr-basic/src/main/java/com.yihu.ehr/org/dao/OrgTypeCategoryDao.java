package com.yihu.ehr.org.dao;

import com.yihu.ehr.org.model.OrgTypeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 机构类型管理 DAO
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
public interface OrgTypeCategoryDao extends JpaRepository<OrgTypeCategory, Integer> {

    @Query(" FROM OrgTypeCategory rmc WHERE rmc.id <> :id AND rmc.code = :code ")
    OrgTypeCategory isUniqueCode(@Param("id") Integer id, @Param("code") String code);

    @Query(" FROM OrgTypeCategory rmc WHERE rmc.id <> :id AND rmc.name = :name ")
    OrgTypeCategory isUniqueName(@Param("id") Integer id, @Param("name") String name);

    @Query(" FROM OrgTypeCategory rc WHERE rc.pid = :pid ")
    List<OrgTypeCategory> getChildrenByPid(@Param("pid") Integer pid);

    @Query(" FROM OrgTypeCategory rc WHERE rc.pid = null ")
    List<OrgTypeCategory> getTopParents();

}
