package com.yihu.ehr.organization.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.organization.service.OrgTypeCategoryClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 机构类型管理 Controller
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "机构类型管理接口", tags = {"机构管理--机构类型管理接口"})
public class OrgTypeCategoryController extends BaseController {

    @Autowired
    private OrgTypeCategoryClient orgTypeCategoryClient;

    @ApiOperation("根据ID获取机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        return orgTypeCategoryClient.getById(id);
    }

    @ApiOperation("获取所有的机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.FindAll, method = RequestMethod.GET)
    public Envelop findAll() {
        return orgTypeCategoryClient.findAll();
    }

    @ApiOperation(value = "根据条件获取机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "codeName", value = "机构类型编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName) {
        return orgTypeCategoryClient.search(codeName);
    }

    @ApiOperation("新增机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "机构类型JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return orgTypeCategoryClient.add(entityJson);
    }

    @ApiOperation("更新机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "机构类型JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return orgTypeCategoryClient.update(entityJson);
    }

    @ApiOperation("删除机构类型")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "机构类型ID", required = true)
            @RequestParam(value = "id") Integer id) {
        return orgTypeCategoryClient.delete(id);
    }

    @ApiOperation("验证机构类型编码是否唯一")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueChannel(
            @ApiParam(name = "id", value = "机构类型ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "机构类型编码", required = true)
            @RequestParam(value = "code") String code) {
        return orgTypeCategoryClient.isUniqueCode(id, code);
    }

    @ApiOperation("验证机构类型名称是否唯一")
    @RequestMapping(value = ServiceApi.Org.TypeCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueChannelName(
            @ApiParam(name = "id", value = "机构类型ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "机构类型名称", required = true)
            @RequestParam(value = "name") String name) {
        return orgTypeCategoryClient.isUniqueName(id, name);
    }

}
