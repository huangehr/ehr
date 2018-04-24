package com.yihu.ehr.basic.address.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.address.service.AddressService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.entity.address.Address;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.id.BizObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AddressEndPoint", description = "获取常用地址，根据选择地址判断数据库中是否存在，否则保存为新地址", tags = {"基础信息-地址信息管理"})
public class AddressEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private AddressService geographyService;

    @RequestMapping(value = ServiceApi.Geography.Address, method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号查询地址")
    public MGeography getAddressById(
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Address address =  geographyService.getAddressById(id);
        return convertToModel(address, MGeography.class);
    }


    @RequestMapping(value = ServiceApi.Geography.AddressCanonical, method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号获取地址中文字符串全拼")
    public String getCanonicalAddress(
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Address address = geographyService.getAddressById(id);
        String addressStr = "";
        if(address != null){
            addressStr = geographyService.getCanonicalAddress(address);
        }
        return addressStr;
    }

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = ServiceApi.Geography.Geographies, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "地址检查,如果地址在数据库中不存在，这新增这条记录，否则返回地址id")
    public String saveAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestBody String jsonData) throws Exception{
//        ObjectMapper objectMapper = new ObjectMapper();
//        Geography geography = objectMapper.readValue(jsonData, Geography.class);
        Address geography = toEntity(jsonData,Address.class);
        if (geography.getCountry() == null) {
            geography.setCountry("中国");
        }
        List<Address> geographies = geographyService.isGeographyExist(geography);
        if(geographies==null || geographies.size()==0){
            geography.setId(getObjectId(BizObject.Geography));
            String addressId = geographyService.saveAddress(geography);
            return addressId;
        }else {
            return geographies.get(0).getId();
        }

    }

    /**
     * 根据省市县查询地址
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = ServiceApi.Geography.Geographies , method = RequestMethod.GET)
    @ApiOperation(value = "根据省市县查询地址并返回地址编号列表")
    public List<String> search(
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province",required = true) String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city",required = false) String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district",required = false) String district) {
        List<String> idList =  geographyService.search(province,city,district);
        return idList;
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping(value = ServiceApi.Geography.GeographiesDelete , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据地址编号删除地址")
    public boolean delete(
            @ApiParam(name = "id" , value = "地址代码" ,defaultValue = "")
            @PathVariable (value = "id") String id) {
        Address address = geographyService.getAddressById(id);
        if(address == null){
            throw new ApiException(ErrorCode.NOT_FOUND, "获取地址失败");
        }
        geographyService.deleteAddress(address);
        return true;
    }


    @RequestMapping(value = ServiceApi.Geography.GeographiesNull , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个空地址")
    public boolean isNullAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestParam( value = "json_data") String jsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Address geography = objectMapper.readValue(jsonData,Address.class);
        return geographyService.isNullAddress(geography);
    }

}
