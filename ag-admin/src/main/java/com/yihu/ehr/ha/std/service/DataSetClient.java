package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by wq on 2016/2/29.
 */

@FeignClient("svr-dataset")
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DataSetClient {
//
//    @RequestMapping(value = "",method = RequestMethod.POST.GET)
//    @ApiOperation("查询数据集的方法")



}
