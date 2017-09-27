package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.resource.client.RsResourceDefaultQueryClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 固化视图过滤条件 client
 *
 * @author 张进军
 * @created 2017.9.8 15:42
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "固化视图过滤条件服务接口", tags = {"视图配置管理-固化视图过滤条件服务接口"})
public class RsResourceDefaultQueryController extends BaseController {

    @Autowired
    private RsResourceDefaultQueryClient rsResourceDefaultQueryClient;

    @ApiOperation("根据ID获取固化视图筛选条件")
    @RequestMapping(value = ServiceApi.Resources.QueryByResourceId, method = RequestMethod.GET)
    public Envelop getByResourceId(
            @ApiParam(name = "resourceId", value = "视图主键")
            @RequestParam(value = "resourceId") String resourceId) throws Exception {
        try {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            String query = rsResourceDefaultQueryClient.getByResourceId(resourceId);
            envelop.setObj(query);
            return envelop;
        } catch (Exception e) {
            LogService.getLogger(RsResourceDefaultQueryController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

}
