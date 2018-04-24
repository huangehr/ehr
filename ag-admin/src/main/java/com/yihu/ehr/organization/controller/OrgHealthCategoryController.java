package com.yihu.ehr.organization.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.organization.service.OrgHealthCategoryClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 卫生机构类别 Controller
 *
 * @author 张进军
 * @date 2017/12/21 12:00
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "卫生机构类别接口", tags = {"机构管理--卫生机构类别接口"})
public class OrgHealthCategoryController extends BaseController {

    @Autowired
    private OrgHealthCategoryClient orgHealthCategoryClient;

    @ApiOperation("根据ID获取卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        return orgHealthCategoryClient.getById(id);
    }

    @ApiOperation("获取所有的卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.FindAll, method = RequestMethod.GET)
    public Envelop findAll() {
        return orgHealthCategoryClient.findAll();
    }

    @ApiOperation(value = "根据条件获取卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "codeName", value = "卫生机构类别编码或名称")
            @RequestParam(value = "codeName", required = false) String codeName) {
        return orgHealthCategoryClient.search(codeName);
    }

    @ApiOperation("新增卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "卫生机构类别JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return orgHealthCategoryClient.add(entityJson);
    }

    @ApiOperation("更新卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "卫生机构类别JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return orgHealthCategoryClient.update(entityJson);
    }

    @ApiOperation("删除卫生机构类别")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id) {
        return orgHealthCategoryClient.delete(id);
    }

    @ApiOperation("验证卫生机构类别编码是否唯一")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueChannel(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "卫生机构类别编码", required = true)
            @RequestParam(value = "code") String code) {
        return orgHealthCategoryClient.isUniqueCode(id, code);
    }

    @ApiOperation("验证卫生机构类别名称是否唯一")
    @RequestMapping(value = ServiceApi.Org.HealthCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueChannelName(
            @ApiParam(name = "id", value = "卫生机构类别ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "卫生机构类别名称", required = true)
            @RequestParam(value = "name") String name) {
        return orgHealthCategoryClient.isUniqueName(id, name);
    }

}
