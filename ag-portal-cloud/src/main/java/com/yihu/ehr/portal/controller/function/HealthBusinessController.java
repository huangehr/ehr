package com.yihu.ehr.portal.controller.function;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.health.MHealthBusiness;
import com.yihu.ehr.portal.service.function.HealthBusinessClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal")
@RestController
@Api(value = "HealthBusiness", description = "指标类别", tags = {"云门户-指标类别"})
public class HealthBusinessController extends BaseController {
    @Autowired
    HealthBusinessClient healthBusinessClient;

    @RequestMapping(value = "/health/getHealthBusinessOfChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类医疗服务子类目列表")
    public Envelop getHealthBusinessOfChild() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(healthBusinessClient.getHealthBusinessOfChild());
            envelop.setSuccessFlg(true);
            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }
}
