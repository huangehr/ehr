package com.yihu.ehr.address.controller;

import com.yihu.ehr.address.service.*;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.address.MAddress;
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
@RequestMapping(ApiVersionPrefix.CommonVersion + "/address")
@Api(protocols = "https", value = "address", description = "通用地址接口", tags = {"地址","地址字典"})
public class AddressController {

    @Autowired
    private AddressService addressService;


    /**
     * 根据地址等级查询地址信息
     * @param level
     * @return
     */
    @RequestMapping(value = "getParent", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址等级查询地址信息")
    public Object getParent(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "level", value = "地址级别", defaultValue = "")
            @RequestParam(value = "level") Integer level) {
        List<AddressDict> addressDictList = addressService.getLevelToAddr(level);
        Map<Integer, String> parentMap = new HashMap<>();
        for (AddressDict addressDict : addressDictList) {
            parentMap.put(addressDict.getId(), addressDict.getName());
        }
        return parentMap;
    }

    @RequestMapping(value = "getChildByParent", method = RequestMethod.GET)
    @ApiOperation(value = "根据父id查询地址信息")
    public Object getChildByParent(
        @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
        @PathVariable( value = "api_version") String apiVersion,
        @ApiParam(name = "pid", value = "上级id", defaultValue = "")
        @RequestParam(value = "pid") Integer pid) {

        List<AddressDict> addressDictList = addressService.getPidToAddr(pid);
        Map<Integer, String> childMap = new HashMap<>();
        for (AddressDict addressDict : addressDictList) {
            childMap.put(addressDict.getId(), addressDict.getName());
        }
        return childMap;
    }

    //--------------------------------------------------------------------

    @RequestMapping(value = "/address", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询地址信息")
    public Object getAddressById(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @RequestParam(value = "id") String id) {

        MAddress model = new MAddress();
        Address address =  addressService.getAddressById(id);
        if(address!=null){
            model.setId(address.getId());
            model.setCountry(address.getCountry());
            model.setProvince(address.getProvince());
            model.setCity(address.getCity());
            model.setDistrict(address.getDistrict());
            model.setTown(address.getTown());
            model.setStreet(address.getStreet());
            model.setExtra(address.getExtra());
            model.setPostalCode(address.getPostalCode());
        }
        return model;
    }


    @RequestMapping(value = "/canonicalAddress", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址代码获取机构详细信息")
    public Object getCanonicalAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @RequestParam(value = "id") String id) {
        Address address = addressService.getAddressById(id);
        String addressStr = addressService.getCanonicalAddress(address);
        return addressStr;
    }

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    @ApiOperation(value = "地址检查并保存")
    public Object saveAddress(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "country", value = "国家" , defaultValue = "")
            @RequestParam(value = "country") String country,
            @ApiParam(name = "province", value = "省" , defaultValue = "")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district") String district,
            @ApiParam(name = "town", value = "", defaultValue = "")
            @RequestParam(value = "town") String town,
            @ApiParam(name = "street", value = "街道", defaultValue = "")
            @RequestParam(value = "street") String street,
            @ApiParam(name = "extra", value = "其他", defaultValue = "")
            @RequestParam(value = "extra") String extra,
            @ApiParam(name = "postalCode", value = "行政编码", defaultValue = "")
            @RequestParam(value = "postalCode") String postalCode) {

        Address address = new Address();

        address.setCountry(country);
        address.setCity(city);
        address.setProvince(province);
        address.setDistrict(district);
        address.setTown(town);
        address.setStreet(street);
        address.setExtra(extra);
        address.setPostalCode(postalCode);
        addressService.saveAddress(address);
        return address.getId();
    }


    /**
     * 根据省市县查询地址
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = "/search" , method = RequestMethod.GET)
    @ApiOperation(value = "根据省市县查询地址")
    public Object search(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district") String district) {

        List<String> idList =  addressService.search(province,city,district);
        if(idList.size()>0){
            return idList;
        }else{
            return null;
        }
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping(value = "/address" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除地址")
    public Object delete(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "/id" , value = "地址代码" ,defaultValue = "")
            @RequestParam (value = "id") String id) {
        Address address = addressService.getAddressById(id);
        if(address!=null){
            addressService.deleteAddres(address);
        }
        return "删除地址成功！";
    }


}
