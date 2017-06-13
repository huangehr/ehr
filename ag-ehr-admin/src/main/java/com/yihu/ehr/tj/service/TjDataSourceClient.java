package com.yihu.ehr.tj.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjDataSource;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2017/6/8.
 */
@FeignClient(name= MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjDataSourceClient {
    @RequestMapping(value = ServiceApi.TJ.GetTjDataSourceList, method = RequestMethod.GET)
    @ApiOperation(value = "数据存储列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.TJ.AddTjDataSource, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增数据存储")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.DeleteTjDataSource, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据存储")
    Result delete(@RequestParam(value = "id") Long id);

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSourceById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询数据源")
    TjDataSource getById(@PathVariable(value = "id") Long id);

    @RequestMapping(value = ServiceApi.TJ.TjDataSourceExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    boolean hasExistsName(@PathVariable("name") String name);

    @RequestMapping(value = ServiceApi.TJ.TjDataSourceExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    boolean hasExistsCode(@PathVariable("code") String code);
}
