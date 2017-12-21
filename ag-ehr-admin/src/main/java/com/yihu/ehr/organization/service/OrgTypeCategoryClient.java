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
 * 机构类型管理 Client
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@FeignClient(name = MicroServices.Organization)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface OrgTypeCategoryClient {

    @ApiOperation("根据ID获取机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation("获取所有的机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.FindAll, method = RequestMethod.GET)
    public Envelop findAll();

    @ApiOperation(value = "根据条件获取机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "codeName", value = "机构类型编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName);

    @ApiOperation("新增机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "机构类型JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("更新机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "机构类型JSON", required = true)
            @RequestBody String entityJson);

    @ApiOperation("删除机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "机构类型ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证机构类型编码是否唯一")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "机构类型ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "机构类型编码", required = true)
            @RequestParam(value = "code") String code);

    @ApiOperation("验证机构类型名称是否唯一")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "机构类型ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "机构类型名称", required = true)
            @RequestParam(value = "name") String name);

}
