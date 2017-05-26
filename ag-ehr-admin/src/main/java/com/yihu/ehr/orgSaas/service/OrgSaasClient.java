package com.yihu.ehr.orgSaas.service;

import com.yihu.ehr.agModel.orgSaas.OrgSaasModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.orgSaas.MAreaSaas;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * Created by zdm on 2017/5/26.
 */
@FeignClient(name=MicroServices.Geography)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgSaasClient {


    @ApiOperation(value = "根据机构获取机构相关授权")
    @RequestMapping(value = "/OrgSaasByOrg", method = RequestMethod.GET)
    ListResult getOrgSaasByorgCode(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type) ;


}
