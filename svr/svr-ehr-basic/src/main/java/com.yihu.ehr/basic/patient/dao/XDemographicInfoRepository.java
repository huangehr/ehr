package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.Demographic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XDemographicInfoRepository extends PagingAndSortingRepository<Demographic, String> {

    @Query("select demographicInfo from Demographic demographicInfo where 1=1")
    public List<Demographic> getOrgByCode(@Param("orgCode") String orgCode);

    @Query("select demographicInfo from Demographic demographicInfo where demographicInfo.telephoneNo=:telephoneNo")
    public List<Demographic> getDemographicInfoByTelephoneNo(@Param("telephoneNo") String telephoneNo);

    @Query("select demographicInfo from Demographic demographicInfo where demographicInfo.idCardNo = :idCardNo")
    List<Demographic> getDemographicInfoByIdCardNo(@Param("idCardNo") String idCardNo);
}
