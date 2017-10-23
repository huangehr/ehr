package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author hzp
 * @created 2017.04.13
 */
@ApiIgnore
@FeignClient(name = MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
public interface PatientEndClient {

    @RequestMapping(value = "/populations",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void registerPatient(@RequestBody String jsonData);

    @RequestMapping(value = "/populations/{id_card_no}/register",method = RequestMethod.GET)
    boolean isRegistered(@PathVariable(value = "id_card_no") String idCardNo);

}
