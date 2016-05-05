package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name= MicroServices.Patient)
@ApiIgnore
public interface PatientClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/populations",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void createPatient(@RequestBody String jsonData);


    @RequestMapping(value = ApiVersion.Version1_0+"/populations/{id_card_no}/register",method = RequestMethod.GET)
    boolean isRegistered(@PathVariable(value = "id_card_no") String idCardNo);
}
