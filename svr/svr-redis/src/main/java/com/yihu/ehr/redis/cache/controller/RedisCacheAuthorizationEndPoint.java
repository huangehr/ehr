package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisCacheAuthorization;
import com.yihu.ehr.redis.cache.entity.RedisCacheAuthorization;
import com.yihu.ehr.redis.cache.service.RedisCacheAuthorizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存授权 接口
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "缓存授权接口", tags = {"缓存服务管理--缓存授权接口"})
public class RedisCacheAuthorizationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheAuthorizationService redisCacheAuthorizationService;

    @ApiOperation("根据ID获取缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.GetById, method = RequestMethod.GET)
    public MRedisCacheAuthorization getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(redisCacheAuthorizationService.getById(id), MRedisCacheAuthorization.class);
    }

    @ApiOperation(value = "根据条件获取缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Search, method = RequestMethod.GET)
    List<MRedisCacheAuthorization> search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RedisCacheAuthorization> redisMqChannels = redisCacheAuthorizationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, redisCacheAuthorizationService.getCount(filters), page, size);
        return (List<MRedisCacheAuthorization>) convertToModels(redisMqChannels, new ArrayList<MRedisCacheAuthorization>(), MRedisCacheAuthorization.class, fields);
    }

    @ApiOperation("新增缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Save, method = RequestMethod.POST)
    public MRedisCacheAuthorization add(
            @ApiParam(name = "entityJson", value = "缓存授权JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisCacheAuthorization newRedisCacheAuthorization = toEntity(entityJson, RedisCacheAuthorization.class);
        newRedisCacheAuthorization = redisCacheAuthorizationService.save(newRedisCacheAuthorization);
        return convertToModel(newRedisCacheAuthorization, MRedisCacheAuthorization.class);
    }

    @ApiOperation("更新缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Save, method = RequestMethod.PUT)
    public MRedisCacheAuthorization update(
            @ApiParam(name = "entityJson", value = "缓存授权JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) throws Exception {
        RedisCacheAuthorization updateRedisCacheAuthorization = toEntity(entityJson, RedisCacheAuthorization.class);
        updateRedisCacheAuthorization = redisCacheAuthorizationService.save(updateRedisCacheAuthorization);
        return convertToModel(updateRedisCacheAuthorization, MRedisCacheAuthorization.class);
    }

    @ApiOperation("删除缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Delete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "缓存授权ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        redisCacheAuthorizationService.delete(id);
    }

    @ApiOperation("验证指定缓存分类下应用ID是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.IsUniqueAppId, method = RequestMethod.GET)
    public boolean isUniqueAppId(
            @ApiParam(name = "id", value = "缓存授权ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "categoryCode", value = "缓存分类编码", required = true)
            @RequestParam(value = "categoryCode") String categoryCode,
            @ApiParam(name = "appId", value = "应用ID", required = true)
            @RequestParam(value = "appId") String appId) throws Exception {
        return redisCacheAuthorizationService.isUniqueAppId(id, categoryCode, appId);
    }

}
