package com.yihu.ehr.feign;

import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.model.profile.MProfileDocument;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.22 9:08
 */
@ApiIgnore
//@FeignClient(MicroServices.HealthProfile)
//@RequestMapping
public interface HealthProfileClient {
    //@RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.HealthProfile.Profiles, method = RequestMethod.GET)
    Collection<MProfile> getProfiles(
            @RequestParam("demographic_id") String demographicId,
            @RequestParam("organizations") String[] organizations,
            @RequestParam("event_type") String[] eventType,
            @RequestParam("since") String since,
            @RequestParam("to") String to,
            @RequestParam("load_std_data_set") boolean loadStdDataSet,
            @RequestParam("load_origin_data_set") boolean loadOriginDataSet);

    //@RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.HealthProfile.Profile, method = RequestMethod.GET)
    MProfile getProfile(
            @PathVariable("profile_id") String profileId,
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet);

    //@RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.HealthProfile.ProfileDocument, method = RequestMethod.GET)
    MProfileDocument getProfileDocument(
            @PathVariable("profile_id") String profileId,
            @PathVariable("document_id") String documentId,
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet);
}
