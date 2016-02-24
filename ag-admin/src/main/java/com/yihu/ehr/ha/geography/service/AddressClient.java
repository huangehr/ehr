package com.yihu.ehr.ha.geography.service;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.model.geogrephy.MGeographyDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by AndyCai on 2016/1/20.
 */
@FeignClient(MicroServices.Geography)
public interface AddressClient {

    @RequestMapping(value = "/rest/v1.0/geographies/{level}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址等级查询地址字典")
    List<MGeographyDict> getAddressByLevel(
            @ApiParam(name = "level", value = "地址级别", defaultValue = "")
            @PathVariable(value = "level") Integer level) ;

    @RequestMapping(value = "/rest/v1.0/geographies/{pid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据父id查询地址字典")
    List<MGeographyDict> getAddressDictByPid(
            @ApiParam(name = "pid", value = "上级id", defaultValue = "")
            @PathVariable(value = "pid") Integer pid) ;


    @RequestMapping(value = "/rest/v1.0/geographies/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询地址")
    MGeography getAddressById(
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @PathVariable(value = "id") String id) ;


    @RequestMapping(value = "/rest/v1.0/geographies/canonical/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号获取地址中文字符串全拼")
    String getCanonicalAddress(
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @PathVariable(value = "id") String id) ;

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = "/rest/v1.0/geographies", method = RequestMethod.POST)
    @ApiOperation(value = "地址检查,如果地址在数据库中不存在，这新增这条记录，否则返回地址id")
    String saveAddress(
            @ApiParam(name = "geography_model_json_data", value = "地址json字符串")
            @RequestParam( value = "geography_model_json_data") String GeographyModelJsonData) ;


    /**
     * 根据省市县查询地址
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = "/rest/v1.0/geographies" , method = RequestMethod.GET)
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
    @RequestMapping(value = "/rest/v1.0/geographies/{id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除地址")
    boolean delete(
            @ApiParam(name = "/id" , value = "地址代码" ,defaultValue = "")
            @PathVariable (value = "id") String id) ;


    @RequestMapping(value = "/rest/v1.0/geographies/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    boolean isNullAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestParam( value = "json_data") String jsonData) ;
}
