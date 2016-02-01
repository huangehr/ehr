package com.yihu.ehr.paient.dao;

import com.yihu.ehr.paient.service.demographic.DemographicId;
import com.yihu.ehr.paient.service.demographic.DemographicInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XDemographicInfoRepository extends PagingAndSortingRepository<DemographicInfo, DemographicId> {

    @Query("select demographicInfo from DemographicInfo demographicInfo where 1=1")
    public List<DemographicInfo> getOrgByCode(@Param("orgCode") String orgCode);

}
