package com.yihu.ehr.tj.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/6/9
 */
@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjDimensionSlaveClient {

    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionSlaveList, method = RequestMethod.GET)
    @ApiOperation(value = "数据从维度列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增/修改从维度")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除从维度")
    Result delete(@RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlaveId, method = RequestMethod.GET)
    @ApiOperation(value = "获取从维度信息", notes = "通知从维度信息")
    TjDimensionSlave getTjDimensionSlave(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlaveCode, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据ID获取从维度")
    public TjDimensionSlave getTjDimensionSlave(
            @RequestParam(value = "code") String code);
}
