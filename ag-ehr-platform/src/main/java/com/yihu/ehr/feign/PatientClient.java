package com.yihu.ehr.feign;

import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.PatientMgr)
@ApiIgnore
public interface PatientClient {

    @RequestMapping(value = "/populations",method = RequestMethod.POST)
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    void createPatient(@RequestParam(value = "demoInfoJsonData") String demoInfoJsonData);


    @RequestMapping(value = "/populations/{id_card_no}/register",method = RequestMethod.GET)
    @ApiOperation(value = "根据身份证号判断病人是否注册")
    boolean isRegistered(@PathVariable(value = "id_card_no") String idCardNo);
}
