package com.yihu.ehr.geography.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.geography.service.GeographyDict;
import com.yihu.ehr.geography.service.GeographyDictService;
import com.yihu.ehr.model.geography.MGeographyDict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Geography-Dict", description = "行政区划地址", tags = {"行政区划地址"})
public class GeographyDictEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private GeographyDictService geographyDictService;

    /**
     * 根据地址等级查询地址信息
     * @param level
     * @return
     */
    @RequestMapping(value = "/geography_entries/level/{level}", method = RequestMethod.GET)
    @ApiOperation(value = "根据等级查询行政区划地址")
    public Collection<MGeographyDict> getAddressByLevel(
            @ApiParam(name = "level", value = "等级", defaultValue = "")
            @PathVariable(value = "level") Integer level) {
        List<GeographyDict> addressDictList = geographyDictService.getLevelToAddr(level);
        return convertToModels(addressDictList,new ArrayList<>(addressDictList.size()), MGeographyDict.class,"");
    }

    @RequestMapping(value = "/geography_entries/pid/{pid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据上级编号查询行政区划地址")
    public Collection<MGeographyDict> getAddressDictByPid(
        @ApiParam(name = "pid", value = "上级id", defaultValue = "")
        @PathVariable(value = "pid") Integer pid) {
        List<GeographyDict> addressDictList = geographyDictService.getPidToAddr(pid);
        return convertToModels(addressDictList,new ArrayList<>(addressDictList.size()), MGeographyDict.class,"");
    }


    @RequestMapping(value = "/geography_entries/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询行政区划地址")
    public MGeographyDict getAddressDictById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        GeographyDict geographyDict =  geographyDictService.findById(id);
        return convertToModel(geographyDict, MGeographyDict.class);
    }

    @RequestMapping(value = "/geography_entries/CacheAddressDict", method = RequestMethod.POST)
    @ApiOperation(value = "缓存行政区划地址")
    public boolean CacheAddressDict( ){
       return   geographyDictService.CacheAddressDict();
    }

    @RequestMapping(value = "/geography_entries/GetAddressDictCache/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取缓存行政区划地址")
    public String GetAddressDictCache(@ApiParam(name = "id", value = "id", defaultValue = "")
                                           @PathVariable(value = "id") String id ){
        return   geographyDictService.GetAddressDictCache(id);
    }


}
