package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrgDept;
import com.yihu.ehr.model.org.MOrganization;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations" , method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建机构")
    MOrganization create(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgModelJsonData ) ;


    @RequestMapping(value =ApiVersion.Version1_0 +  "/organizations" , method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改机构")
    MOrganization update(
            @ApiParam(name = "mOrganizationJsonData", value = "机构代码", defaultValue = "")
            @RequestBody String orgModelJsonData ) ;


    @RequestMapping(value = ApiVersion.Version1_0 + "/organizations/images" , method = RequestMethod.POST)
    @ApiOperation(value = "机构资质图片上传")
    public String uploadPicture(
            @ApiParam(name = "jsonData", value = "jsonData", defaultValue = "")
            @RequestBody String jsonData);
    @RequestMapping(value =ApiVersion.Version1_0 + "/organizations/list", method = RequestMethod.POST)
    @ApiOperation(value = "根据条件查询机构列表")
    ResponseEntity<List<MOrganization>> searchOrgs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) ;

    @RequestMapping(value =ApiVersion.Version1_0 + "/orgDept/getOrgDeptByDeptName", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "检查机构下部门相同名称的个数")
    MOrgDept getOrgDeptByDeptName(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) Integer orgId,
            @ApiParam(name = "name", value = "新部门名称")
            @RequestParam(value = "name", required = true) String name);

}
