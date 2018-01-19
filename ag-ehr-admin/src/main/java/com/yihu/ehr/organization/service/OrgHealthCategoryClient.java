package com.yihu.ehr.organization.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 卫生机构类别 Client
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@FeignClient(name = MicroServices.Organization)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface OrgHealthCategoryClient {

    @ApiOperation("根据ID获取卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation("获取所有的卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.FindAll, method = RequestMethod.GET)
    public Envelop findAll();

    @ApiOperation(value = "根据条件获取卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "codeName", value = "卫生机构类别编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName);

    @ApiOperation("新增卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "卫生机构类别JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("更新卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "卫生机构类别JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("删除卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证卫生机构类别编码是否唯一")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "卫生机构类别编码", required = true)
            @RequestParam(value = "code") String code);

    @ApiOperation("验证卫生机构类别名称是否唯一")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "卫生机构类别名称", required = true)
            @RequestParam(value = "name") String name);

}
