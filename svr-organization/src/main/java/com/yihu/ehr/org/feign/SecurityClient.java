package com.yihu.ehr.org.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MUserSecurity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.Security)
@RequestMapping(value = "/rest/v1.0")
@ApiIgnore
public interface SecurityClient {

    @RequestMapping(value = "/securities/org/{org_code}", method = POST, consumes = "application/json")
    MUserSecurity createSecurityByOrgCode(@PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = "/securities/org/{org_code}", method = GET )
    MUserSecurity getUserSecurityByOrgCode(@PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = "/securities/{id}", method = DELETE  )
    void deleteSecurity(@PathVariable(value = "id") String id);

    @RequestMapping(value = "/user_keys/{user_key_id}", method = DELETE  )
    void deleteUserKey(@PathVariable(value = "user_key_id") String userKeyId);

    @RequestMapping(value = "/user_keys/org/{org_code}", method = GET  )
    String getUserKeyIdByOrgCd(@PathVariable(value = "org_code") String orgCode);

}
