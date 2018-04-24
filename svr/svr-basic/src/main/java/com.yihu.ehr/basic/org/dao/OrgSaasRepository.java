package com.yihu.ehr.basic.org.dao;

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

    @Query("select os.saasName from OrgSaas os where os.type = 1 and os.orgCode = :orgCode")
    List<String> findSaasName(@Param("orgCode") String orgCode);

    @Query("select os.saasName from OrgSaas os where os.type = 2 and os.orgCode = :orgCode")
    List<String> findSaasNameByType(@Param("orgCode") String orgCode);

    @Query("select os.saasCode from OrgSaas os where os.type = 1 and os.orgCode = :orgCode")
    List<String> findSaasCode(@Param("orgCode") String orgCode);

    @Query("select os.saasCode from OrgSaas os where os.type = 2 and os.orgCode = :orgCode")
    List<String> findSaasCodeByType(@Param("orgCode") String orgCode);

    //根据机构获取saas机构数据
    @Query("select os.saasCode from OrgSaas os where  os.orgCode in (:orgCode) and os.type = :type")
    List<String> getOrgSaasCodeByorgCode(@Param("orgCode")  List<String> orgCode, @Param("type") String type);
}
