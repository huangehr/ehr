package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.patient.DemographicInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XDemographicInfoRepository extends PagingAndSortingRepository<DemographicInfo, String> {

    @Query("select demographicInfo from DemographicInfo demographicInfo where 1=1")
    public List<DemographicInfo> getOrgByCode(@Param("orgCode") String orgCode);

    @Query("select demographicInfo from DemographicInfo demographicInfo where demographicInfo.telephoneNo=:telephoneNo")
    public List<DemographicInfo> getDemographicInfoByTelephoneNo(@Param("telephoneNo") String telephoneNo);

    @Query("select demographicInfo from DemographicInfo demographicInfo where demographicInfo.idCardNo = :idCardNo")
    List<DemographicInfo> getDemographicInfoByIdCardNo(@Param("idCardNo") String idCardNo);
}
