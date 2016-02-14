package com.yihu.ehr.address.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.address.service.Geography;
import com.yihu.ehr.address.service.GeographyDict;
import com.yihu.ehr.address.service.GeographyService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.address.MGeography;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "geography", description = "通用地址接口,维护常用地址，用户产生新地址增加新地址，通常情况下地址不做为删除", tags = {"地址","地址字典"})
public class GeographyController extends BaseRestController{

    @Autowired
    private GeographyService addressService;

    /**
     * 根据地址等级查询地址信息
     * @param level
     * @return
     */
    @RequestMapping(value = "/geographies/{level}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址等级查询地址信息")
    public Object getAddressByLevel(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "level", value = "地址级别", defaultValue = "")
            @PathVariable(value = "level") Integer level) {
        List<GeographyDict> addressDictList = addressService.getLevelToAddr(level);
        Map<Integer, String> parentMap = new HashMap<>();
        for (GeographyDict addressDict : addressDictList) {
            parentMap.put(addressDict.getId(), addressDict.getName());
        }
        return parentMap;
    }

    @RequestMapping(value = "/geographies/{pid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据父id查询地址信息")
    public Object getAddressDictByPid(
        @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
        @PathVariable( value = "api_version") String apiVersion,
        @ApiParam(name = "pid", value = "上级id", defaultValue = "")
        @PathVariable(value = "pid") Integer pid) {

        List<GeographyDict> addressDictList = addressService.getPidToAddr(pid);
        Map<Integer, String> childMap = new HashMap<>();
        for (GeographyDict addressDict : addressDictList) {
            childMap.put(addressDict.getId(), addressDict.getName());
        }
        return childMap;
    }


    @RequestMapping(value = "/geographies/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询地址")
    public MGeography getAddressById(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Geography address =  addressService.getAddressById(id);
        return convertToModel(address, MGeography.class);
    }


    @RequestMapping(value = "geographies/canonical/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号获取地址")
    public String getCanonicalAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Geography address = addressService.getAddressById(id);
        String addressStr = addressService.getCanonicalAddress(address);
        return addressStr;
    }

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = "/geographies", method = RequestMethod.POST)
    @ApiOperation(value = "地址检查,如果地址在数据库中不存在，这新增这条记录，否则返回地址id")
    public String saveAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "geography_model_json_data", value = "地址json字符串")
            @RequestParam( value = "geography_model_json_data") String GeographyModelJsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Geography geography = objectMapper.readValue(GeographyModelJsonData, Geography.class);
        String addressId = addressService.saveAddress(geography);
        return addressId;
    }


    /**
     * 根据省市县查询地址
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = "/geographies/{province}/{city}/{district}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据省市县查询地址并返回地址编号列表")
    public List<String> search(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @PathVariable(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @PathVariable(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @PathVariable(value = "district") String district) {
        List<String> idList =  addressService.search(province,city,district);
        return idList;
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping(value = "geographies/{id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除地址")
    public boolean delete(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "/id" , value = "地址代码" ,defaultValue = "")
            @PathVariable (value = "id") String id) {
        Geography address = addressService.getAddressById(id);
        if(address!=null){
            addressService.deleteAddress(address);
        }
        return true;
    }


    @RequestMapping(value = "/geographies/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    public boolean isNullAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "geography_model_json_data", value = "地址json字符串")
            @RequestParam( value = "geography_model_json_data") String geographyModelJsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Geography geography = objectMapper.readValue(geographyModelJsonData,Geography.class);
        return addressService.isNullAddress(geography);
    }

}
