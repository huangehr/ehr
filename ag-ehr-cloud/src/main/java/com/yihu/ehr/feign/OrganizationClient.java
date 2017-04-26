package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.org.MOrganization;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.03 15:01
 */
@ApiIgnore
@FeignClient(name = MicroServices.Organization)
public interface OrganizationClient {

    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations/{org_code}", method = GET)
    MOrganization getOrg(@PathVariable(value = "org_code") String orgCode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations/admin/{admin_login_code}", method = GET)
    MOrganization getOrgByAdminLoginCode(@PathVariable(value = "admin_login_code") String adminLoginCode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations/list", method = GET)
    List<MOrganization> search(
            @RequestParam(value = "fields") String fields,
            @RequestParam(value = "filters") String filters,
            @RequestParam(value = "sorts") String sorts,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    /**
     * 创建机构
     * @param orgModelJsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/organizations" , method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建机构")
    MOrganization create(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgModelJsonData ) ;



    @RequestMapping(value = "/organizations" , method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改机构")
    MOrganization update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgModelJsonData ) ;


    @RequestMapping(value = "/organizations/images" , method = RequestMethod.POST)
    @ApiOperation(value = "机构资质图片上传")
    public String uploadPicture(
            @ApiParam(name = "jsonData", value = "jsonData", defaultValue = "")
            @RequestBody String jsonData);

}
