package com.yihu.ehr.tj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.tj.TjQuota;
import com.yihu.ehr.entity.tj.TjQuotaWarn;
import com.yihu.ehr.model.tj.MTjQuotaWarn;
import com.yihu.ehr.tj.service.TjQuotaService;
import com.yihu.ehr.tj.service.TjQuotaWarnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjQuotaWarnEndPoint", description = "指标统计", tags = {"指标预警"})
public class TjQuotaWarnEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TjQuotaWarnService tjQuotaWarnService;
    @Autowired
    TjQuotaService tjQuotaService;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaWarn, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标预警信息", notes = "获取指标预警信息")
    List<MTjQuotaWarn> getTjQuotaWarn(@RequestParam(value = "userId") String userId){
        List<MTjQuotaWarn> mTjQuotaWarnList = new ArrayList<>();
        List<TjQuotaWarn> tjQuotaWarnList =  tjQuotaWarnService.findByUserId(null);
        for (TjQuotaWarn tjQuotaWarn : tjQuotaWarnList){
            MTjQuotaWarn mTjQuotaWarn = new MTjQuotaWarn();
            mTjQuotaWarn = objectMapper.convertValue(tjQuotaWarn, MTjQuotaWarn.class);
            TjQuota tjQuota = tjQuotaService.findByCode(tjQuotaWarn.getQuotaCode());
            mTjQuotaWarn.setQuotaName(tjQuota.getName());
            mTjQuotaWarnList.add(mTjQuotaWarn);
        }

        return mTjQuotaWarnList;
    };

}
