package com.yihu.ehr.api.adaption;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * 标准适配。标准适配分为两个方面：机构数据标准与适配方案。前者用于定义医疗机构信息化系统的数据视图，
 * 后者根据此视图与平台的健康档案数据标准进行匹配与映射。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/adaptions")
@Api(protocols = "https", value = "adaptions", description = "标准适配")
public class AdaptionController {

    @ApiOperation(value = "获取机构数据标准", produces = "application/gzip")
    @RequestMapping(value = "/organization/{org_code}/standard", method = {RequestMethod.GET})
    public Object getOrgSchema(@ApiParam(name = "org_code", value = "机构代码")
                               @PathVariable(value = "org_code") String orgCode) {
        return null;
    }

    @RequestMapping(value = "/organization/{org_code}/adaption", method = {RequestMethod.GET})
    @ApiOperation(value = "获取机构适配方案", produces = "application/gzip")
    public Object getOrgAdaption(@ApiParam(name = "org_code", value = "机构代码")
                                 @PathVariable(value = "org_code") String orgCode) {
        return null;
    }

    @RequestMapping(value = "/organization/{org_code}", method = {RequestMethod.GET})
    @ApiOperation(value = "获取机构所有适配方案", produces = "application/gzip")
    public Object getOrgAdaptions(@ApiParam(name = "org_code", value = "机构代码")
                                  @PathVariable(value = "org_code") String orgCode) {
        return "{message: Hello}";
    }
}
