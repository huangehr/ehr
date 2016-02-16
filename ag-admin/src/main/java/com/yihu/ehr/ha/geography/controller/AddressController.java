package com.yihu.ehr.ha.geography.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.ha.geography.service.AddressClient;
import com.yihu.ehr.model.address.MGeography;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by AndyCai on 2016/1/20.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0)
@RestController
@Api(value = "address", description = "地址信息管理接口，用于地址信息管理", tags = {"地址信息管理接口"})
public class AddressController extends BaseRestController {
    @Autowired
    private AddressClient addressClient;

    @RequestMapping(value = "/geographies/{level}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址等级查询地址信息")
    public Object getAddressByLevel(
            @ApiParam(name = "level", value = "地址级别", defaultValue = "")
            @PathVariable(value = "level") Integer level) {
       return addressClient.getAddressByLevel(level);
    }

    @RequestMapping(value = "/geographies/{pid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据父id查询地址信息")
    public Object getAddressDictByPid(
            @ApiParam(name = "pid", value = "上级id", defaultValue = "")
            @PathVariable(value = "pid") Integer pid) {

        return addressClient.getAddressDictByPid(pid);
    }


    @RequestMapping(value = "/geographies/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询地址")
    public MGeography getAddressById(
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        return addressClient.getAddressById(id);
    }


    @RequestMapping(value = "geographies/canonical/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号获取地址")
    public String getCanonicalAddress(
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @PathVariable(value = "id") String id) {
        return addressClient.getCanonicalAddress(id);
    }

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = "/geographies", method = RequestMethod.POST)
    @ApiOperation(value = "地址检查,如果地址在数据库中不存在，这新增这条记录，否则返回地址id")
    public String saveAddress(
            @ApiParam(name = "geography_model_json_data", value = "地址json字符串")
            @RequestParam( value = "geography_model_json_data") String geographyModelJsonData) throws Exception{
        return addressClient.saveAddress(geographyModelJsonData);
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
        return addressClient.search(province,city,district);
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping(value = "geographies/{id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除地址")
    public boolean delete(
            @ApiParam(name = "/id" , value = "地址代码" ,defaultValue = "")
            @PathVariable (value = "id") String id) {
        return addressClient.delete(id);
    }


    @RequestMapping(value = "/geographies/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    public boolean isNullAddress(
            @ApiParam(name = "geography_model_json_data", value = "地址json字符串")
            @RequestParam( value = "geography_model_json_data") String geographyModelJsonData) throws Exception{
        return addressClient.isNullAddress(geographyModelJsonData);
    }

}
