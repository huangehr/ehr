package com.yihu.ehr.geography.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.geography.service.Geography;
import com.yihu.ehr.geography.service.GeographyService;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "Geography", description = "获取常用地址，根据选择地址判断数据库中是否存在，否则保存为新地址", tags = {"地址"})
public class GeographyController extends BaseRestController{

    @Autowired
    private GeographyService geographyService;

    @RequestMapping(value = "/geographies/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号查询地址")
    public MGeography getAddressById(
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Geography address =  geographyService.getAddressById(id);
        return convertToModel(address, MGeography.class);
    }


    @RequestMapping(value = "geographies/{id}/canonical", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号获取地址中文字符串全拼")
    public String getCanonicalAddress(
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Geography address = geographyService.getAddressById(id);
        String addressStr = geographyService.getCanonicalAddress(address);
        return addressStr;
    }

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = "/geographies", method = RequestMethod.POST)
    @ApiOperation(value = "地址检查,如果地址在数据库中不存在，这新增这条记录，否则返回地址id")
    public String saveAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestParam( value = "json_data") String jsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Geography geography = objectMapper.readValue(jsonData, Geography.class);
        if (geography.getCountry() == null) {
            geography.setCountry("中国");
        }
        List<Geography> geographys = geographyService.isGeographyExist(geography);
        if(geographys.size()==0){
            geography.setId(getObjectId(BizObject.Geography));
            String addressId = geographyService.saveAddress(geography);
            return addressId;
        }else {
            return geographys.get(0).getId();
        }

    }


    /**
     * 根据省市县查询地址
     * @param province
     * @param city
     * @param district
     * @return
     */
    @RequestMapping(value = "/geographies" , method = RequestMethod.GET)
    @ApiOperation(value = "根据省市县查询地址并返回地址编号列表")
    public List<String> search(
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district") String district) {
        List<String> idList =  geographyService.search(province,city,district);
        return idList;
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping(value = "geographies/{id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据地址编号删除地址")
    public boolean delete(
            @ApiParam(name = "/id" , value = "地址代码" ,defaultValue = "")
            @PathVariable (value = "id") String id) {
        Geography address = geographyService.getAddressById(id);
        if(address==null){
            throw new ApiException(ErrorCode.GetGeographyFailed,"获取地址失败");
        }
        geographyService.deleteAddress(address);
        return true;
    }


    @RequestMapping(value = "/geographies/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    public boolean isNullAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestParam( value = "json_data") String jsonData) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        Geography geography = objectMapper.readValue(jsonData,Geography.class);
        return geographyService.isNullAddress(geography);
    }

}
