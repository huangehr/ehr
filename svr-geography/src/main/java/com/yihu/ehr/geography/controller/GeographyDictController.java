package com.yihu.ehr.geography.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.geography.service.GeographyDict;
import com.yihu.ehr.geography.service.GeographyDictService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "Geography-Dict", description = "行政区划地址", tags = {"行政区划地址"})
public class GeographyDictController extends BaseRestController{

    @Autowired
    private GeographyDictService geographyDictService;

    /**
     * 根据地址等级查询地址信息
     * @param level
     * @return
     */
    @RequestMapping(value = "/geography_entries/{level}", method = RequestMethod.GET)
    @ApiOperation(value = "根据等级查询行政区划地址")
    public List<GeographyDict> getAddressByLevel(
            @ApiParam(name = "level", value = "等级", defaultValue = "")
            @PathVariable(value = "level") Integer level) {
        List<GeographyDict> addressDictList = geographyDictService.getLevelToAddr(level);
        return addressDictList;
    }

    @RequestMapping(value = "/geography_entries/{pid}", method = RequestMethod.GET)
    @ApiOperation(value = "根据上级编号查询行政区划地址")
    public List<GeographyDict> getAddressDictByPid(
        @ApiParam(name = "pid", value = "上级id", defaultValue = "")
        @PathVariable(value = "pid") Integer pid) {
        List<GeographyDict> addressDictList = geographyDictService.getPidToAddr(pid);
        return addressDictList;
    }

}
