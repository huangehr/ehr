package com.yihu.quota.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.singledisease.DiabetesCheckService;
import com.yihu.quota.service.singledisease.DiabetesMedicineService;
import com.yihu.quota.service.singledisease.DiabetesService;
import com.yihu.quota.service.singledisease.DiabetesSymptomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by janseny on 2018/4/25.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "糖尿病相关数据信息接口")
public class DiabetesController extends BaseController {

    @Autowired
    private  DiabetesService diabetesService;
    @Autowired
    private DiabetesSymptomService diabetesSymptomService;
    @Autowired
    private DiabetesCheckService diabetesCheckService;
    @Autowired
    private DiabetesMedicineService diabetesMedicineService;

    @ApiOperation(value = "获取糖尿病相关数据信息接口")
    @RequestMapping(value = "getDiabetesByType", method = RequestMethod.GET)
    public Envelop getDiabetesByType(
            @ApiParam(name = "type", value = "type 1：个人信息 2：并发症相关信息 3：糖耐和血糖检测相关信息 4：用药情况", required = true)
            @RequestParam(value = "type", required = true) int type) {
        Envelop envelop = new Envelop();
        if(type == 1){
            diabetesService.validatorIdentity();
        }else if(type == 2){
            diabetesSymptomService.validatorIdentity();
        }else if(type == 3){
            diabetesCheckService.validatorIdentity();
        }else if(type == 4){
            diabetesMedicineService.validatorIdentity();
        }
        return envelop;

    }

}
