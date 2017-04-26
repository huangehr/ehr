package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name = MicroServices.Geography)
@ApiIgnore
public interface GeographyClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/geographies/{id}", method = RequestMethod.GET)
    MGeography getAddressById(@PathVariable(value = "id") String id);

    @RequestMapping(value = ApiVersion.Version1_0 + "/geographies", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String saveAddress(@RequestBody String jsonData);

    @RequestMapping(value = ApiVersion.Version1_0 + "/geographies", method = RequestMethod.GET)
    List<String> search(
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "district") String district);

    @RequestMapping(value = "/geography_entries/getAddressDict" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址中文名 查询地址编号")
    Collection<MGeographyDict> getAddressDict(
            @ApiParam(name = "fields", value = "fields", defaultValue = "")
            @RequestParam(value = "fields") String[] fields ,
            @ApiParam(name = "values", value = "values", defaultValue = "")
            @RequestParam(value = "values") String[] values);

}
