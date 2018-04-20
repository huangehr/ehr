package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.redis.client.RedisCacheAuthorizationClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 缓存授权 Controller
 *
 * @author 张进军
 * @date 2017/11/27 16:21
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(description = "缓存授权接口", tags = {"缓存服务管理--缓存授权接口"})
public class RedisCacheAuthorizationController extends BaseController {

    @Autowired
    private RedisCacheAuthorizationClient redisCacheAuthorizationClient;

    @ApiOperation("根据ID获取缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        return redisCacheAuthorizationClient.getById(id);
    }

    @ApiOperation(value = "根据条件获取缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) int size) {
        return redisCacheAuthorizationClient.search(fields, filters, sorts, page, size);
    }

    @ApiOperation("新增缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "缓存授权JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return redisCacheAuthorizationClient.add(entityJson);
    }

    @ApiOperation("更新缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "缓存授权JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        return redisCacheAuthorizationClient.update(entityJson);
    }

    @ApiOperation("删除缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存授权ID", required = true)
            @RequestParam(value = "id") Integer id) {
        return redisCacheAuthorizationClient.delete(id);
    }

    @ApiOperation("验证缓存授权名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.IsUniqueAppId, method = RequestMethod.GET)
    public Envelop isUniqueAppId(
            @ApiParam(name = "id", value = "缓存授权ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "categoryCode", value = "缓存分类编码", required = true)
            @RequestParam(value = "categoryCode") String categoryCode,
            @ApiParam(name = "appId", value = "应用ID", required = true)
            @RequestParam(value = "appId") String appId) {
        return redisCacheAuthorizationClient.isUniqueAppId(id, categoryCode, appId);
    }

}
