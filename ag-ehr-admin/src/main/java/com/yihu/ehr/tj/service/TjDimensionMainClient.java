package com.yihu.ehr.tj.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDimensionMain;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
public interface TjDimensionMainClient {

    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionMainList, method = RequestMethod.GET)
    @ApiOperation(value = "数据统计主维度列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMain, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增/修改主维度")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMain, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除主维度")
    Result delete(@RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainId, method = RequestMethod.GET)
    @ApiOperation(value = "获取主维度信息", notes = "通知主维度信息")
    TjDimensionMain getTjDimensionMain(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainCode, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据Code获取主维度")
    public TjDimensionMain getTjDimensionMain(  @RequestParam(value = "code") String code);

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainName,method = RequestMethod.GET)
    @ApiOperation(value = "验证名称是否存在")
    public boolean isNameExists( @RequestParam(value = "name") String name);

}
