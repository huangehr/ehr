package com.yihu.ehr.geography.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ObjectResult;
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
 * Created by AndyCai on 2016/1/20.
 */
@FeignClient(name=MicroServices.Geography)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface AddressClient {

    @RequestMapping(value = "/geography_entries/level/{level}", method = RequestMethod.GET)
    @ApiOperation(value = "根据等级查询行政区划地址")
    List<MGeographyDict> getAddressByLevel(
            @ApiParam(name = "level", value = "地址级别", defaultValue = "")
            @PathVariable(value = "level") Integer level) ;

    @RequestMapping(value = "/geography_entries/pid/{pid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据上级编号查询行政区划地址")
    List<MGeographyDict> getAddressDictByPid(
            @ApiParam(name = "pid", value = "上级id", defaultValue = "")
            @PathVariable(value = "pid") Integer pid) ;


    @RequestMapping(value = "/geographies/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询地址")
    MGeography getAddressById(
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @PathVariable(value = "id") String id) ;


    @RequestMapping(value = "/geographies/{id}/canonical", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号获取地址中文字符串全拼")
    String getCanonicalAddress(
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @PathVariable(value = "id") String id) ;

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = "/geographies", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "地址检查,如果地址在数据库中不存在，这新增这条记录，否则返回地址id")
    String saveAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestBody String GeographyModelJsonData) ;


    /**
     * 根据省市县查询地址
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = "/geographies" , method = RequestMethod.GET)
    @ApiOperation(value = "根据省市县查询地址并返回地址编号列表")
    List<String> search(
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district") String district) ;

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping(value = "/geographies/{id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除地址")
    boolean delete(
            @ApiParam(name = "/id" , value = "地址代码" ,defaultValue = "")
            @PathVariable (value = "id") String id) ;


    @RequestMapping(value = "/geographies/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    boolean isNullAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestParam( value = "json_data") String jsonData) ;


    @RequestMapping(value = "/geography_entries/getAddressDict" , method = RequestMethod.GET)
    @ApiOperation(value = "根据地址中文名 查询地址编号")
    Collection<MGeographyDict> getAddressDict(
            @ApiParam(name = "fields", value = "fields", defaultValue = "")
            @RequestParam(value = "fields") String[] fields ,
            @ApiParam(name = "values", value = "values", defaultValue = "")
            @RequestParam(value = "values") String[] values);

    @RequestMapping(value = "/geography_entries/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询行政区划地址")
    MGeographyDict getAddressDictById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) ;

    @RequestMapping(value = ServiceApi.Geography.GetAddressNameByCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据地址中文名 查询地址编号")
    ObjectResult getAddressNameByCode(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name);

}
