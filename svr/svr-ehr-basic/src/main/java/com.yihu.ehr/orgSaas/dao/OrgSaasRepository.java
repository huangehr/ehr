package com.yihu.ehr.orgSaas.dao;

import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.entity.organizations.OrgSaas;
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
public interface OrgSaasRepository extends PagingAndSortingRepository<OrgSaas, Integer> {

    //根据机构获取saas机构数据
    @Query("select os.id,os.orgCode,os.type,os.saasCode,os.saasName from OrgSaas os where  os.orgCode = :orgCode and os.type = :type")
    List<Object> getOrgSaasByorgCode(@Param("orgCode") String orgCode, @Param("type") String type);

    //新增查询数据是否存在
    @Query("select os.id,os.orgCode,os.type,os.saasCode,os.saasName from OrgSaas os where  os.orgCode = :orgCode and os.type = :type and os.saasCode = :saasCode")
    List<OrgSaas> getOrgSaasByorgCode(@Param("orgCode") String orgCode, @Param("type") String type,@Param("saasCode") String saasCode);

    //根据机构和类别删除既存数据
    @Modifying
    @Query("delete from OrgSaas os where os.orgCode = :orgCode and os.type=:type")
    void deleteOrgSaas(@Param("orgCode") String orgCode, @Param("type") String type);

}
