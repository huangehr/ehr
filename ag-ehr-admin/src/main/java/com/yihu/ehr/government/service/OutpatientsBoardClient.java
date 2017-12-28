package com.yihu.ehr.government.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by zdm on 2017/12/28.
 */
@FeignClient(name= MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OutpatientsBoardClient {

    @RequestMapping(value = ServiceApi.Government.OutpatientsBoardCount, method = RequestMethod.POST)
    @ApiOperation(value = "新增浏览记录")
    ListResult outpatientsBoardCount(
            @ApiParam(name = "periodType", value = " 周期类型：1-本月  2-本年", defaultValue = "")
            @RequestParam(value = "periodType", required = false) String periodType,
            @ApiParam(name = "startDate", value = " 周期开始时间", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = " 周期结束时间", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(name = "quotaCode", value = " 指标编码", defaultValue = "")
            @RequestParam(value = "quotaCode", required = false) String quotaCode);

}
