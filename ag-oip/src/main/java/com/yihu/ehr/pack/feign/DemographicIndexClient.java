package com.yihu.ehr.pack.feign;

import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 11:54
 */
@FeignClient(MicroServices.PatientMgr)
public interface DemographicIndexClient {
    boolean isRegistered(String demographicId);
}
