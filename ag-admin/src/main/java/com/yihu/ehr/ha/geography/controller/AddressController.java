package com.yihu.ehr.ha.geography.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.geography.service.AddressClient;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.model.geogrephy.MGeographyDict;
import com.yihu.ehr.agModel.geogrephy.GeographyDictModel;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.lang.management.BufferPoolMXBean;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/20.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0)
@RestController
@Api(value = "address", description = "地址信息管理接口，用于地址信息管理", tags = {"地址信息管理接口"})
public class AddressController {
    @Autowired
    private AddressClient addressClient;

    @RequestMapping(value = "/geography_entries/level/{level}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址等级查询地址字典")
    public Envelop getAddressByLevel(
            @ApiParam(name = "level", value = "地址级别", defaultValue = "")
            @PathVariable(value = "level") Integer level) {

        Envelop envelop = new Envelop();

        List<MGeographyDict> mGeographyDictList = addressClient.getAddressByLevel(level);

        if(mGeographyDictList.size()>0){
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(mGeographyDictList);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("根据地址等级查询地址字典失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/geography_entries/pid/{pid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据上级编号查询行政区划地址")
    public Envelop getAddressDictByPid(
            @ApiParam(name = "pid", value = "上级id", defaultValue = "")
            @PathVariable(value = "pid") Integer pid) {

        Envelop envelop = new Envelop();

        List<MGeographyDict> mGeographyDictList = addressClient.getAddressDictByPid(pid);

        if(mGeographyDictList.size()>0){
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(mGeographyDictList);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("根据上级编号查询行政区划地址失败");
        }

        return envelop;
    }


    @RequestMapping(value = "/geographies/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询地址")
    public Envelop getAddressById(
            @ApiParam(name = "id", value = "地址编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();

        MGeography mGeography = addressClient.getAddressById(id);

        if(mGeography != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(mGeography);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("地址查询失败");
        }

        return envelop;
    }


    @RequestMapping(value = "geographies/canonical/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据地址编号获取地址中文字符串全拼")
    public Envelop getCanonicalAddress(
            @ApiParam(name = "id", value = "地址代码", defaultValue = "")
            @PathVariable(value = "id") String id) {

        Envelop envelop = new Envelop();
        GeographyDictModel geographyDictModel = new GeographyDictModel();

        String address = addressClient.getCanonicalAddress(id);

        if(address != null){
            geographyDictModel.setName(address);
            envelop.setSuccessFlg(true);
            envelop.setObj(geographyDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("地址查询失败");
        }

        return envelop;
    }

    /**
     * 地址检查并保存
     * @return
     */
    @RequestMapping(value = "/geographies", method = RequestMethod.POST)
    @ApiOperation(value = "地址检查,如果地址在数据库中不存在，这新增这条记录，否则返回地址id")
    public Envelop saveAddress(
            @ApiParam(name = "geography_model_json_data", value = "地址json字符串")
            @RequestParam( value = "geography_model_json_data") String geographyModelJsonData) throws Exception{

        Envelop envelop = new Envelop();
        GeographyDictModel geographyDictModel = new GeographyDictModel();

        String id = addressClient.saveAddress(geographyModelJsonData);

        if(id != null){
            envelop.setSuccessFlg(true);
            geographyDictModel.setId(id);
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("地址新增失败");
        }
        envelop.setObj(geographyDictModel);

        return envelop;
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
    public Envelop search(
            @ApiParam(name = "province", value = "省", defaultValue = "")
            @RequestParam(value = "province") String province,
            @ApiParam(name = "city", value = "市", defaultValue = "")
            @RequestParam(value = "city") String city,
            @ApiParam(name = "district", value = "县", defaultValue = "")
            @RequestParam(value = "district") String district) {
        Envelop envelop = new Envelop();

        List<String> mGeographyList = addressClient.search(province,city,district);

        if(mGeographyList.size()>0){
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(mGeographyList);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("地址查询失败");
        }

        return envelop;
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping(value = "geographies/{id}" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除地址")
    public Envelop delete(
            @ApiParam(name = "/id" , value = "地址代码" ,defaultValue = "")
            @PathVariable (value = "id") String id) {

        Envelop envelop = new Envelop();

        Boolean bo = addressClient.delete(id);

        if(bo){
            envelop.setSuccessFlg(true);
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("地址删除失败");
        }

        return envelop;
    }


    @RequestMapping(value = "/geographies/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是个地址")
    public Envelop isNullAddress(
            @ApiParam(name = "json_data", value = "地址json字符串")
            @RequestParam( value = "json_data") String jsonData) throws Exception{

        Envelop envelop = new Envelop();

        Boolean bo = addressClient.isNullAddress(jsonData);
        envelop.setSuccessFlg(bo);

        return envelop;
    }

}
