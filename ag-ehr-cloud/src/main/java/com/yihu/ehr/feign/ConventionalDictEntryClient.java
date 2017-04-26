package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient(name=MicroServices.Dictionary)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
 public interface ConventionalDictEntryClient {

    @RequestMapping(value = "/dictionaries/org_type", method = RequestMethod.GET)
    @ApiOperation(value = "获取组织机构类别字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getOrgType(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/settled_way", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构入驻方式字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getSettledWay(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/martial_status", method = RequestMethod.GET)
    @ApiOperation(value = "获取婚姻状态字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getMartialStatus(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/nation", method = RequestMethod.GET)
    @ApiOperation(value = "获取国家字典项", response = MConventionalDict.class, produces = "application/json")
    MConventionalDict getNation(
            @ApiParam(name = "code", value = "字典代码", defaultValue = "")
            @RequestParam(value = "code") String code);


}
