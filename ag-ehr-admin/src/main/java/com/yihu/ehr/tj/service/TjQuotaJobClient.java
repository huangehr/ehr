package com.yihu.ehr.tj.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2017/6/9.
 */
@FeignClient(name= MicroServices.Quota)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaJobClient {

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExecute, method = RequestMethod.GET)
    @ApiOperation(value = "执行指标任务")
    boolean tjQuotaExecute(@RequestParam("id") Integer id);

}
