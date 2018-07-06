package com.yihu.ehr.basic.government.controller;

import com.yihu.ehr.basic.government.entity.GovQuotaCategory;
import com.yihu.ehr.basic.government.service.GovQuotaCategoryService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 政府服务平台首页指标分类 Controller
 *
 * @author 张进军
 * @date 2018/7/5 17:59
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "首页指标分类接口", tags = {"政府服务平台--首页指标分类"})
public class GovQuotaCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private GovQuotaCategoryService govQuotaCategoryService;

    @ApiOperation("根据ID获取政府服务平台首页指标分类")
    @RequestMapping(value = ServiceApi.GovFirsPage.QuotaCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            GovQuotaCategory entity = govQuotaCategoryService.getById(id);
            envelop.setObj(entity);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取政府服务平台首页指标分类。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取政府服务平台首页指标分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取政府服务平台首页指标分类")
    @RequestMapping(value = ServiceApi.GovFirsPage.QuotaCategory.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<GovQuotaCategory> entityList = govQuotaCategoryService.search(fields, filters, sorts, page, size);
            int count = (int) govQuotaCategoryService.getCount(filters);
            envelop = getPageResult(entityList, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取政府服务平台首页指标分类列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取政府服务平台首页指标分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增政府服务平台首页指标分类")
    @RequestMapping(value = ServiceApi.GovFirsPage.QuotaCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "政府服务平台首页指标分类JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            GovQuotaCategory newEntity = objectMapper.readValue(entityJson, GovQuotaCategory.class);
            newEntity = govQuotaCategoryService.save(newEntity);

            envelop.setObj(newEntity);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增政府服务平台首页指标分类。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增政府服务平台首页指标分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新政府服务平台首页指标分类")
    @RequestMapping(value = ServiceApi.GovFirsPage.QuotaCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "政府服务平台首页指标分类JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            GovQuotaCategory updateEntity = objectMapper.readValue(entityJson, GovQuotaCategory.class);
            updateEntity = govQuotaCategoryService.save(updateEntity);

            envelop.setObj(updateEntity);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新政府服务平台首页指标分类。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新政府服务平台首页指标分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除政府服务平台首页指标分类")
    @RequestMapping(value = ServiceApi.GovFirsPage.QuotaCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "政府服务平台首页指标分类ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        govQuotaCategoryService.delete(id);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @ApiOperation("验证政府服务平台首页指标分类编码是否唯一")
    @RequestMapping(value = ServiceApi.GovFirsPage.QuotaCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueChannel(
            @ApiParam(name = "id", value = "政府服务平台首页指标分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "政府服务平台首页指标分类编码", required = true)
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = govQuotaCategoryService.isUniqueCode(id, code);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该政府服务平台首页指标分类编码已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证政府服务平台首页指标分类名称是否唯一")
    @RequestMapping(value = ServiceApi.GovFirsPage.QuotaCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueChannelName(
            @ApiParam(name = "id", value = "政府服务平台首页指标分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "政府服务平台首页指标分类名称", required = true)
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = govQuotaCategoryService.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该政府服务平台首页指标分类名称已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
