package com.yihu.ehr.org.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.DictMgr)
@RequestMapping(value = "/rest/v1.0", method = GET )
@ApiIgnore
@Api(protocols = "https", value = "dict", hidden = true , description = "获取常用字典项", tags = {"惯用字典"})
public interface ConventionalDictClient {

    @RequestMapping(value = "/dictionaries/org_type", method = GET )
    @ApiOperation(value = "根据前端传回来的json创建一个人口信息")
    MConventionalDict getOrgType(
            @RequestParam(value = "code") String code);


    @RequestMapping(value = "/dictionaries/settled_way", method = GET )
    MConventionalDict getSettledWay(
            @RequestParam(value = "code") String code);



}
