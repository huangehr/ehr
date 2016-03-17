package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name= MicroServiceName.Patient,url = MicroServiceIpAddressStr.Patient+MicroServicePort.Patient)
@ApiIgnore
public interface PatientClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/populations",method = RequestMethod.POST)
    void createPatient(@RequestParam(value = "json_data") String jsonData);


    @RequestMapping(value = ApiVersion.Version1_0+"/populations/{id_card_no}/register",method = RequestMethod.GET)
    boolean isRegistered(@PathVariable(value = "id_card_no") String idCardNo);
}
