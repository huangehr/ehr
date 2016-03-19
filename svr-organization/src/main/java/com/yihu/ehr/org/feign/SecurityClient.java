package com.yihu.ehr.org.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.security.MKey;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name = MicroServices.Security,url = MicroServiceIpAddressStr.Security+MicroServicePort.Security)
@ApiIgnore
public interface SecurityClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/securities/org/{org_code}", method = POST, consumes = "application/json")
    MKey createSecurityByOrgCode(@PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0+"/securities/org/{org_code}", method = GET )
    MKey getUserSecurityByOrgCode(@PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0+"/securities/{id}", method = DELETE  )
    void deleteSecurity(@PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0+"/user_keys/{user_key_id}", method = DELETE  )
    void deleteUserKey(@PathVariable(value = "user_key_id") String userKeyId);

    @RequestMapping(value = ApiVersion.Version1_0+"/user_keys/org/{org_code}", method = GET  )
    String getUserKeyIdByOrgCd(@PathVariable(value = "org_code") String orgCode);

}
