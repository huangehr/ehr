package com.yihu.ehr.api.legacy;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * 历史遗留接口，以rest开头。这些接口只被集成开放平台使用，目前先整合到这边来。待所有版本均升级好之后，可以去掉此接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 10:13
 */
@RestController
@RequestMapping("/rest/v1.0")
public class LegacyController {
    @ApiOperation(value = "下载标准版本", response = String.class)
    @RequestMapping(value = "/adapter-dispatcher/versionplan", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String getVersion() {
        return null;
    }

    @ApiOperation(value = "下载标准版本列表", response = String.class)
    @RequestMapping(value = "/adapter-dispatcher/allSchemaMappingPlan", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String getAllVerions() {
        return null;
    }

    @ApiOperation(value = "机构数据元", response = String.class)
    @RequestMapping(value = "/adapter-dispatcher/schema", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String downloadOrgMeta() {
        return null;
    }

    @ApiOperation(value = "标准映射", response = String.class)
    @RequestMapping(value = "adapter-dispatcher/schemaMappingPlan", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String downloadMapper() {
        return null;
    }

    @ApiOperation(value = "注册病人", response = String.class)
    @RequestMapping(value = "/patient/registration", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String registerPatient() {
        return null;
    }

    @ApiOperation(value = "上传档案包", response = String.class)
    @RequestMapping(value = "/json_package", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String uploadPackage() {
        return null;
    }

    @ApiOperation(value = "公钥", response = String.class)
    @RequestMapping(value = "/user_key", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String getUserKey() {
        return null;
    }

    @ApiOperation(value = "临时Token", response = String.class)
    @RequestMapping(value = "/token", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String getAppToken() {
        return null;
    }
}
