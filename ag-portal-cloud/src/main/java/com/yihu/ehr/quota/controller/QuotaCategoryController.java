package com.yihu.ehr.quota.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.quota.service.QuotaCategoryClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wxw on 2017/8/31.
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal")
@RestController
@Api(value = "QuotaCategory", description = "指标类别", tags = {"云门户-指标类别"})
public class QuotaCategoryController extends BaseController {
    @Autowired
    private QuotaCategoryClient quotaCategoryClient;

    @RequestMapping(value = "/quota/getQuotaCategoryOfChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类医疗服务子类目列表")
    public Envelop getQuotaCategoryOfChild() {
        try {
            Envelop envelop = new Envelop();
            envelop.setDetailModelList(quotaCategoryClient.getQuotaCategoryOfChild());
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
